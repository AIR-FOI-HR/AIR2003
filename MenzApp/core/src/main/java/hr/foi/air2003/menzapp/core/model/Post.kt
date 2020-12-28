package hr.foi.air2003.menzapp.core.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.Exclude

data class Post (
        @DocumentId
        @Exclude
        var postId: String = "",
        var authorId: String = "",
        var timestamp: Timestamp = Timestamp.now(),
        var description: String = "",
        var numberOfPeople: Int = 0,
        var userRequests: List<Map<String,Any>> = listOf()
)