package hr.foi.air2003.menzapp.database.model

data class User (
    var userId: String = "",
    var fullName: String = "",
    var email: String = "",
    var bio: String = "",
    var profilePicture: String = "",
    var notificationsOn: Boolean = true
)