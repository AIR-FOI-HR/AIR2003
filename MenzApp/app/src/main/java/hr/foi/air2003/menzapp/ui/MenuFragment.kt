package hr.foi.air2003.menzapp.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.transition.Explode
import androidx.transition.TransitionManager
import com.google.firebase.Timestamp
import hr.foi.air2003.menzapp.R
import hr.foi.air2003.menzapp.assistants.DateTimePicker
import hr.foi.air2003.menzapp.assistants.SharedViewModel
import hr.foi.air2003.menzapp.core.model.Menu
import hr.foi.air2003.menzapp.recyclerview.FoodMenuRecyclerViewAdapter
import kotlinx.android.synthetic.main.fragment_menu.*

class MenuFragment : Fragment() {
    private val viewModel = SharedViewModel()
    private lateinit var dateTimePicker: DateTimePicker
    private lateinit var adapterLunch: FoodMenuRecyclerViewAdapter
    private lateinit var adapterDinner: FoodMenuRecyclerViewAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_menu, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dateTimePicker = DateTimePicker()

        expandViewListener()
        createRecyclerViews()
        retrieveMenus()
    }

    private fun createRecyclerViews() {
        adapterLunch = FoodMenuRecyclerViewAdapter()
        adapterDinner = FoodMenuRecyclerViewAdapter()

        rvLunchList.hasFixedSize()
        rvLunchList.layoutManager = LinearLayoutManager(context)
        rvLunchList.itemAnimator = DefaultItemAnimator()
        rvLunchList.adapter = adapterLunch

        rvDinnerList.hasFixedSize()
        rvDinnerList.layoutManager = LinearLayoutManager(context)
        rvDinnerList.itemAnimator = DefaultItemAnimator()
        rvDinnerList.adapter = adapterDinner
    }

    @SuppressLint("SetTextI18n")
    private fun retrieveMenus() {
        val currentTimestamp = dateTimePicker.dateToTimestamp()
        val dayBefore = Timestamp(currentTimestamp.seconds - 86400, 0)

        val liveData = viewModel.getMenus()
        liveData.observe(viewLifecycleOwner, {
            val data = it.data
            if (!data.isNullOrEmpty()) {
                val menus = data.sortedByDescending { menu -> menu.timestamp }
                for (menu in menus) {
                    if (menu.timestamp == currentTimestamp && menu.lunch.isNotEmpty()) {
                        populateMenus(menu)
                        break
                    } else if (menu.timestamp == dayBefore && menu.lunch.isNotEmpty()) {
                        populateMenus(menu)
                        break
                    } else {
                        populateMenus(menu)
                        break
                    }
                }
            }
        })
    }

    @SuppressLint("SetTextI18n")
    private fun populateMenus(menu: Menu) {
        tvLunch.text = "Ručak, ${menu.date}"
        tvDinner.text = "Večera, ${menu.date}"
        adapterLunch.addItems(menu.lunch)
        adapterDinner.addItems(menu.dinner)
    }

    private fun expandViewListener() {
        expandableLunchItems.visibility = View.VISIBLE
        expandableDinnerItems.visibility = View.GONE

        cvMenuLunch.setOnClickListener {
            if (expandableLunchItems.visibility == View.GONE) {
                TransitionManager.beginDelayedTransition(cvMenuLunch, Explode())
                expandableLunchItems.visibility = View.VISIBLE
            } else {
                TransitionManager.beginDelayedTransition(cvMenuLunch, Explode())
                expandableLunchItems.visibility = View.GONE
            }
        }

        cvMenuDinner.setOnClickListener {
            if (expandableDinnerItems.visibility == View.GONE) {
                TransitionManager.beginDelayedTransition(cvMenuDinner, Explode())
                expandableDinnerItems.visibility = View.VISIBLE
            } else {
                TransitionManager.beginDelayedTransition(cvMenuDinner, Explode())
                expandableDinnerItems.visibility = View.GONE
            }
        }
    }
}