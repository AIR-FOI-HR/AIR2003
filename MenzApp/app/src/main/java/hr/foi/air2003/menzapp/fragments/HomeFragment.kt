package hr.foi.air2003.menzapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import hr.foi.air2003.menzapp.R
import hr.foi.air2003.menzapp.assistants.DateTimePicker
import hr.foi.air2003.menzapp.communicators.FragmentsCommunicator
import hr.foi.air2003.menzapp.core.model.Post
import hr.foi.air2003.menzapp.viewmodel.HomeViewModel
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.home_post.view.*
import java.text.SimpleDateFormat

class HomeFragment : Fragment(), FragmentsCommunicator {
    private lateinit var dateTimePicker: DateTimePicker
    private lateinit var dynamicalViewPosts: View
    private lateinit var viewModel: HomeViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onStart() {
        super.onStart()

        dateTimePicker = DateTimePicker()
        dynamicalViewPosts = View(context)
        viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        val currentDateTime = Timestamp(System.currentTimeMillis() / 1000, 0)
        filterPosts(currentDateTime)

        filterDateTime.setOnClickListener {
            var bottomFragment = BottomFilterFragment()
            bottomFragment.setTargetFragment(this, 1)
            bottomFragment.show(requireFragmentManager(), "Filter")
        }

        btnNewPost.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("post", "")

            var newPostFragment = NewPostFragment()
            newPostFragment.setTargetFragment(this, 1)
            newPostFragment.arguments = bundle
            newPostFragment.show(requireFragmentManager(), "New post")
        }
    }


    override fun sendData(data: String) {
        updateFilter(data)

        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm").parse(data)
        filterPosts(Timestamp(sdf.time / 1000, 0))
    }

    private fun filterPosts(timestamp: Timestamp) {
        val userId: String? = Firebase.auth.currentUser?.uid

        if(userId != null){
            val liveData = viewModel.getAllPosts(userId)
            liveData.observe(this, {
                val posts = it.data
                if(posts != null){
                    for (post in posts){
                        if(post.timestamp >= timestamp)
                            createPostLayout(post)
                    }
                }
            })
        }
    }

    private fun createPostLayout(post: Post) {
        dynamicalViewPosts = LayoutInflater.from(context).inflate(R.layout.home_post, null)
        homeLayout.addView(dynamicalViewPosts)

        val liveData = viewModel.getAuthor(post.authorId)
        liveData.observe(this, {
            val user = it.data
            if(user != null)
                dynamicalViewPosts.tvAuthorName.text = user.fullName
        })

        val dateTime = dateTimePicker.timestampToString(post.timestamp).split("/")
        dynamicalViewPosts.tvDateTime.text = "${dateTime[0]} ${dateTime[1]}"
        dynamicalViewPosts.tvNumberOfPeople.text = "Optimalan broj ljudi: ${post.numberOfPeople}"
        dynamicalViewPosts.tvDescription.text = post.description
        dynamicalViewPosts.btnRespond.setOnClickListener {
            requestToJoin(post)
            it.visibility = View.GONE

            // TODO Maybe implement button to undo request
        }
    }

    private fun updateFilter(data: String) {
        var dataSplit = data.split("-")
        tvSelectedDateTime?.text = "${dataSplit[2].substring(0, 2)}.${dataSplit[1]}.${dataSplit[0]}. ${dataSplit[2].substring(2)}"
    }

    private fun requestToJoin(post: Post) {
        val updatedUserRequests: MutableList<String> = mutableListOf()
        if (post.userRequests.isNotEmpty()) {
            post.userRequests.forEach { updatedUserRequests.add(it) }
        }

        updatedUserRequests.add(Firebase.auth.currentUser!!.uid)
        post.userRequests = updatedUserRequests
        viewModel.updateUserRequests(post)

        // TODO implement listener on Post for author, when data on userRequests is changed, notify user
    }
}