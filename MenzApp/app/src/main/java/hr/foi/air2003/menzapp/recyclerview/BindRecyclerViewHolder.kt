package hr.foi.air2003.menzapp.recyclerview

interface BindRecyclerViewHolder<T> {
    fun onBind(item: T)
}