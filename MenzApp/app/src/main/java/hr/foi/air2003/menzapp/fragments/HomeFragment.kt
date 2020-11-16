package hr.foi.air2003.menzapp.fragments

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import hr.foi.air2003.menzapp.R
import hr.foi.air2003.menzapp.assistants.DateTimePicker
import hr.foi.air2003.menzapp.communicators.FragmentsCommunicator
import hr.foi.air2003.menzapp.database.FirestoreService
import hr.foi.air2003.menzapp.database.model.Post
import hr.foi.air2003.menzapp.database.model.User
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.post.view.tvDateTime
import kotlinx.android.synthetic.main.post.view.tvDescription
import kotlinx.android.synthetic.main.post.view.tvNumberOfPeople
import kotlinx.android.synthetic.main.post_home.view.*
import java.text.SimpleDateFormat

class HomeFragment : Fragment(), FragmentsCommunicator {
    private lateinit var dateTimePicker: DateTimePicker

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onResume() {
        super.onResume()
        Toast.makeText(context, "On resume started", Toast.LENGTH_LONG).show()
    }

    override fun onStart() {
        super.onStart()

        dateTimePicker = DateTimePicker()

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
        val userId = Firebase.auth.currentUser?.uid

        //Populate posts with data from firestore
        FirestoreService.instance.getAllWithQuery(FirestoreService.Collection.POST, FirestoreService.Operation.NOT_EQUAL_TO, "authorId", userId.toString())
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val json = Gson().toJson(document.data)
                        val post = Gson().fromJson(json, Post::class.java)

                        if (post.timestamp >= timestamp) { createPostLayout(post) }
                    }
                }
                .addOnFailureListener { e -> Log.w(ContentValues.TAG, "Error retrieving document", e) }
    }

    private fun createPostLayout(post: Post) {
        val dynamicalViewPosts: View = LayoutInflater.from(context).inflate(R.layout.post_home, null)
        homeLayout.addView(dynamicalViewPosts)

        val dateTime = dateTimePicker.timestampToString(post.timestamp).split("/")
        dynamicalViewPosts.tvDateTime.text = "${dateTime[0]} ${dateTime[1]}"
        dynamicalViewPosts.tvNumberOfPeople.text = "Optimalan broj ljudi: ${post.numberOfPeople}"
        dynamicalViewPosts.tvDescription.text = post.description
        dynamicalViewPosts.btnRespond.setOnClickListener {
            requestToJoin(post)
            it.visibility = View.GONE

            // TODO Maybe implement button to undo request
        }

        FirestoreService.instance.getDocumentByID(FirestoreService.Collection.USER, post.authorId)
                .addOnSuccessListener { document ->
                    val json = Gson().toJson(document.data)
                    val user = Gson().fromJson(json, User::class.java)
                    dynamicalViewPosts.tvAuthorName.text = user.fullName
                }
                .addOnFailureListener { e -> Log.w(ContentValues.TAG, "Error retrieving document", e) }
    }

    private fun updateFilter(data: String) {
        var dataSplit = data.split("-")
        tvSelectedDateTime?.text = "${dataSplit[2].substring(0, 2)}.${dataSplit[1]}.${dataSplit[0]}. ${dataSplit[2].substring(2, 8)}"
    }

    private fun requestToJoin(post: Post) {
        val updatedUserRequests: MutableList<String> = mutableListOf()
        if (post.userRequests.isNotEmpty()) {
            post.userRequests.forEach { updatedUserRequests.add(it) }
        }

        updatedUserRequests.add(Firebase.auth.currentUser!!.uid)
        FirestoreService.instance.updateField(FirestoreService.Collection.POST, post.postId, "userRequests", updatedUserRequests)

        // TODO implement listener on Post for author, when data on userRequests is changed, notify user
    }
}