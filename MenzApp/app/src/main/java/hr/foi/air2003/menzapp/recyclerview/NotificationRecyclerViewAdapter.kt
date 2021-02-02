package hr.foi.air2003.menzapp.recyclerview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import coil.api.load
import coil.size.Scale
import hr.foi.air2003.menzapp.R
import hr.foi.air2003.menzapp.assistants.DateTimePicker
import hr.foi.air2003.menzapp.assistants.SharedViewModel
import hr.foi.air2003.menzapp.core.model.Notification
import kotlinx.android.synthetic.main.notification_list_item.view.*

class NotificationRecyclerViewAdapter : GenericRecyclerViewAdaper<Notification>() {
    private val viewModel = SharedViewModel()
    private val dateTimePicker = DateTimePicker()
    var confirmClick: ((Notification) -> Unit)? = null
    var deleteClick: ((Notification) -> Unit)? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): GenericViewHolder<Notification> {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.notification_list_item, parent, false)
        return NotificationViewHolder(view)
    }

    inner class NotificationViewHolder(itemView: View) : GenericViewHolder<Notification>(itemView) {

        init {
            itemView.btnConfirm.setOnClickListener {
                confirmClick?.invoke(items[adapterPosition])
            }

            itemView.btnIgnore.setOnClickListener {
                deleteClick?.invoke(items[adapterPosition])
            }
        }

        override fun onBind(item: Notification) {
            val timestamp = dateTimePicker.timestampToString(item.timestamp).split("/")
            itemView.tvNotificationTimestampDate.text = timestamp[0]
            itemView.tvNotificationTimestampTime.text = timestamp[1]
            itemView.tvNotificationText.text = item.content

            if (item.request) {
                itemView.btnConfirm.visibility = View.VISIBLE
                itemView.btnIgnore.visibility = View.VISIBLE
            } else {
                itemView.btnConfirm.visibility = View.GONE
                itemView.btnIgnore.visibility = View.GONE
            }

            val liveData = viewModel.getUser(item.authorId)
            liveData.observeForever {
                val user = it.data

                if (user != null) {

                    itemView.tvProfileUserName.text = user.fullName

                    val imgUri = user.profilePicture
                    viewModel.getImage(imgUri)
                            .addOnSuccessListener { url ->
                                itemView.ivProfileUserPhoto.load(url) {
                                    scale(Scale.FILL)
                                }
                            }
                }

                return@observeForever
            }
        }
    }
}