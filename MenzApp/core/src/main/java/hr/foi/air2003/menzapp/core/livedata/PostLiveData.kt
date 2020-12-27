package hr.foi.air2003.menzapp.core.livedata

import com.google.firebase.firestore.*
import com.google.firebase.firestore.ktx.getField
import hr.foi.air2003.menzapp.core.model.Post
import hr.foi.air2003.menzapp.core.other.DataOrException

typealias PostOrException = DataOrException<Post, FirebaseFirestoreException>

class PostLiveData(private val documentReference: DocumentReference) : FirestoreLiveData<PostOrException>(documentReference) {

    override fun onEvent(snapshot: DocumentSnapshot?, error: FirebaseFirestoreException?) {
        if(snapshot != null && snapshot.exists()){
            val model = Post(
                postId = snapshot.id,
                authorId = snapshot.getString("authorId")!!,
                timestamp = snapshot.getTimestamp("timestamp")!!,
                description = snapshot.getString("description")!!,
                numberOfPeople = snapshot.getField<Int>("numberOfPeople")!!,
                userRequests = snapshot.getField<List<Map<String,Any>>>("userRequests")!!
            )

            value = PostOrException(model, error)
        }
        else if(error != null){
            // TODO Handle error
        }
    }
}