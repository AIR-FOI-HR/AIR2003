package hr.foi.air2003.menzapp.core.model

import com.google.firebase.firestore.DocumentId
import hr.foi.air2003.menzapp.core.other.QueryItem

data class Chat(
        @DocumentId
        var chatId: String = "",
        var chatName: String = "",
        var postId: String = "",
        var lastMessage: String = "",
        var participantsId: List<String> = listOf()
) : QueryItem<Chat>{
        override val item: Chat
                get() = this
        override val id: String
                get() = chatId
}