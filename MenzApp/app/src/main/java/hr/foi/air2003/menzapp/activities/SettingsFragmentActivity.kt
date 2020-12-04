package hr.foi.air2003.menzapp.activities

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.FragmentActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import hr.foi.air2003.menzapp.R
import hr.foi.air2003.menzapp.core.model.User
import kotlinx.android.synthetic.main.fragment_settings.*
import kotlinx.android.synthetic.main.popup_filter.*

class SettingsFragmentActivity : FragmentActivity() {
    private lateinit var user: User
    private lateinit var filePath: Uri
    private val viewModel = SettingsViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_settings)
        user = Gson().fromJson(intent.getStringExtra("user"), User::class.java)
    }

    override fun onStart() {
        super.onStart()

        btnMore.setOnClickListener {
            showPopup(btnMore)
        }

        btnBackSettings.setOnClickListener {
            finish()
        }

        btnNewProfilePhoto.setOnClickListener {
            launchFileChooser()
        }

        btn_saveSettings.setOnClickListener {
            val task = viewModel.uploadImage(filePath)

            if(task.isSuccessful){
                user.fullName = tvSettingsFullName.text.toString()
                user.bio = tvSelectedDate.text.toString()
                user.profilePicture = task.result.toString()

                viewModel.updateUser(user)
            }else if(task.exception != null){
                // TODO Handle exception
                Toast.makeText(this, task.exception!!.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1 && resultCode == Activity.RESULT_OK && data != null) {
            filePath = data.data!!
            val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, filePath)
            ivSettingsProfilePhoto.setImageBitmap(bitmap)
        }
    }

    private fun launchFileChooser() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Odaberi fotografiju"), 1)
    }

    private fun showPopup(view: View) {
        val popup = PopupMenu(this, view)
        popup.inflate(R.menu.header_menu_settings)

        // TODO Set notification toggle base on user data

        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.headerMenuCheckNotifications -> {
                    // TODO Implement toggle button
                    item.isChecked = !item.isChecked
                    user.notificationsOn = item.isChecked
                }
                R.id.headerMenuPasswordChange -> {
                    FirebaseAuth.getInstance().sendPasswordResetEmail(user.email)
                        .addOnCompleteListener(this) { task ->
                            if (task.isSuccessful) {
                                // TODO Add some kind of popup to notify the mail is sent
                                Toast.makeText(this, "Email poslan!", Toast.LENGTH_SHORT).show()
                            }
                        }
                }
                R.id.headerMenuLogOut -> {
                    FirebaseAuth.getInstance().signOut()
                    startActivity(Intent(this, SplashScreenActivity::class.java))
                }
            }

            true
        }

        popup.show()
    }
}