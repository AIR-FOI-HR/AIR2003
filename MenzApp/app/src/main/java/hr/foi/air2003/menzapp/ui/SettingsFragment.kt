package hr.foi.air2003.menzapp.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
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
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_settings.*
import kotlinx.android.synthetic.main.fragment_visited_profile.*

class SettingsFragment : Fragment() {
    private lateinit var user: User

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
                    newPasswordFragment.show(requireFragmentManager(), "New post")
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