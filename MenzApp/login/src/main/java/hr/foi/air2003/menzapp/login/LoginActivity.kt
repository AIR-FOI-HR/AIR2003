package hr.foi.air2003.menzapp.login

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import hr.foi.air2003.menzapp.core.ModulePresenter
import hr.foi.air2003.menzapp.core.services.UserService
import kotlinx.android.synthetic.main.dialog_password.*
import kotlinx.android.synthetic.main.login_main.*
import kotlinx.android.synthetic.main.login_main.txtPassword

class LoginActivity() : AppCompatActivity(), ModulePresenter {
    private lateinit var auth: FirebaseAuth
    private lateinit var viewModel: LoginViewModel

    override var userData: FirebaseUser? = null
    override var GoogleAccountData: GoogleSignInAccount? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_main)

        auth = FirebaseAuth.getInstance()
        viewModel = LoginViewModel()

        btnLogin.setOnClickListener {
            userLogin()
        }

        txtRegister.setOnClickListener {
            startActivity(Intent(this, RegistrationActivity::class.java))
        }

        txtForgotPassword.setOnClickListener {
            sendResetPass()
        }
    }

    override fun userLogin() {
        if(checkLoginInput()){
            auth.signInWithEmailAndPassword(txtUsername.text.toString(), txtPassword.text.toString())
                .addOnSuccessListener { authResult ->
                    val user = authResult.user
                    if(user != null){
                        if(user.isEmailVerified){
                            finish()
                        }else{
                            user.sendEmailVerification()
                            updateUI()
                            auth.signOut()
                        }
                    }
                }
                .addOnFailureListener {
                    updateUI()
                }
        }
    }

    override fun userLogOut() {
        auth = FirebaseAuth.getInstance()
        auth.signOut()
    }

    private fun checkLoginInput() : Boolean {
        if (txtUsername.text.toString().isEmpty()) {
            txtUsername.error = "Molimo unesite email"
            txtUsername.requestFocus()
            return false
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(txtUsername.text.toString()).matches()) {
            txtUsername.error = "Molimo unesite ispravan email"
            txtUsername.requestFocus()
            return false
        }

        if (txtPassword.text.toString().isEmpty()) {
            txtPassword.error = "Molimo unesite lozinku"
            txtPassword.requestFocus()
            return false
        }

        return true
    }

    private fun updateUI() {
        val builder = AlertDialog.Builder(this)

        builder.setTitle("Greška")
        builder.setMessage("Prijava nije uspjela! Provjerite unesene podatke ili potvrdite email adresu!")

        builder.setPositiveButton("U redu") { dialog, which ->
            dialog.dismiss()
        }

        val dialog = builder.create()
        dialog.show()
    }

    private fun sendResetPass() {
        val popupForgotPassword = Dialog(this)
        popupForgotPassword.setContentView(layoutInflater.inflate(R.layout.dialog_password, null))
        popupForgotPassword.show()
        popupForgotPassword.btn_sendEmail.setOnClickListener {
            if (popupForgotPassword.txt_sendToMail.text.toString().isNotEmpty() &&
                Patterns.EMAIL_ADDRESS.matcher(popupForgotPassword.txt_sendToMail.text.toString())
                    .matches()
            ) {
                auth.sendPasswordResetEmail(popupForgotPassword.txt_sendToMail.text.toString())
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            popupForgotPassword.hide()
                            Toast.makeText(
                                this,
                                "Email za reset lozinke je poslan!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            }
        }

        popupForgotPassword.btn_cancel.setOnClickListener {
            popupForgotPassword.hide()
        }
    }
}