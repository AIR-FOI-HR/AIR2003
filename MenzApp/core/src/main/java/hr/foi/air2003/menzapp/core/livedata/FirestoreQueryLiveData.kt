package hr.foi.air2003.menzapp.core.livedata

import androidx.lifecycle.LiveData
import com.google.firebase.firestore.*
import hr.foi.air2003.menzapp.core.other.DataOrException
import hr.foi.air2003.menzapp.core.other.QueryItem

typealias QueryResultOrException<T, E> = DataOrException<List<QueryItem<T>>, E>

open class FirestoreQueryLiveData<T>(private val query: Query) : LiveData<T>(), EventListener<QuerySnapshot> {
    private var listenerRegistration: ListenerRegistration? = null

    override fun onActive() {
        super.onActive()
        listenerRegistration = query.addSnapshotListener(this)
    }

    override fun onInactive() {
        super.onInactive()
        listenerRegistration?.remove()
    }

    override fun onEvent(snapshot: QuerySnapshot?, error: FirebaseFirestoreException?) { }
}