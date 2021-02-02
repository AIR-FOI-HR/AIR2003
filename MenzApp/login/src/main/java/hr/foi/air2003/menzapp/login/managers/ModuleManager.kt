package hr.foi.air2003.menzapp.login.managers

import android.content.Context
import android.widget.PopupWindow
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.GoogleAuthProvider
import hr.foi.air2003.menzapp.core.ModulePresenter
import hr.foi.air2003.menzapp.login.LoginActivity
import hr.foi.air2003.menzapp.module_google_login.GoogleLoginActivity
import kotlinx.android.synthetic.main.popup_menu_login.view.*

object ModuleManager {
    private lateinit var presenters: MutableList<ModulePresenter>
    private lateinit var option: ModulePresenter

    private fun loadPresenters(){
        presenters = mutableListOf(LoginActivity(), GoogleLoginActivity())
        option = presenters[0]
    }

    fun chooseLoginOption(window: PopupWindow, listener: ModuleListener){
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
}