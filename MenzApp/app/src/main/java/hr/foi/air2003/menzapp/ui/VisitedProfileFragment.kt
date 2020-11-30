package hr.foi.air2003.menzapp.ui

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

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        authorId = (targetFragment as HomeFragment).getAuthorId()
        return inflater.inflate(R.layout.fragment_visited_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        expandViewListener()
        createRecyclerViews()

        viewModel = ViewModelProvider(this).get(VisitedProfileViewModel::class.java)
        dateTimePicker = DateTimePicker()

        retrieveUserData(authorId)

        btnBack.setOnClickListener {
            (activity as MainActivity).setCurrentFragment(HomeFragment())
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

    private fun retrieveUserData(authorId: String?) {
        if (authorId != null) {
            //Populate user info with data from firestore
            createUserLayout(authorId)
            //Populate posts with data from firestore
            createPostLayout(authorId)
            //Populate feedbacks with data from firestore
            createFeedbackLayout(authorId)
        }
    }

    private fun createUserLayout(authorId: String) {
        val liveData = viewModel.getUser(authorId)
        liveData.observe(viewLifecycleOwner, {
            val data = it.data
            if (data != null) {
                tvProfileFullName.text = data.fullName
                tvProfileAboutMe.text = data.bio
            }
        })

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
}