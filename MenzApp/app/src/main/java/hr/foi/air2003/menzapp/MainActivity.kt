package hr.foi.air2003.menzapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



    }

    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        println(currentUser)
        // if (currentUser === null) {
            // val launchIntent = packageManager.getLaunchIntentForPackage("hr.foi.air2003.menzapp.prijava")
            // startActivity(launchIntent)
             startActivity(Intent(this, RegistrationActivity::class.java))
            finish()
        // }
    }
}