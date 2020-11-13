package hr.foi.air2003.menzapp.database.model

import com.google.firebase.firestore.DocumentId
import java.sql.Timestamp

data class Post(
        @DocumentId
        var postId: String = "",
        var authorId: String = "",
        var timestamp: Timestamp,
        var description: String = "",
        var numberOfPeople: Int = 0,
        var userRequests: List<String> = listOf()
)