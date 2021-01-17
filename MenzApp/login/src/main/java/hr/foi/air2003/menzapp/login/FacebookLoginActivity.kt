package hr.foi.air2003.menzapp.login
//
//import android.app.Dialog
//import android.content.Intent
//import android.os.Bundle
//import android.util.Log
//import android.util.Patterns
//import android.widget.Toast
//import androidx.appcompat.app.AppCompatActivity
//import com.google.firebase.auth.FacebookAuthProvider
//import com.google.firebase.auth.FirebaseAuth
//import kotlinx.android.synthetic.main.dialog_password.*
//import kotlinx.android.synthetic.main.login_main.*
//
//class FacebookLoginActivity : AppCompatActivity() {
//    private lateinit var auth: FirebaseAuth
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.login_main)
//        auth = FirebaseAuth.getInstance()
//
//        callbackManager = CallbackManager.Factory.create()
//
//        binding.buttonFacebookLogin.setReadPermissions("email", "public_profile")
//        binding.buttonFacebookLogin.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
//            override fun onSuccess(loginResult: LoginResult) {
//                Log.d(TAG, "facebook:onSuccess:$loginResult")
//                handleFacebookAccessToken(loginResult.accessToken)
//            }
//
//            override fun onCancel() {
//                Log.d(TAG, "facebook:onCancel")
//                // ...
//            }
//
//            override fun onError(error: FacebookException) {
//                Log.d(TAG, "facebook:onError", error)
//                // ...
//            }
//        })
//
//        btnLogin.setOnClickListener {
//            checkLoginInput()
//        }
//
//        txtRegister.setOnClickListener {
//            startActivity(Intent(this, RegistrationActivity::class.java))
//        }
//
//        txtForgotPassword.setOnClickListener {
//            Toast.makeText(this, "Toast", Toast.LENGTH_LONG).show()
//
//            val popupForgotPassword = Dialog(this)
//            popupForgotPassword.setContentView(layoutInflater.inflate(R.layout.dialog_password, null))
//            popupForgotPassword.show()
//            popupForgotPassword.btn_sendEmail.setOnClickListener {
//                if (!popupForgotPassword.txt_sendToMail.text.toString().isNullOrEmpty() &&
//                    Patterns.EMAIL_ADDRESS.matcher(popupForgotPassword.txt_sendToMail.text.toString()).matches()) {
//                    auth.sendPasswordResetEmail(popupForgotPassword.txt_sendToMail.text.toString())
//                        .addOnCompleteListener(this) { task ->
//                            if (task.isSuccessful) {
//                                popupForgotPassword.hide()
//                                // TODO Add some kind of popup to notify the mail is sent
//                            }
//                        }
//                }
//            }
//            popupForgotPassword.btn_cancel.setOnClickListener {
//                popupForgotPassword.hide()
//            }
//        }
//
//    }
//
//
//    private fun handleFacebookAccessToken(token: AccessToken) {
//        Log.d(TAG, "handleFacebookAccessToken:$token")
//
//        val credential = FacebookAuthProvider.getCredential(token.token)
//        auth.signInWithCredential(credential)
//            .addOnCompleteListener(this) { task ->
//                if (task.isSuccessful) {
//                    // Sign in success, update UI with the signed-in user's information
//                    Log.d(TAG, "signInWithCredential:success")
//                    val user = auth.currentUser
//                } else {
//                    // If sign in fails, display a message to the user.
//                    Log.w(TAG, "signInWithCredential:failure", task.exception)
//                    Toast.makeText(baseContext, "Authentication failed.",
//                        Toast.LENGTH_SHORT).show()
//                }
//
//                // ...
//            }
//    }
//
//    public override fun onStart() {
//        super.onStart()
//        // Check if user is signed in (non-null) and update UI accordingly.
//        val currentUser = auth.currentUser
//    }
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//
//        // Pass the activity result back to the Facebook SDK
//        callbackManager.onActivityResult(requestCode, resultCode, data)
//    }
//
//}