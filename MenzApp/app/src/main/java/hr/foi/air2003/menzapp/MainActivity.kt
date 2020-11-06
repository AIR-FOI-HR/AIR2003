package hr.foi.air2003.menzapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import hr.foi.air2003.menzapp.fragments.*
import hr.foi.air2003.menzapp.login.LoginActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    // private lateinit var dbService: FirestoreService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        auth = FirebaseAuth.getInstance();
        // dbService = FirestoreService().getInstance();

        val homeFragment = HomeFragment()
        val profileFragment = ProfileFragment()
        val menuFragment = MenuFragment()
        val chatFragment = ChatFragment()
        val searchFragment = SearchFragment()

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
        // println(dbService.getAll("Test"))
    }

    private fun setCurrentFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit()
    }

    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        println(currentUser)
         if (currentUser === null) {
            // val launchIntent = packageManager.getLaunchIntentForPackage("hr.foi.air2003.menzapp.prijava")
            // startActivity(launchIntent)
             startActivity(Intent(this, LoginActivity::class.java))
             //finish()
         }
    }
}