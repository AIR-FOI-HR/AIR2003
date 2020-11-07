package hr.foi.air2003.menzapp.login

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.login_main.*
import kotlinx.android.synthetic.main.login_main.txtPassword


class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_main)
        auth = FirebaseAuth.getInstance();

        btnLogin.setOnClickListener {
            checkLoginInput()
        }

        txtRegister.setOnClickListener {
            startActivity(Intent(this, RegistrationActivity::class.java))
        }
    }

    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }

    private fun checkLoginInput() {
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

        userLogin()
    }

    private fun userLogin() {
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