package com.dicoding.dicoevent.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DateUtils {

    data class EventDate(
        val timeMain: String?, val timeSub: String?
    )

    fun formatEventDate(beginTime: String?, endTime: String?): EventDate {
        if (beginTime == null && endTime == null) {
            return EventDate("-", "-")
        }

        // 1. Definisikan Locale Indonesia agar nama bulan jadi "Mei", "Agustus", dsb.
        val idLocale = Locale.forLanguageTag("id-ID")

        // 2. Format Input dari API (yyyy-MM-dd HH:mm:ss)
        val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        // Opsional: Set timezone ke GMT/UTC jika API mengembalikan UTC,

        try {
            if (beginTime != null && endTime != null) {
                val dateBegin: Date = inputFormat.parse(beginTime) ?: return EventDate("-", "-")
                val dateEnd: Date = inputFormat.parse(endTime) ?: return EventDate("-", "-")

                // 3. Siapkan Formatter untuk Output
                val dayFormat = SimpleDateFormat("d", idLocale)         // "17"
                val monthYearFormat = SimpleDateFormat("MMMM yyyy", idLocale) // "Mei 2024"
                val fullDateFormat = SimpleDateFormat("d MMMM yyyy", idLocale) // "17 Mei 2024"
                val timeFormat = SimpleDateFormat("HH:mm", idLocale)    // "16:00"

                // 4. Logic timeSub (Jam) -> "16:00 - 17:00 WIB"
                val timeSub = "${timeFormat.format(dateBegin)} - ${timeFormat.format(dateEnd)} WIB"

                // 5. Logic timeMain (Tanggal)
                val startDay = dayFormat.format(dateBegin)
                val endDay = dayFormat.format(dateEnd)
                val startMonthYear = monthYearFormat.format(dateBegin)
                val endMonthYear = monthYearFormat.format(dateEnd)

                val timeMain = if (startMonthYear == endMonthYear) {
                    if (startDay == endDay) {
                        // Skenario 1: Hari sama (17 Mei 2024)
                        fullDateFormat.format(dateBegin)
                    } else {
                        // Skenario 2: Beda hari, bulan sama (18 - 19 Mei 2024)
                        "$startDay - $endDay $startMonthYear"
                    }
                } else {
                    // Skenario 3: Beda bulan (30 April - 02 Mei 2024)
                    "${fullDateFormat.format(dateBegin)} - ${fullDateFormat.format(dateEnd)}"
                }

                return EventDate(timeMain, timeSub)
            }

            return EventDate("-", "-")
        } catch (e: Exception) {
            e.printStackTrace()
            return EventDate(beginTime, endTime) // Fallback jika error parsing
        }
    }
}