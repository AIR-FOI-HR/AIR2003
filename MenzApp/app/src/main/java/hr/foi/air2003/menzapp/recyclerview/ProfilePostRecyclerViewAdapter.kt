package hr.foi.air2003.menzapp.recyclerview

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import hr.foi.air2003.menzapp.R
import hr.foi.air2003.menzapp.assistants.DateTimePicker
import hr.foi.air2003.menzapp.core.model.Post
import hr.foi.air2003.menzapp.core.services.UserService
import hr.foi.air2003.menzapp.ui.VisitedProfileFragment
import kotlinx.android.synthetic.main.home_post_list_item.view.tvProfilePostDescription
import kotlinx.android.synthetic.main.profile_post_list_item.view.*

class ProfilePostRecyclerViewAdapter(private val fragment: Fragment) : GenericRecyclerViewAdaper<Post>() {
    private val dateTimePicker = DateTimePicker()
    private var currentUser: String? = null
    var editClick: ((Post) -> Unit)? = null
    var sendRequest: ((Post) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenericViewHolder<Post> {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.profile_post_list_item, parent, false)
        currentUser = UserService.getCurrentUser()
        return ProfilePostViewHolder(view)
    }

    inner class ProfilePostViewHolder(itemView: View) : GenericViewHolder<Post>(itemView) {

        init {
            itemView.btnEditPost.setOnClickListener {
                editClick?.invoke(items[adapterPosition])
            }

            itemView.btnSendRequest.setOnClickListener {
                sendRequest?.invoke(items[adapterPosition])
            }
        }

        @SuppressLint("SetTextI18n")
        override fun onBind(item: Post) {
            val dateTime = dateTimePicker.timestampToString(item.timestamp).split("/")
            itemView.tvProfilePostTimestamp.text = "${dateTime[0]} ${dateTime[1]}"
            itemView.tvProfilePostPeople.text = "Optimalan broj ljudi: ${item.numberOfPeople}"
            itemView.tvProfilePostDescription.text = item.description

            if(fragment.javaClass == VisitedProfileFragment::class.java){
                itemView.btnEditPost.visibility = View.GONE

                var found = false
                for (map in item.userRequests) {
                    if (map.containsValue(currentUser!!)) {
                        found = true
                    }
                }

                if (found) {
                    itemView.btnSendRequest.visibility = View.GONE
                }else{
                    itemView.btnSendRequest.visibility = View.VISIBLE
                }
            }
        }
    }
}