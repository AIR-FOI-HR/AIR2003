package hr.foi.air2003.menzapp.activities

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import hr.foi.air2003.menzapp.R
import hr.foi.air2003.menzapp.assistants.SharedViewModel
import hr.foi.air2003.menzapp.core.model.User
import hr.foi.air2003.menzapp.core.services.MenuReciever
import hr.foi.air2003.menzapp.core.services.MenuWorker
import hr.foi.air2003.menzapp.ui.*
import hr.foi.air2003.menzapp.login.LoginActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    private val viewModel: SharedViewModel = SharedViewModel()
    private var currentUser: FirebaseUser? = null
    private lateinit var user: User
    private lateinit var alarmManager: AlarmManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //setWorkRequest()
        setMenuService()
    }

    override fun onStart() {
        super.onStart()

        currentUser = FirebaseAuth.getInstance().currentUser
        if(currentUser === null) { userLogin() }

        requireUserData()

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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == REQUEST_EXIT && resultCode == RESULT_OK){
            startActivity(Intent(this, SplashScreenActivity::class.java))
            finish()
        }
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

    private fun setMenuService() {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = System.currentTimeMillis()
        calendar.set(Calendar.HOUR_OF_DAY, 16)
        calendar.set(Calendar.MINUTE, 36)
        calendar.set(Calendar.SECOND, 0)

        alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        val intent = Intent(applicationContext, MenuReciever::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            applicationContext,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
    }

    private fun setWorkRequest(){
        val workManager = WorkManager.getInstance(applicationContext)
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        val workRequest = PeriodicWorkRequest.Builder(MenuWorker::class.java, 2, TimeUnit.HOURS)
            .setConstraints(constraints)
            .build()

        workManager.enqueue(workRequest)
    }

    private fun loadUserData(user: User){
        this.user = user
        setCurrentFragment(getSelectedFragment())
    }

    private fun getSelectedFragment() : Fragment{
        return supportFragmentManager.findFragmentById(R.id.fragment_container) ?: HomeFragment()
    }

    fun setCurrentFragment(fragment: Fragment) {
        // Open fragment view inside of container
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit()
    }

    fun getCurrentUser() : User{
        return user
    }
}