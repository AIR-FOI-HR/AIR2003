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

class NotificationReceiver : BroadcastReceiver() {
    private lateinit var channel: NotificationChannel
    private var isChannelCreated = false
    private val EVENT_CHANNEL_ID = "EVENT_CHANNEL_ID"
    private var currentUser: FirebaseUser? = null

    override fun onReceive(context: Context?, intent: Intent?) {
        Thread { notifyUser(context) }
            .start()
    }

    private fun notifyUser(context: Context?) {
        currentUser = FirebaseAuth.getInstance().currentUser
        val users = listOf(currentUser!!.uid)
        if (!isChannelCreated) {
            createChannel(context)
        }

        FirestoreService.getAllWithQuery(Collection.NOTIFICATION, Operation.ARRAY_CONTAINS_ANY, "recipientsId", users)
                .addSnapshotListener { snapshot, error ->
                    val documents = snapshot?.documents

                    if (documents != null) {
                        for ((i, doc) in documents.withIndex()) {
                            if (!doc.getBoolean("seen")!!) {
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

                                            val mBuilder = NotificationCompat.Builder(context!!, EVENT_CHANNEL_ID)
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

        //TODO Implement notifications for messages
    }

    private fun createChannel(context: Context?) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            channel = NotificationChannel(
                EVENT_CHANNEL_ID,
                "MenzaApp",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            channel.description = "A channel for MenzApp notification"
            channel.lightColor = Color.GREEN
            val notificationManager =
                ContextCompat.getSystemService(context!!, NotificationManager::class.java)
            notificationManager!!.createNotificationChannel(channel)
            isChannelCreated = true
        }
    }
}