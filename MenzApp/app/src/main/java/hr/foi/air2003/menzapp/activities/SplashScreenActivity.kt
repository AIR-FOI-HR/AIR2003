package hr.foi.air2003.menzapp.activities

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Handler
import android.view.Gravity
import android.view.View
import android.widget.PopupWindow
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import hr.foi.air2003.menzapp.R
import hr.foi.air2003.menzapp.core.ModulePresenter
import hr.foi.air2003.menzapp.core.services.UserService
import hr.foi.air2003.menzapp.login.managers.ModuleListener
import hr.foi.air2003.menzapp.login.managers.ModuleManager
import kotlinx.android.synthetic.main.dialog_internet.*

class SplashScreenActivity : AppCompatActivity(), ModuleListener {
    private lateinit var handler: Handler
    private lateinit var window: PopupWindow

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handler = Handler()
        handler.postDelayed(runnable, 1000)
    }

    override fun onResume() {
        super.onResume()
        handler.postDelayed(runnable, 1000)
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(runnable)
    }

    private fun checkConnection():Boolean {
        val manager = applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = manager.activeNetworkInfo
        if(null!=networkInfo){
            return true
        }
        else{
           val dialog = Dialog(this)
            dialog.setContentView(R.layout.dialog_internet)
            dialog.setCanceledOnTouchOutside(false)
            dialog.btn_checkConnection.setOnClickListener{
                recreate()
            }
            dialog.show()
            dialog.btn_cancel.setOnClickListener{
                finish();
                System.exit(0);
            }
            return false
        }
    }

    private val runnable = Runnable {
        if(checkConnection()) {
            if (!isFinishing) {
                if (UserService.getCurrentUser() === null) {
                    window = showLoginOptions()
                    ModuleManager.chooseLoginOption(window, this)
                } else {
                    ModuleManager.setLoginOption(UserService.getLoginProvider(), this)
                    startActivity(Intent(this@SplashScreenActivity, MainActivity::class.java))
                }
            }
        }
    }

    private fun showLoginOptions(): PopupWindow {
        val window = PopupWindow(this)
        val layout = layoutInflater.inflate(R.layout.popup_menu_login, null)
        window.contentView = layout
        window.isOutsideTouchable = false
        window.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.popup_login))
        window.showAtLocation(View(this), Gravity.CENTER, 0,0)

        return window
    }

    override fun startModule(module: ModulePresenter) {
        if(window.isShowing){
            window.dismiss()
        }

        startActivity(Intent(this, (module as AppCompatActivity)::class.java))
    }
}
