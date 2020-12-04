package hr.foi.air2003.menzapp.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import hr.foi.air2003.menzapp.R
import hr.foi.air2003.menzapp.core.model.User
import hr.foi.air2003.menzapp.ui.*
import hr.foi.air2003.menzapp.login.LoginActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private var viewModel: MainViewModel = MainViewModel()
    private var currentUser: FirebaseUser? = null
    private lateinit var user: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        currentUser = FirebaseAuth.getInstance().currentUser
        if(currentUser === null) { userLogin() }

        requireUserData()
    }

    override fun onStart() {
        super.onStart()

        // Set current fragment when navigation icon is selected
        bottom_nav_bar.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.ic_user -> setCurrentFragment(ProfileFragment())
                R.id.ic_search -> setCurrentFragment(SearchFragment())
                R.id.ic_food -> setCurrentFragment(MenuFragment())
                R.id.ic_chat -> setCurrentFragment(ChatFragment())
                R.id.ic_home -> setCurrentFragment(HomeFragment())
            }
            true
        }
    }

    override fun onRestart() {
        super.onRestart()
        requireUserData()
    }

    private fun userLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
    }

    private fun requireUserData(){
        if(currentUser != null){
            val liveData = viewModel.getUser(currentUser!!.uid)
            liveData.observe(this, {
                val data = it.data
                if(data != null)
                    loadUserData(data)
            })
        }
    }

    private fun loadUserData(user: User){
        this.user = user
        setCurrentFragment(getSelectedFragment())
    }

    private fun getSelectedFragment() : Fragment{
        return when (bottom_nav_bar.selectedItemId) {
            R.id.ic_user -> ProfileFragment()
            R.id.ic_search -> SearchFragment()
            R.id.ic_food -> MenuFragment()
            R.id.ic_chat -> ChatFragment()
            R.id.ic_home -> HomeFragment()
            else -> HomeFragment()
        }
    }

    fun setCurrentFragment(fragment: Fragment) {
        // Open fragment view inside of container
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit()
    }

    fun getCurrentUser() : User{
        return user
    }
}