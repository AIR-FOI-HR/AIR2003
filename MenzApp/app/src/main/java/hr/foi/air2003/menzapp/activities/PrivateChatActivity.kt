package hr.foi.air2003.menzapp.activities

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.auth.User
import com.google.gson.Gson
import hr.foi.air2003.menzapp.R
import hr.foi.air2003.menzapp.assistants.SharedViewModel
import hr.foi.air2003.menzapp.core.model.Chat
import hr.foi.air2003.menzapp.recyclerview.MessagesRecyclerViewAdapter
import kotlinx.android.synthetic.main.activity_private_chat.*

class PrivateChatActivity : FragmentActivity() {
    private val viewModel = SharedViewModel()
    private lateinit var user: User
    private lateinit var chat: Chat
    private lateinit var adapterMessages: MessagesRecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_private_chat)

        user = Gson().fromJson(intent.getStringExtra("user"), User::class.java)
        chat = Gson().fromJson(intent.getStringExtra("chat"), Chat::class.java)
    }

    override fun onStart() {
        super.onStart()
        createRecyclerView()
        getAllMessages()
    }

    private fun createRecyclerView() {
        adapterMessages = MessagesRecyclerViewAdapter()

        rvAllMessages.hasFixedSize()
        rvAllMessages.layoutManager = LinearLayoutManager(applicationContext)
        rvAllMessages.itemAnimator = DefaultItemAnimator()
        rvAllMessages.adapter = adapterMessages
    }

    private fun getAllMessages() {
        val livedata = viewModel.getAllMessages(chat.chatId)
        livedata.observe(this, {
            val data = it.data
            if(data != null)
                adapterMessages.addItems(data)
        })
    }
}