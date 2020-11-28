package hr.foi.air2003.core.model

import com.google.firebase.firestore.DocumentId

data class User(
        @DocumentId
        var userId: String = "",
        var fullName: String = "",
        var email: String = "",
        var bio: String = "",
        var profilePicture: String = "",
        var notificationsOn: Boolean = true,
        var subscribersCount: Int = 0
)