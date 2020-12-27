package hr.foi.air2003.menzapp.recyclerview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import hr.foi.air2003.menzapp.R
import hr.foi.air2003.menzapp.assistants.DateTimePicker
import hr.foi.air2003.menzapp.assistants.ImageConverter
import hr.foi.air2003.menzapp.assistants.SharedViewModel
import hr.foi.air2003.menzapp.core.model.Notification
import kotlinx.android.synthetic.main.notification_list_item.view.*

class NotificationRecyclerViewAdapter(private val fragment: Fragment) : GenericRecyclerViewAdaper<Notification>() {
    private val viewModel = SharedViewModel()
    private val dateTimePicker = DateTimePicker()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenericViewHolder<Notification> {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.notification_list_item, parent, false)
        return NotificationViewHolder(view)
    }

    inner class NotificationViewHolder(itemView: View) : GenericViewHolder<Notification>(itemView) {

        override fun onBind(item: Notification) {

            val timeStamp = dateTimePicker.timestampToString(item.timestamp).split("/")
            itemView.tvNotificationTimestampDate.text = timeStamp[0]
            itemView.tvNotificationTimestampTime.text = timeStamp[1]
            itemView.tvNotificationText.text = item.content

            if (item.request){
                itemView.btnConfirm.visibility = View.VISIBLE
                itemView.btnIgnore.visibility = View.VISIBLE
            }

            val liveData = viewModel.getUser(item.authorId)
            liveData.observe(fragment.viewLifecycleOwner, {
                val user = it.data

                if (user != null) {

                    itemView.tvProfileUserName.text = user.fullName

                    val imgUri = user.profilePicture
                    viewModel.getImage(imgUri)
                            .addOnSuccessListener { bytes ->
                                val bitmap = ImageConverter.convertBytesToBitmap(bytes)
                                val resized = ImageConverter.resizeBitmap(bitmap, itemView.ivProfileUserPhoto)
                                itemView.ivProfileUserPhoto.setImageBitmap(resized)
                            }
                }
            })
        }
    }

}