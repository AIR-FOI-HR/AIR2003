package hr.foi.air2003.menzapp.core.services

import android.annotation.SuppressLint
import android.app.job.JobParameters
import android.app.job.JobService
import com.google.firebase.Timestamp
import hr.foi.air2003.menzapp.core.model.Menu
import hr.foi.air2003.menzapp.core.other.Collection
import org.jsoup.Jsoup
import java.text.SimpleDateFormat
import java.util.*

class MenuService : JobService() {
    override fun onStartJob(params: JobParameters?): Boolean {
        val cal = Calendar.getInstance()
        val currentDay = cal.get(Calendar.DAY_OF_MONTH)
        val settings = getSharedPreferences("PREFS", 0)
        val lastDay = settings.getInt("day", 0)

        if (currentDay != lastDay) {
            // Code runs once a day
            val editor = settings.edit()
            editor.putInt("day", currentDay)
            editor.apply()

            val thread = Thread { getMenuItems() }
            thread.start()
        }

        return false
    }

    @SuppressLint("SimpleDateFormat")
    private fun getMenuItems() {
        val url = "https://www.scvz.unizg.hr/jelovnik-varazdin/"
        val doc = Jsoup.connect(url).get()
        val date = doc.getElementsByClass("date").text()
        val timestamp = Timestamp(SimpleDateFormat("dd.MM.yyyy.").parse(date)!!.time / 1000, 0)
        val menus = doc.getElementsByClass("jelovnik-date-content")
        val lunchMenus = menus.eq(0).select("p")
        val dinnerMenus = menus.eq(1).select("p")
        val lunch: MutableList<String> = mutableListOf()
        val dinner: MutableList<String> = mutableListOf()

        for (i in 0 until lunchMenus.size) {
            var data = lunchMenus.eq(i).toString()
            data = data.substring(3, data.length - 4)
            val dataSplit = data.split("<br> ")

            var lunchItem = ""
            for (k in dataSplit.indices) {
                lunchItem += when (k) {
                    (dataSplit.size - 1) -> dataSplit[k]
                    0 -> "${dataSplit[k]}/"
                    else -> "${dataSplit[k]} • "
                }
            }

            lunch.add(lunchItem)
        }

        for (i in 0 until dinnerMenus.size) {
            var data = dinnerMenus.eq(i).toString()
            data = data.substring(3, data.length - 4)
            val dataSplit = data.split("<br> ")

            var dinnerItem = ""
            for (k in dataSplit.indices) {
                dinnerItem += when (k) {
                    (dataSplit.size - 1) -> dataSplit[k]
                    0 -> "${dataSplit[k]}/"
                    else -> "${dataSplit[k]} • "
                }
            }

            dinner.add(dinnerItem)
        }

        val model = Menu(
                date = date,
                lunch = lunch,
                dinner = dinner,
                timestamp = timestamp
        )

        FirestoreService.post(Collection.MENU, model)
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        return false
    }
}