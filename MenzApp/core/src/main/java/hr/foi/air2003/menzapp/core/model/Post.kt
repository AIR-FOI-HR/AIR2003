package hr.foi.air2003.menzapp.core.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import hr.foi.air2003.menzapp.core.other.QueryItem

data class Post (
        @DocumentId
        var postId: String = "",
        var authorId: String = "",
        var timestamp: Timestamp = Timestamp.now(),
        var description: String = "",
        var numberOfPeople: Int = 0,
        var userRequests: List<String> = listOf()
) : QueryItem<Post> {
        override val item: Post
                get() = this
        override val id: String
                get() = postId
}