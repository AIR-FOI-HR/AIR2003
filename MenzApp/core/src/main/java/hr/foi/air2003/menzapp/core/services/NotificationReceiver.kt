package hr.foi.air2003.menzapp.core.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.ktx.getField
import com.google.gson.Gson
import hr.foi.air2003.menzapp.core.R
import hr.foi.air2003.menzapp.core.model.Chat
import hr.foi.air2003.menzapp.core.model.Message
import hr.foi.air2003.menzapp.core.model.Notification
import hr.foi.air2003.menzapp.core.model.User
import hr.foi.air2003.menzapp.core.other.Collection
import hr.foi.air2003.menzapp.core.other.Operation

class NotificationReceiver : BroadcastReceiver() {
    private lateinit var channelNotif: NotificationChannel
    private lateinit var channelMsg: NotificationChannel
    private var isChannelNotifCreated = false
    private var isChannelMsgCreated = false
    private val NOTIFICATION_CHANNEL_ID = "NOTIFICATION_CHANNEL_ID"
    private val MESSAGE_CHANNEL_ID = "MESSAGE_CHANNEL_ID"
    private lateinit var currentUser: String

    override fun onReceive(context: Context?, intent: Intent?) {
        Thread { notifyUser(context) }
            .start()
    }

    private fun notifyUser(context: Context?) {
        currentUser = UserService.getCurrentUser().toString()
        val users = listOf(currentUser)

        FirestoreService.getAllWithQuery(Collection.NOTIFICATION, Operation.ARRAY_CONTAINS_ANY, "recipientsId", users)
                .addSnapshotListener { snapshot, error ->
                    val documents = snapshot?.documents

                    if (!isChannelNotifCreated) {
                        createNotifChannel(context)
                    }

                    if (documents != null) {
                        for ((i, doc) in documents.withIndex()) {
                            val seen = doc.getBoolean("seen")
                            if (seen != null && !seen) {
                                val json = Gson().toJson(doc.data)
                                val n = Gson().fromJson(json, Notification::class.java)
                                n.notificationId = doc.id

                                FirestoreService.getDocumentByID(Collection.USER, n.authorId)
                                    .addSnapshotListener { snapshot, error ->
                                        if (snapshot != null && snapshot.exists()) {
                                            val model = User(
                                                    userId = snapshot.id,
                                                    fullName = snapshot.getString("fullName")!!,
                                                    email = snapshot.getString("email")!!,
                                                    bio = snapshot.getString("bio")!!,
                                                    profilePicture = snapshot.getString("profilePicture")!!,
                                                    notificationsOn = snapshot.getBoolean("notificationsOn")!!,
                                                    subscribersCount = snapshot.getField<Long>("subscribersCount")!!
                                                            .toInt(),
                                                    subscribedTo = snapshot.get("subscribedTo")!! as List<String>
                                            )

                                            val mBuilder = NotificationCompat.Builder(context!!, NOTIFICATION_CHANNEL_ID)
                                                    .setSmallIcon(R.drawable.ic_logo_vector)
                                                    .setContentTitle(model.fullName)
                                                    .setContentText(n.content)
                                            val notification = mBuilder.build()
                                            val notificationManagerCompat = NotificationManagerCompat.from(context)
                                            notificationManagerCompat.notify(i, notification)
                                            FirestoreService.updateField(Collection.NOTIFICATION, n.notificationId, "seen", true)
                                        }
                                    }
                            }
                        }
                    }
                }

        FirestoreService.getAllWithQuery(Collection.CHAT, Operation.ARRAY_CONTAINS_ANY, "participantsId", users)
                .addSnapshotListener { snapshot, error ->
                    val documents = snapshot?.documents

                    if (!isChannelMsgCreated) {
                        createMsgChannel(context)
                    }

                    if (documents != null) {
                        for ((i, doc) in documents.withIndex()) {
                            val json = Gson().toJson(doc.data)
                            val chat = Gson().fromJson(json, Chat::class.java)
                            chat.chatId = doc.id

                            FirestoreService.getAllWithQuery(Collection.MESSAGE, Operation.EQUAL_TO, "chatId", chat.chatId)
                                    .addSnapshotListener { snapshot, error ->
                                        val documents = snapshot?.documents

                                        if (documents != null) {
                                            for (doc in documents) {
                                                val json = Gson().toJson(doc.data)
                                                val message = Gson().fromJson(json, Message::class.java)
                                                message.messageId = doc.id

                                                if (message.authorId != currentUser && !message.seen){

                                                    FirestoreService.getDocumentByID(Collection.USER, message.authorId)
                                                            .addSnapshotListener { snapshot, error ->

                                                                if (snapshot != null && snapshot.exists()) {

                                                                    val model = User(
                                                                            userId = snapshot.id,
                                                                            fullName = snapshot.getString("fullName")!!,
                                                                            email = snapshot.getString("email")!!,
                                                                            bio = snapshot.getString("bio")!!,
                                                                            profilePicture = snapshot.getString("profilePicture")!!,
                                                                            notificationsOn = snapshot.getBoolean("notificationsOn")!!,
                                                                            subscribersCount = snapshot.getField<Long>("subscribersCount")!!
                                                                                    .toInt(),
                                                                            subscribedTo = snapshot.get("subscribedTo")!! as List<String>
                                                                    )

                                                                    val mBuilder = NotificationCompat.Builder(context!!, MESSAGE_CHANNEL_ID)
                                                                            .setSmallIcon(R.drawable.ic_message)
                                                                            .setContentTitle(model.fullName)
                                                                            .setContentText(message.content)
                                                                    val notification = mBuilder.build()
                                                                    val notificationManagerCompat = NotificationManagerCompat.from(context)
                                                                    notificationManagerCompat.notify(i, notification)
                                                                    FirestoreService.updateField(Collection.MESSAGE, message.messageId, "seen", true)
                                                                }
                                                            }
                                                }

                                            }
                                        }
                                    }
                        }
                    }

                }
    }

    private fun createNotifChannel(context: Context?) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            channelNotif = NotificationChannel(
                    NOTIFICATION_CHANNEL_ID,
                    "MenzaApp",
                    NotificationManager.IMPORTANCE_DEFAULT
            )
            channelNotif.description = "A channel for MenzApp notification"
            channelNotif.enableLights(true)
            channelNotif.lightColor = Color.GREEN
            channelNotif.enableVibration(true)
            channelNotif.shouldVibrate()
            channelNotif.vibrationPattern = longArrayOf(0, 200, 60, 200)
            val notificationManager =
                ContextCompat.getSystemService(context!!, NotificationManager::class.java)
            notificationManager!!.createNotificationChannel(channelNotif)
            isChannelNotifCreated = true
        }
    }

    private fun createMsgChannel(context: Context?) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            channelMsg = NotificationChannel(
                    MESSAGE_CHANNEL_ID,
                    "MenzaApp",
                    NotificationManager.IMPORTANCE_DEFAULT
            )
            channelMsg.description = "A channel for MenzApp messages"
            channelMsg.enableLights(true)
            channelMsg.lightColor = Color.BLUE
            channelMsg.enableVibration(true)
            channelMsg.shouldVibrate()
            channelMsg.vibrationPattern = longArrayOf(0, 200, 60, 200)
            val notificationManager =
                    ContextCompat.getSystemService(context!!, NotificationManager::class.java)
            notificationManager!!.createNotificationChannel(channelMsg)
            isChannelMsgCreated = true
        }
    }
}