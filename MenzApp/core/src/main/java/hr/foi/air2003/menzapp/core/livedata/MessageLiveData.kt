package hr.foi.air2003.menzapp.core.livedata

import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestoreException
import hr.foi.air2003.menzapp.core.model.Message
import hr.foi.air2003.menzapp.core.other.DataOrException

typealias MessageOrException = DataOrException<Message, FirebaseFirestoreException>

class MessageLiveData(private val documentReference: DocumentReference) : FirestoreLiveData<MessageOrException>(documentReference) {
    override fun onEvent(snapshot: DocumentSnapshot?, error: FirebaseFirestoreException?) {
        if(snapshot != null && snapshot.exists()){
            val model = Message(
                messageId = snapshot.id,
                authorId = snapshot.getString("authorId")!!,
                chatId = snapshot.getString("chatId")!!,
                sentTimestamp = snapshot.getTimestamp("sentTimestamp")!!,
                seenTimestamp = snapshot.getTimestamp("seenTimestamp")!!,
                content = snapshot.getString("content")!!
            )

            value = MessageOrException(model, error)
        }
        else if(error != null){
            // TODO Handle error
        }
    }
}