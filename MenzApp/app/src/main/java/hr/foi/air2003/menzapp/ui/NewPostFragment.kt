package hr.foi.air2003.menzapp.ui

import android.app.AlertDialog
import android.graphics.Point
import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import hr.foi.air2003.menzapp.R
import hr.foi.air2003.menzapp.assistants.DateTimePicker
import hr.foi.air2003.menzapp.core.model.Post
import hr.foi.air2003.menzapp.core.model.User
import kotlinx.android.synthetic.main.dialog_new_post.*
import kotlinx.android.synthetic.main.dialog_new_post.tvDescription
import kotlinx.android.synthetic.main.dialog_new_post.tvNumberOfPeople
import kotlinx.android.synthetic.main.fragment_home.*
import java.lang.Exception

class NewPostFragment : DialogFragment() {
    private lateinit var dateTimePicker: DateTimePicker
    private lateinit var viewModel: NewPostViewModel
    private lateinit var post: Post
    private lateinit var user: User

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_new_post, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setDialogLayout()

        dateTimePicker = DateTimePicker()
        viewModel = ViewModelProvider(this).get(NewPostViewModel::class.java)

        if(arguments != null){
            if(arguments!!.getString("post") != "")
                post = Gson().fromJson(arguments!!.getString("post"), Post::class.java)

            if(!arguments!!.getString("currentUser").isNullOrEmpty())
                user = Gson().fromJson(arguments!!.getString("currentUser"), User::class.java)
        }

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
    }

    private fun loadPost(post: Post) {
        val dateTime = dateTimePicker.timestampToString(post.timestamp).split("/")
        tvDate.text = dateTime[0]
        tvTime.text = dateTime[1]
        tvDescription.setText(post.description)
        tvNumberOfPeople.setText(post.numberOfPeople.toString())
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
        val display = window?.windowManager?.defaultDisplay?.getRealSize(size)

        val width = (size.x * 0.90).toInt()
        val height = (size.y * 0.75).toInt()

        window?.setLayout(width, height)
        window?.setGravity(Gravity.CENTER)
    }

    private fun checkPostInput(postId: String?) {
        val date = tvDate.text.toString()
        val time = tvTime.text.toString()
        val numberOfPeople = tvNumberOfPeople.text.toString()
        val description = tvDescription.text.toString()

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
            tvNumberOfPeople.error = "Molimo unesite broj osoba"
            tvNumberOfPeople.requestFocus()
            return
        }

        if (description.isEmpty()) {
            tvDescription.error = "Molimo unesite opis"
            tvDescription.requestFocus()
            return
        }

        val post = Post(
                author = mapOf(Pair("authorId", user.userId), Pair("fullName", user.fullName)),
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
            rvPostsLayout.adapter?.notifyDataSetChanged()
            this.dismiss()
            notifyUser(true)
        }catch (e: Exception){
            notifyUser(false)
        }
    }

    private fun saveNewPost(post: Post) {
        try {
            viewModel.createPost(post)
            rvPostsLayout.adapter?.notifyDataSetChanged()
            this.dismiss()
            notifyUser(true)
        } catch (e: Exception) {
            notifyUser(false)
        }
    }

    private fun notifyUser(success: Boolean) {
        val builder = AlertDialog.Builder(context)

        // TODO Show custom dialog
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