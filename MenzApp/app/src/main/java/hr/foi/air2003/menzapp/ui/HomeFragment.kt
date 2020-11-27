package hr.foi.air2003.menzapp.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.Timestamp
import com.google.gson.Gson
import hr.foi.air2003.menzapp.R
import hr.foi.air2003.menzapp.assistants.DateTimePicker
import hr.foi.air2003.menzapp.communicators.FragmentsCommunicator
import hr.foi.air2003.menzapp.core.model.Post
import hr.foi.air2003.menzapp.core.model.User
import hr.foi.air2003.menzapp.recyclerview.HomeRecyclerViewAdapter
import kotlinx.android.synthetic.main.fragment_home.*
import java.text.SimpleDateFormat

class HomeFragment : Fragment(), FragmentsCommunicator {
    private lateinit var dateTimePicker: DateTimePicker
    private lateinit var viewModel: HomeViewModel
    private lateinit var adapter: HomeRecyclerViewAdapter
    private lateinit var user: User

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        if(arguments != null)
            user = Gson().fromJson(arguments!!.getString("currentUser"), User::class.java)

        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        createRecyclerView()

        dateTimePicker = DateTimePicker()
        viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        val currentDateTime = Timestamp(System.currentTimeMillis() / 1000, 0)
        filterPosts(currentDateTime)

        filterDateTime.setOnClickListener {
            var bottomFragment = BottomFilterFragment()
            bottomFragment.setTargetFragment(this, 1)
            bottomFragment.show(requireFragmentManager(), "Filter")
        }

        btnNewPost.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("post", "")
            bundle.putString("user", arguments?.getString("currentUser"))

            var newPostFragment = NewPostFragment()
            newPostFragment.setTargetFragment(this, 1)
            newPostFragment.arguments = bundle
            newPostFragment.show(requireFragmentManager(), "New post")
        }
    }

    private fun createRecyclerView() {
        adapter = HomeRecyclerViewAdapter()

        rvPostsLayout.hasFixedSize()
        rvPostsLayout.layoutManager = LinearLayoutManager(context)
        rvPostsLayout.itemAnimator = DefaultItemAnimator()
        rvPostsLayout.adapter = adapter

        adapter.itemClick = { post ->
            requestToJoin(post)
        }
    }

    @SuppressLint("SimpleDateFormat")
    override fun bindData(data: Any) {
        updateFilter(data.toString())

        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm").parse(data.toString())
        filterPosts(Timestamp(sdf.time / 1000, 0))
    }

    private fun filterPosts(timestamp: Timestamp) {
        val liveData = viewModel.getAllPosts(user.userId)
        liveData.observe(viewLifecycleOwner, {
            val posts: MutableList<Post> = mutableListOf()
            val data = it.data
            if(data != null){
                for(d in data){
                    if(d.item.timestamp >= timestamp)
                        posts.add(d.item)
                }

                adapter.addItems(posts)
            }
        })
    }

    @SuppressLint("SetTextI18n")
    private fun updateFilter(data: String) {
        val dataSplit = data.split("-")
        tvSelectedDateTime?.text = "${dataSplit[2].substring(0, 2)}.${dataSplit[1]}.${dataSplit[0]}. ${dataSplit[2].substring(2)}"
    }

    private fun requestToJoin(post: Post) {
        val updatedUserRequests: MutableList<String> = mutableListOf()
        if (post.userRequests.isNotEmpty()) {
            post.userRequests.forEach { updatedUserRequests.add(it) }
        }

        updatedUserRequests.add(user.userId)
        post.userRequests = updatedUserRequests
        viewModel.updateUserRequests(post)

        // TODO implement listener on Post for author, when data on userRequests is changed, notify user
    }
}