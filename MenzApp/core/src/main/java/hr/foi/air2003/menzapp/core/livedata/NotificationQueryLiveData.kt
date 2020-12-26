package hr.foi.air2003.menzapp.core.livedata

import com.google.firebase.firestore.*
import com.google.gson.Gson
import hr.foi.air2003.menzapp.core.model.Notification

typealias NotificationQueryResult = QueryResultOrException<Notification, FirebaseFirestoreException>

class NotificationQueryLiveData(query: Query) : FirestoreQueryLiveData<NotificationQueryResult>(query) {
    override fun onEvent(snapshot: QuerySnapshot?, error: FirebaseFirestoreException?) {
        val documents = snapshot?.documents
        val notifications: MutableList<Notification> = mutableListOf()

        if(documents != null){
            for(doc in documents){
                val json = Gson().toJson(doc.data)
                val notification = Gson().fromJson(json, Notification::class.java)
                notification.notificationId = doc.id
                notifications.add(notification)
            }
        }

        postValue(NotificationQueryResult(notifications, error))
    }
}