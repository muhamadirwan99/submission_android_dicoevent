package com.dicoding.dicoevent.utils

import android.widget.EditText
import androidx.core.widget.doAfterTextChanged
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
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

fun EditText.textChangesAsFlow(): Flow<String> {
    return callbackFlow {
        val watcher = doAfterTextChanged { trySend(it.toString()) }
        awaitClose { removeTextChangedListener(watcher) }
    }
}