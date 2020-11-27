package hr.foi.air2003.core.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

data class Post(
        @DocumentId
        var postId: String = "",
        var authorId: String = "",
        var timestamp: Timestamp,
        var description: String = "",
        var numberOfPeople: Int = 0,
        var userRequests: List<String> = listOf()
)