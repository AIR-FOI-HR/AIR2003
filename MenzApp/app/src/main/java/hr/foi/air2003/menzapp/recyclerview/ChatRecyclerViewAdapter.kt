package hr.foi.air2003.menzapp.recyclerview

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import coil.api.load
import coil.size.Scale
import hr.foi.air2003.menzapp.R
import hr.foi.air2003.menzapp.assistants.DateTimePicker
import hr.foi.air2003.menzapp.assistants.SharedViewModel
import hr.foi.air2003.menzapp.core.model.Chat
import hr.foi.air2003.menzapp.core.services.UserService
import hr.foi.air2003.menzapp.ui.ChatFragment
import kotlinx.android.synthetic.main.chat_message_list_item.view.*

class ChatRecyclerViewAdapter(private val fragment: ChatFragment) : GenericRecyclerViewAdaper<Chat>() {
    private val viewModel = SharedViewModel()
    private val dateTimePicker = DateTimePicker()
    private var currentUserId: String? = null
    var chatClick: ((Chat) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenericViewHolder<Chat> {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.chat_message_list_item, parent, false)
        currentUserId = UserService.getCurrentUser()
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
                        if (item.participantsId.contains(user.userId) && user.userId != currentUserId) {
                            chatName += "${user.fullName}, "
                            imgUri = user.profilePicture
                        }
                    }

                    if(item.participantsId.size > 2){
                        imgUri = "https://firebasestorage.googleapis.com/v0/b/menzapp-fa21a.appspot.com/o/photos%2Fdefault.png?alt=media&token=6e187c1f-2705-45ae-91dd-e40cfb3cab38"
                    }

                    try {
                        itemView.tvChatName.text = chatName.substring(0, chatName.length - 2)
                    }catch (e: Exception){
                        Log.w("Chat Fragment", e)
                        return@observeForever
                    }

                    viewModel.getImage(imgUri)
                            .addOnSuccessListener { uri ->
                                itemView.ivChatUserImage.load(uri) {
                                    scale(Scale.FILL)
                                }
                            }
                }

               return@observeForever
            }

            messageLiveData.observeForever{
                val message = it.data
                if (message != null) {
                    if (message.authorId == currentUserId)
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