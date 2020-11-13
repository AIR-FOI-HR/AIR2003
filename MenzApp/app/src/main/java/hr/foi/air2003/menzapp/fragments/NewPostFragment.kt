package hr.foi.air2003.menzapp.fragments

import android.graphics.Point
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import hr.foi.air2003.menzapp.R
import hr.foi.air2003.menzapp.database.FirestoreService
import hr.foi.air2003.menzapp.database.model.Post
import kotlinx.android.synthetic.main.dialog_new_post.*
import kotlinx.android.synthetic.main.fragment_profile.*
import java.lang.Exception
import java.sql.Timestamp

class NewPostFragment : DialogFragment() {
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

        btn_saveNewPost.setOnClickListener {
            val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
            val post = Post(
                    authorId = currentUserId.toString(),
                    timestamp = Timestamp.valueOf(tvDateTime.text.toString()),
                    description = tvDescription.text.toString(),
                    numberOfPeople = tvNumberOfPeople.text as Int
            )
            saveNewPost(post)
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

    private fun saveNewPost(post: Post) {
        try {
            // TODO Show popup window for success
            FirestoreService.instance.post("Posts", post)
        } catch (e: Exception) {
            // TODO Show popup window for failure
            Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
        }
    }
}