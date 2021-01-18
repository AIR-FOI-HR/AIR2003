package hr.foi.air2003.menzapp.ui

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.Timestamp
import hr.foi.air2003.menzapp.activities.MainActivity
import hr.foi.air2003.menzapp.R
import hr.foi.air2003.menzapp.assistants.AlertDialogBuilder
import hr.foi.air2003.menzapp.assistants.DateTimePicker
import hr.foi.air2003.menzapp.assistants.SharedViewModel
import hr.foi.air2003.menzapp.core.model.Notification
import hr.foi.air2003.menzapp.core.model.Post
import hr.foi.air2003.menzapp.core.model.User
import hr.foi.air2003.menzapp.recyclerview.HomePostRecyclerViewAdapter
import kotlinx.android.synthetic.main.alert_dialog.*
import kotlinx.android.synthetic.main.alert_dialog.view.*
import kotlinx.android.synthetic.main.fragment_home.*
import java.lang.Exception
import java.text.SimpleDateFormat


class HomeFragment : Fragment() {
    private lateinit var dateTimePicker: DateTimePicker
    private lateinit var adapterPost: HomePostRecyclerViewAdapter
    private lateinit var user: User
    private lateinit var authorId: String
    private lateinit var builder: AlertDialog.Builder
    private lateinit var vd: View
    private var alertDialogBuilder = AlertDialogBuilder()
    private val viewModel = SharedViewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        user = (activity as MainActivity).getCurrentUser()
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        createRecyclerView()

        dateTimePicker = DateTimePicker()
        builder = alertDialogBuilder.createAlertDialog(requireContext(), layoutInflater)
        vd = alertDialogBuilder.getView()

        val currentDateTime = Timestamp(System.currentTimeMillis() / 1000, 0)
        filterPosts(currentDateTime)

        filterDateTime.setOnClickListener {
            val bottomFragment = BottomFilterFragment()
            bottomFragment.setTargetFragment(this, 1)
            bottomFragment.show(requireFragmentManager(), "Filter")
        }

        btnNewPost.setOnClickListener {
            val newPostFragment = NewPostFragment()
            newPostFragment.show(requireFragmentManager(), "New post")
        }
    }

    private fun createRecyclerView() {
        adapterPost = HomePostRecyclerViewAdapter()

        rvPostsLayout.hasFixedSize()
        rvPostsLayout.layoutManager = LinearLayoutManager(context)
        rvPostsLayout.itemAnimator = DefaultItemAnimator()
        rvPostsLayout.adapter = adapterPost

        adapterPost.respondClick = { post ->
            try {
                requestToJoin(post)

                vd.tvAlertMessage.text = getString(R.string.alert_success_join)
                val dialog = builder.create()
                dialog.show()
                vd.tvOkButton.setOnClickListener {
                    dialog.dismiss()
                }

            } catch (e: Exception) {
                vd.tvAlertTitle.text = getString(R.string.alert_fail)
                vd.tvAlertMessage.text = getString(R.string.alert_fail_join)
                vd.ivAlertIcon.background = ContextCompat.getDrawable(requireContext(), R.drawable.ic_warning)
                val dialog = builder.create()
                dialog.show()
                vd.tvOkButton.setOnClickListener {
                    dialog.dismiss()
                }
            }
        }

        adapterPost.authorClick = { post ->
            val visitedProfileFragment = VisitedProfileFragment()
            authorId = post.authorId
            visitedProfileFragment.setTargetFragment(this, 1)
            (activity as MainActivity).setCurrentFragment(visitedProfileFragment)
        }
    }

    @SuppressLint("SimpleDateFormat")
    fun bindData(data: Any) {
        updateFilter(data.toString())

        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm").parse(data.toString())
        filterPosts(Timestamp(sdf.time / 1000, 0))
    }

    private fun filterPosts(timestamp: Timestamp) {
        val liveData = viewModel.getAllPosts(user.userId)
        liveData.observe(viewLifecycleOwner, {
            val posts: MutableList<Post> = mutableListOf()
            val data = it.data
            if (!data.isNullOrEmpty()) {
                for (d in data) {
                    if (d.timestamp >= timestamp)
                        posts.add(d)
                }
                val sorted = posts.sortedByDescending { post -> post.timestamp }
                adapterPost.addItems(sorted)
            }
        })
    }

    @SuppressLint("SetTextI18n")
    private fun updateFilter(data: String) {
        val dataSplit = data.split("-")
        tvSelectedDateTime?.text = "${
            dataSplit[2].substring(0, 2)}.${dataSplit[1]}.${dataSplit[0]}. ${dataSplit[2].substring(2)}"
    }

    private fun requestToJoin(post: Post) {
        val updatedUserRequests: MutableList<Map<String, Any>> = mutableListOf()
        if (post.userRequests.isNotEmpty()) {
            updatedUserRequests.addAll(0, post.userRequests)
        }

        updatedUserRequests.add(mapOf(Pair("opened", true), Pair("userId", user.userId)))
        post.userRequests = updatedUserRequests
        viewModel.updateUserRequests(post)
        rvPostsLayout.adapter?.notifyDataSetChanged()

        val notification = Notification(
            authorId = user.userId,
            content = "Novi zahtjev",
            request = true,
            postId = post.postId,
            recipientsId = listOf(post.authorId),
            timestamp = Timestamp(System.currentTimeMillis() / 1000, 0),
        )

        viewModel.createNotificationRequest(notification)
    }

    fun getAuthorId(): String {
        return authorId
    }
}