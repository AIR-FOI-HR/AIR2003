package hr.foi.air2003.menzapp.recyclerview

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import coil.api.load
import coil.size.Scale
import hr.foi.air2003.menzapp.R
import hr.foi.air2003.menzapp.assistants.DateTimePicker
import hr.foi.air2003.menzapp.assistants.SharedViewModel
import hr.foi.air2003.menzapp.core.model.Post
import hr.foi.air2003.menzapp.core.services.UserService
import kotlinx.android.synthetic.main.home_post_list_item.view.*

class HomePostRecyclerViewAdapter : GenericRecyclerViewAdaper<Post>() {
    private val dateTimePicker = DateTimePicker()
    private val viewModel = SharedViewModel()
    private var currentUser: String? = null
    var authorClick: ((Post) -> Unit)? = null
    var respondClick: ((Post) -> Unit)? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenericViewHolder<Post> {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.home_post_list_item, parent, false)
        currentUser = UserService.getCurrentUser()
        return HomeViewHolder(view)
    }

    inner class HomeViewHolder(itemView: View) : GenericViewHolder<Post>(itemView) {

        init {
            itemView.btnRespond.setOnClickListener {
                respondClick?.invoke(items[adapterPosition])
            }

            itemView.tvHomePostAuthorName.setOnClickListener {
                authorClick?.invoke(items[adapterPosition])
            }
        }


        @SuppressLint("SetTextI18n")
        override fun onBind(item: Post) {
            val dateTime = dateTimePicker.timestampToString(item.timestamp).split("/")
            itemView.tvHomePostTimestamp.text = "${dateTime[0]} ${dateTime[1]}"
            itemView.tvHomePostPeople.text = "Optimalan broj ljudi: ${item.numberOfPeople}"
            itemView.tvProfilePostDescription.text = item.description

            val liveData = viewModel.getUser(item.authorId)
            liveData.observeForever {
                val user = it.data
                if (user != null) {
                    itemView.tvHomePostAuthorName.text = user.fullName
                    val imgUri = user.profilePicture

                    viewModel.getImage(imgUri)
                            .addOnSuccessListener { url ->
                                itemView.ivHomePostImage.load(url) {
                                    scale(Scale.FILL)
                                }
                            }

                    var found = false

                    for (map in item.userRequests) {
                        if (map.containsValue(currentUser!!)) {
                            found = true
                        }
                    }

                    if (found) {
                        itemView.btnRespond.visibility = View.GONE
                    }
                }

                return@observeForever
            }
        }
    }
}