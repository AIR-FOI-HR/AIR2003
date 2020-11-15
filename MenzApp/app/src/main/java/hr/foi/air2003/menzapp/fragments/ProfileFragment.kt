package hr.foi.air2003.menzapp.fragments

import android.content.ContentValues
import android.content.Intent
import android.icu.text.DateIntervalFormat
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import androidx.fragment.app.Fragment
import androidx.transition.Explode
import androidx.transition.TransitionManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.google.type.DateTime
import hr.foi.air2003.menzapp.R
import hr.foi.air2003.menzapp.database.FirestoreService
import hr.foi.air2003.menzapp.database.model.Feedback
import hr.foi.air2003.menzapp.database.model.User
import hr.foi.air2003.menzapp.login.LoginActivity
import kotlinx.android.synthetic.main.feedback.*
import kotlinx.android.synthetic.main.feedback.view.*
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.post.view.*
import java.sql.Time
import java.text.DateFormat
import java.time.format.DateTimeFormatter
import java.util.*


class ProfileFragment : Fragment() {
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onStart() {
        super.onStart()

        val userId = Firebase.auth.currentUser?.uid
        print(userId)
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
           val intent = Intent (activity, LoginActivity::class.java)
           activity?.startActivity(intent)
       }

    }

    private fun retrieveUserData(userId: String?) {
        if (!userId.isNullOrEmpty()) {

            //Populate user info with data from firestore
            FirestoreService.instance.getDocumentByID("Users", userId.toString())
                .addOnSuccessListener { document ->
                    val json = Gson().toJson(document.data)
                    val user = Gson().fromJson(json, User::class.java)

                    tvFullName.text = user.fullName
                    tvAboutMe.text = user.bio
                }
                .addOnFailureListener { e -> Log.w(ContentValues.TAG, "Error retrieving document", e) }

            //Populate posts with data from firestore
            FirestoreService.instance.getAllWithQuery("Posts", FirestoreService.Operation.EQUAL_TO,"authorId", userId.toString())
                    .addOnSuccessListener { documents ->
                        for (document in documents){
                            //val json = Gson().toJson(document.data)
                            //val post = Gson().fromJson(json, Post::class.java)

                            val dynamicalViewPosts: View = LayoutInflater.from(context).inflate(R.layout.post, null)
                            expandablePosts.addView(dynamicalViewPosts)

                            val currentDate: String = DateFormat.getDateInstance().format(document.getTimestamp("timestamp")?.toDate())
                            dynamicalViewPosts.tvDateTime.text = currentDate
                            dynamicalViewPosts.tvNumberOfPeople.text = "Optimalan broj ljudi: ${document.get("numberOfPeople").toString()}"
                            dynamicalViewPosts.tvDescription.text = document.getString("description")
                        }
                    }
                    .addOnFailureListener { e -> Log.w(ContentValues.TAG, "Error retrieving document", e) }

            //Populate feedbacks with data from firestore
            FirestoreService.instance.getAllWithQuery("Feedbacks", FirestoreService.Operation.EQUAL_TO,"recipientId", userId.toString())
                    .addOnSuccessListener { documents ->
                        for (document in documents){
                            val json = Gson().toJson(document.data)
                            val feedback = Gson().fromJson(json, Feedback::class.java)

                            val dynamicalViewFeedbacks: View = LayoutInflater.from(context).inflate(R.layout.feedback, null)
                            expandableFeedbacks.addView(dynamicalViewFeedbacks)

                            FirestoreService.instance.getDocumentByID("Users", feedback.authorId)
                                    .addOnSuccessListener { document ->
                                            val json = Gson().toJson(document.data)
                                            val user = Gson().fromJson(json, User::class.java)
                                        dynamicalViewFeedbacks.userName.text = user.fullName
                                    }
                                    .addOnFailureListener { e -> Log.w(ContentValues.TAG, "Error retrieving document", e) }

                            when (feedback.mark) {
                                4 -> dynamicalViewFeedbacks.ivStar5.drawable.setTint(resources.getColor(R.color.grey_light))
                                3 -> {
                                    dynamicalViewFeedbacks.ivStar5.drawable.setTint(resources.getColor(R.color.grey_light))
                                    dynamicalViewFeedbacks.ivStar4.drawable.setTint(resources.getColor(R.color.grey_light))
                                }
                                2 -> {
                                    dynamicalViewFeedbacks.ivStar5.drawable.setTint(resources.getColor(R.color.grey_light))
                                    dynamicalViewFeedbacks.ivStar4.drawable.setTint(resources.getColor(R.color.grey_light))
                                    dynamicalViewFeedbacks.ivStar3.drawable.setTint(resources.getColor(R.color.grey_light))
                                }
                                1 -> {
                                    dynamicalViewFeedbacks.ivStar5.drawable.setTint(resources.getColor(R.color.grey_light))
                                    dynamicalViewFeedbacks.ivStar4.drawable.setTint(resources.getColor(R.color.grey_light))
                                    dynamicalViewFeedbacks.ivStar3.drawable.setTint(resources.getColor(R.color.grey_light))
                                    dynamicalViewFeedbacks.ivStar2.drawable.setTint(resources.getColor(R.color.grey_light))
                                }
                                else -> {
                                    print("Mark is not between 1 and 5")
                                }
                            }
                            dynamicalViewFeedbacks.tvFeedbackDescription.text = feedback.feedback
                        }
                    }
                    .addOnFailureListener { e -> Log.w(ContentValues.TAG, "Error retrieving document", e) }
        }
    }
}