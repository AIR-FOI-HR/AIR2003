package hr.foi.air2003.menzapp.core.model

import com.google.firebase.firestore.DocumentId

data class User(
        @DocumentId
        var userId: String = "",
        var fullName: String = "",
        var email: String = "",
        var bio: String = "",
        var profilePicture: String = "https://firebasestorage.googleapis.com/v0/b/menzapp-fa21a.appspot.com/o/photos%2Fdefault.png?alt=media&token=6e187c1f-2705-45ae-91dd-e40cfb3cab38",
        var notificationsOn: Boolean = true,
        var subscribersCount: Int = 0,
        var subscribedTo: List<String> = listOf()
)