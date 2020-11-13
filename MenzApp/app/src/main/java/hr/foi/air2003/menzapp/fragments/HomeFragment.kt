package hr.foi.air2003.menzapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import hr.foi.air2003.menzapp.R
import hr.foi.air2003.menzapp.communicators.FragmentsCommunicator
import hr.foi.air2003.menzapp.database.FirestoreService
import kotlinx.android.synthetic.main.fragment_home.*
import java.sql.Timestamp

class HomeFragment : Fragment(), FragmentsCommunicator {
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onStart() {
        super.onStart()

        val bundle = arguments

        filterDateTime.setOnClickListener {
            var bottomFragment = BottomFilterFragment()
            bottomFragment.setTargetFragment(this, 1)
            bottomFragment.show(requireFragmentManager(), "Filter")
        }

        btnNewPost.setOnClickListener {
            var newPostFragment = NewPostFragment()
            newPostFragment.arguments = bundle
            newPostFragment.setTargetFragment(this, 1)
            newPostFragment.show(requireFragmentManager(), "New post")
        }
    }

    override fun sendData(data: String) {
        updateFilter(data)

        // TODO Implement filtering posts by selected date and time
        var timestamp = Timestamp.valueOf(data)
        filterPosts(timestamp)
    }

    private fun filterPosts(timestamp: Timestamp) {
        // TODO get posts filtered by timestamp from Firestore
    }

    private fun updateFilter(data: String) {
        var dataSplit = data.split("-")
        tvSelectedDateTime?.text = "${dataSplit[2].substring(0, 2)}.${dataSplit[1]}.${dataSplit[0]}. ${dataSplit[2].substring(2, 8)}"
    }
}