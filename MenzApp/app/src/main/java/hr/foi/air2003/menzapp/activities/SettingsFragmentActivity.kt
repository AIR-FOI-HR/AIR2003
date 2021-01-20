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
import coil.api.load
import coil.size.Scale
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import hr.foi.air2003.menzapp.R
import hr.foi.air2003.menzapp.assistants.AlertDialogBuilder
import hr.foi.air2003.menzapp.assistants.ImageConverter
import hr.foi.air2003.menzapp.assistants.SharedViewModel
import hr.foi.air2003.menzapp.core.model.User
import kotlinx.android.synthetic.main.activity_settings.*
import kotlinx.android.synthetic.main.alert_dialog.view.*
import kotlinx.android.synthetic.main.popup_menu_settings.view.*

const val REQUEST_FILE_CHOOSER = 1

class SettingsFragmentActivity : FragmentActivity() {
    private lateinit var user: User
    private var filePath: Uri? = null
    private lateinit var builder: AlertDialog.Builder
    private lateinit var window: PopupWindow
    private val viewModel = SharedViewModel()
    private var alertDialogBuilder = AlertDialogBuilder()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        user = Gson().fromJson(intent.getStringExtra("user"), User::class.java)
        builder = alertDialogBuilder.createAlertDialog(this, layoutInflater)
        createPopup()
    }

    private fun createPopup() {
        window = PopupWindow(this)
        val layout = layoutInflater.inflate(R.layout.popup_menu_settings, null)
        window.contentView = layout
        window.isOutsideTouchable = true
    }

    override fun onStart() {
        super.onStart()

        tvSettingsFullName.setText(user.fullName)
        tvSettingsBio.setText(user.bio)

        viewModel.getImage(user.profilePicture)
                .addOnSuccessListener { url ->
                    ivSettingsProfilePhoto.load(url){
                        scale(Scale.FIT)
                    }
                }

        btnMore.setOnClickListener {
            showPopup(btnMore)
        }

        btnBackSettings.setOnClickListener {
            super.onBackPressed()
        }

        window.setOnDismissListener {
            btnMore.postDelayed({ btnMore.isEnabled = true }, 1000)
        }

        btnNewProfilePhoto.setOnClickListener {
            launchFileChooser()
        }

        btn_saveSettings.setOnClickListener {
            user.fullName = tvSettingsFullName.text.toString()
            user.bio = tvSettingsBio.text.toString()

            if(filePath != null){
                viewModel.uploadImage(filePath!!)
                        .addOnSuccessListener {uri ->
                            user.profilePicture = uri.toString()
                            viewModel.updateUser(user)
                            super.onBackPressed()
                        }
            }else{
                viewModel.updateUser(user)
                        .addOnSuccessListener {
                            super.onBackPressed()
                        }
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
        btnMore.isEnabled = false
        window.showAsDropDown(view, 0, 30)
        window.contentView.btnToggleNotifications.isChecked = user.notificationsOn

        window.contentView.btnToggleNotifications.setOnClickListener {
            user.notificationsOn = window.contentView.btnToggleNotifications.isChecked
        }

        window.contentView.tvChangePassword.setOnClickListener {
            FirebaseAuth.getInstance().sendPasswordResetEmail(user.email)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            notifyUser(true)
                        }else{
                            notifyUser(false)
                        }
                    }
        }

        window.contentView.tvLogOut.setOnClickListener {
            window.dismiss()
            FirebaseAuth.getInstance().signOut()
            finishAffinity()
            startActivity(Intent(this, SplashScreenActivity::class.java))
        }
    }

    private fun notifyUser(success: Boolean){
        val vd = alertDialogBuilder.getView()
        window.dismiss()
        if(success){
            vd.tvAlertMessage.text = getString(R.string.alert_email_sent)
            val dialog = builder.create()
            dialog.show()
            vd.tvOkButton.setOnClickListener {
                dialog.dismiss()
                this.removeDialog(R.layout.alert_dialog)
            }
        }else{
            vd.tvAlertTitle.text = getString(R.string.alert_fail)
            vd.tvAlertMessage.text = getString(R.string.alert_fail_email_sent)
            vd.ivAlertIcon.background = ContextCompat.getDrawable(this, R.drawable.ic_warning)
            val dialog = builder.create()
            dialog.show()
            vd.tvOkButton.setOnClickListener {
                dialog.dismiss()
            }
        }
    }
}