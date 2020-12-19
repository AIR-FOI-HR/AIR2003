package hr.foi.air2003.menzapp.recyclerview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

open class GenericRecyclerViewAdaper<T> : RecyclerView.Adapter<GenericViewHolder<T>>() {
    var items: List<T> = listOf()
    //var itemClick: ((T)->Unit)? = null

    fun addItems(items: List<T>){
        this.items = items
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenericViewHolder<T> {
        val view = LayoutInflater.from(parent.context).inflate(viewType, parent, false)
        return GenericViewHolder(view)
    }

    override fun onBindViewHolder(holder: GenericViewHolder<T>, position: Int) {
        holder.onBind(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }
}

open class GenericViewHolder<T>(itemView: View) : RecyclerView.ViewHolder(itemView), BindRecyclerViewHolder<T>{
    override fun onBind(item: T) {}
}