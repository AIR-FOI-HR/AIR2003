package hr.foi.air2003.menzapp.recyclerview

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import hr.foi.air2003.menzapp.R
import kotlinx.android.synthetic.main.food_menu_list_item.view.*

class FoodMenuRecyclerViewAdapter : GenericRecyclerViewAdaper<String>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenericViewHolder<String> {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.food_menu_list_item, parent, false)
        return MenuViewHolder(view)
    }

    inner class MenuViewHolder(itemView: View) : GenericViewHolder<String>(itemView){
        @SuppressLint("SetTextI18n")
        override fun onBind(item: String) {
            val splitMenu = item.split("/")
            itemView.tvMenuDescription.text = splitMenu[1]
            if(splitMenu[0] == "DODATNA JELA")
                itemView.tvMenuName.text = splitMenu[0]
            else
                itemView.tvMenuName.text = "Meni ${splitMenu[0]}"
        }
    }
}