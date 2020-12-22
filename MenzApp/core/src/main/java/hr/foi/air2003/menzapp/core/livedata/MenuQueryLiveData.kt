package hr.foi.air2003.menzapp.core.livedata

import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.gson.Gson
import hr.foi.air2003.menzapp.core.model.Menu

typealias MenuQueryResult = QueryResultOrException<Menu, FirebaseFirestoreException>

class MenuQueryLiveData(private val query: Query) : FirestoreQueryLiveData<MenuQueryResult>(query) {

    override fun onEvent(snapshot: QuerySnapshot?, error: FirebaseFirestoreException?) {
        val documents = snapshot?.documents
        val menus: MutableList<Menu> = mutableListOf()

        if(documents != null){
            for(doc in documents){
                val json = Gson().toJson(doc.data)
                val menu = Gson().fromJson(json, Menu::class.java)
                menu.menuId = doc.id
                menus.add(menu)
            }
        }

        postValue(MenuQueryResult(menus, error))
    }
}