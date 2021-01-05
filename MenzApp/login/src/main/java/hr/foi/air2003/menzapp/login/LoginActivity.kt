package hr.foi.air2003.menzapp.login

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.dialog_password.*
import kotlinx.android.synthetic.main.login_main.*
import kotlinx.android.synthetic.main.login_main.txtPassword


class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_main)
        auth = FirebaseAuth.getInstance()

        btnLogin.setOnClickListener {
            checkLoginInput()
        }

        btnGoogle.setOnClickListener {
            signInWithGoogle()
        }

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

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
                            .addOnCompleteListener(this) { task ->
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

    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, @Nullable data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                Log.d(LoginActivity.TAG, "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                Log.w(LoginActivity.TAG, "Google sign in failed", e)
                updateUI(null)
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d(LoginActivity.TAG, "signInWithCredential:success")
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    Log.w(LoginActivity.TAG, "signInWithCredential:failure", task.exception)
                    updateUI(null)
                }
            }
    }

    override fun onStart() {
        super.onStart()
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
                        super.onBackPressed()
                        //finish()
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

    companion object {
        private const val TAG = "LoginActivity"
        private const val RC_SIGN_IN = 10000
    }
}