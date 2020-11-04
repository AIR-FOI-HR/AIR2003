package hr.foi.air2003.menzapp

import android.R.attr
import android.content.Intent
import android.os.Bundle
import android.os.PatternMatcher
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import hr.foi.air2003.menzapp.prijava.LoginActivity
import kotlinx.android.synthetic.main.fragment_registration.*

class RegistrationActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_registration)
        auth = FirebaseAuth.getInstance();

        kreirajRacun.setOnClickListener {
            provjeriUnosIKreirajRacun()
        }
    }

    private fun updateUI(user: FirebaseUser?) {
        // TODO add user successfully created message or something like that
    }

    private fun provjeriUnosIKreirajRacun() {
        if (email.text.toString().isEmpty()) {
            email.error = "Molimo unesite email"
            email.requestFocus()
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email.text.toString()).matches()) {
            email.error = "Molimo unesite ispravan email"
            email.requestFocus()
            return
        }

        if (lozinka.text.toString().isEmpty()) {
            lozinka.error = "Molimo unesite lozinku"
            lozinka.requestFocus()
            return
        }

        kreirajRacun()
    }

    private fun kreirajRacun() {
        auth.createUserWithEmailAndPassword(email.text.toString(), lozinka.text.toString())
            .addOnCompleteListener(
                this
            ) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    // startActivity(Intent(this, MainActivity::class.java))
                    // finish()
                    updateUI(user)
                } else {
                    // TODO update notification to user in case of failure
                    updateUI(null)
                }
            }
    }
}

