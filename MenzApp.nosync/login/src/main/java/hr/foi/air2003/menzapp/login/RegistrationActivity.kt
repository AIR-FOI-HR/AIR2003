package hr.foi.air2003.menzapp.login

import android.os.Bundle
import android.util.Patterns
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import hr.foi.air2003.menzapp.database.FirestoreService
import hr.foi.air2003.menzapp.database.model.User
import hr.foi.air2003.menzapp.database.other.Collection
import kotlinx.android.synthetic.main.registration_main.*

class RegistrationActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.registration_main)
        auth = FirebaseAuth.getInstance()

        btnCreateAccount.setOnClickListener {
            checkRegistrationInput()
        }

        txtSignIn.setOnClickListener {
            finish()
        }
    }

    private fun checkRegistrationInput() {
        if (txtEmail.text.toString().isEmpty()) {
            txtEmail.error = "Molimo unesite email"
            txtEmail.requestFocus()
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(txtEmail.text.toString()).matches()) {
            txtEmail.error = "Molimo unesite ispravan email"
            txtEmail.requestFocus()
            return
        }

        if (txtPassword.text.toString().isEmpty()) {
            txtPassword.error = "Molimo unesite lozinku"
            txtPassword.requestFocus()
            return
        }

        createAccount()
    }

    private fun createAccount() {
        auth.createUserWithEmailAndPassword(txtEmail.text.toString(), txtPassword.text.toString())
            .addOnCompleteListener(
                this
            ) { task ->
                if (task.isSuccessful) {
                    FirestoreService.instance.postDocumentWithID(Collection.USER, auth.currentUser?.uid.toString(), getUserInfo())
                    this.sendVerificationEmail()
                    // TODO some kind of notification to user, splash screen or similar
                } else {
                    // TODO update notification to user in case of failure
                }
            }
    }

    private fun getUserInfo(): User {
        return User(
            userId = auth.currentUser?.uid.toString(),
            fullName = txtFullName.text.toString(),
            email = txtEmail.text.toString(),
            bio = "Ready to eat!",
            profilePicture = "",
            notificationsOn = true
        )
    }

    private fun sendVerificationEmail() {
        if (auth.currentUser !== null) {
            auth.currentUser!!.sendEmailVerification().addOnCompleteListener( this ) { task ->
                if (task.isSuccessful) {
                    finish();
                } else {
                    // Verification email not sent, display error message and suggest to retry registration
                }
            }
        } else {
            // What now?
        }
    }
}