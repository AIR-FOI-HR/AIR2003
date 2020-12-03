package hr.foi.air2003.menzapp.core.livedata

import androidx.lifecycle.LiveData
import com.google.firebase.firestore.*
import com.google.firebase.firestore.ktx.getField
import hr.foi.air2003.menzapp.core.model.Post
import hr.foi.air2003.menzapp.core.other.DataOrException

typealias PostOrException = DataOrException<Post, FirebaseFirestoreException>

class PostLiveData (private val documentReference: DocumentReference) : LiveData<PostOrException>(), EventListener<DocumentSnapshot> {
    private var listenerRegistration: ListenerRegistration? = null

    override fun onActive() {
        super.onActive()
        listenerRegistration = documentReference.addSnapshotListener(this)
    }

    override fun onInactive() {
        super.onInactive()
        listenerRegistration?.remove()
    }

    override fun onEvent(snapshot: DocumentSnapshot?, error: FirebaseFirestoreException?) {
        if(snapshot != null && snapshot.exists()){
            val model = Post(
                postId = snapshot.id,
                author = snapshot.getField<Map<String, String>>("author")!!,
                timestamp = snapshot.getTimestamp("timestamp")!!,
                description = snapshot.getString("description")!!,
                numberOfPeople = snapshot.getField<Int>("numberOfPeople")!!,
                userRequests = snapshot.getField<List<String>>("userRequests")!!
            )

            value = PostOrException(model, error)
        }
        else if(error != null){
            // TODO Handle error
        }
    }
}