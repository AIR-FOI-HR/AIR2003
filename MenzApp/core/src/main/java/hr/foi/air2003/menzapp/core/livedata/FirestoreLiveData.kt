package hr.foi.air2003.menzapp.core.livedata

import androidx.lifecycle.LiveData
import com.google.firebase.firestore.*

open class FirestoreLiveData<T>(private val documentReference: DocumentReference) : LiveData<T>(),
    EventListener<DocumentSnapshot> {
    private var listenerRegistration: ListenerRegistration? = null

    override fun onActive() {
        super.onActive()
        listenerRegistration = documentReference.addSnapshotListener(this)
    }

    override fun onInactive() {
        super.onInactive()
        listenerRegistration?.remove()
    }

    override fun onEvent(snapshot: DocumentSnapshot?, error: FirebaseFirestoreException?) {}
}