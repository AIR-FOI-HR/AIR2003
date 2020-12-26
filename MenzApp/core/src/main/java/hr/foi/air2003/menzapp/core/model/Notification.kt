package hr.foi.air2003.menzapp.core.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

data class Notification (
    @DocumentId
    var authorId: String = "",
    var content: String = "",
    var isRequest: Boolean = true,
    var notificationId: String = "",
    var postId: String = "",
    var timestamp: Timestamp = Timestamp.now(),
    var recipientsId: List<String> = listOf()
)