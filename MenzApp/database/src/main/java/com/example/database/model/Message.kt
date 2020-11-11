package com.example.database.model

import java.sql.Timestamp

data class Message (
    var messageId: String = "",
    var authorId: String = "",
    var chatId: String = "",
    var sentTimestamp: Timestamp,
    var seenTimestamp: Timestamp,
    var content: String = ""
)