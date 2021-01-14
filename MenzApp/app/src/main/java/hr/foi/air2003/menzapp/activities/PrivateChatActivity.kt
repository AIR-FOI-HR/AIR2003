package hr.foi.air2003.menzapp.activities

import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import coil.api.load
import com.google.firebase.Timestamp
import com.google.gson.Gson
import hr.foi.air2003.menzapp.R
import hr.foi.air2003.menzapp.assistants.SharedViewModel
import hr.foi.air2003.menzapp.core.model.Chat
import hr.foi.air2003.menzapp.core.model.Message
import hr.foi.air2003.menzapp.core.model.User
import hr.foi.air2003.menzapp.recyclerview.MessagesRecyclerViewAdapter
import kotlinx.android.synthetic.main.activity_private_chat.*

class PrivateChatActivity : FragmentActivity() {
    private val viewModel = SharedViewModel()
    lateinit var user: User
    private lateinit var chat: Chat
    private lateinit var adapterMessages: MessagesRecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_private_chat)

        svScrollMessages.postDelayed({
            svScrollMessages.smoothScrollTo(0, svScrollMessages.bottom)
        }, 1000)

        user = Gson().fromJson(intent.getStringExtra("user"), User::class.java)
        chat = Gson().fromJson(intent.getStringExtra("chat"), Chat::class.java)
    }

    override fun onStart() {
        super.onStart()
        createRecyclerView()
        getChatInfo()
        getAllMessages()

        btnSendMessage.setOnClickListener {
            if(tvTextMessage.text.isNotEmpty()) {
                sendMessage()
                tvTextMessage.text.clear()
            }
        }

        btnBackChat.setOnClickListener {
            super.onBackPressed()
        }
    }

    private fun getChatInfo() {
        if(chat.participantsId.size == 2){
            for(userId in chat.participantsId){
                if(userId != user.userId){
                    val livedata = viewModel.getUser(userId)
                    livedata.observe(this, {
                        val data = it.data
                        if(data != null){
                            tvPrivateChatName.text = data.fullName
                            viewModel.getImage(data.profilePicture)
                                    .addOnSuccessListener { url ->
                                        ivPrivateChatImage.load(url)
                                    }
                        }
                    })
                }
            }
        }else{
            ivPrivateChatImage.visibility = View.GONE
            var chatName = ""
            val livedata = viewModel.getAllUsers()
            livedata.observe(this, {
                val data = it.data
                if (data != null) {
                    for (d in data) {
                        if (chat.participantsId.contains(d.userId)) {
                            chatName += "${d.fullName}, "
                        }
                    }

                    tvPrivateChatName.text = chatName.substring(chatName.length - 2)
                }
            })
        }
    }

    private fun createRecyclerView() {
        adapterMessages = MessagesRecyclerViewAdapter(this)

        rvAllMessages.hasFixedSize()
        rvAllMessages.layoutManager = LinearLayoutManager(applicationContext)
        rvAllMessages.itemAnimator = DefaultItemAnimator()
        rvAllMessages.adapter = adapterMessages
    }

    private fun getAllMessages() {
        val livedata = viewModel.getAllMessages(chat.chatId)
        livedata.observe(this, {
            val data = it.data
            if (data != null) {
                val sorted = data.sortedBy { message -> message.sentTimestamp }
                adapterMessages.addItems(sorted)
            }
        })
    }

    private fun sendMessage() {
        val message = Message(
                authorId = user.userId,
                chatId = chat.chatId,
                sentTimestamp = Timestamp.now(),
                content = tvTextMessage.text.toString()
        )

        chat.lastMessage = message.messageId
        //chat.timestamp = message.sentTimestamp

        viewModel.sendMessage(message)
        viewModel.updateChat(chat)
    }
}