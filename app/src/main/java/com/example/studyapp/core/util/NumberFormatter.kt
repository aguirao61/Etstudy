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
}
