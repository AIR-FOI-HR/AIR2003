package hr.foi.air2003.menzapp.ui

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import hr.foi.air2003.menzapp.R
import hr.foi.air2003.menzapp.assistants.DateTimePicker
import kotlinx.android.synthetic.main.popup_filter.*

class BottomFilterFragment : BottomSheetDialogFragment() {
    private lateinit var dateTimePicker : DateTimePicker

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.popup_filter, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dateTimePicker = DateTimePicker()
        setCurrentDateTime()

        layoutDate.setOnClickListener {
            openDatePicker()
        }

        layoutTime.setOnClickListener {
            openTimePicker()
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        (targetFragment as HomeFragment).bindData(dateTimePicker.getTimestampString())
    }

    private fun setCurrentDateTime() {
        updateDate()
        updateTime()
    }

    private fun openTimePicker() {
        val timePicker = dateTimePicker.getTimePicker(requireContext())
        timePicker.show()
        timePicker.setOnDismissListener { updateTime() }
    }

    private fun updateTime() {
        tvSelectedTime.text = dateTimePicker.getTimeString()
    }

    private fun openDatePicker() {
        val datePicker = dateTimePicker.getDatePicker(requireContext())
        datePicker.datePicker.minDate = System.currentTimeMillis()
        datePicker.show()

        datePicker.setOnDismissListener { updateDate() }
    }

    private fun updateDate() {
        tvSelectedDate.text = dateTimePicker.getDateString()
    }
}