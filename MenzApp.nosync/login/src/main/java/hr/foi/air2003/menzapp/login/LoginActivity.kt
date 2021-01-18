package hr.foi.air2003.menzapp.login

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.dialog_password.*
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

        txtForgotPassword.setOnClickListener {
            Toast.makeText(this, "Toast", Toast.LENGTH_LONG).show()

            val popupForgotPassword = Dialog(this)
            popupForgotPassword.setContentView(layoutInflater.inflate(R.layout.dialog_password, null))
            popupForgotPassword.show()
            popupForgotPassword.btn_sendEmail.setOnClickListener {
                if (!popupForgotPassword.txt_sendToMail.text.toString().isNullOrEmpty() &&
                    Patterns.EMAIL_ADDRESS.matcher(popupForgotPassword.txt_sendToMail.text.toString()).matches()) {
                    auth.sendPasswordResetEmail(popupForgotPassword.txt_sendToMail.text.toString())
                        .addOnCompleteListener( this ) { task ->
                            if (task.isSuccessful) {
                                popupForgotPassword.hide()
                                // TODO Add some kind of popup to notify the mail is sent
                            }
                        }
                    }
                }
            popupForgotPassword.btn_cancel.setOnClickListener {
                popupForgotPassword.hide()
            }
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
                    // TODO Achtung, achtung!!!!
                    // When you uncomment these lines, all mails have to be verified to log in,
                    // for development purposes we will leave this commented
                    // if (auth.currentUser!!.isEmailVerified) {
                        // All good, login the user
                        updateUI(user)
                        finish()
                    //} else {
                        //auth.signOut()
                        // Display message that the user email is not verified
                   // }
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