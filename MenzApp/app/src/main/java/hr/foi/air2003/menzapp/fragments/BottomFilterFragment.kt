package hr.foi.air2003.menzapp.fragments

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import hr.foi.air2003.menzapp.R
import kotlinx.android.synthetic.main.popup_filter.*
import kotlinx.android.synthetic.main.popup_filter.view.*
import java.util.*

class BottomFilterFragment : BottomSheetDialogFragment() {
    private val calendar = Calendar.getInstance()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.popup_filter, container, false)

        // TODO Set current date and time

        /* TODO Send selected date and time to home fragment
        view.setOnClickListener {
            startActivity(Intent(requireContext(), HomeFragment::class.java))
            dismiss()
        }*/

        view.layoutDate.setOnClickListener {
            openDatePicker(view)
        }

        view.layoutTime.setOnClickListener {
            openTimePicker(view)
        }

        return view
    }

    private fun openTimePicker(view: View) {
        val hourOfDay = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(
                view.context,
                {view, hourOfDay, minute ->
                    val selectedTime = "$hourOfDay:$minute"
                    tvSelectedTime.text = selectedTime
                },
                hourOfDay,
                minute,
                true
        ).show()
    }

    private fun openDatePicker(view: View) {
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val months = arrayOf("siječnja", "veljače", "ožujka", "travnja", "svibnja", "lipnja", "srpnja", "kolovoza", "rujna", "listopada", "studonega", "prosinca")

        val datePickerDialog = DatePickerDialog(
                view.context,
                { view, year, monthOfYear, dayOfMonth ->
                    val selectedDate = "$dayOfMonth. ${months[monthOfYear+1]} $year."
                    tvSelectedDate.text = selectedDate },
                year,
                month,
                day
        )

        datePickerDialog.datePicker.minDate = System.currentTimeMillis()
        datePickerDialog.show()
    }
}