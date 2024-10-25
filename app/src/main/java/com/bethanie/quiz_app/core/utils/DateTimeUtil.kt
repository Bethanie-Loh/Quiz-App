package com.bethanie.quiz_app.core.utils

import android.content.Context
import android.util.Log
import com.bethanie.quiz_app.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DateTimeUtil {
    fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        return dateFormat.format(Date())
    }

    fun getCurrentTime(): String {
        val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        return timeFormat.format(Date())
    }

    fun formatTime(context: Context, totalTimeInSecs: Int): String {
        val mins = totalTimeInSecs / 60
        val secs = totalTimeInSecs % 60

        Log.d("debugging", "DateTimeUtil -> totalTimeInSecs: $totalTimeInSecs")
        return if (mins != 0 && secs != 0) {
            context.getString(R.string.formattedTimeWithMinsSecs, mins, secs)
        } else if (mins != 0) {
            context.getString(R.string.formattedTimeWithMinsOnly, mins)
        } else {
            context.getString(R.string.formattedTimeWithSecsOnly, secs)
        }
    }
}