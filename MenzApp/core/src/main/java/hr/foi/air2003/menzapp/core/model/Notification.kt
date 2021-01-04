package hr.foi.air2003.menzapp.core.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

data class Notification (
    @DocumentId
    var notificationId: String = "",
    var authorId: String = "",
    var content: String = "",
    var request: Boolean = true,
    var seen: Boolean = false,
    var postId: String = "",
    var timestamp: Timestamp = Timestamp.now(),
    var recipientsId: List<String> = listOf()
)