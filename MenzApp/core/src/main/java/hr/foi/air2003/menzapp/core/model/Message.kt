package hr.foi.air2003.menzapp.core.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

data class Message(
        @DocumentId
        var messageId: String = "",
        var authorId: String = "",
        var chatId: String = "",
        var sentTimestamp: Timestamp,
        var seen: Boolean = false,
        var content: String = ""
)