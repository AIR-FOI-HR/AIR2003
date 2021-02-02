package hr.foi.air2003.menzapp.recyclerview

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.transition.Explode
import androidx.transition.TransitionManager
import hr.foi.air2003.menzapp.R
import hr.foi.air2003.menzapp.activities.PrivateChatActivity
import hr.foi.air2003.menzapp.assistants.DateTimePicker
import hr.foi.air2003.menzapp.assistants.SharedViewModel
import hr.foi.air2003.menzapp.core.model.Message
import hr.foi.air2003.menzapp.core.services.UserService
import kotlinx.android.synthetic.main.fragment_menu.*
import kotlinx.android.synthetic.main.private_chat_list_item.*
import kotlinx.android.synthetic.main.private_chat_list_item.view.*

class MessagesRecyclerViewAdapter(private val fragment: PrivateChatActivity) : GenericRecyclerViewAdaper<Message>() {
    private val dateTimePicker = DateTimePicker()
    private val viewModel = SharedViewModel()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenericViewHolder<Message> {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.private_chat_list_item, parent, false)
        return MessageViewHolder(view)
    }

    inner class MessageViewHolder(itemView: View) : GenericViewHolder<Message>(itemView){
        override fun onBind(item: Message) {
            val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            itemView.tvMessageContent.text = item.content
            itemView.tvMessageTimestamp.text = dateTimePicker.getMessageTimestamp(item.sentTimestamp)

            val currentUser = UserService.getCurrentUser()
            if(item.authorId == currentUser){
                params.gravity = Gravity.END
                itemView.llMessageLayout.layoutParams = params
                itemView.tvMessageUsername.visibility = View.GONE
                itemView.llMessageLayout.background = ContextCompat.getDrawable(fragment, R.drawable.message_background_green)
                itemView.tvMessageContent.setTextColor(ContextCompat.getColor(fragment, R.color.white))

            }else{
                val livedata = viewModel.getUser(item.authorId)
                livedata.observeForever {
                    val data = it.data
                    if (data != null) {
                        itemView.tvMessageUsername.text = data.fullName
                    }

                    return@observeForever
                }

                params.gravity = Gravity.START
                itemView.llMessageLayout.layoutParams = params
                itemView.llTimestampLayout.layoutParams = params
                itemView.tvMessageUsername.visibility = View.VISIBLE
                itemView.llMessageLayout.background = ContextCompat.getDrawable(fragment, R.drawable.message_background_grey)
                itemView.tvMessageContent.setTextColor(ContextCompat.getColor(fragment, R.color.black_typo))
            }

            itemView.llMessageLayout.setOnClickListener {
                if(itemView.llTimestampLayout.visibility == View.GONE){
                    TransitionManager.beginDelayedTransition(itemView.llMessageLayout, Explode())
                    itemView.llTimestampLayout.visibility = View.VISIBLE
                }else{
                    TransitionManager.beginDelayedTransition(itemView.llMessageLayout, Explode())
                    itemView.llTimestampLayout.visibility = View.GONE
                }
            }
        }
    }
}