package com.dicoding.dicoevent.utils

import android.content.Context
import android.util.DisplayMetrics

class DisplayUtils {
   companion object{
       fun convertDpToPixel(context: Context): Int {
           return (16f * (context.resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)).toInt()
       }
   }
}