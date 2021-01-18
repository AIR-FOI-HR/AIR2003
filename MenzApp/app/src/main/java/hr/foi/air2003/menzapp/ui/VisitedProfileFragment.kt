package hr.foi.air2003.menzapp.ui

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.transition.Explode
import androidx.transition.TransitionManager
import coil.api.load
import com.google.firebase.Timestamp
import hr.foi.air2003.menzapp.activities.MainActivity
import hr.foi.air2003.menzapp.R
import hr.foi.air2003.menzapp.assistants.AlertDialogBuilder
import hr.foi.air2003.menzapp.assistants.DateTimePicker
import hr.foi.air2003.menzapp.assistants.SharedViewModel
import hr.foi.air2003.menzapp.core.model.Notification
import hr.foi.air2003.menzapp.core.model.Post
import hr.foi.air2003.menzapp.core.model.User
import hr.foi.air2003.menzapp.recyclerview.ProfileFeedbackRecyclerViewAdapter
import hr.foi.air2003.menzapp.recyclerview.ProfilePostRecyclerViewAdapter
import kotlinx.android.synthetic.main.alert_dialog.view.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_visited_profile.*
import java.lang.Exception

class VisitedProfileFragment : Fragment() {
    private lateinit var dateTimePicker: DateTimePicker
    private lateinit var adapterPost: ProfilePostRecyclerViewAdapter
    private lateinit var adapterFeedback: ProfileFeedbackRecyclerViewAdapter
    private var authorId: String? = null
    private lateinit var user: User
    private lateinit var parent: Fragment
    private lateinit var visitedUser: User
    private lateinit var builder: AlertDialog.Builder
    private lateinit var vd: View
    private var alertDialogBuilder = AlertDialogBuilder()
    private val viewModel = SharedViewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (targetRequestCode == 1) {
            parent = targetFragment as HomeFragment
            authorId = (parent as HomeFragment).getAuthorId()
        } else if (targetRequestCode == 2) {
            parent = targetFragment as SearchFragment
            visitedUser = (parent as SearchFragment).getVisitedUser()
        }

