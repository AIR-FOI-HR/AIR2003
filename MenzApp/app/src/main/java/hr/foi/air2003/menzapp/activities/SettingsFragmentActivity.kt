package hr.foi.air2003.menzapp.activities

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.PopupWindow
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import hr.foi.air2003.menzapp.R
import hr.foi.air2003.menzapp.assistants.ImageConverter
import hr.foi.air2003.menzapp.assistants.SharedViewModel
import hr.foi.air2003.menzapp.core.model.User
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_settings.*
import kotlinx.android.synthetic.main.popup_menu_settings.view.*

const val REQUEST_EXIT = 0
const val REQUEST_FILE_CHOOSER = 1

class SettingsFragmentActivity : FragmentActivity() {
    private lateinit var user: User
    private lateinit var filePath: Uri
    private val viewModel = SharedViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_settings)
        user = Gson().fromJson(intent.getStringExtra("user"), User::class.java)
    }

    override fun onStart() {
        super.onStart()

        tvSettingsFullName.setText(user.fullName)
        tvSettingsBio.setText(user.bio)

        viewModel.getImage(user.profilePicture)
                .addOnSuccessListener { bytes ->
                    val bitmap = ImageConverter.convertBytesToBitmap(bytes)
                    val resized = ImageConverter.resizeBitmap(bitmap, ivSettingsProfilePhoto)
                    ivSettingsProfilePhoto.setImageBitmap(resized)
                }

        btnMore.setOnClickListener {
            showPopup(btnMore)
        }

        btnBackSettings.setOnClickListener {
            super.onBackPressed()
        }

        btnNewProfilePhoto.setOnClickListener {
            launchFileChooser()
        }

        btn_saveSettings.setOnClickListener {
            user.fullName = tvSettingsFullName.text.toString()
            user.bio = tvSettingsBio.text.toString()
            viewModel.uploadImage(filePath)
                .addOnSuccessListener {uri ->
                    user.profilePicture = uri.toString()
                    viewModel.updateUser(user)
                    finish()
                }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_FILE_CHOOSER && resultCode == Activity.RESULT_OK && data != null) {
            filePath = data.data!!

            val bitmap = ImageConverter.resizeBitmap(MediaStore.Images.Media.getBitmap(contentResolver, filePath), ivSettingsProfilePhoto)
            ivSettingsProfilePhoto.setImageBitmap(bitmap)
        }
    }

    private fun launchFileChooser() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Odaberi fotografiju"), REQUEST_FILE_CHOOSER)
    }

    private fun showPopup(view: View){
        val window = PopupWindow(this)
        val layout = layoutInflater.inflate(R.layout.popup_menu_settings, null)
        window.contentView = layout
        window.isOutsideTouchable = true
        window.showAsDropDown(view, 0, 30)

       window.contentView.btnToggleNotifications.isChecked = user.notificationsOn

        window.contentView.btnToggleNotifications.setOnClickListener {
            user.notificationsOn = window.contentView.btnToggleNotifications.isChecked
        }

        window.contentView.tvChangePassword.setOnClickListener {
            FirebaseAuth.getInstance().sendPasswordResetEmail(user.email)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // TODO Add some kind of popup to notify the mail is sent
                            Toast.makeText(this, "Email poslan!", Toast.LENGTH_SHORT).show()
                        }
                    }
        }

        window.contentView.tvLogOut.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            startActivityForResult(Intent(this, MainActivity::class.java), REQUEST_EXIT)
            finish()
        }
    }
}