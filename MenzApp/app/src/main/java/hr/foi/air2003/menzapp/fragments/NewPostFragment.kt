package hr.foi.air2003.menzapp.fragments

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.graphics.Point
import android.graphics.Typeface
import android.os.Bundle
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AlertDialogLayout
import androidx.fragment.app.DialogFragment
import com.google.firebase.auth.FirebaseAuth
import hr.foi.air2003.menzapp.R
import hr.foi.air2003.menzapp.database.FirestoreService
import hr.foi.air2003.menzapp.database.model.Post
import kotlinx.android.synthetic.main.dialog_new_post.*
import kotlinx.android.synthetic.main.popup_filter.*
import java.lang.Exception
import java.sql.Timestamp
import java.util.*

class NewPostFragment : DialogFragment() {
    private val calendar = Calendar.getInstance()
    private lateinit var timeString: String
    private lateinit var dateString: String
    private val months = arrayOf("siječnja", "veljače", "ožujka", "travnja", "svibnja", "lipnja", "srpnja", "kolovoza", "rujna", "listopada", "studenoga", "prosinca")

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

        tvDate.setOnClickListener {
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                    requireContext(),
                    { view, year, monthOfYear, dayOfMonth ->
                        tvDate.text = "$dayOfMonth. ${months[monthOfYear]} $year."
                        dateString = "$year-${monthOfYear + 1}-$dayOfMonth"
                    },
                    year,
                    month,
                    day
            )

            datePickerDialog.datePicker.minDate = System.currentTimeMillis()
            datePickerDialog.show()
        }

        tvTime.setOnClickListener {
            val hourOfDay = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)

            TimePickerDialog(
                    requireContext(),
                    { view, hours, minutes ->
                        val selectedTime = "${String.format("%02d", hours)}:${String.format("%02d", minutes)}"
                        tvTime.text = selectedTime
                        timeString = "$selectedTime:00"
                    },
                    hourOfDay,
                    minute,
                    true
            ).show()
        }

        btn_saveNewPost.setOnClickListener {
            checkPostInput()
        }
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
            postId = "$currentUserId-$dateString-$timeString",
            authorId = currentUserId.toString(),
            timestamp = Timestamp.valueOf("$dateString $timeString"),
            description = description,
            numberOfPeople = numberOfPeople.toInt()
        )
        saveNewPost(post)
    }

    private fun saveNewPost(post: Post) {
        try {
            FirestoreService.instance.postDocumentWithID("Posts", post.postId, post)
            this.dismiss()
            notifyUser(true)
        } catch (e: Exception) {
            notifyUser(false)
        }
    }

    private fun notifyUser(success: Boolean) {
        val builder = AlertDialog.Builder(context)

        if (success) {
            // TODO Change style of alert dialog in themes.xml
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