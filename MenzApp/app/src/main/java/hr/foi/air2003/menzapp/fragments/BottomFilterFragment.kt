package hr.foi.air2003.menzapp.fragments

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import hr.foi.air2003.menzapp.R
import kotlinx.android.synthetic.main.popup_filter.*
import kotlinx.android.synthetic.main.popup_filter.view.*
import java.sql.Timestamp
import java.util.*

class BottomFilterFragment : BottomSheetDialogFragment() {
    private val calendar = Calendar.getInstance()
    private val months = arrayOf("siječnja", "veljače", "ožujka", "travnja", "svibnja", "lipnja", "srpnja", "kolovoza", "rujna", "listopada", "studenoga", "prosinca")
    private var dateString = ""
    private var timeString = ""

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.popup_filter, container, false)

        setCurrentDateTime(view)

        view.layoutDate.setOnClickListener {
            openDatePicker(view)
        }

        view.layoutTime.setOnClickListener {
            openTimePicker(view)
        }

        return view
    }

    private fun setCurrentDateTime(view: View) {
        val currentDay = calendar.get(Calendar.DAY_OF_MONTH)
        val currentMonth = months[calendar.get(Calendar.MONTH)]
        val currentYear = calendar.get(Calendar.YEAR)
        val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
        val currentMinute = calendar.get(Calendar.MINUTE)

        val currentDate = "$currentDay. $currentMonth $currentYear."
        val currentTime = "$currentHour:$currentMinute"

        view.tvSelectedDate.text = currentDate
        view.tvSelectedTime.text = currentTime

        dateString = "$currentYear-${calendar.get(Calendar.MONTH) + 1}-$currentDay "
        timeString = "$currentTime:00"
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)

        val dateTimeString = dateString + timeString
        val timestamp = Timestamp.valueOf(dateTimeString)

        // TODO Pass timestamp to home fragment
    }

    private fun openTimePicker(view: View) {
        val hourOfDay = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(
                view.context,
                {view, hourOfDay, minute ->
                    val selectedTime = "$hourOfDay:$minute"
                    tvSelectedTime.text = selectedTime
                    timeString = "$selectedTime:00"
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

        val datePickerDialog = DatePickerDialog(
                view.context,
                { view, year, monthOfYear, dayOfMonth ->
                    val selectedDate = "$dayOfMonth. ${months[monthOfYear]} $year."
                    tvSelectedDate.text = selectedDate
                    dateString = "$year-${monthOfYear + 1}-$dayOfMonth "
                },
                year,
                month,
                day
        )

        datePickerDialog.datePicker.minDate = System.currentTimeMillis()
        datePickerDialog.show()
    }
}