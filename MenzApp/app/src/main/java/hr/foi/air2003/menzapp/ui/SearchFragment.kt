package hr.foi.air2003.menzapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import hr.foi.air2003.menzapp.R
import hr.foi.air2003.menzapp.activities.MainActivity
import hr.foi.air2003.menzapp.assistants.SharedViewModel
import hr.foi.air2003.menzapp.core.model.User
import hr.foi.air2003.menzapp.recyclerview.SearchUserRecyclerViewAdapter
import kotlinx.android.synthetic.main.fragment_search.*

class SearchFragment : Fragment() {
    private lateinit var adapterUser: SearchUserRecyclerViewAdapter
    private lateinit var user: User
    private lateinit var visitedUser: User
    private val viewModel = SharedViewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        user = (activity as MainActivity).getCurrentUser()
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        createRecyclerView()

        svSearchText.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                getUsers(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                getUsers(newText)
                return true
            }
        })
    }

    private fun getUsers(query: String?) {
        if (!query.isNullOrEmpty()) {
            val liveData = viewModel.searchUsers(query)
            liveData.observe(viewLifecycleOwner, {
                val data = it.data
                if (!data.isNullOrEmpty()) {
                    val users: MutableList<User> = data as MutableList<User>
                    users.remove(user)
                    adapterUser.addItems(users)
                }
            })
        }
    }

    private fun createRecyclerView() {
        adapterUser = SearchUserRecyclerViewAdapter()

        rvSearchResults.hasFixedSize()
        rvSearchResults.layoutManager = LinearLayoutManager(context)
        rvSearchResults.itemAnimator = DefaultItemAnimator()
        rvSearchResults.adapter = adapterUser

        adapterUser.userClick = { user ->
            val visitedProfileFragment = VisitedProfileFragment()
            visitedUser = user
            visitedProfileFragment.setTargetFragment(this, 2)
            (activity as MainActivity).setCurrentFragment(visitedProfileFragment)
        }
    }

    fun getVisitedUser(): User {
        return visitedUser
    }
}