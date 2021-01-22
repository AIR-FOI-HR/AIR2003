package hr.foi.air2003.menzapp.core.services

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

object FirebaseAuthService {
    private val instance = FirebaseAuth.getInstance()

    fun getCurrentUser(): FirebaseUser?{
        return FirebaseAuth.getInstance().currentUser
    }
}