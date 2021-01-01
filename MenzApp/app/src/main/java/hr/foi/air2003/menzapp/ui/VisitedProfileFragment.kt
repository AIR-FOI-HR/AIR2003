package hr.foi.air2003.menzapp.ui

import android.annotation.SuppressLint
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
import com.google.firebase.Timestamp
import hr.foi.air2003.menzapp.activities.MainActivity
import hr.foi.air2003.menzapp.R
import hr.foi.air2003.menzapp.assistants.DateTimePicker
import hr.foi.air2003.menzapp.assistants.ImageConverter
import hr.foi.air2003.menzapp.assistants.SharedViewModel
import hr.foi.air2003.menzapp.core.model.Feedback
import hr.foi.air2003.menzapp.core.model.Notification
import hr.foi.air2003.menzapp.core.model.Post
import hr.foi.air2003.menzapp.core.model.User
import hr.foi.air2003.menzapp.recyclerview.ProfileFeedbackRecyclerViewAdapter
import hr.foi.air2003.menzapp.recyclerview.ProfilePostRecyclerViewAdapter
import kotlinx.android.synthetic.main.fragment_visited_profile.*

class VisitedProfileFragment : Fragment() {
    private lateinit var dateTimePicker: DateTimePicker
    private lateinit var adapterPost: ProfilePostRecyclerViewAdapter
    private lateinit var adapterFeedback: ProfileFeedbackRecyclerViewAdapter
    private lateinit var authorId: String
    private lateinit var user: User
    private lateinit var visitedUser: User
    private val viewModel = SharedViewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        authorId = (targetFragment as HomeFragment).getAuthorId()
        user = (activity as MainActivity).getCurrentUser()
        return inflater.inflate(R.layout.fragment_visited_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        expandViewListener()
        createRecyclerViews()

        dateTimePicker = DateTimePicker()

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
            (activity as MainActivity).setCurrentFragment((targetFragment as HomeFragment))
        }
    }

    fun getVisitedUser(): User{
        return visitedUser
    }

    fun getCurrentUser(): User{
        return user
    }

    private fun createRecyclerViews() {
        adapterPost = ProfilePostRecyclerViewAdapter()
        adapterFeedback = ProfileFeedbackRecyclerViewAdapter(this)

        rvVisitedProfilePosts.hasFixedSize()
        rvVisitedProfilePosts.layoutManager = LinearLayoutManager(context)
        rvVisitedProfilePosts.itemAnimator = DefaultItemAnimator()
        rvVisitedProfilePosts.adapter = adapterPost

        rvVisitedProfileFeedbacks.hasFixedSize()
        rvVisitedProfileFeedbacks.layoutManager = LinearLayoutManager(context)
        rvVisitedProfileFeedbacks.itemAnimator = DefaultItemAnimator()
        rvVisitedProfileFeedbacks.adapter = adapterFeedback
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
        val liveData = viewModel.getUser(authorId)
        liveData.observe(viewLifecycleOwner, {
            val data = it.data
            if (data != null) {
                visitedUser = data
                retrieveUserData(visitedUser)
                checkSubscription(visitedUser)
            }
        })
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

        viewModel.getImage(user.profilePicture)
            .addOnSuccessListener { bytes ->
                val bitmap = ImageConverter.convertBytesToBitmap(bytes)
                val resized = ImageConverter.resizeBitmap(bitmap, ivVisitedProfilePhoto)
                ivVisitedProfilePhoto.setImageBitmap(resized)
            }
    }

    private fun createFeedbackLayout(authorId: String) {
        val liveData = viewModel.getFeedbacks(authorId)
        liveData.observe(viewLifecycleOwner, {
            val feedbacks: MutableList<Feedback> = mutableListOf()
            val data = it.data
            if (data != null) {
                for (d in data) {
                    feedbacks.add(d)
                }

                adapterFeedback.addItems(feedbacks)
            }
        })
    }

    private fun createPostLayout(authorId: String) {
        val liveData = viewModel.getPostsByAuthor(authorId)
        liveData.observe(viewLifecycleOwner, {
            val posts: MutableList<Post> = mutableListOf()
            val data = it.data
            if (data != null) {
                for (d in data) {
                    posts.add(d)
                }

                adapterPost.addItems(posts)
            }
        })
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

    private fun sendSubscriptionNotification(){
        val usersList: MutableList<String> = mutableListOf()
        usersList.add(visitedUser.userId)

        val notification = Notification(
                authorId = user.userId,
                content = "Nova pretplata!",
                request = false,
                postId = "subscription",
                timestamp = Timestamp(System.currentTimeMillis()/1000,0),
                recipientsId = usersList
        )

        viewModel.createNotification(notification)
    }

}