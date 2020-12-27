package hr.foi.air2003.menzapp.ui

import android.app.AlertDialog
import android.graphics.Point
import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.Timestamp
import hr.foi.air2003.menzapp.activities.MainActivity
import hr.foi.air2003.menzapp.R
import hr.foi.air2003.menzapp.assistants.DateTimePicker
import hr.foi.air2003.menzapp.assistants.SharedViewModel
import hr.foi.air2003.menzapp.core.model.Notification
import hr.foi.air2003.menzapp.core.model.Post
import hr.foi.air2003.menzapp.core.model.User
import kotlinx.android.synthetic.main.dialog_new_post.*
import kotlinx.android.synthetic.main.dialog_new_post.tvProfilePostDescription
import kotlinx.android.synthetic.main.dialog_new_post.tvHomePostPeople
import kotlinx.android.synthetic.main.fragment_profile.*
import java.lang.Exception
import java.util.UUID.randomUUID

class NewPostFragment : DialogFragment() {
    private lateinit var dateTimePicker: DateTimePicker
    private lateinit var viewModel: SharedViewModel
    private lateinit var post: Post
    private lateinit var user: User

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
        setDialogLayout()

        dateTimePicker = DateTimePicker()
        viewModel = ViewModelProvider(this).get(SharedViewModel::class.java)

        if (post.postId != "") {
            textNewPost.text = "UREDI OBJAVU"
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

    private fun setDialogLayout() {
        val window = dialog?.window
        val size = Point()
        window?.windowManager?.defaultDisplay?.getRealSize(size)

        val width = (size.x * 0.90).toInt()
        val height = (size.y * 0.75).toInt()

        window?.setLayout(width, height)
        window?.setGravity(Gravity.CENTER)
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
            notifyUser(true)
        }catch (e: Exception){
            notifyUser(false)
        }
    }

    private fun saveNewPost(post: Post) {
        try {

            val uuid = randomUUID().toString()
            post.postId = uuid

            val users = getAllSubscribers(post.authorId)


            val notification = Notification(
                    authorId = post.authorId,
                    content = "Nova objava!",
                    request = false,
                    postId = post.postId,
                    timestamp = Timestamp(System.currentTimeMillis()/1000,0),
                    recipientsId = users
            )

            viewModel.createPost(post)
            viewModel.createNotification(notification)

            targetFragment?.rvProfilePosts?.adapter?.notifyDataSetChanged()
            notifyUser(true)

            this.dismiss()

        } catch (e: Exception) {
            notifyUser(false)
        }
    }

    private fun getAllSubscribers(authorId: String) : MutableList<String>{
        val users: MutableList<String> = mutableListOf()

        //TODO Fix getting all user subscribers
        val liveData = viewModel.getAllSubscribersByUser(authorId)
        liveData.observe(viewLifecycleOwner,{
            val data = it.data
            if(data != null){
                for(d in data){
                    users.add(d.userId)
                }
            }
        })
        return users
    }

    private fun notifyUser(success: Boolean) {
        val builder = AlertDialog.Builder(context)

        // TODO Create and show custom dialog
        if (success) {
            builder.setTitle("Uspjeh")
            builder.setMessage("Objava je uspješno kreirana!")
            builder.setPositiveButton("OK", null)
        } else {
            builder.setTitle("Greška")
            builder.setMessage("Objava nije kreirana!")
            builder.setPositiveButton("OK", null)
        }

        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
}