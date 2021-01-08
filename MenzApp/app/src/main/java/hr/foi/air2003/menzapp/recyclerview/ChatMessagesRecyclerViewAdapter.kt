package hr.foi.air2003.menzapp.recyclerview

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import coil.api.load
import coil.size.Scale
import hr.foi.air2003.menzapp.R
import hr.foi.air2003.menzapp.assistants.DateTimePicker
import hr.foi.air2003.menzapp.assistants.SharedViewModel
import hr.foi.air2003.menzapp.core.model.Chat
import hr.foi.air2003.menzapp.ui.ChatFragment
import kotlinx.android.synthetic.main.chat_message_list_item.view.*

class ChatMessagesRecyclerViewAdapter(private val fragment: ChatFragment) :
    GenericRecyclerViewAdaper<Chat>() {
    private val viewModel = SharedViewModel()
    private val dateTimePicker = DateTimePicker()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenericViewHolder<Chat> {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.chat_message_list_item, parent, false)
        return ChatViewHolder(view)
    }

    inner class ChatViewHolder(itemView: View) : GenericViewHolder<Chat>(itemView) {
        @SuppressLint("SetTextI18n")
        override fun onBind(item: Chat) {
            itemView.tvChatUsername.text = item.chatName

            val userLiveData = viewModel.getUser(item.participantsId[1])
            userLiveData.observe(fragment.viewLifecycleOwner, {
                val user = it.data
                if (user != null) {
                    val imgUri = user.profilePicture

                    viewModel.getImage(imgUri)
                        .addOnSuccessListener { url ->
                            itemView.ivChatUserImage.load(url) {
                                scale(Scale.FIT)
                            }
                        }
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