package hr.foi.air2003.menzapp.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import hr.foi.air2003.menzapp.R
import kotlinx.android.synthetic.main.fragment_home.view.*
import java.sql.Timestamp

class HomeFragment : Fragment(), FragmentsCommunicator {
    private lateinit var timestamp: Timestamp
    override fun sendData(data: String) {
        // TODO Implement function to show new date and time
        timestamp = Timestamp.valueOf(data)
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        view.filterDateTime.setOnClickListener {
            var bottomFragment = BottomFilterFragment()
            bottomFragment.setTargetFragment(this, 1)
            bottomFragment.show(requireFragmentManager(), "Filter")
        }

        view.btnNewPost.setOnClickListener{
            Toast.makeText(it.context, "Otvoriti popup prozor za novu objavu", Toast.LENGTH_SHORT).show()
        }

        return view
    }
}