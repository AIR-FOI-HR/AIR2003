package hr.foi.air2003.menzapp.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.transition.Explode
import androidx.transition.TransitionManager
import hr.foi.air2003.menzapp.MainActivity
import hr.foi.air2003.menzapp.R
import hr.foi.air2003.menzapp.assistants.DateTimePicker
import hr.foi.air2003.menzapp.core.model.Feedback
import hr.foi.air2003.menzapp.core.model.Post
import hr.foi.air2003.menzapp.core.model.User
import hr.foi.air2003.menzapp.recyclerview.ProfileFeedbackRecyclerViewAdapter
import hr.foi.air2003.menzapp.recyclerview.ProfilePostRecyclerViewAdapter
import kotlinx.android.synthetic.main.fragment_profile.cvProfileFeedback
import kotlinx.android.synthetic.main.fragment_profile.cvProfileMyPosts
import kotlinx.android.synthetic.main.fragment_profile.expandableProfileFeedbacks
import kotlinx.android.synthetic.main.fragment_profile.expandableProfilePosts
import kotlinx.android.synthetic.main.fragment_profile.rvProfileFeedbacks
import kotlinx.android.synthetic.main.fragment_profile.rvProfilePosts
import kotlinx.android.synthetic.main.fragment_profile.tvProfileAboutMe
import kotlinx.android.synthetic.main.fragment_profile.tvProfileFullName
import kotlinx.android.synthetic.main.fragment_visited_profile.*

class VisitedProfileFragment : Fragment() {
    private lateinit var dateTimePicker: DateTimePicker
    private lateinit var viewModel: VisitedProfileViewModel
    private lateinit var adapterPost: ProfilePostRecyclerViewAdapter
    private lateinit var adapterFeedback: ProfileFeedbackRecyclerViewAdapter
    private lateinit var authorId: String
    private lateinit var user: User
    private lateinit var visitedUser: User

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

        viewModel = ViewModelProvider(this).get(VisitedProfileViewModel::class.java)
        dateTimePicker = DateTimePicker()

        requireUserData()

        btnBack.setOnClickListener {
            (activity as MainActivity).setCurrentFragment((targetFragment as HomeFragment))
        }

        btnSubscribe.setOnClickListener {
            updateSubscription(true)
            btnSubscribe.visibility = View.GONE
            btnUnsubscribe.visibility = View.VISIBLE

        }

        btnUnsubscribe.setOnClickListener {
            updateSubscription(false)
            btnUnsubscribe.visibility = View.VISIBLE
            btnSubscribe.visibility = View.GONE
        }
    }

    private fun createRecyclerViews() {
        adapterPost = ProfilePostRecyclerViewAdapter()
        adapterFeedback = ProfileFeedbackRecyclerViewAdapter()

        rvProfilePosts.hasFixedSize()
        rvProfilePosts.layoutManager = LinearLayoutManager(context)
        rvProfilePosts.itemAnimator = DefaultItemAnimator()
        rvProfilePosts.adapter = adapterPost

        rvProfileFeedbacks.hasFixedSize()
        rvProfileFeedbacks.layoutManager = LinearLayoutManager(context)
        rvProfileFeedbacks.itemAnimator = DefaultItemAnimator()
        rvProfileFeedbacks.adapter = adapterFeedback
    }

    private fun expandViewListener() {
        expandableProfilePosts.visibility = View.GONE
        expandableProfileFeedbacks.visibility = View.GONE

        cvProfileMyPosts.setOnClickListener {
            if (expandableProfilePosts.visibility == View.GONE) {
                TransitionManager.beginDelayedTransition(cvProfileMyPosts, Explode())
                expandableProfilePosts.visibility = View.VISIBLE
            } else {
                TransitionManager.beginDelayedTransition(cvProfileMyPosts, Explode())
                expandableProfilePosts.visibility = View.GONE
            }
        }

        cvProfileFeedback.setOnClickListener {
            if (expandableProfileFeedbacks.visibility == View.GONE) {
                TransitionManager.beginDelayedTransition(cvProfileFeedback, Explode())
                expandableProfileFeedbacks.visibility = View.VISIBLE
            } else {
                TransitionManager.beginDelayedTransition(cvProfileFeedback, Explode())
                expandableProfileFeedbacks.visibility = View.GONE
            }
        }
    }

    private fun requireUserData(){
        val liveData = viewModel.getUser(authorId)
        liveData.observe(viewLifecycleOwner, {
            val data = it.data
            if(data != null){
                visitedUser = data
                retrieveUserData(visitedUser)
                checkSubscription(visitedUser)
            }
        })
    }

    private fun checkSubscription(visitedUser: User) {
        if (user.subscribedTo.contains(visitedUser.userId)) {
            btnUnsubscribe.visibility = View.VISIBLE
            btnSubscribe.visibility = View.GONE
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
        tvProfileFullName.text = visitedUser.fullName
        tvProfileAboutMe.text = visitedUser.bio
        tvProfileSubscribers.text = "Broj pretplatnika: ${visitedUser.subscribersCount}"

        // TODO Show user profile picture
    }

    private fun createFeedbackLayout(authorId: String) {
        val liveData = viewModel.getFeedbacks(authorId)
        liveData.observe(viewLifecycleOwner, {
            val feedbacks: MutableList<Feedback> = mutableListOf()
            val data = it.data
            if(data != null){
                for(d in data){
                    feedbacks.add(d.item)
                }

                adapterFeedback.addItems(feedbacks)
            }
        })
    }

    private fun createPostLayout(authorId: String) {
        val liveData = viewModel.getPosts(authorId)
        liveData.observe(viewLifecycleOwner, {
            val posts: MutableList<Post> = mutableListOf()
            val data = it.data
            if(data != null){
                for(d in data){
                    posts.add(d.item)
                }

                adapterPost.addItems(posts)
            }
        })
    }

    private fun updateSubscription(subscribe: Boolean) {
        if(subscribe){
            visitedUser.subscribersCount++
            val subscription: MutableList<String> = user.subscribedTo as MutableList<String>
            subscription.add(visitedUser.userId)
            user.subscribedTo = subscription
        }else{
            visitedUser.subscribersCount--
            val subscription: MutableList<String> = user.subscribedTo as MutableList<String>
            subscription.remove(visitedUser.userId)
            user.subscribedTo = subscription
        }

        viewModel.updateUser(user)
        viewModel.updateUser(visitedUser)
    }
}