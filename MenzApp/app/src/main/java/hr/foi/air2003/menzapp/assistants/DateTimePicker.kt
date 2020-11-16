package hr.foi.air2003.menzapp.assistants

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.*

private val calendar = Calendar.getInstance()

class DateTimePicker(
        private var day: Int = calendar.get(Calendar.DAY_OF_MONTH),
        private var month: Int = calendar.get(Calendar.MONTH),
        private var year: Int = calendar.get(Calendar.YEAR),
        private var hour: Int = calendar.get(Calendar.HOUR_OF_DAY),
        private var minute: Int = calendar.get(Calendar.MINUTE)
) {
    private val months = arrayOf("siječnja", "veljače", "ožujka", "travnja", "svibnja", "lipnja", "srpnja", "kolovoza", "rujna", "listopada", "studenoga", "prosinca")

    fun getDatePicker(context: Context): DatePickerDialog {
        return DatePickerDialog(
                context,
                { view, selectedYear, selectedMonth, selectedDay ->
                    day = selectedDay
                    month = selectedMonth
                    year = selectedYear
                },
                year,
                month,
                day
        )
    }

    fun getTimePicker(context: Context): TimePickerDialog {
        return TimePickerDialog(
                context,
                { view, selectedHour, selectedMinute ->
                    hour = selectedHour
                    minute = selectedMinute
                },
                hour,
                minute,
                true
        )
    }

    fun getDateString(): String {
        return "$day. ${months[month]} $year."
    }

    fun getTimeString(): String {
        return "${String.format("%02d", hour)}:${String.format("%02d", minute)}"
    }

    fun getTimestamp(): Timestamp {
        val timestamp = "$year-${month + 1}-$day ${String.format("%02d", hour)}:${String.format("%02d", minute)}"
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm").parse(timestamp)
        return Timestamp(sdf.time/1000, 0)
    }

    fun getTimestampString(): String {
        return "$year-${month + 1}-$day ${String.format("%02d", hour)}:${String.format("%02d", minute)}"
    }

    fun timestampToString(timestamp: Timestamp): String {
        calendar.timeInMillis = timestamp.seconds * 1000

        day = calendar.get(Calendar.DAY_OF_MONTH)
        month = calendar.get(Calendar.MONTH)
        year = calendar.get(Calendar.YEAR)
        hour = calendar.get(Calendar.HOUR_OF_DAY)
        minute = calendar.get(Calendar.MINUTE)

        return "${getDateString()}/${getTimeString()}"
    }
}