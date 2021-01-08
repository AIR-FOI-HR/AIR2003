package hr.foi.air2003.menzapp.core.livedata

import com.google.firebase.firestore.*
import com.google.gson.Gson
import hr.foi.air2003.menzapp.core.model.Feedback
import hr.foi.air2003.menzapp.core.other.DataOrException

typealias FeedbackOrException = DataOrException<Feedback, FirebaseFirestoreException>

class FeedbackLiveData(documentReference: DocumentReference) :
    FirestoreLiveData<FeedbackOrException>(documentReference) {

    override fun onEvent(snapshot: DocumentSnapshot?, error: FirebaseFirestoreException?) {
        if (snapshot != null && snapshot.exists()) {
            val json = Gson().toJson(snapshot)
            val feedback = Gson().fromJson(json, Feedback::class.java)
            feedback.feedbackId = snapshot.id

            value = FeedbackOrException(feedback, error)
        } else if (error != null) {
            // TODO Handle error
        }
    }
}