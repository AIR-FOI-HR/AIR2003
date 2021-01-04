package hr.foi.air2003.menzapp.ui

import android.app.NotificationChannel
import android.app.NotificationManager
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import hr.foi.air2003.menzapp.R
import kotlinx.android.synthetic.main.fragment_search.*

class SearchFragment : Fragment() {

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnProva.setOnClickListener {
            notifyUser()
        }

    }

    private var isChannelCreated = false
    private lateinit var channel : NotificationChannel
    private val EVENT_CHANNEL_ID = "EVENT_CHANNEL_ID"

    private fun notifyUser() {

        if (!isChannelCreated) {
            createChannel()
        }
        val mBuilder = NotificationCompat.Builder(context!!, EVENT_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_logo_vector)
            .setContentTitle("Iva Papac")
            .setContentText("Iva papac je najbolja cura!")
        val notification = mBuilder.build()
        val notificationManagerCompat = NotificationManagerCompat.from(context!!)
        notificationManagerCompat.notify(1, notification)

    }

    private fun createChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            channel = NotificationChannel(EVENT_CHANNEL_ID, "Plant Events", NotificationManager.IMPORTANCE_DEFAULT)
            channel.description = "A channel for plant events"
            channel.lightColor = Color.GREEN
            val notificationManager =
                ContextCompat.getSystemService(context!!, NotificationManager::class.java)
            notificationManager!!.createNotificationChannel(channel)
            isChannelCreated = true
        }
    }
}