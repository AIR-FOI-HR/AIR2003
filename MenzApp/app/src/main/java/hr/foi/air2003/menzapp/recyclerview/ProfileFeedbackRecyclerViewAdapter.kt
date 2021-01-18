package hr.foi.air2003.menzapp.recyclerview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import coil.api.load
import coil.size.Scale
import hr.foi.air2003.menzapp.R
import hr.foi.air2003.menzapp.assistants.SharedViewModel
import hr.foi.air2003.menzapp.core.model.Feedback
import kotlinx.android.synthetic.main.profile_feedback_list_item.view.*

class ProfileFeedbackRecyclerViewAdapter : GenericRecyclerViewAdaper<Feedback>() {
    private val viewModel = SharedViewModel()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenericViewHolder<Feedback> {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.profile_feedback_list_item, parent, false)
        return ProfileFeedbackViewHolder(view)
    }

    inner class ProfileFeedbackViewHolder(itemView: View) : GenericViewHolder<Feedback>(itemView) {
        override fun onBind(item: Feedback) {
            itemView.tvFeedbackDescription.text = item.feedback
            itemView.rbFeedbackRating.rating = item.mark.toFloat()

            val liveData = viewModel.getUser(item.authorId)
            liveData.observeForever {
                val user = it.data

                if (user != null) {
                    itemView.tvProfileUserName.text = user.fullName

                    val imgUri = user.profilePicture
                    viewModel.getImage(imgUri)
                            .addOnSuccessListener { url ->
                                itemView.ivProfileUserPhoto.load(url) {
                                    scale(Scale.FIT)
                                }
                            }
                }

                return@observeForever
            }

            if (items.indexOf(item) == items.lastIndex)
                itemView.breakLineFeedback.visibility = View.GONE
        }
    }
}