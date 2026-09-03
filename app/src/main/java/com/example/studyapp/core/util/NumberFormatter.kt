package com.example.studyapp.core.util

import java.text.NumberFormat
import java.util.Locale

object NumberFormatter {
    private val usFormatter = NumberFormat.getNumberInstance(Locale.US)

    fun formatWithCommas(value: Int): String {
        return usFormatter.format(value)
    }

    fun formatWithCommas(value: Long): String {
        return usFormatter.format(value)
    }

    fun formatDuration(seconds: Long): String {
        val mins = seconds / 60
        val secs = seconds % 60
        return "%02d:%02d".format(mins, secs)
    }
}
