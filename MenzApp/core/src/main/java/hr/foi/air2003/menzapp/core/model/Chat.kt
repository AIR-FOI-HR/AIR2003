package hr.foi.air2003.menzapp.core.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

data class Chat(
        @DocumentId
        var chatId: String = "",
        var postId: String = "",
        var lastMessage: String = "",
        var timestamp: Timestamp = Timestamp.now(),
        var participantsId: List<String> = listOf()
)