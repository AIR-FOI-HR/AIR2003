package hr.foi.air2003.menzapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import hr.foi.air2003.menzapp.database.FirestoreService
import hr.foi.air2003.menzapp.fragments.*
import hr.foi.air2003.menzapp.login.LoginActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // FirestoreService.instance.getAll("Users")

        // Get current user
        auth = FirebaseAuth.getInstance();
        val currentUser = auth.currentUser
        userLogin(currentUser)

        val homeFragment = HomeFragment()
        val profileFragment = ProfileFragment()
        val menuFragment = MenuFragment()
        val chatFragment = ChatFragment()
        val searchFragment = SearchFragment()

        // Set current fragment when navigation icon is selected
        setCurrentFragment(homeFragment)
        bottom_nav_bar.setOnNavigationItemSelectedListener {
            when(it.itemId){
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
        // Pass user data to selected fragment
        //val bundle = Bundle()
        //bundle.putString("userId", auth.uid)
        //fragment.arguments = bundle

        // Open fragment view inside of container
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit()
    }

    private fun userLogin(currentUser : FirebaseUser?){
        // Check if user is signed in (non-null) and update UI accordingly
        println(currentUser)
        if (currentUser === null) {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }
}