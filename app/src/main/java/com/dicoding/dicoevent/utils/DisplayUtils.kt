package com.dicoding.dicoevent.utils

import android.content.Context
import android.util.DisplayMetrics
import android.view.View

class DisplayUtils {
   companion object{
       fun convertDpToPixel(context: Context): Int {
           return (16f * (context.resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)).toInt()
       }

       fun toggleLoading(
           isLoading: Boolean,
           shimmerContainer: com.facebook.shimmer.ShimmerFrameLayout,
           recyclerView: androidx.recyclerview.widget.RecyclerView
       ) {
           if (isLoading) {
               shimmerContainer.visibility = View.VISIBLE
               shimmerContainer.startShimmer()
               recyclerView.visibility = View.GONE
           } else {
               shimmerContainer.stopShimmer()
               shimmerContainer.visibility = View.GONE
               recyclerView.visibility = View.VISIBLE
           }
       }
   }
}