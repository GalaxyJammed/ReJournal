package com.example.rejournal.notifications

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.rejournal.MainActivity
import com.example.rejournal.R

// This fires once when the alarm goes off. Android's exact alarms are
// one-shot, so after showing the notification, we immediately schedule
// tomorrow's alarm too — this keeps the daily reminder going indefinitely.
class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        showNotification(context)

        if (ReminderPrefs.isEnabled(context)) {
            ReminderScheduler.schedule(
                context,
                ReminderPrefs.getHour(context),
                ReminderPrefs.getMinute(context)
            )
        }
    }

    private fun showNotification(context: Context) {
        val channelId = "daily_reminder"
        val contentIntent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("How are you feeling today?")
            .setContentText("Take a moment to log your mood.")
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(1, notification)
    }
}