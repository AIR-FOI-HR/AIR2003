package hr.foi.air2003.menzapp.ui

import android.graphics.Point
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.DialogFragment
import hr.foi.air2003.menzapp.R
import hr.foi.air2003.menzapp.assistants.SharedViewModel
import hr.foi.air2003.menzapp.core.model.Feedback
import hr.foi.air2003.menzapp.core.model.User
import kotlinx.android.synthetic.main.dialog_new_feedback.*

class NewFeedbackFragment : DialogFragment() {
    private lateinit var visitedUser: User
    private lateinit var currentUser: User
    private lateinit var feedback: Feedback
    private val viewModel = SharedViewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        visitedUser = (targetFragment as VisitedProfileFragment).getVisitedUser()
        currentUser = (targetFragment as VisitedProfileFragment).getCurrentUser()
        feedback = Feedback()
        return inflater.inflate(R.layout.dialog_new_feedback, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireFeedbackData()

        btnCancelFeedback.setOnClickListener {
            this.dismiss()
        }

        btnDeleteFeedback.setOnClickListener {
            viewModel.deleteFeedback(feedback)
                    .addOnSuccessListener { dismiss() }
        }

        btnSaveFeedback.setOnClickListener {
            checkFeedbackInput()
        }
    }

    override fun onStart() {
        super.onStart()

        val window = dialog?.window
        val size = Point()
        window?.windowManager?.defaultDisplay?.getRealSize(size)

        window?.setLayout(size.x, LinearLayout.LayoutParams.WRAP_CONTENT)
        window?.setGravity(Gravity.CENTER)
    }

    private fun requireFeedbackData() {
        val liveData = viewModel.getFeedbacks(visitedUser.userId)
        liveData.observe(viewLifecycleOwner, {
            val data = it.data
            if (!data.isNullOrEmpty()) {
                for (d in data) {
                    if (d.authorId == currentUser.userId) {
                        feedback = d
                        rbScore.rating = d.mark.toFloat()
                        tvFeedbackDescription.setText(d.feedback)
                        btnDeleteFeedback.visibility = View.VISIBLE
                    }
                }
            }
        })
    }

    private fun checkFeedbackInput() {
        val rating = rbScore.rating
        val description = tvFeedbackDescription.text.toString()

        if (description.isEmpty()) {
            tvFeedbackDescription.error = "Molimo unesite opis"
            tvFeedbackDescription.requestFocus()
            return
        }

        feedback.recipientId = visitedUser.userId
        feedback.authorId = currentUser.userId
        feedback.mark = rating.toInt()
        feedback.feedback = description

        if (feedback.feedbackId == "")
            saveFeedback(feedback)
        else
            editFeedback(feedback)
    }

    private fun editFeedback(feedback: Feedback) {
        viewModel.updateFeedback(feedback)
                .addOnSuccessListener { dismiss() }
    }

    private fun saveFeedback(feedback: Feedback) {
        viewModel.createFeedback(feedback)
        this.dismiss()
    }
}