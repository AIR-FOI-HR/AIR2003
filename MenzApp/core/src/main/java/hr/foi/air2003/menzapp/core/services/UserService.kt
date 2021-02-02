package hr.foi.air2003.menzapp.core.services

import com.google.firebase.auth.FirebaseAuth

object UserService {
    private var auth = FirebaseAuth.getInstance()

    fun getCurrentUser() : String? {
        return auth.currentUser?.uid
    }

    fun getLoginProvider() : String? {
        return auth.currentUser?.providerData?.get(1)?.providerId
    }
}