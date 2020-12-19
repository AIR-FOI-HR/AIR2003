package hr.foi.air2003.menzapp.core.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import hr.foi.air2003.menzapp.core.other.QueryItem

data class Message(
        @DocumentId
        var messageId: String = "",
        var authorId: String = "",
        var chatId: String = "",
        var sentTimestamp: Timestamp,
        var seenTimestamp: Timestamp,
        var content: String = ""
) : QueryItem<Message> {
        override val item: Message
                get() = this
        override val id: String
                get() = messageId
}