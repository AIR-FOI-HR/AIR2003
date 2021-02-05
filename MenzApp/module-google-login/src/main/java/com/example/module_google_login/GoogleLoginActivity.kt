package hr.foi.air2003.menzapp.module_google_login

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import hr.foi.air2003.menzapp.core.ModulePresenter
import hr.foi.air2003.menzapp.core.model.User
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class GoogleLoginActivity : AppCompatActivity(), ModulePresenter {
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    companion object {
        private const val TAG = "GoogleLoginActivity"
        private const val RC_SIGN_IN = 10000
        private const val REQUEST_TOKEN = "211328840636-3ngg2a5uno5hofdbq6sbsqqb199arl1g.apps.googleusercontent.com"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_google)

        auth = FirebaseAuth.getInstance()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(REQUEST_TOKEN)
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)
        userLogin()
    }

    override fun userLogin() {
        signInWithGoogle()
    }

    override var userData: FirebaseUser? = null

    override var GoogleAccountData: GoogleSignInAccount? = null

    override fun userLogOut() {
        auth = FirebaseAuth.getInstance()
        auth.signOut()
    }

    override fun isUserLoggedIn(): Boolean {
        return super.isUserLoggedIn()
    }

    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, @Nullable data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            GoogleSignIn.getSignedInAccountFromIntent(data)
                .addOnSuccessListener { account ->
                    Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
                    firebaseAuthWithGoogle(account)
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Google sign in failed", e)
                }
        }
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount) = runBlocking<Unit> {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        auth.signInWithCredential(credential)
            .addOnSuccessListener { authResult ->
                userData = authResult.user
                GoogleAccountData = account
                launch {
                    isUserLoggedIn.send(true)
                }
                finish()
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "signInWithCredential:failure", e)
                finish()
            }
    }
}