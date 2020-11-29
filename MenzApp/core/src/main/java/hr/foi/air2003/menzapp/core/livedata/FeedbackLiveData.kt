package hr.foi.air2003.menzapp.core.livedata

import androidx.lifecycle.LiveData
import com.google.firebase.firestore.*
import com.google.firebase.firestore.ktx.getField
import hr.foi.air2003.menzapp.core.model.Feedback
import hr.foi.air2003.menzapp.core.other.DataOrException

typealias FeedbackOrException = DataOrException<Feedback, FirebaseFirestoreException>

class FeedbackLiveData(private val documentReference: DocumentReference) : LiveData<FeedbackOrException>(), EventListener<DocumentSnapshot> {
    private var listenerRegistration: ListenerRegistration? = null

    override fun onActive() {
        super.onActive()
        listenerRegistration = documentReference.addSnapshotListener(this)
    }

    override fun onInactive() {
        super.onInactive()
        listenerRegistration?.remove()
    }

    override fun onEvent(snapshot: DocumentSnapshot?, error: FirebaseFirestoreException?) {
        if(snapshot != null && snapshot.exists()){
            val model = Feedback(
                snapshot.id,
                snapshot.getField<Map<String,String>>("author")!!,
                snapshot.getString("recipientId")!!,
                snapshot.getField<Int>("mark")!!,
                snapshot.getString("feedback")!!
            )

            value = FeedbackOrException(model, error)
        }
        else if(error != null){
            // TODO Handle error
        }
    }
}