package hr.foi.air2003.menzapp.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.transition.Explode
import androidx.transition.TransitionManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import hr.foi.air2003.menzapp.R
import hr.foi.air2003.menzapp.assistants.DateTimePicker
import hr.foi.air2003.menzapp.login.LoginActivity
import hr.foi.air2003.menzapp.viewmodel.ProfileViewModel
import kotlinx.android.synthetic.main.fragment_profile.*

class ProfileFragment : Fragment() {
    private lateinit var dateTimePicker: DateTimePicker
    private lateinit var viewModel: ProfileViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onStart() {
        super.onStart()
        viewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)
        dateTimePicker = DateTimePicker()

        val userId = Firebase.auth.currentUser?.uid
        retrieveUserData(userId)

        expandablePosts.visibility = View.GONE
        expandableFeedbacks.visibility = View.GONE

        cvMyPosts.setOnClickListener {
            if (expandablePosts.visibility == View.GONE) {
                TransitionManager.beginDelayedTransition(cvMyPosts, Explode())
                expandablePosts.visibility = View.VISIBLE
            } else {
                TransitionManager.beginDelayedTransition(cvMyPosts, Explode())
                expandablePosts.visibility = View.GONE
            }
        }

        cvFeedback.setOnClickListener {
            if (expandableFeedbacks.visibility == View.GONE) {
                TransitionManager.beginDelayedTransition(cvFeedback, Explode())
                expandableFeedbacks.visibility = View.VISIBLE
            } else {
                TransitionManager.beginDelayedTransition(cvFeedback, Explode())
                expandableFeedbacks.visibility = View.GONE
            }
        }

        btnLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(activity, LoginActivity::class.java)
            activity?.startActivity(intent)
        }
    }

    private fun retrieveUserData(userId: String?) {
        if (!userId.isNullOrEmpty()) {

            //Populate user info with data from firestore
            createUserLayout(userId)

            //Populate posts with data from firestore
            createPostLayout(userId)

            //Populate feedbacks with data from firestore
            createFeedbackLayout(userId)
        }
    }

    private fun createFeedbackLayout(userId: String) {
        val liveData = viewModel.getFeedbacks(userId)
        liveData.observe(this, {
            val feedbacks = it.data
            if (feedbacks != null){
                for(feedback in feedbacks){
                    // TODO Populate data inside RecyclerView
                }
            }
        })
    }

    private fun createPostLayout(userId: String) {
        val liveData = viewModel.getPosts(userId)
        liveData.observe(this, {
            val posts = it.data
            if(posts != null){
                for(post in posts){
                    // TODO Populate data inside RecyclerView
                }
            }
        })
    }

    private fun createUserLayout(userId: String) {
        val liveData = viewModel.getUser(userId)
        liveData.observe(this, {
            val user = it.data
            if (user != null) {
                tvFullName.text = user.fullName
                tvAboutMe.text = user.bio
            }
        })
    }

    private fun editPost(postId: String) {
        val bundle = Bundle()
        bundle.putString("post", postId)

        val newPostFragment = NewPostFragment()
        newPostFragment.setTargetFragment(this, 1)
        newPostFragment.arguments = bundle
        newPostFragment.show(requireFragmentManager(), "Post")
    }
}