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
        var userRequests: Array<String>
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Post

        if (postId != other.postId) return false
        if (authorId != other.authorId) return false
        if (timestamp != other.timestamp) return false
        if (description != other.description) return false
        if (numberOfPeople != other.numberOfPeople) return false
        if (!userRequests.contentEquals(other.userRequests)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = postId.hashCode()
        result = 31 * result + authorId.hashCode()
        result = 31 * result + timestamp.hashCode()
        result = 31 * result + description.hashCode()
        result = 31 * result + numberOfPeople.hashCode()
        result = 31 * result + userRequests.contentHashCode()
        return result
    }
}