        user = (activity as MainActivity).getCurrentUser()
        return inflater.inflate(R.layout.fragment_visited_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        expandViewListener()
        createRecyclerViews()

        dateTimePicker = DateTimePicker()
        builder = alertDialogBuilder.createAlertDialog(requireContext(), layoutInflater)
        vd = alertDialogBuilder.getView()

        requireUserData()

        btnSubscribe.setOnClickListener {
            if (btnSubscribe.text == getString(R.string.subscribe)) {
                updateSubscription(true)
                btnSubscribe.text = getString(R.string.unsubscribe)
                btnSubscribe.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.btn_fill_circled)
                btnSubscribe.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.green_primary
                    )
                )
            } else {
                updateSubscription(false)
                btnSubscribe.text = getString(R.string.subscribe)
                btnSubscribe.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.btn_transparent_circled)
                btnSubscribe.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            }
        }

        tvRateUser.setOnClickListener {
            val newFeedbackFragment = NewFeedbackFragment()
            newFeedbackFragment.setTargetFragment(this, 1)
            newFeedbackFragment.show(requireFragmentManager(), "Feedback")
            rvVisitedProfileFeedbacks.adapter?.notifyDataSetChanged()
        }

        btnBack.setOnClickListener {
            (activity as MainActivity).setCurrentFragment(parent)
        }
    }

    fun getVisitedUser(): User {
        return visitedUser
    }

    fun getCurrentUser(): User {
        return user
    }

    private fun createRecyclerViews() {
        adapterPost = ProfilePostRecyclerViewAdapter(this)
        adapterFeedback = ProfileFeedbackRecyclerViewAdapter()

        rvVisitedProfilePosts.hasFixedSize()
        rvVisitedProfilePosts.layoutManager = LinearLayoutManager(context)
        rvVisitedProfilePosts.itemAnimator = DefaultItemAnimator()
        rvVisitedProfilePosts.adapter = adapterPost

        rvVisitedProfileFeedbacks.hasFixedSize()
        rvVisitedProfileFeedbacks.layoutManager = LinearLayoutManager(context)
        rvVisitedProfileFeedbacks.itemAnimator = DefaultItemAnimator()
        rvVisitedProfileFeedbacks.adapter = adapterFeedback

        adapterPost.sendRequest = { post ->
            try {
                requestToJoin(post)

                vd.tvAlertMessage.text = getString(R.string.alert_success_join)
                val dialog = builder.create()
                dialog.show()
                vd.tvOkButton.setOnClickListener {
                    dialog.dismiss()
                }

            } catch (e: Exception) {
                vd.tvAlertTitle.text = getString(R.string.alert_fail)
                vd.tvAlertMessage.text = getString(R.string.alert_fail_join)
                vd.ivAlertIcon.background = ContextCompat.getDrawable(requireContext(), R.drawable.ic_warning)
                val dialog = builder.create()
                dialog.show()
                vd.tvOkButton.setOnClickListener {
                    dialog.dismiss()
                }
            }
        }
    }

    private fun expandViewListener() {
        expandableVisitedProfilePosts.visibility = View.GONE
        expandableVisitedProfileFeedbacks.visibility = View.GONE

        cvVisitedProfileMyPosts.setOnClickListener {
            if (expandableVisitedProfilePosts.visibility == View.GONE) {
                TransitionManager.beginDelayedTransition(cvVisitedProfileMyPosts, Explode())
                expandableVisitedProfilePosts.visibility = View.VISIBLE
            } else {
                TransitionManager.beginDelayedTransition(cvVisitedProfileMyPosts, Explode())
                expandableVisitedProfilePosts.visibility = View.GONE
            }
        }

        cvVisitedProfileFeedback.setOnClickListener {
            if (expandableVisitedProfileFeedbacks.visibility == View.GONE) {
                TransitionManager.beginDelayedTransition(cvVisitedProfileFeedback, Explode())
                expandableVisitedProfileFeedbacks.visibility = View.VISIBLE
            } else {
                TransitionManager.beginDelayedTransition(cvVisitedProfileFeedback, Explode())
                expandableVisitedProfileFeedbacks.visibility = View.GONE
            }
        }
    }

    private fun requireUserData() {
        if (authorId != null) {
            val liveData = viewModel.getUser(authorId!!)
            liveData.observe(viewLifecycleOwner, {
                val data = it.data
                if (data != null) {
                    visitedUser = data
                    retrieveUserData(visitedUser)
                    checkSubscription(visitedUser)
                }
            })
        } else {
            retrieveUserData(visitedUser)
            checkSubscription(visitedUser)
        }
    }

    private fun checkSubscription(visitedUser: User) {
        if (user.subscribedTo.contains(visitedUser.userId)) {
            btnSubscribe.text = getString(R.string.unsubscribe)
            btnSubscribe.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.btn_fill_circled)
            btnSubscribe.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.green_primary
                )
            )
        }
    }

    private fun retrieveUserData(visitedUser: User) {
        //Populate user info with data from firestore
        createUserLayout(visitedUser)
        //Populate posts with data from firestore
        createPostLayout(visitedUser.userId)
        //Populate feedbacks with data from firestore
        createFeedbackLayout(visitedUser.userId)
    }

    @SuppressLint("SetTextI18n")
    private fun createUserLayout(visitedUser: User) {
        tvVisitedProfileFullName.text = visitedUser.fullName
        tvVisitedProfileAboutMe.text = visitedUser.bio
        tvVisitedProfileSubscribers.text = "Broj pretplatnika: ${visitedUser.subscribersCount}"

        viewModel.getImage(visitedUser.profilePicture)
            .addOnSuccessListener { url ->
                ivVisitedProfilePhoto.load(url)
            }
    }

    private fun createFeedbackLayout(authorId: String) {
        val liveData = viewModel.getFeedbacks(authorId)
        liveData.observe(viewLifecycleOwner, {
            val data = it.data
            if (!data.isNullOrEmpty()) {
                val feedbacks = data.sortedByDescending { feedback -> feedback.mark }
                adapterFeedback.addItems(feedbacks)
            }
        })
    }

    private fun createPostLayout(authorId: String) {
        val liveData = viewModel.getPostsByAuthor(authorId)
        liveData.observe(viewLifecycleOwner, {
            val data = it.data
            val posts: MutableList<Post> = mutableListOf()
            if (!data.isNullOrEmpty()) {
                for(d in data){
                    if(d.timestamp >= Timestamp.now())
                        posts.add(d)
                }

                val sorted = posts.sortedByDescending { post -> post.timestamp }
                adapterPost.addItems(sorted)
            }
        })
    }

    private fun requestToJoin(post: Post) {
        val updatedUserRequests: MutableList<Map<String, Any>> = mutableListOf()
        if (post.userRequests.isNotEmpty()) {
            updatedUserRequests.addAll(0, post.userRequests)
        }

        updatedUserRequests.add(mapOf(Pair("opened", true), Pair("userId", user.userId)))
        post.userRequests = updatedUserRequests
        viewModel.updateUserRequests(post)
        rvVisitedProfilePosts.adapter?.notifyDataSetChanged()

        val timestamp = dateTimePicker.timestampToString(post.timestamp).split("/")

        val notification = Notification(
                authorId = user.userId,
                content = "Novi zahtjev na dan: ${timestamp[0]} ${timestamp[1]}",
                request = true,
                postId = post.postId,
                recipientsId = listOf(post.authorId),
                timestamp = Timestamp(System.currentTimeMillis() / 1000, 0),
        )
        viewModel.createNotificationRequest(notification)
    }

    private fun updateSubscription(subscribe: Boolean) {
        if (subscribe) {
            visitedUser.subscribersCount++
            val subscription: MutableList<String> = user.subscribedTo as MutableList<String>
            subscription.add(visitedUser.userId)
            user.subscribedTo = subscription
            sendSubscriptionNotification()
        } else {
            visitedUser.subscribersCount--
            val subscription: MutableList<String> = user.subscribedTo as MutableList<String>
            subscription.remove(visitedUser.userId)
            user.subscribedTo = subscription
        }

        viewModel.updateUser(user)
        viewModel.updateUser(visitedUser)
    }

    private fun sendSubscriptionNotification() {
        val usersList: MutableList<String> = mutableListOf()
        usersList.add(visitedUser.userId)

        val notification = Notification(
            authorId = user.userId,
            content = "Nova pretplata!",
            request = false,
            postId = "subscription",
            timestamp = Timestamp(System.currentTimeMillis() / 1000, 0),
            recipientsId = usersList
        )

        viewModel.createNotification(notification)
    }
}