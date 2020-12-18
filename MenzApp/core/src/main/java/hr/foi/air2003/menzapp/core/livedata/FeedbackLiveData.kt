package hr.foi.air2003.menzapp.core.livedata

import com.google.firebase.firestore.*
import com.google.firebase.firestore.ktx.getField
import hr.foi.air2003.menzapp.core.model.Feedback
import hr.foi.air2003.menzapp.core.other.DataOrException

typealias FeedbackOrException = DataOrException<Feedback, FirebaseFirestoreException>

class FeedbackLiveData(private val documentReference: DocumentReference) : FirestoreLiveData<FeedbackOrException>(documentReference) {

    override fun onEvent(snapshot: DocumentSnapshot?, error: FirebaseFirestoreException?) {
        if(snapshot != null && snapshot.exists()){
            val model = Feedback(
                feedbackId = snapshot.id,
                authorId = snapshot.getString("authorId")!!,
                recipientId = snapshot.getString("recipientId")!!,
                mark = snapshot.getField<Int>("mark")!!,
                feedback = snapshot.getString("feedback")!!
            )

            value = FeedbackOrException(model, error)
        }
        else if(error != null){
            // TODO Handle error
        }
    }
}