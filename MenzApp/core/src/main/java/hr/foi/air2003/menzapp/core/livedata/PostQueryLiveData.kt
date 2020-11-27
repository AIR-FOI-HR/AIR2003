package hr.foi.air2003.menzapp.core.livedata

import com.google.firebase.firestore.*
import com.google.gson.Gson
import hr.foi.air2003.menzapp.core.model.Post
import hr.foi.air2003.menzapp.core.other.QueryItem

typealias PostQueryResult = QueryResultOrException<Post, Exception>
//typealias PostQueryResult = DataOrException<List<Post>, Exception>

class PostQueryLiveData(query: Query) : FirestoreQueryLiveData<PostQueryResult>(query) {
    override fun onEvent(snapshot: QuerySnapshot?, error: FirebaseFirestoreException?) {
        val documents = snapshot?.documents
        val posts: MutableList<QueryItem<Post>> = mutableListOf()

        if(documents != null){
            for(doc in documents){
                val json = Gson().toJson(doc.data)
                val post = Gson().fromJson(json, Post::class.java)
                post.postId = doc.id
                posts.add(post)
            }
        }

        postValue(PostQueryResult(posts, error))
    }
}