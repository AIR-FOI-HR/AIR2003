package hr.foi.air2003.menzapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import hr.foi.air2003.menzapp.R
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        /*PROVJERITI
        btnNewPost.setOnClickListener{
            Toast.makeText(context, "Nova objava", Toast.LENGTH_LONG).show()
        }*/

        return inflater.inflate(R.layout.fragment_home, container, false)


    }
}