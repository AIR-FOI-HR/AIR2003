package com.example.database.model

data class Feedback (
    var feedbackId: String = "",
    var authorId: String = "",
    var recipientId: String = "",
    var mark: Int = 0,
    var feedback: String = ""
)