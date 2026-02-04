package com.dicoding.dicoevent.utils

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import java.security.MessageDigest
import kotlin.math.max

class TopCropTransformation : BitmapTransformation() {

    override fun transform(
        pool: BitmapPool,
        toTransform: Bitmap,
        outWidth: Int,
        outHeight: Int
    ): Bitmap {
        if (toTransform.width == outWidth && toTransform.height == outHeight) {
            return toTransform
        }

        // Hitung skala agar gambar memenuhi area
        val scaleX = outWidth.toFloat() / toTransform.width
        val scaleY = outHeight.toFloat() / toTransform.height
        val scale = max(scaleX, scaleY)

        // Buat bitmap baru untuk hasil cropping
        val scaledWidth = scale * toTransform.width
        val scaledHeight = scale * toTransform.height
        val config = if (toTransform.config != null) toTransform.config else Bitmap.Config.ARGB_8888
        val bitmap = pool.get(outWidth, outHeight, config)
        bitmap.setHasAlpha(toTransform.hasAlpha())

        // Atur posisi (Matrix).
        // dx ditaruh di tengah secara horizontal.
        // dy ditaruh di 0 (ATAS). Ini kuncinya.
        val dx = (outWidth - scaledWidth) * 0.5f
        val dy = 0f // Top alignment

        val matrix = Matrix()
        matrix.setScale(scale, scale)
        matrix.postTranslate(dx + 0.5f, dy + 0.5f)

        val canvas = Canvas(bitmap)
        val paint = Paint(Paint.DITHER_FLAG or Paint.FILTER_BITMAP_FLAG)
        canvas.drawBitmap(toTransform, matrix, paint)

        return bitmap
    }

    override fun equals(other: Any?): Boolean {
        return other is TopCropTransformation
    }

    override fun hashCode(): Int {
        return ID.hashCode()
    }

    override fun updateDiskCacheKey(messageDigest: MessageDigest) {
        messageDigest.update(ID_BYTES)
    }

    companion object {
        private const val ID = "com.yourpackage.TopCropTransformation" // Ganti dengan package Anda
        private val ID_BYTES = ID.toByteArray(CHARSET)
    }
}