package hr.foi.air2003.menzapp.core.livedata

import androidx.lifecycle.LiveData
import com.google.firebase.firestore.*
import hr.foi.air2003.menzapp.core.other.DataOrException

typealias DocumentSnapshotOrException = DataOrException<List<DocumentSnapshot>?, FirebaseFirestoreException?>

open class FirestoreQueryLiveData(private val query: Query) : LiveData<DocumentSnapshotOrException>(),
    EventListener<QuerySnapshot> {
    private var listenerRegistration: ListenerRegistration? = null

    override fun onActive() {
        super.onActive()
        listenerRegistration = query.addSnapshotListener(this)
    }

    override fun onInactive() {
        super.onInactive()
        listenerRegistration?.remove()
    }

    override fun onEvent(snapshot: QuerySnapshot?, error: FirebaseFirestoreException?) {
        val documents: List<DocumentSnapshot>? = snapshot?.documents
        postValue(DocumentSnapshotOrException(documents, error))
    }
}