package com.dicoding.dicoevent.util

import java.text.SimpleDateFormat
import java.util.Locale

fun String.formatDateForDisplay(): String {
    if (this.isEmpty()) return ""

    return try {
        // Input string = "2026-02-02 14:30:00"
        val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
        val date = inputFormat.parse(this) ?: return this

        // Output string = "Senin, 02 Februari 2026"
        val outputFormat = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.forLanguageTag("id-ID"))
        outputFormat.format(date)
    } catch (e: Exception) {
        e.printStackTrace()
        this
    }
}