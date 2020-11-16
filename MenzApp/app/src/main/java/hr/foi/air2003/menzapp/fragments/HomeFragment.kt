package hr.foi.air2003.menzapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import hr.foi.air2003.menzapp.R
import hr.foi.air2003.menzapp.communicators.FragmentsCommunicator
import hr.foi.air2003.menzapp.database.FirestoreService
import hr.foi.air2003.menzapp.database.model.Post
import kotlinx.android.synthetic.main.fragment_home.*
import java.text.SimpleDateFormat

class HomeFragment : Fragment(), FragmentsCommunicator {
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onStart() {
        super.onStart()

        val currentDateTime = Timestamp(System.currentTimeMillis()/1000,0)
        filterPosts(currentDateTime)

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

        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm").parse(data)
        filterPosts(Timestamp(sdf.time/1000, 0))
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

        /*
        val list = listOf<String>("A", "B")
        try {
            FirestoreService.instance.getAllWithQuery("Posts", FirestoreService.Operation.IN, "Posts", list)
        }catch (e: Exception){
        }
         */
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
        FirestoreService.instance.updateField("Posts", post.postId, "userRequests", updatedUserRequests) // Get all with query -> FieldValue.serverTimestamp()
        // TODO implement listener on Post for author, when data on userRequests is changed, notify user
    }
}