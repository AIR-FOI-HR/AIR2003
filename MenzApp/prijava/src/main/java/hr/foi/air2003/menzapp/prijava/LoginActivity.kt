package hr.foi.air2003.menzapp.prijava

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Patterns
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.dialog_password.view.*
import kotlinx.android.synthetic.main.login_main.*


class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_main)
        auth = FirebaseAuth.getInstance();

        btnLogin.setOnClickListener {
            ulogirajSe()
        }
    }

    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }

    private fun provjeriUnosIUlogirajSe() {
        if (txtUsername.text.toString().isEmpty()) {
            txtUsername.error = "Molimo unesite email"
            txtUsername.requestFocus()
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(txtUsername.text.toString()).matches()) {
            txtUsername.error = "Molimo unesite ispravan email"
            txtUsername.requestFocus()
            return
        }

        if (txtPassword.text.toString().isEmpty()) {
            txtPassword.error = "Molimo unesite lozinku"
            txtPassword.requestFocus()
            return
        }

        ulogirajSe()
    }

    private fun ulogirajSe() {
        auth.signInWithEmailAndPassword(txtUsername.text.toString(), txtPassword.text.toString())
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    updateUI(user)
                    // startActivity(Intent(this, MainActivity::class.java)) // TODO check how to solve circular dependecy
                    finish()
                } else {
                    Toast.makeText(baseContext, "Na žalost, nismo Vas uspjeli ulogirati",
                        Toast.LENGTH_SHORT).show()
                    updateUI(null)
                }
            }
    }

    private fun updateUI(user: FirebaseUser?) {
        // TODO add user successfully created message or something like that
    }

}