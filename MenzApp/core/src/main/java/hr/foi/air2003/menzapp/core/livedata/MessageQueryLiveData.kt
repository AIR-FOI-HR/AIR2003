package hr.foi.air2003.menzapp.core.livedata

import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.gson.Gson
import hr.foi.air2003.menzapp.core.model.Message

typealias MessageQueryResult = QueryResultOrException<Message, FirebaseFirestoreException>

class MessageQueryLiveData(query: Query) : FirestoreQueryLiveData<MessageQueryResult>(query) {

    override fun onEvent(snapshot: QuerySnapshot?, error: FirebaseFirestoreException?) {
        val documents = snapshot?.documents
        val messages: MutableList<Message> = mutableListOf()

        if (documents != null) {
            for (doc in documents) {
                val json = Gson().toJson(doc.data)
                val message = Gson().fromJson(json, Message::class.java)
                message.messageId = doc.id
                messages.add(message)
            }
        }

        postValue(MessageQueryResult(messages, error))
    }
}