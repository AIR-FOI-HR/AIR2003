package hr.foi.air2003.menzapp.core

import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.channels.ConflatedBroadcastChannel

interface ModulePresenter {

    fun userLogin(){}
    fun userLogOut(){}
    fun isUserLoggedIn(): Boolean {
        return userData != null
    }
    var userData: FirebaseUser?
    var GoogleAccountData: GoogleSignInAccount?
    val isUserLoggedIn: ConflatedBroadcastChannel<Boolean>
        get() = ConflatedBroadcastChannel<Boolean>()
}