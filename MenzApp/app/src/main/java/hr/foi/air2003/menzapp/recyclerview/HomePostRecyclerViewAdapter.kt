package hr.foi.air2003.menzapp.recyclerview

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import hr.foi.air2003.menzapp.R
import hr.foi.air2003.menzapp.assistants.DateTimePicker
import hr.foi.air2003.menzapp.assistants.ImageConverter
import hr.foi.air2003.menzapp.assistants.SharedViewModel
import hr.foi.air2003.menzapp.core.model.Notification
import hr.foi.air2003.menzapp.core.model.Post
import hr.foi.air2003.menzapp.ui.HomeFragment
import kotlinx.android.synthetic.main.home_post_list_item.view.*

class HomePostRecyclerViewAdapter(private val fragment: HomeFragment) : GenericRecyclerViewAdaper<Post>() {
    private val dateTimePicker = DateTimePicker()
    private val viewModel = SharedViewModel()
    private var currentUser: FirebaseUser? = null
    var authorClick: ((Post) -> Unit)? = null
    var respondClick: ((Post) -> Unit)? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenericViewHolder<Post> {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.home_post_list_item, parent, false)
        currentUser = FirebaseAuth.getInstance().currentUser
        return HomeViewHolder(view)
    }

    inner class HomeViewHolder(itemView: View) : GenericViewHolder<Post>(itemView) {

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
            itemView.tvHomePostTimestamp.text = "${dateTime[0]} ${dateTime[1]}"
            itemView.tvHomePostPeople.text = "Optimalan broj ljudi: ${item.numberOfPeople}"
            itemView.tvProfilePostDescription.text = item.description

            val liveData = viewModel.getUser(item.authorId)
            liveData.observe(fragment.viewLifecycleOwner, {
                val user = it.data
                if (user != null) {
                    itemView.tvHomePostAuthorName.text = user.fullName
                    val imgUri = user.profilePicture

                    viewModel.getImage(imgUri)
                            .addOnSuccessListener { bytes ->
                                val bitmap = ImageConverter.convertBytesToBitmap(bytes)
                                val resized = ImageConverter.resizeBitmap(bitmap, itemView.ivHomePostImage)
                                itemView.ivHomePostImage.setImageBitmap(resized)
                            }

                    var found = false

                    for(map in item.userRequests){
                        if(map.containsValue(currentUser?.uid.toString())) {
                            found = true
                        }
                    }

                    if (found){
                        itemView.btnRespond.visibility = View.GONE
                    }


                    itemView.btnRespond.setOnClickListener {
                        val notification = Notification(
                                authorId = currentUser?.uid.toString(),
                                content = "Novi zahtjev",
                                request = true,
                                postId = item.postId,
                                recipientsId = listOf(item.authorId) ,
                                timestamp = Timestamp(System.currentTimeMillis()/1000,0),
                        )
                        viewModel.createNotificationRequest(notification)
                        itemView.btnRespond.visibility = View.GONE
                    }

                }
            })
        }
    }
}