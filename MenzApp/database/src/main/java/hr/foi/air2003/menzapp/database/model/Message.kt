package hr.foi.air2003.menzapp.database.model

import com.google.firebase.firestore.DocumentId
import java.sql.Timestamp

data class Message(
        @DocumentId
        var messageId: String = "",
        var authorId: String = "",
        var chatId: String = "",
        var sentTimestamp: Timestamp,
        var seenTimestamp: Timestamp,
        var content: String = ""
)