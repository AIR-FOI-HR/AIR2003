package hr.foi.air2003.menzapp.recyclerview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import hr.foi.air2003.menzapp.R
import hr.foi.air2003.menzapp.core.model.Message
import kotlinx.android.synthetic.main.private_chat_list_item.view.*

class MessagesRecyclerViewAdapter : GenericRecyclerViewAdaper<Message>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenericViewHolder<Message> {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.private_chat_list_item, parent, false)
        return MessageViewHolder(view)
    }

    inner class MessageViewHolder(itemView: View) : GenericViewHolder<Message>(itemView){
        override fun onBind(item: Message) {
            itemView.tvMessageTimestamp.text = item.sentTimestamp.toString()
            itemView.tvMessageContent.text = item.content
        }
    }
}