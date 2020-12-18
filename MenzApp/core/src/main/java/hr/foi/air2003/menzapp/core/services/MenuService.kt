package hr.foi.air2003.menzapp.core.services

import android.app.job.JobParameters
import android.app.job.JobService
import hr.foi.air2003.menzapp.core.model.Menu
import hr.foi.air2003.menzapp.core.other.Collection
import org.jsoup.Jsoup


class MenuService : JobService() {
    override fun onStartJob(params: JobParameters?): Boolean {
        val thread = Thread { getMenuItems() }
        thread.start()
        return false
    }

    private fun getMenuItems() {
        val url = "https://www.scvz.unizg.hr/jelovnik-varazdin/"
        val doc = Jsoup.connect(url).get()
        val date = doc.getElementsByClass("date").text()
        val menus = doc.getElementById("jelovnik-date-0").getElementsByClass("jelovnik-date-content")
        val lunchMenus = menus.eq(0).select("p")
        val dinnerMenus = menus.eq(1).select("p")
        val lunch: MutableList<String> = mutableListOf()
        val dinner: MutableList<String> = mutableListOf()

        for (i in 0 until lunchMenus.size) {
            val lunchItem = lunchMenus.eq(i).text()
            lunch.add(lunchItem)
        }

        for (i in 0 until dinnerMenus.size) {
            val dinnerItem = dinnerMenus.eq(i).text()
            dinner.add(dinnerItem)
        }

        val model = Menu(
            date = date,
            lunch = lunch,
            dinner = dinner,
        )

        FirestoreService.post(Collection.MENU, model)
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        return false
    }
}