package hr.foi.air2003.menzapp.database.model

import java.sql.Timestamp

data class Message (
    var messageId: String = "",
    var authorId: String = "",
    var chatId: String = "",
    var sentTimestamp: Timestamp,
    var seenTimestamp: Timestamp,
    var content: String = ""
)