package hr.foi.air2003.menzapp.core.livedata

import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.gson.Gson
import hr.foi.air2003.menzapp.core.model.Chat

typealias ChatQueryResult = QueryResultOrException<Chat, FirebaseFirestoreException>

class ChatQueryLiveData(query: Query) : FirestoreQueryLiveData<ChatQueryResult>(query) {

    override fun onEvent(snapshot: QuerySnapshot?, error: FirebaseFirestoreException?) {
        val documents = snapshot?.documents
        val chats: MutableList<Chat> = mutableListOf()

        if (documents != null) {
            for (doc in documents) {
                val json = Gson().toJson(doc.data)
                val chat = Gson().fromJson(json, Chat::class.java)
                chat.chatId = doc.id
                chats.add(chat)
            }
        }

        postValue(ChatQueryResult(chats, error))
    }
}