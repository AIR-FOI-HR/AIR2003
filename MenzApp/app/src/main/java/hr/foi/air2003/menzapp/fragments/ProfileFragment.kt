package hr.foi.air2003.menzapp.fragments

import android.content.Intent
import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.transition.Explode
import androidx.transition.TransitionManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import hr.foi.air2003.menzapp.R
import hr.foi.air2003.menzapp.database.FirestoreService
import hr.foi.air2003.menzapp.database.model.User
import hr.foi.air2003.menzapp.login.LoginActivity
import kotlinx.android.synthetic.main.fragment_profile.*

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
            FirestoreService.instance.getDocumentByID("Users", userId.toString())
                .addOnSuccessListener { document ->
                    val json = Gson().toJson(document.data)
                    val user = Gson().fromJson(json, User::class.java)

                    tvFullName.text = user.fullName
                    tvAboutMe.text = user.bio

                    // TODO Show user posts, feedbacks and profile photo
                }
                .addOnFailureListener { e -> Log.w(ContentValues.TAG, "Error retrieving document", e) }
        }
    }
}