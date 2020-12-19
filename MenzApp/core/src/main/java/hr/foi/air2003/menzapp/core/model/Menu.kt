package hr.foi.air2003.menzapp.core.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.Exclude
import hr.foi.air2003.menzapp.core.other.QueryItem

data class Menu(
        @DocumentId
        var menuId: String = "",
        var date: String = "",
        var lunch: MutableList<String> = mutableListOf(),
        var dinner: MutableList<String> = mutableListOf(),
        var timestamp: Timestamp
) : QueryItem<Menu> {
    override val item: Menu
        get() = this
    override val id: String
        get() = menuId
}