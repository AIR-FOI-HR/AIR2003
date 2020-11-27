package hr.foi.air2003.menzapp.recyclerview

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import hr.foi.air2003.menzapp.R
import hr.foi.air2003.menzapp.assistants.DateTimePicker
import hr.foi.air2003.menzapp.core.model.Post
import kotlinx.android.synthetic.main.home_post_list_item.view.*
import kotlinx.android.synthetic.main.home_post_list_item.view.tvDescription
import kotlinx.android.synthetic.main.profile_post_list_item.view.*

class ProfilePostRecyclerViewAdapter : GenericRecyclerViewAdaper<Post>(){
    private val dateTimePicker = DateTimePicker()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenericViewHolder<Post> {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.profile_post_list_item, parent, false)
        return ProfilePostViewHolder(view)
    }

    inner class ProfilePostViewHolder(itemView: View) : GenericViewHolder<Post>(itemView){

        init {
            itemView.btnEditPost.setOnClickListener {
                itemClick?.invoke(items[adapterPosition])
            }
        }

        @SuppressLint("SetTextI18n")
        override fun onBind(item: Post) {
            val dateTime = dateTimePicker.timestampToString(item.timestamp).split("/")
            itemView.tvPostDateTime.text = "${dateTime[0]} ${dateTime[1]}"
            itemView.tvNumOfPeople.text = "Optimalan broj ljudi: ${item.numberOfPeople}"
            itemView.tvDescription.text = item.description
        }
    }
}