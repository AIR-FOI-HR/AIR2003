package hr.foi.air2003.menzapp.database.model

import com.google.firebase.firestore.DocumentId

data class Chat(
        @DocumentId
        var chatId: String = "",
        var chatName: String = "",
        var participantsId: List<String> = listOf()
)