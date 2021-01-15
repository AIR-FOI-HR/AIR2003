package hr.foi.air2003.menzapp.recyclerview

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import coil.api.load
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
            val messageLiveData = viewModel.getMessage(item.lastMessage)
            var chatName = ""
            var imgUri = ""

            val timestamp = dateTimePicker.timestampToShortString(item.timestamp)
            itemView.tvChatTimestamp.text = timestamp

            usersLiveData.observeForever{
                val data = it.data
                if (data != null) {
                    for (user in data) {
                        if (item.participantsId.contains(user.userId) && user.userId != fragment.user.userId) {
                            chatName += "${user.fullName}, "
                            imgUri = user.profilePicture
                        }
                    }

                    itemView.tvChatName.text = chatName.substring(0, chatName.length - 2)
                    viewModel.getImage(imgUri)
                            .addOnSuccessListener { uri ->
                                itemView.ivChatUserImage.load(uri)
                            }
                }

                return@observeForever
            }

            messageLiveData.observeForever{
                val message = it.data
                if (message != null) {
                    if (message.authorId == fragment.user.userId)
                        itemView.tvChatMessage.text = "Vi: ${message.content}"
                    else {
                        itemView.tvChatMessage.text = message.content

                        if(!message.seen)
                            itemView.tvChatMessage.typeface = ResourcesCompat.getFont(fragment.requireContext(), R.font.raleway_semibold)
                    }
                }

                return@observeForever
            }
        }
    }
}