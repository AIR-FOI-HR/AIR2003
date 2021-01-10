package hr.foi.air2003.menzapp.core.livedata

import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.gson.Gson
import hr.foi.air2003.menzapp.core.model.Message
import hr.foi.air2003.menzapp.core.other.DataOrException

typealias MessageOrException = DataOrException<Message, FirebaseFirestoreException>

class MessageLiveData(documentReference: DocumentReference) :
    FirestoreLiveData<MessageOrException>(documentReference) {
    override fun onEvent(snapshot: DocumentSnapshot?, error: FirebaseFirestoreException?) {
        if (snapshot != null && snapshot.exists()) {
            val json = Gson().toJson(snapshot)
            val message = Gson().fromJson(json, Message::class.java)
            message.messageId = snapshot.id

            value = MessageOrException(message, error)
        } else if (error != null) {
            // TODO Handle error
        }
    }
}