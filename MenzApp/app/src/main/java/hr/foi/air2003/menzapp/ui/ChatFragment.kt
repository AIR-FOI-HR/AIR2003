package hr.foi.air2003.menzapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import hr.foi.air2003.menzapp.R
import hr.foi.air2003.menzapp.activities.MainActivity
import hr.foi.air2003.menzapp.assistants.SharedViewModel
import hr.foi.air2003.menzapp.core.model.Chat
import hr.foi.air2003.menzapp.core.model.User
import hr.foi.air2003.menzapp.recyclerview.ChatMessagesRecyclerViewAdapter
import kotlinx.android.synthetic.main.fragment_chat.*

class ChatFragment : Fragment() {
    private lateinit var adapterChat: ChatMessagesRecyclerViewAdapter
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
            val chats: MutableList<Chat> = mutableListOf()
            val data = it.data
            if (data != null) {
                for (d in data)
                    chats.add(d)

                adapterChat.addItems(chats)
            }
        })
    }

    private fun createRecyclerView() {
        adapterChat = ChatMessagesRecyclerViewAdapter(this)

        rvAllChats.hasFixedSize()
        rvAllChats.layoutManager = LinearLayoutManager(context)
        rvAllChats.itemAnimator = DefaultItemAnimator()
        rvAllChats.adapter = adapterChat
    }
}