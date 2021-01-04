package hr.foi.air2003.menzapp.core.services

import android.content.BroadcastReceiver
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.getField
import com.google.gson.Gson
import hr.foi.air2003.menzapp.core.R
import hr.foi.air2003.menzapp.core.model.Notification
import hr.foi.air2003.menzapp.core.model.User
import hr.foi.air2003.menzapp.core.other.Collection
import hr.foi.air2003.menzapp.core.other.Operation

class NotificationReceiver: BroadcastReceiver(){
    private var context: Context? = null
    private lateinit var channel : NotificationChannel
    private var isChannelCreated = false
    private val EVENT_CHANNEL_ID = "EVENT_CHANNEL_ID"
    private var currentUser: FirebaseUser? = null

    override fun onReceive(context: Context?, intent: Intent?) {
        Thread{ notifyUser(context) }
            .start()
    }

    private fun notifyUser(context: Context?){
        getNotificationDetails().addSnapshotListener { snapshot, error ->
            val documents = snapshot?.documents
            val notifications: MutableList<Notification> = mutableListOf()

            if(documents != null){
                for(doc in documents){
                    if (!doc.getBoolean("seen")!!){
                        val json = Gson().toJson(doc.data)
                        val notification = Gson().fromJson(json, Notification::class.java)
                        notification.notificationId = doc.id
                        notifications.add(notification)
                    }
                }
            }
            createNotification(notifications, context)
        }
    }

    private fun createNotification(notifications: MutableList<Notification>, context: Context?) {
        if (!isChannelCreated) {
            createChannel(context)
        }

        for ((i, n) in notifications.withIndex()){
            val fullName: String = getUserFullName(n.authorId)

            val mBuilder = NotificationCompat.Builder(context!!, EVENT_CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_logo_vector)
                    .setContentTitle(fullName)
                    .setContentText(n.content)
            val notification = mBuilder.build()
            val notificationManagerCompat = NotificationManagerCompat.from(context!!)
            notificationManagerCompat.notify(i, notification)
            FirestoreService.updateField(Collection.NOTIFICATION, n.notificationId, "seen", true)
        }
    }

    private fun createChannel(context: Context?) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            channel = NotificationChannel(EVENT_CHANNEL_ID, "MenzaApp", NotificationManager.IMPORTANCE_DEFAULT)
            channel.description = "A channel for MenzApp notification"
            channel.lightColor = Color.GREEN
            val notificationManager =
                ContextCompat.getSystemService(context!!, NotificationManager::class.java)
            notificationManager!!.createNotificationChannel(channel)
            isChannelCreated = true
        }
    }

    private fun getNotificationDetails(): Query {
        currentUser = FirebaseAuth.getInstance().currentUser
        val users = listOf(currentUser!!.uid)
        return FirestoreService.getAllWithQuery(Collection.NOTIFICATION, Operation.ARRAY_CONTAINS_ANY, "recipientsId", users)
    }

    private fun getUserFullName(authorId: String): String {
        var fullName = "Default"

        FirestoreService.getDocumentByID(Collection.USER, authorId).addSnapshotListener { snapshot, error ->

        if (snapshot != null && snapshot.exists()) {
            val model = User(
                    userId = snapshot.id,
                    fullName = snapshot.getString("fullName")!!,
                    email = snapshot.getString("email")!!,
                    bio = snapshot.getString("bio")!!,
                    profilePicture = snapshot.getString("profilePicture")!!,
                    notificationsOn = snapshot.getBoolean("notificationsOn")!!,
                    subscribersCount = snapshot.getField<Long>("subscribersCount")!!.toInt(),
                    subscribedTo = snapshot.get("subscribedTo")!! as List<String>
                    )
            fullName = model.fullName
            }
        }
        return fullName
    }

}