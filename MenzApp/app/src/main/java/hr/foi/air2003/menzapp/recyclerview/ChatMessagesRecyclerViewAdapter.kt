package hr.foi.air2003.menzapp.recyclerview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import hr.foi.air2003.menzapp.R
import hr.foi.air2003.menzapp.core.model.Chat

class ChatMessagesRecyclerViewAdapter : GenericRecyclerViewAdaper<Chat>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenericViewHolder<Chat> {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.chat_message_list_item, parent, false)
        return ChatViewHolder(view)
    }

    inner class ChatViewHolder(itemView: View) : GenericViewHolder<Chat>(itemView){
        override fun onBind(item: Chat) {
            // TODO Populate recycler view
        }
    }
}