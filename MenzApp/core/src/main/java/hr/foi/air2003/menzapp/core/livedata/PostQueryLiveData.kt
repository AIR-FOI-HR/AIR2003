package hr.foi.air2003.menzapp.core.livedata

import com.google.firebase.firestore.*
import com.google.gson.Gson
import hr.foi.air2003.menzapp.core.model.Post

typealias PostQueryResult = QueryResultOrException<Post, FirebaseFirestoreException>

class PostQueryLiveData(query: Query) : FirestoreQueryLiveData<PostQueryResult>(query) {
    override fun onEvent(snapshot: QuerySnapshot?, error: FirebaseFirestoreException?) {
        val documents = snapshot?.documents
        val posts: MutableList<Post> = mutableListOf()

        if (documents != null) {
            for (doc in documents) {
                val json = Gson().toJson(doc.data)
                val post = Gson().fromJson(json, Post::class.java)
                post.postId = doc.id
                posts.add(post)
            }
        }

        postValue(PostQueryResult(posts, error))
    }
}