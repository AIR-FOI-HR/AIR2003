package hr.foi.air2003.menzapp.database.model

data class Chat(
        var chatId: String = "",
        var chatName: String = "",
        var participantsId: Array<String>
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Chat

        if (chatId != other.chatId) return false
        if (chatName != other.chatName) return false
        if (!participantsId.contentEquals(other.participantsId)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = chatId.hashCode()
        result = 31 * result + chatName.hashCode()
        result = 31 * result + participantsId.contentHashCode()
        return result
    }
}