package hr.foi.air2003.menzapp.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import hr.foi.air2003.menzapp.login.LoginActivity


class SplashScreenActivity : AppCompatActivity() {
    private lateinit var handler: Handler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handler = Handler()
        handler.postDelayed(runnable, 1000)
    }

    private val runnable = Runnable {
        if (!isFinishing) {
            if(Firebase.auth.currentUser === null)
                startActivity(Intent(this@SplashScreenActivity, LoginActivity::class.java))
            else
                startActivity(Intent(this@SplashScreenActivity, MainActivity::class.java))
            finish()
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