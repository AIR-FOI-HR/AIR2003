package hr.foi.air2003.menzapp.core.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

data class Post (
        @DocumentId
        var postId: String = "",
        var author: Map<String, String> = mapOf(Pair("authorId", ""), Pair("fullName", "")),
        var timestamp: Timestamp,
        var description: String = "",
        var numberOfPeople: Int = 0,
        var userRequests: List<String> = listOf()
)