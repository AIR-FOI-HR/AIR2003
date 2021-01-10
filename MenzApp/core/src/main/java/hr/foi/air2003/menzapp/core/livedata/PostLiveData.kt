package hr.foi.air2003.menzapp.core.livedata

import com.google.firebase.firestore.*
import com.google.gson.Gson
import hr.foi.air2003.menzapp.core.model.Post
import hr.foi.air2003.menzapp.core.other.DataOrException

typealias PostOrException = DataOrException<Post, FirebaseFirestoreException>

class PostLiveData(documentReference: DocumentReference) :
    FirestoreLiveData<PostOrException>(documentReference) {

    override fun onEvent(snapshot: DocumentSnapshot?, error: FirebaseFirestoreException?) {
        if (snapshot != null && snapshot.exists()) {
            val json = Gson().toJson(snapshot)
            val post = Gson().fromJson(json, Post::class.java)
            post.postId = snapshot.id

            value = PostOrException(post, error)
        } else if (error != null) {
            // TODO Handle error
        }
    }
}