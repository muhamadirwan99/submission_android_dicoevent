package com.dicoding.dicoevent.utils

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.net.toUri

// Extension function pada Context
// Artinya: Semua Activity dan Fragment (via requireContext) otomatis punya fungsi ini.
fun Context.openUrl(url: String?) {
    try {
        if (url.isNullOrEmpty()) {
            Toast.makeText(this, "Link tidak valid", Toast.LENGTH_SHORT).show()
            return
        }

        // 1. Cek apakah ada prefix http/https, jika tidak ada, tambahkan (untuk safety)
        val finalUrl = if (!url.startsWith("http://") && !url.startsWith("https://")) {
            "https://$url"
        } else {
            url
        }

        // 2. Buat Intent ACTION_VIEW
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = finalUrl.toUri()

        // 3. Jalankan
        startActivity(intent)

    } catch (e: Exception) {
        // Handle jika tidak ada browser atau link rusak
        Toast.makeText(this, "Gagal membuka link: ${e.message}", Toast.LENGTH_SHORT).show()
        e.printStackTrace()
    }
}