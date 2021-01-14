package hr.foi.air2003.menzapp.recyclerview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import coil.api.load
import coil.size.Scale
import hr.foi.air2003.menzapp.R
import hr.foi.air2003.menzapp.assistants.SharedViewModel
import hr.foi.air2003.menzapp.core.model.User
import kotlinx.android.synthetic.main.search_list_item.view.*

class SearchUserRecyclerViewAdapter : GenericRecyclerViewAdaper<User>() {
    private var viewModel = SharedViewModel()
    var userClick: ((User) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenericViewHolder<User> {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.search_list_item, parent, false)
        return SearchUserViewHolder(view)
    }

    inner class SearchUserViewHolder(itemView: View) : GenericViewHolder<User>(itemView) {

        init {
            itemView.searchUser.setOnClickListener {
                userClick?.invoke(items[adapterPosition])
            }
        }

        override fun onBind(item: User) {
            itemView.tvSearchUserName.text = item.fullName
            itemView.tvSearchUserDescription.text = item.bio

            val imgUri = item.profilePicture
            viewModel.getImage(imgUri)
                .addOnSuccessListener { url ->
                    itemView.ivSearchUserPhoto.load(url) {
                        scale(Scale.FIT)
                    }
                }
        }
    }
}