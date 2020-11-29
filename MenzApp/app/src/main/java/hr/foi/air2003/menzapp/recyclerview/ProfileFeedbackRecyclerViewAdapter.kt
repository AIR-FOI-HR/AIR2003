package hr.foi.air2003.menzapp.recyclerview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import hr.foi.air2003.menzapp.R
import hr.foi.air2003.menzapp.core.model.Feedback
import kotlinx.android.synthetic.main.profile_feedback_list_item.view.*
import kotlinx.android.synthetic.main.profile_post_list_item.view.*

class ProfileFeedbackRecyclerViewAdapter : GenericRecyclerViewAdaper<Feedback>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenericViewHolder<Feedback> {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.profile_feedback_list_item, parent, false)
        return ProfileFeedbackViewHolder(view)
    }

    inner class ProfileFeedbackViewHolder(itemView: View) : GenericViewHolder<Feedback>(itemView){
        override fun onBind(item: Feedback) {
            itemView.tvProfileUserName.text = item.author["fullName"]
            itemView.tvFeedbackDescription.text = item.feedback

            // TODO Show user profile picture

            val color = ContextCompat.getColor(itemView.context, R.color.grey_light)

            when(item.mark){
                in 1..4 -> itemView.ivStar5.drawable.setTint(color)
                in 1..3 -> itemView.ivStar4.drawable.setTint(color)
                in 1..2 -> itemView.ivStar3.drawable.setTint(color)
                1 -> itemView.ivStar2.drawable.setTint(color)
            }

            if(items.indexOf(item) == items.lastIndex)
                itemView.breakLineFeedback.visibility = View.GONE
        }
    }
}