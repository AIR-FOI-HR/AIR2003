package hr.foi.air2003.menzapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
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
        viewModel = MainViewModel()

        // Get current user
        currentUser = FirebaseAuth.getInstance().currentUser
        userLogin(currentUser)
        user = getUser()

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

    private fun setCurrentFragment(fragment: Fragment) {
        val bundle = Bundle()
        val json = Gson().toJson(user)
        bundle.putString("currentUser", json)
        fragment.arguments = bundle

        // Open fragment view inside of container
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit()
    }

    // TODO Maybe move this to Login Activity
    private fun getUser(): User{
        user = User()
        var user = User()
        if(currentUser != null){
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

        return user
    }

    private fun userLogin(currentUser: FirebaseUser?) {
        // Check if user is signed in (non-null) and update UI accordingly
        println(currentUser)
        if (currentUser === null) {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }
}