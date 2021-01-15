package hr.foi.air2003.menzapp.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import hr.foi.air2003.menzapp.R
import hr.foi.air2003.menzapp.activities.MainActivity
import hr.foi.air2003.menzapp.activities.PrivateChatActivity
import hr.foi.air2003.menzapp.assistants.SharedViewModel
import hr.foi.air2003.menzapp.core.model.Chat
import hr.foi.air2003.menzapp.core.model.User
import hr.foi.air2003.menzapp.recyclerview.ChatRecyclerViewAdapter
import kotlinx.android.synthetic.main.fragment_chat.*

class ChatFragment : Fragment() {
    private lateinit var adapterChat: ChatRecyclerViewAdapter
    lateinit var user: User
    private val viewModel = SharedViewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        user = (activity as MainActivity).getCurrentUser()
        return inflater.inflate(R.layout.fragment_chat, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        createRecyclerView()
        getChat()
    }

    private fun getChat() {
        val liveData = viewModel.getChat(user.userId)
        liveData.observe(viewLifecycleOwner, {
            val data = it.data
            if (data != null) {
                val chats = data.sortedBy { chat -> chat.timestamp }
                adapterChat.addItems(chats)
            }
        })
    }

    private fun createRecyclerView() {
        adapterChat = ChatRecyclerViewAdapter(this)

        rvAllChats.hasFixedSize()
        rvAllChats.layoutManager = LinearLayoutManager(context)
        rvAllChats.itemAnimator = DefaultItemAnimator()
        rvAllChats.adapter = adapterChat

        adapterChat.chatClick = { chat ->
            val intent = Intent(context, PrivateChatActivity::class.java)
            val jsonChat = Gson().toJson(chat)
            val jsonUser = Gson().toJson(user)
            intent.putExtra("chat", jsonChat)
            intent.putExtra("user", jsonUser)
            startActivity(intent)
        }

    }
}