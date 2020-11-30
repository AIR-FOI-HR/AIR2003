package hr.foi.air2003.menzapp.ui

import android.graphics.Point
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import hr.foi.air2003.menzapp.MainActivity
import hr.foi.air2003.menzapp.R
import hr.foi.air2003.menzapp.assistants.DateTimePicker
import hr.foi.air2003.menzapp.core.model.Post
import hr.foi.air2003.menzapp.core.model.User
import kotlinx.android.synthetic.main.dialog_new_password.*
import kotlinx.android.synthetic.main.dialog_new_post.*

class NewPasswordFragment : DialogFragment() {

    private lateinit var user: User

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        user = (activity as MainActivity).getCurrentUser()
        return inflater.inflate(R.layout.dialog_new_password, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setDialogLayout()

        btnCancelNewPassword.setOnClickListener {
            this.dismiss()
        }
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
}