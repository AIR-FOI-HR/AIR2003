package hr.foi.air2003.menzapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import hr.foi.air2003.menzapp.R
import hr.foi.air2003.menzapp.activities.MainActivity
import hr.foi.air2003.menzapp.assistants.SharedViewModel
import hr.foi.air2003.menzapp.core.model.Notification
import hr.foi.air2003.menzapp.core.model.User
import hr.foi.air2003.menzapp.recyclerview.NotificationRecyclerViewAdapter
import kotlinx.android.synthetic.main.fragment_dialog_notifications.*
import kotlinx.android.synthetic.main.fragment_dialog_notifications.btnNotifications

class NotificationFragment : Fragment() {

    private lateinit var adapterNotification: NotificationRecyclerViewAdapter
    private lateinit var user: User
    private val viewModel = SharedViewModel()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        user = (activity as MainActivity).getCurrentUser()
        return inflater.inflate(R.layout.fragment_dialog_notifications, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        createRecyclerViews()

        retrieveUserData(user)

        btnNotifications.setOnClickListener {
            val profileFragment = ProfileFragment()
            profileFragment.setTargetFragment(this, 1)
            (activity as MainActivity).setCurrentFragment(profileFragment)
        }

    }

    private fun createRecyclerViews() {
        adapterNotification = NotificationRecyclerViewAdapter(this)

        rvNotifications.hasFixedSize()
        rvNotifications.layoutManager = LinearLayoutManager(context)
        rvNotifications.itemAnimator = DefaultItemAnimator()
        rvNotifications.adapter = adapterNotification
    }

    private fun retrieveUserData(user: User) {
        //Populate posts with data from firestore
        createNotificationLayout(user.userId)
    }

    private fun createNotificationLayout(userId: String) {
        val liveData = viewModel.getAllNotifications(userId)
        liveData.observe(viewLifecycleOwner, {
            val notifications: MutableList<Notification> = mutableListOf()
            val data = it.data
            if(data != null){
                for(d in data){
                    notifications.add(d)
                }

                adapterNotification.addItems(notifications)
            }
        })
    }

}