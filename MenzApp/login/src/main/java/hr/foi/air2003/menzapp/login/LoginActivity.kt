package hr.foi.air2003.menzapp.login

import android.app.Dialog
import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import hr.foi.air2003.menzapp.core.Repository
import hr.foi.air2003.menzapp.core.model.User
import kotlinx.android.synthetic.main.dialog_password.*
import kotlinx.android.synthetic.main.login_main.*
import kotlinx.android.synthetic.main.login_main.txtPassword

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private var repository = Repository()


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
                .requestIdToken(REQUEST_TOKEN)
                .requestEmail()
                .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        txtRegister.setOnClickListener {
            startActivity(Intent(this, RegistrationActivity::class.java))
        }
        txtForgotPassword.setOnClickListener {
            val popupForgotPassword = Dialog(this)
            popupForgotPassword.setContentView(layoutInflater.inflate(R.layout.dialog_password, null))
            popupForgotPassword.show()
            popupForgotPassword.btn_sendEmail.setOnClickListener {
                if (popupForgotPassword.txt_sendToMail.text.toString().isNotEmpty() &&
                        Patterns.EMAIL_ADDRESS.matcher(popupForgotPassword.txt_sendToMail.text.toString()).matches()) {
                    auth.sendPasswordResetEmail(popupForgotPassword.txt_sendToMail.text.toString())
                            .addOnCompleteListener(this) { task ->
                                if (task.isSuccessful) {
                                    popupForgotPassword.hide()
                                    Toast.makeText(this, "Email za reset lozinke je poslan!", Toast.LENGTH_SHORT).show()
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
                firebaseAuthWithGoogle(account)
            } catch (e: ApiException) {
                Log.w(LoginActivity.TAG, "Google sign in failed", e)
                updateUI(false)
            }
        }
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Log.d(LoginActivity.TAG, "signInWithCredential:success")
                        val userInDatabase = FirebaseFirestore.getInstance().collection("Users")
                                .document(auth.currentUser?.uid!!).get()
                                .addOnSuccessListener { document ->
                                    if (document.data === null) {
                                        Log.d(LoginActivity.TAG, "User not in db, creating entry...")
                                        val user = getUserInfo(account, auth.currentUser?.uid!!)
                                        repository.uploadImage(account.photoUrl!!)
                                                .addOnSuccessListener { url ->
                                                    user.profilePicture = url.toString()
                                                    repository.createUser(auth.currentUser?.uid!!, user)
                                                }
                                                .addOnFailureListener {
                                                    repository.createUser(auth.currentUser?.uid!!, user)
                                                }
                                    }
                                }
                                .addOnFailureListener { e -> Log.w(ContentValues.TAG, "Error getting document", e) }
                        super.onBackPressed()
                    } else { Log.w(LoginActivity.TAG, "signInWithCredential:failure", task.exception) }
                }
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
                        if(user != null){
                            if(user.isEmailVerified){
                                super.onBackPressed()
                            }else{
                                updateUI(false)
                                auth.signOut()
                            }
                        }
                    } else {
                        updateUI(false)
                    }
                }
    }

    private fun updateUI(success: Boolean) {
        val builder = AlertDialog.Builder(this)

        if(success) {
            builder.setTitle("Uspjeh")
            builder.setMessage("Uspješno ste se prijavili!")
        }else{
            builder.setTitle("Greška")
            builder.setMessage("Prijava nije uspjela! Provjerite unesene podatke ili potvrdite email adresu!")
        }

        builder.setPositiveButton("U redu") { dialog, which ->
            dialog.dismiss()
        }

        val dialog = builder.create()
        dialog.show()
    }

    private fun getUserInfo(user: GoogleSignInAccount, firebaseUserId: String): User {
        return User(
                userId = firebaseUserId,
                fullName = user.displayName!!,
                email = user.email!!
        )
    }

    companion object {
        private const val TAG = "LoginActivity"
        private const val RC_SIGN_IN = 10000
        private const val REQUEST_TOKEN = "211328840636-3ngg2a5uno5hofdbq6sbsqqb199arl1g.apps.googleusercontent.com"
    }
}