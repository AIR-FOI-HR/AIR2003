package hr.foi.air2003.menzapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.gson.Gson
import hr.foi.air2003.menzapp.core.model.User
import hr.foi.air2003.menzapp.ui.*
import hr.foi.air2003.menzapp.login.LoginActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private lateinit var viewModel: MainViewModel
    private var currentUser: FirebaseUser? = null
    private lateinit var user: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onStart() {
        super.onStart()
        viewModel = MainViewModel()
        user = User()

        // Get current user
        currentUser = FirebaseAuth.getInstance().currentUser
        if(currentUser === null)
            userLogin()
        else
            setUser()

        val homeFragment = HomeFragment()
        val profileFragment = ProfileFragment()
        val menuFragment = MenuFragment()
        val chatFragment = ChatFragment()
        val searchFragment = SearchFragment()

        // Set current fragment when navigation icon is selected
        setCurrentFragment(homeFragment)
        bottom_nav_bar.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.ic_user -> setCurrentFragment(profileFragment)
                R.id.ic_search -> setCurrentFragment(searchFragment)
                R.id.ic_food -> setCurrentFragment(menuFragment)
                R.id.ic_chat -> setCurrentFragment(chatFragment)
                R.id.ic_home -> setCurrentFragment(homeFragment)
            }
            true
        }
    }

    private fun userLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        setUser()
    }

    private fun setCurrentFragment(fragment: Fragment) {
        // Pass user data to fragments
        val bundle = Bundle()
        val json = Gson().toJson(user)
        bundle.putString("currentUser", json)
        fragment.arguments = bundle

        // Open fragment view inside of container
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit()
    }

    private fun setUser(){
        val liveData = viewModel.getUser(currentUser!!.uid)
        liveData.observe(this, {
            val data = it.data
            if(data != null) {
                user.userId = data.userId
                user.fullName = data.fullName
                user.bio = data.bio
                user.email = data.email
                user.profilePicture = data.profilePicture
                user.notificationsOn = data.notificationsOn
            }
        })
    }
}