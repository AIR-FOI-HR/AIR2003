package hr.foi.air2003.menzapp.core.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

data class Notifications (
    @DocumentId
    var notificationId: String = "",
    var authorId: String = "",
    var content: String = "",
    var isRequest: Boolean = true,
    var postId: String = "",
    var timestamp: Timestamp = Timestamp.now(),
    var recipientsId: List<String> = listOf()
)