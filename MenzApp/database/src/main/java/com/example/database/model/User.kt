package com.example.database.model

data class User (
    var userId: String = "",
    var fullName: String = "",
    var email: String = "",
    var bio: String = "",
    var profilePicture: String = "",
    var notificationsOn: Boolean = true
)