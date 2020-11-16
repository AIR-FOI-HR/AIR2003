
package hr.foi.air2003.menzapp.fragments

import android.app.AlertDialog
import android.content.ContentValues
import android.graphics.Point
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import hr.foi.air2003.menzapp.R
import hr.foi.air2003.menzapp.assistants.DateTimePicker
import hr.foi.air2003.menzapp.database.FirestoreService
import hr.foi.air2003.menzapp.database.model.Post
import kotlinx.android.synthetic.main.dialog_new_post.*
import kotlinx.android.synthetic.main.dialog_new_post.tvDescription
import kotlinx.android.synthetic.main.dialog_new_post.tvNumberOfPeople
import kotlinx.android.synthetic.main.post.*
import java.lang.Exception

class NewPostFragment : DialogFragment() {
    private lateinit var dateTimePicker: DateTimePicker
    private var postId: String? = ""

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_new_post, container, false)
    }

    override fun onStart() {
        super.onStart()
        setDialogLayout()
        dateTimePicker = DateTimePicker()

        postId = arguments?.getString("post")

        if (postId != "") {
            textNewPost.text = "UREDI OBJAVU"
            loadPost(postId)
        }

        tvDate.setOnClickListener {
            openDatePicker()
        }

        tvTime.setOnClickListener {
            openTimePicker()
        }

        btn_saveNewPost.setOnClickListener {
            checkPostInput()
        }
    }

    private fun loadPost(postId: String?) {
        if (!postId!!.isNullOrEmpty()) {
            FirestoreService.instance.getDocumentByID(FirestoreService.Collection.POST, postId)
                    .addOnSuccessListener { document ->
                        val json = Gson().toJson(document.data)
                        val post = Gson().fromJson(json, Post::class.java)

                        val dateTime = dateTimePicker.timestampToString(post.timestamp).split("/")
                        tvDate.text = dateTime[0]
                        tvTime.text = dateTime[1]
                        tvDescription.setText(post.description)
                        tvNumberOfPeople.setText(post.numberOfPeople.toString())
                    }
                    .addOnFailureListener { e -> Log.w(ContentValues.TAG, "Error retrieving document", e) }
        }
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

        var width = (size.x * 0.90).toInt()
        var height = (size.y * 0.75).toInt()

        window?.setLayout(width, height)
        window?.setGravity(Gravity.CENTER)
    }

    private fun checkPostInput() {
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

        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
        val post = Post(
                authorId = currentUserId.toString(),
                timestamp = dateTimePicker.getTimestamp(),
                description = description,
                numberOfPeople = numberOfPeople.toInt()
        )

        if(postId.isNullOrEmpty()){
            saveNewPost(post)
        }else{
            post.postId = arguments?.getString("post").toString()
            editPost(post)
        }
    }

    private fun editPost(post: Post) {
        val json = Gson().toJson(post)
        val map = Gson().fromJson(json, HashMap<String, Any>()::class.java)

        try {
            FirestoreService.instance.update(FirestoreService.Collection.POST, post.postId, map)
            this.dismiss()
            notifyUser(true)
        }catch (e: Exception){
            notifyUser(false)
        }
    }

    private fun saveNewPost(post: Post) {
        try {
            FirestoreService.instance.post(FirestoreService.Collection.POST, post)
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