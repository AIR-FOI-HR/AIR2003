package hr.foi.air2003.menzapp.core.livedata

import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestoreException
import hr.foi.air2003.menzapp.core.model.Message
import hr.foi.air2003.menzapp.core.other.DataOrException

typealias MessageOrException = DataOrException<Message, FirebaseFirestoreException>

class MessageLiveData(private val documentReference: DocumentReference) : FirestoreLiveData<MessageOrException>(documentReference) {
    override fun onEvent(value: DocumentSnapshot?, error: FirebaseFirestoreException?) {
        TODO("Not yet implemented")
    }
}