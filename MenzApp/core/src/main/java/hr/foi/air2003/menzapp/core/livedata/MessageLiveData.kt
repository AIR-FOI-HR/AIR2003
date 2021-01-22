package hr.foi.air2003.menzapp.core.livedata

import android.util.Log
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestoreException
import hr.foi.air2003.menzapp.core.model.Message
import hr.foi.air2003.menzapp.core.other.DataOrException

typealias MessageOrException = DataOrException<Message, FirebaseFirestoreException>

class MessageLiveData(documentReference: DocumentReference) : FirestoreLiveData<MessageOrException>(documentReference) {
    override fun onEvent(snapshot: DocumentSnapshot?, error: FirebaseFirestoreException?) {
        if (snapshot != null && snapshot.exists()) {
            val message = Message(
                    messageId = snapshot.id,
                    authorId = snapshot.getString("authorId")!!,
                    chatId = snapshot.getString("chatId")!!,
                    sentTimestamp = snapshot.getTimestamp("sentTimestamp")!!,
                    seen = snapshot.getBoolean("seen")!!,
                    content = snapshot.getString("content")!!
            )

            value = MessageOrException(message, error)
        } else if (error != null) {
            Log.d("MessageLiveData", error.message!!)
        }
    }
}