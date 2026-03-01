package com.dicoding.dicoevent.ui.settings

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.TaskStackBuilder
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.dicoding.dicoevent.R
import com.dicoding.dicoevent.data.remote.retrofit.ApiConfig
import com.dicoding.dicoevent.ui.detail.DetailActivity
import com.dicoding.dicoevent.utils.formatDateForDisplay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DailyReminderWorker(private val context: Context, workerParams: WorkerParameters) :
    CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO) {
            try {
                val response = ApiConfig.getApiService().getEvents(1)
                val events = response.listEvents

                if (events.isNotEmpty()) {
                    val nearestEvent = events[0]
                    val eventId = nearestEvent.id ?: 0
                    val eventName = nearestEvent.name ?: "New Event"
                    val eventTime = nearestEvent.beginTime ?: "Time to be announced"

                    showNotification(eventId, eventName, eventTime)
                }

                Result.success()
            } catch (_: Exception) {
                Result.retry()
            }
        }
    }

    private fun showNotification(eventId: Int, title: String, description: String) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val message = "Don't miss the event: $title on ${description.formatDateForDisplay()}"

        val intent = Intent(context, DetailActivity::class.java).apply {
            putExtra("eventId", eventId)
        }

        val pendingIntent = TaskStackBuilder.create(context).run {
            addNextIntentWithParentStack(intent)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE)
            } else {
                getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
            }
        }
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.baseline_notifications_24)
            .setContentTitle("Dicoding Event Reminder")
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            builder.setChannelId(CHANNEL_ID)
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(NOTIFICATION_ID, builder.build())
    }

    companion object {
        const val NOTIFICATION_ID = 1
        const val CHANNEL_ID = "channel_reminder"
        const val CHANNEL_NAME = "Event Reminder Channel"
    }
}