package hr.foi.air2003.menzapp.core.livedata

import androidx.lifecycle.LiveData
import com.google.firebase.firestore.*
import com.google.firebase.firestore.ktx.getField
import com.google.gson.Gson
import hr.foi.air2003.menzapp.core.model.Post
import hr.foi.air2003.menzapp.core.other.DataOrException
import hr.foi.air2003.menzapp.core.other.QueryItem

//typealias QueryResultOrException<T, E> = DataOrException<List<QueryItem<T>>, E>
//typealias PostQueryResult = QueryResultOrException<Post, Exception>
typealias PostQueryResult = DataOrException<List<Post>, Exception>

class PostQueryLiveData(private val query: Query) : LiveData<PostQueryResult>(), EventListener<QuerySnapshot> {
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
        val documents = snapshot?.documents
        val posts: MutableList<Post> = mutableListOf()

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