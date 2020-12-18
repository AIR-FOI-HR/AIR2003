package hr.foi.air2003.menzapp.recyclerview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import hr.foi.air2003.menzapp.R
import hr.foi.air2003.menzapp.assistants.DateTimePicker
import hr.foi.air2003.menzapp.assistants.ImageConverter
import hr.foi.air2003.menzapp.assistants.SharedViewModel
import hr.foi.air2003.menzapp.core.model.Chat
import hr.foi.air2003.menzapp.ui.ChatFragment
import kotlinx.android.synthetic.main.chat_message_list_item.view.*

class ChatMessagesRecyclerViewAdapter(private val fragment: ChatFragment) : GenericRecyclerViewAdaper<Chat>() {
    private val viewModel = SharedViewModel()
    private val dateTimePicker = DateTimePicker()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenericViewHolder<Chat> {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.chat_message_list_item, parent, false)
        return ChatViewHolder(view)
    }

    inner class ChatViewHolder(itemView: View) : GenericViewHolder<Chat>(itemView){
        override fun onBind(item: Chat) {
            itemView.tvChatUsername.text = item.chatName

            val userLiveData = viewModel.getUser(item.participantsId[1])
            userLiveData.observe(fragment.viewLifecycleOwner, {
                val user = it.data
                if (user != null) {
                    val imgUri = user.profilePicture

                    viewModel.getImage(imgUri)
                        .addOnSuccessListener { bytes ->
                            val bitmap = ImageConverter.convertBytesToBitmap(bytes)
                            val resized = ImageConverter.resizeBitmap(bitmap, itemView.ivChatUserImage)
                            itemView.ivChatUserImage.setImageBitmap(resized)
                        }
                }
            })

            val messageLiveData = viewModel.getMessage(item.lastMessage)
            messageLiveData.observe(fragment.viewLifecycleOwner, {
                val message = it.data
                if(message != null){
                    val timestamp = dateTimePicker.timestampToShortString(message.sentTimestamp)

                    itemView.tvChatTimestamp.text = timestamp
                    itemView.tvChatMessage.text = message.content
                }
            })
        }
    }
}