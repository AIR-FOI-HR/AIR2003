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

    override fun onEvent(value: DocumentSnapshot?, error: FirebaseFirestoreException?) {
        if(value != null && value.exists()){
            val model = Feedback(
                value.id,
                value.getString("authorId")!!,
                value.getString("recipientId")!!,
                value.getField<Int>("mark")!!,
                value.getString("feedback")!!
            )

            setValue(FeedbackOrException(model, error))
        }
        else if(error != null){
            // TODO Handle error
        }
    }
}