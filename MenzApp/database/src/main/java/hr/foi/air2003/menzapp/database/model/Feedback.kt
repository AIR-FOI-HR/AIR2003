package hr.foi.air2003.menzapp.database.model

data class Feedback (
    var feedbackId: String = "",
    var authorId: String = "",
    var recipientId: String = "",
    var mark: Int = 0,
    var feedback: String = ""
)