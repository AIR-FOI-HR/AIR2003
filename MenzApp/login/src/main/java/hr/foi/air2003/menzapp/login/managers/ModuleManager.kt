package hr.foi.air2003.menzapp.login.managers

import android.content.Context
import android.widget.PopupWindow
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import hr.foi.air2003.menzapp.core.ModulePresenter
import hr.foi.air2003.menzapp.login.LoginActivity
import hr.foi.air2003.menzapp.module_google_login.GoogleLoginActivity
import kotlinx.android.synthetic.main.popup_menu_login.view.*

object ModuleManager: DataManager() {
    private lateinit var presenters: MutableList<ModulePresenter>
    private lateinit var option: ModulePresenter

    private fun loadPresenters(){
        presenters = mutableListOf(LoginActivity(), GoogleLoginActivity())
        option = presenters[0]
    }

    fun chooseLoginOption(window: PopupWindow, listener: ModuleListener): ModulePresenter {
        val view = window.contentView

        loadPresenters()

        view.btnMenzAppLogin.setOnClickListener {
            listener.startModule(presenters[0])
            option = presenters[0]
        }

        view.btnGoogle.setOnClickListener {
            listener.startModule(presenters[1])
            option = presenters[1]
        }

        return option
    }

    fun setLoginOption(provider: String?, context: Context){
        loadPresenters()

        option = when(provider){
            EmailAuthProvider.PROVIDER_ID -> presenters[0]
            GoogleAuthProvider.PROVIDER_ID -> presenters[1]
            else -> presenters[0]
        }
    }

    fun userLogout(){
        option.userLogOut()
    }

    fun isUserLoggedIn(): Boolean {
        return option.isUserLoggedIn()
    }

    fun userData(): FirebaseUser? {
        return option.userData
    }

    fun googleUserData(): GoogleSignInAccount? {
        return option.GoogleAccountData
    }

}