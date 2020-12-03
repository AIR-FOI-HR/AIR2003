package hr.foi.air2003.menzapp.ui

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import hr.foi.air2003.menzapp.MainActivity
import hr.foi.air2003.menzapp.R
import hr.foi.air2003.menzapp.SplashScreenActivity
import hr.foi.air2003.menzapp.core.model.User
import kotlinx.android.synthetic.main.fragment_settings.*

class SettingsFragment : Fragment() {
    private lateinit var user: User
    private lateinit var filePath: Uri
    private lateinit var imgUri: Uri
    private val viewModel = SettingsViewModel()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        user = (activity as MainActivity).getCurrentUser()
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnMore.setOnClickListener {
            showPopup(btnMore)
        }

        btnBackSettings.setOnClickListener {
            (activity as MainActivity).setCurrentFragment(ProfileFragment())
        }

        btnNewProfilePhoto.setOnClickListener {
            launchFileChooser()
        }

        btn_saveSettings.setOnClickListener {
            user.bio = tvSettingsBio.text.toString()
            user.fullName = tvSettingsFullName.text.toString()
            user.profilePicture = filePath.toString()

            viewModel.updateUser(user)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == 1 && resultCode == Activity.RESULT_OK && data != null){
            filePath = data.data!!
            val bitmap = MediaStore.Images.Media.getBitmap(activity?.contentResolver, filePath)
            ivSettingsProfilePhoto.setImageBitmap(bitmap)

            viewModel.uploadImage(filePath).observe(viewLifecycleOwner, {
                val uri = it.data
                if(uri != null)
                    imgUri = uri
            })
        }
    }

    private fun launchFileChooser() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Odaberi fotografiju"), 1)
    }

    private fun showPopup(view: View) {
        val popup = PopupMenu(requireContext(), view)
        popup.inflate(R.menu.header_menu_settings)

        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.headerMenuCheckNotifications -> {
                    Toast.makeText(requireContext(), item.title, Toast.LENGTH_SHORT).show()
                }
                R.id.headerMenuPasswordChange -> {
                    val newPasswordFragment = NewPasswordFragment()
                    newPasswordFragment.show(requireFragmentManager(), "New password")
                }
                R.id.headerMenuLogOut -> {
                    FirebaseAuth.getInstance().signOut()
                    val intent = Intent(activity, SplashScreenActivity::class.java)
                    (activity as MainActivity).startActivity(intent)

                }
            }

            true
        }

        popup.show()
    }
}