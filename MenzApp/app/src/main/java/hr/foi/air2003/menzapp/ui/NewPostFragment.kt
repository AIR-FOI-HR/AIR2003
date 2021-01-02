package hr.foi.air2003.menzapp.ui

import android.app.AlertDialog
import android.graphics.Point
import android.os.Bundle
import android.view.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.google.firebase.Timestamp
import hr.foi.air2003.menzapp.activities.MainActivity
import hr.foi.air2003.menzapp.R
import hr.foi.air2003.menzapp.assistants.AlertDialogBuilder
import hr.foi.air2003.menzapp.assistants.DateTimePicker
import hr.foi.air2003.menzapp.assistants.SharedViewModel
import hr.foi.air2003.menzapp.core.model.Feedback
import hr.foi.air2003.menzapp.core.model.Notification
import hr.foi.air2003.menzapp.core.model.Post
import hr.foi.air2003.menzapp.core.model.User
import kotlinx.android.synthetic.main.alert_dialog.*
import kotlinx.android.synthetic.main.dialog_new_post.*
import kotlinx.android.synthetic.main.dialog_new_post.tvProfilePostDescription
import kotlinx.android.synthetic.main.dialog_new_post.tvHomePostPeople
import kotlinx.android.synthetic.main.fragment_profile.*
import java.lang.Exception
import java.util.UUID.randomUUID

class NewPostFragment : DialogFragment() {
    private lateinit var dateTimePicker: DateTimePicker
    private lateinit var post: Post
    private lateinit var user: User
    private lateinit var builder: AlertDialog.Builder
    private var viewModel = SharedViewModel()
    private var alertDialogBuilder = AlertDialogBuilder()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        post = if(targetFragment != null)
            (targetFragment as ProfileFragment).getPost()
        else
            Post()

        user = (activity as MainActivity).getCurrentUser()
        return inflater.inflate(R.layout.dialog_new_post, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dateTimePicker = DateTimePicker()
        builder = alertDialogBuilder.createAlertDialog(requireContext(), layoutInflater)

        if (post.postId != "") {
            textNewPost.text = getString(R.string.edit_post)
            loadPost(post)
        }

        tvDate.setOnClickListener {
            openDatePicker()
        }

        tvTime.setOnClickListener {
            openTimePicker()
        }

        btn_saveNewPost.setOnClickListener {
            checkPostInput(post.postId)
        }

        btnCancelNewPost.setOnClickListener {
            this.dismiss()
        }
    }

    override fun onStart() {
        super.onStart()

        val window = dialog?.window
        val size = Point()
        window?.windowManager?.defaultDisplay?.getRealSize(size)

        val width = size.x
        val height = (size.y * 0.60).toInt()

        window?.setLayout(width, height)
        window?.setGravity(Gravity.CENTER)
    }

    private fun loadPost(post: Post) {
        val dateTime = dateTimePicker.timestampToString(post.timestamp).split("/")
        tvDate.text = dateTime[0]
        tvTime.text = dateTime[1]
        tvProfilePostDescription.setText(post.description)
        tvHomePostPeople.setText(post.numberOfPeople.toString())
    }

    private fun openTimePicker() {
        val timePicker = dateTimePicker.getTimePicker(requireContext())
        timePicker.show()
        timePicker.setOnDismissListener { updateTime() }
    }

    private fun updateTime() {
        tvTime.text = dateTimePicker.getTimeString()
    }

    private fun openDatePicker() {
        val datePicker = dateTimePicker.getDatePicker(requireContext())
        datePicker.datePicker.minDate = System.currentTimeMillis()
        datePicker.show()
        datePicker.setOnDismissListener { updateDate() }
    }

    private fun updateDate() {
        tvDate.text = dateTimePicker.getDateString()
    }

    private fun checkPostInput(postId: String?) {
        val date = tvDate.text.toString()
        val time = tvTime.text.toString()
        val numberOfPeople = tvHomePostPeople.text.toString()
        val description = tvProfilePostDescription.text.toString()

        if (date.isEmpty()) {
            tvDate.error = "Molimo odaberite datum"
            tvDate.requestFocus()
            return
        }

        if (time.isEmpty()) {
            tvTime.error = "Molimo odaberite vrijeme"
            tvTime.requestFocus()
            return
        }

        if (numberOfPeople.isEmpty()) {
            tvHomePostPeople.error = "Molimo unesite broj osoba"
            tvHomePostPeople.requestFocus()
            return
        }

        if (description.isEmpty()) {
            tvProfilePostDescription.error = "Molimo unesite opis"
            tvProfilePostDescription.requestFocus()
            return
        }

        val post = Post(
                authorId = user.userId,
                timestamp = dateTimePicker.getTimestamp(),
                description = description,
                numberOfPeople = numberOfPeople.toInt()
        )

        if(postId.isNullOrEmpty()){
            saveNewPost(post)
        }else{
            post.postId = postId
            editPost(post)
        }
    }

    private fun editPost(post: Post) {
        try {
            viewModel.updatePost(post)
            targetFragment?.rvProfilePosts?.adapter?.notifyDataSetChanged()
            this.dismiss()

            tvAlertMessage.text = getString(R.string.alert_edit_post)
            val dialog = builder.create()
            dialog.show()
            tvOkButton.setOnClickListener {
                dialog.dismiss()
            }

        }catch (e: Exception){
            tvAlertTitle.text = getString(R.string.alert_fail)
            tvAlertMessage.text = getString(R.string.alert_fail_edit_post)
            ivAlertIcon.background = ContextCompat.getDrawable(requireContext(), R.drawable.ic_warning)
            val dialog = builder.create()
            dialog.show()
            tvOkButton.setOnClickListener {
                dialog.dismiss()
            }
        }
    }

    private fun saveNewPost(post: Post) {
        try {
            val uuid = randomUUID().toString()
            post.postId = uuid
            viewModel.createPost(post)
            createPostNotification(post)
            targetFragment?.rvProfilePosts?.adapter?.notifyDataSetChanged()
            this.dismiss()

            tvAlertMessage.text = getString(R.string.alert_new_post)
            val dialog = builder.create()
            dialog.show()
            tvOkButton.setOnClickListener {
                dialog.dismiss()
            }

        } catch (e: Exception) {
            tvAlertTitle.text = getString(R.string.alert_fail)
            tvAlertMessage.text = getString(R.string.alert_fail_new_post)
            ivAlertIcon.background = ContextCompat.getDrawable(requireContext(), R.drawable.ic_warning)
            val dialog = builder.create()
            dialog.show()
            tvOkButton.setOnClickListener {
                dialog.dismiss()
            }
        }
    }

    private fun createPostNotification(post: Post) {
        val users: MutableList<String> = mutableListOf()

        val liveData = viewModel.getAllSubscribersByUser(post.authorId)
        liveData.observe(viewLifecycleOwner,{
            val data = it.data
            if(data != null){
                for(d in data){
                    users.add(d.userId)
                }

                val notification = Notification(
                        authorId = post.authorId,
                        content = "Nova objava!",
                        request = false,
                        postId = post.postId,
                        timestamp = Timestamp(System.currentTimeMillis()/1000,0),
                        recipientsId = users
                )

                viewModel.createNotification(notification)
            }
        })
    }
}