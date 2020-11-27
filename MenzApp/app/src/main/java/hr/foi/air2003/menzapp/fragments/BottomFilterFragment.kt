package hr.foi.air2003.menzapp.fragments

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import hr.foi.air2003.menzapp.R
import hr.foi.air2003.menzapp.assistants.DateTimePicker
import hr.foi.air2003.menzapp.communicators.FragmentsCommunicator
import kotlinx.android.synthetic.main.popup_filter.*
import java.lang.ClassCastException
import java.text.SimpleDateFormat

class BottomFilterFragment : BottomSheetDialogFragment() {
    private lateinit var fragmentsCommunicator: FragmentsCommunicator
    private lateinit var dateTimePicker : DateTimePicker

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.popup_filter, container, false)
    }

    override fun onStart() {
        super.onStart()

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

        fragmentsCommunicator.sendData(dateTimePicker.getTimestampString())
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        try {
            fragmentsCommunicator = targetFragment as FragmentsCommunicator
        } catch (e: ClassCastException) {
            Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show()
        }
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