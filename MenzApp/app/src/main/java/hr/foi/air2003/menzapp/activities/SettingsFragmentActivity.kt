package hr.foi.air2003.menzapp.activities

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.PopupWindow
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import hr.foi.air2003.menzapp.R
import hr.foi.air2003.menzapp.assistants.AlertDialogBuilder
import hr.foi.air2003.menzapp.assistants.ImageConverter
import hr.foi.air2003.menzapp.assistants.SharedViewModel
import hr.foi.air2003.menzapp.core.model.User
import kotlinx.android.synthetic.main.alert_dialog.*
import kotlinx.android.synthetic.main.fragment_settings.*
import kotlinx.android.synthetic.main.popup_menu_settings.view.*

const val REQUEST_FILE_CHOOSER = 1

class SettingsFragmentActivity : FragmentActivity() {
    private lateinit var user: User
    private lateinit var filePath: Uri
    private lateinit var builder: AlertDialog.Builder
    private val viewModel = SharedViewModel()
    private var alertDialogBuilder = AlertDialogBuilder()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_settings)
        user = Gson().fromJson(intent.getStringExtra("user"), User::class.java)
        builder = alertDialogBuilder.createAlertDialog(this, layoutInflater)
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
                            tvAlertMessage.text = getString(R.string.alert_email_sent)
                            val dialog = builder.create()
                            dialog.show()
                            tvOkButton.setOnClickListener {
                                dialog.dismiss()
                            }
                        }else{
                            tvAlertTitle.text = getString(R.string.alert_fail)
                            tvAlertMessage.text = getString(R.string.alert_fail_email_sent)
                            ivAlertIcon.background = ContextCompat.getDrawable(this, R.drawable.ic_warning)
                            val dialog = builder.create()
                            dialog.show()
                            tvOkButton.setOnClickListener {
                                dialog.dismiss()
                            }
                        }
                    }
        }

        window.contentView.tvLogOut.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            finishAffinity()
            startActivity(Intent(this, SplashScreenActivity::class.java))
        }
    }
}