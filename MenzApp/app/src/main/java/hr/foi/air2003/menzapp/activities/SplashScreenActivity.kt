package hr.foi.air2003.menzapp.activities

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import hr.foi.air2003.menzapp.R
import hr.foi.air2003.menzapp.core.services.FirebaseAuthService
import hr.foi.air2003.menzapp.login.LoginActivity
import kotlinx.android.synthetic.main.dialog_internet.*

class SplashScreenActivity : AppCompatActivity() {
    private lateinit var handler: Handler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handler = Handler()
        handler.postDelayed(runnable, 1000)
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
                if (FirebaseAuthService.getCurrentUser() === null) {
                    startActivity(Intent(this@SplashScreenActivity, LoginActivity::class.java))
                } else {
                    startActivity(Intent(this@SplashScreenActivity, MainActivity::class.java))
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        handler.postDelayed(runnable, 1000)
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(runnable)
    }

}
