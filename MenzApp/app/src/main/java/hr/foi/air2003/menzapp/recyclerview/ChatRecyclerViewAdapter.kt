package hr.foi.air2003.menzapp.recyclerview

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import hr.foi.air2003.menzapp.R
import hr.foi.air2003.menzapp.assistants.DateTimePicker
import hr.foi.air2003.menzapp.assistants.SharedViewModel
import hr.foi.air2003.menzapp.core.model.Chat
import hr.foi.air2003.menzapp.ui.ChatFragment
import kotlinx.android.synthetic.main.chat_message_list_item.view.*

class ChatRecyclerViewAdapter(private val fragment: ChatFragment) : GenericRecyclerViewAdaper<Chat>() {
    private val viewModel = SharedViewModel()
    private val dateTimePicker = DateTimePicker()
    var chatClick: ((Chat) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenericViewHolder<Chat> {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.chat_message_list_item, parent, false)
        return ChatViewHolder(view)
    }

    inner class ChatViewHolder(itemView: View) : GenericViewHolder<Chat>(itemView){

        init {
            itemView.chatLayout.setOnClickListener {
                chatClick?.invoke(items[adapterPosition])
            }
        }

        @SuppressLint("SetTextI18n")
        override fun onBind(item: Chat) {
            val usersLiveData = viewModel.getAllUsers()
            var chatName = ""
            usersLiveData.observe(fragment.viewLifecycleOwner, {
                val data = it.data
                if (data != null) {
                    for (user in data) {
                        if (item.participantsId.contains(user.userId)) {
                            chatName += "${user.fullName}, "
                        }
                    }

                    itemView.tvChatName.text = chatName
                }
            })

            val messageLiveData = viewModel.getMessage(item.lastMessage)
            messageLiveData.observe(fragment.viewLifecycleOwner, {
                val message = it.data
                if (message != null) {
                    val timestamp = dateTimePicker.timestampToShortString(message.sentTimestamp)
                    itemView.tvChatTimestamp.text = timestamp

                    if (message.authorId == fragment.user.userId)
                        itemView.tvChatMessage.text = "Vi: ${message.content}"
                    else
                        itemView.tvChatMessage.text = message.content
                }
            })
        }
    }
}