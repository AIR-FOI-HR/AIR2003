package hr.foi.air2003.menzapp.recyclerview

import android.annotation.SuppressLint
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import hr.foi.air2003.menzapp.R
import hr.foi.air2003.menzapp.assistants.DateTimePicker
import hr.foi.air2003.menzapp.assistants.ImageConverter
import hr.foi.air2003.menzapp.core.model.Post
import hr.foi.air2003.menzapp.ui.HomeViewModel
import kotlinx.android.synthetic.main.home_post_list_item.view.*

class HomePostRecyclerViewAdapter : GenericRecyclerViewAdaper<Post>(){
    private val dateTimePicker = DateTimePicker()
    private val viewModel = HomeViewModel()
    var authorClick: ((Post)->Unit)? = null
    var respondClick: ((Post)->Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenericViewHolder<Post> {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.home_post_list_item, parent, false)
        return HomeViewHolder(view)
    }

    inner class HomeViewHolder(itemView: View) : GenericViewHolder<Post>(itemView){

        init {
            itemView.btnRespond.setOnClickListener {
                respondClick?.invoke(items[adapterPosition])
                itemView.btnRespond.visibility = View.GONE

                // TODO Implement toggle button maybe
            }

            itemView.tvHomePostAuthorName.setOnClickListener {
                authorClick?.invoke(items[adapterPosition])
            }
        }

        @SuppressLint("SetTextI18n")
        override fun onBind(item: Post) {
            val dateTime = dateTimePicker.timestampToString(item.timestamp).split("/")
            itemView.tvHomePostAuthorName.text = item.author["fullName"]
            itemView.tvHomePostTimestamp.text = "${dateTime[0]} ${dateTime[1]}"
            itemView.tvHomePostPeople.text = "Optimalan broj ljudi: ${item.numberOfPeople}"
            itemView.tvProfilePostDescription.text = item.description

            // TODO Fetch author by userId

            val imgUri = item.author["profilePicture"]
            viewModel.getUserImage(imgUri!!)
                .addOnSuccessListener { bytes ->
                    itemView.ivHomePostImage.setImageBitmap(ImageConverter.convertBytesToBitmap(bytes))
                }
        }
    }
}