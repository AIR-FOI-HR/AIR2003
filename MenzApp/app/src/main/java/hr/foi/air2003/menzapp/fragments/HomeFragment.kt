package hr.foi.air2003.menzapp.fragments

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import hr.foi.air2003.menzapp.R
import hr.foi.air2003.menzapp.communicators.FragmentsCommunicator
import hr.foi.air2003.menzapp.database.FirestoreService
import hr.foi.air2003.menzapp.database.model.Post
import kotlinx.android.synthetic.main.fragment_home.*
import java.sql.Timestamp
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class HomeFragment : Fragment(), FragmentsCommunicator {
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStart() {
        super.onStart()

        val currentDateTime = LocalDateTime.now()
        val timestampFormatter = DateTimeFormatter.ofPattern("yyyy-mm-dd hh:mm:ss.SSS")
        val timestampDateTime = currentDateTime.format(timestampFormatter).toString()

        filterPosts(Timestamp.valueOf(timestampDateTime))

        filterDateTime.setOnClickListener {
            var bottomFragment = BottomFilterFragment()
            bottomFragment.setTargetFragment(this, 1)
            bottomFragment.show(requireFragmentManager(), "Filter")
        }

        btnNewPost.setOnClickListener {
            var newPostFragment = NewPostFragment()
            newPostFragment.setTargetFragment(this, 1)
            newPostFragment.show(requireFragmentManager(), "New post")
        }
    }

    override fun sendData(data: String) {
        updateFilter(data)
        var timestamp = Timestamp.valueOf(data)
        filterPosts(timestamp)
    }

    private fun filterPosts(timestamp: Timestamp) { // TODO implement filtering
        println(timestamp)
        val posts: MutableList<Any> = ArrayList() // TODO change type to Post

        FirestoreService.instance.getAll("Posts") // Get all with query timestamp
                .addOnSuccessListener { collection ->
                    collection.documents.forEach {
                        posts.add(it.getData()!!) // Mutable Map retrieved, convert to Post
                    }
                    println(posts)
                }
    }

    private fun updateFilter(data: String) {
        var dataSplit = data.split("-")
        tvSelectedDateTime?.text = "${dataSplit[2].substring(0, 2)}.${dataSplit[1]}.${dataSplit[0]}. ${dataSplit[2].substring(2, 8)}"
    }

    private fun requestToJoin(post: Post) { // Use this function to respond to a Post
        val updatedUserRequests: MutableList<String> = ArrayList()
        if (post.userRequests.size > 0) {
            post.userRequests.forEach { updatedUserRequests.add(it) }
        }
        updatedUserRequests.add(Firebase.auth.currentUser!!.uid)
        FirestoreService.instance.update("Posts", post.postId, "userRequests", updatedUserRequests) // Get all with query -> FieldValue.serverTimestamp()
        // TODO implement listener on Post for author, when data on userRequests is changed, notify user
    }

}