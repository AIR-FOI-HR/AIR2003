package hr.foi.air2003.menzapp.activities

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import hr.foi.air2003.menzapp.R
import hr.foi.air2003.menzapp.assistants.SharedViewModel
import hr.foi.air2003.menzapp.core.model.User
import hr.foi.air2003.menzapp.core.services.MenuReciever
import hr.foi.air2003.menzapp.core.services.NotificationReceiver
import hr.foi.air2003.menzapp.core.services.UserService
import hr.foi.air2003.menzapp.ui.*
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {
    private val viewModel: SharedViewModel = SharedViewModel()
    private var currentUser: String? = null
    private lateinit var alarmManager: AlarmManager
    private var user = User()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setMenuService()
    }

    override fun onStart() {
        super.onStart()

        currentUser = UserService.getCurrentUser()
        requireUserData()

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

    override fun onResume() {
        super.onResume()
        requireUserData()
    }

    private fun requireUserData(){
        if(currentUser != null){
            val liveData = viewModel.getUser(currentUser!!)
            liveData.observe(this, {
                val data = it.data
                if (data != null) {
                    user = data
                    setCurrentFragment(getSelectedFragment())
                    if(user.notificationsOn){
                        setNotificationService()
                    }
                }
            })
        }
    }

    private fun setMenuService() {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = System.currentTimeMillis()
        calendar.set(Calendar.HOUR_OF_DAY, 8)
        calendar.set(Calendar.MINUTE, 0)
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

    private fun setNotificationService() {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)

        val repeatingTime = 15 * 60 * 1000.toLong()

        alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        val intent = Intent(applicationContext, NotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
                applicationContext,
                1,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
        )

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,calendar.timeInMillis, repeatingTime, pendingIntent)
    }

    private fun getSelectedFragment() : Fragment{
        return supportFragmentManager.findFragmentById(R.id.fragment_container) ?: HomeFragment()
    }

    fun setCurrentFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit()
    }

    fun getCurrentUser() : User{
        return user
    }
}