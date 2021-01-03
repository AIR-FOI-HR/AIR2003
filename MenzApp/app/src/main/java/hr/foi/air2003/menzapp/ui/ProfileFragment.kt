package hr.foi.air2003.menzapp.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.transition.Explode
import androidx.transition.TransitionManager
import coil.api.load
import com.google.gson.Gson
import hr.foi.air2003.menzapp.activities.MainActivity
import hr.foi.air2003.menzapp.R
import hr.foi.air2003.menzapp.activities.SettingsFragmentActivity
import hr.foi.air2003.menzapp.assistants.DateTimePicker
import hr.foi.air2003.menzapp.assistants.ImageConverter
import hr.foi.air2003.menzapp.assistants.SharedViewModel
import hr.foi.air2003.menzapp.core.model.Feedback
import hr.foi.air2003.menzapp.core.model.Post
import hr.foi.air2003.menzapp.core.model.User
import hr.foi.air2003.menzapp.recyclerview.ProfileFeedbackRecyclerViewAdapter
import hr.foi.air2003.menzapp.recyclerview.ProfilePostRecyclerViewAdapter
import kotlinx.android.synthetic.main.fragment_profile.*

class ProfileFragment : Fragment() {
    private lateinit var dateTimePicker: DateTimePicker
    private lateinit var adapterPost: ProfilePostRecyclerViewAdapter
    private lateinit var adapterFeedback: ProfileFeedbackRecyclerViewAdapter
    private lateinit var user: User
    private lateinit var post: Post
    private val viewModel = SharedViewModel()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        user = (activity as MainActivity).getCurrentUser()
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        expandViewListener()
        createRecyclerViews()

        dateTimePicker = DateTimePicker()

        retrieveUserData(user)

        btnSettings.setOnClickListener {
            val jsonUser = Gson().toJson(user)
            val intent = Intent(context, SettingsFragmentActivity::class.java)
            intent.putExtra("user", jsonUser)
            startActivity(intent)
        }

        btnNotifications.setOnClickListener {
            val notificationFragment = NotificationFragment()
            notificationFragment.setTargetFragment(this, 1)
            (activity as MainActivity).setCurrentFragment(notificationFragment)
        }
    }

    private fun createRecyclerViews() {
        adapterPost = ProfilePostRecyclerViewAdapter()
        adapterFeedback = ProfileFeedbackRecyclerViewAdapter(this)

        rvProfilePosts.hasFixedSize()
        rvProfilePosts.layoutManager = LinearLayoutManager(context)
        rvProfilePosts.itemAnimator = DefaultItemAnimator()
        rvProfilePosts.adapter = adapterPost

        rvProfileFeedbacks.hasFixedSize()
        rvProfileFeedbacks.layoutManager = LinearLayoutManager(context)
        rvProfileFeedbacks.itemAnimator = DefaultItemAnimator()
        rvProfileFeedbacks.adapter = adapterFeedback

        adapterPost.editClick = { post ->
            editPost(post)
        }
    }

    private fun expandViewListener() {
        expandableProfilePosts.visibility = View.VISIBLE
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

    private fun retrieveUserData(user: User) {
        //Populate user info with data from firestore
        createUserLayout(user)

        //Populate posts with data from firestore
        createPostLayout(user.userId)

        //Populate feedbacks with data from firestore
        createFeedbackLayout(user.userId)
    }

    private fun createFeedbackLayout(userId: String) {
        val liveData = viewModel.getFeedbacks(userId)
        liveData.observe(viewLifecycleOwner, {
            val feedbacks: MutableList<Feedback> = mutableListOf()
            val data = it.data
            if(data != null){
                for(d in data){
                    feedbacks.add(d)
                }

                adapterFeedback.addItems(feedbacks)
            }
        })
    }

    private fun createPostLayout(userId: String) {
        val liveData = viewModel.getPostsByAuthor(userId)
        liveData.observe(viewLifecycleOwner, {
            val posts: MutableList<Post> = mutableListOf()
            val data = it.data
            if(data != null){
                for(d in data){
                        posts.add(d)
                }

                adapterPost.addItems(posts)
            }
        })
    }

    @SuppressLint("SetTextI18n")
    private fun createUserLayout(user: User) {
        tvProfileFullName.text = user.fullName
        tvProfileAboutMe.text = user.bio
        tvProfileSubscribers.text = "Broj pretplatnika: ${user.subscribersCount}"

        viewModel.getImage(user.profilePicture)
            .addOnSuccessListener { url ->
                //val bitmap = ImageConverter.convertBytesToBitmap(bytes)
                //val resized = ImageConverter.resizeBitmap(bitmap, ivProfilePhoto)
                ivProfilePhoto.load(url)
            }
    }

    private fun editPost(post: Post) {
        this.post = post
        val newPostFragment = NewPostFragment()
        newPostFragment.setTargetFragment(this, 1)
        newPostFragment.show(requireFragmentManager(), "Post")
    }


    fun getPost(): Post{
        return this.post
    }
}