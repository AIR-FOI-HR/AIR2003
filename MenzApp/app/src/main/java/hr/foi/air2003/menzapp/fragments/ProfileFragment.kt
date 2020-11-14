package hr.foi.air2003.menzapp.fragments

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.transition.Explode
import androidx.transition.TransitionManager
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import hr.foi.air2003.menzapp.R
import hr.foi.air2003.menzapp.database.FirestoreService
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

        //Get user data from firestore
        try {
            val userID = Firebase.auth.currentUser?.uid
            val snapshot = FirestoreService.instance.getDocumentByID("Users", userID.toString())
            snapshot
                    .addOnSuccessListener {
                        Log.d(ContentValues.TAG, "Document successfully loaded!")
                        tvFullName.text = it.getString("fullName")
                        tvSubscribers.text = "${it.get("subscribersCount")} pretplatnika"
                        tvAboutMe.text = it.getString("bio")
                    }
                    .addOnFailureListener {
                        e -> Log.w(ContentValues.TAG, "Error loading data: ", e)
                    }
        } catch(e : Exception) {
            Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
        }

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
    }
}