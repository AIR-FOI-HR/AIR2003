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
import hr.foi.air2003.menzapp.core.model.User
import hr.foi.air2003.menzapp.recyclerview.ChatMessagesRecyclerViewAdapter
import hr.foi.air2003.menzapp.recyclerview.HomePostRecyclerViewAdapter
import kotlinx.android.synthetic.main.fragment_home.*

class ChatFragment : Fragment() {
    private lateinit var adapterChat: ChatMessagesRecyclerViewAdapter
    private lateinit var user: User
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
        // TODO Show list of chats
        viewModel.getChat(user.userId).observe(viewLifecycleOwner, {

        })
    }

    private fun createRecyclerView() {
        adapterChat = ChatMessagesRecyclerViewAdapter()

        rvPostsLayout.hasFixedSize()
        rvPostsLayout.layoutManager = LinearLayoutManager(context)
        rvPostsLayout.itemAnimator = DefaultItemAnimator()
        rvPostsLayout.adapter = adapterChat
    }
}