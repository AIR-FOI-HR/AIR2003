package hr.foi.air2003.menzapp.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import hr.foi.air2003.menzapp.R
import kotlinx.android.synthetic.main.fragment_home.view.*

class HomeFragment : Fragment() {
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        view.filterDateTime.setOnClickListener {
            var bottomFragment = BottomFilterFragment()
            bottomFragment.show(requireFragmentManager(), "Filtriranje objava")
        }

        view.btnNewPost.setOnClickListener{
            Toast.makeText(view.context, "Otvoriti popup prozor za novu objavu", Toast.LENGTH_SHORT).show()
        }

        return view
    }
}