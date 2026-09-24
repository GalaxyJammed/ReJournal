package com.example.rejournal.notifications

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.core.app.NotificationCompat
import com.example.rejournal.MainActivity
import com.example.rejournal.R
import com.example.rejournal.data.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate

class MicroWinReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (!MicroWinNotificationPrefs.isEnabled(context)) return

        val today = LocalDate.now()
        val todayStr = today.toString()

        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val db = AppDatabase.getDatabase(context)
                val countToday = db.microWinDao().getCountForDate(today)

                MicroWinNotificationPrefs.setLastNotificationDate(context, todayStr)
                MicroWinNotificationPrefs.setScheduledTriggerTime(context, 0L)

                if (countToday == 0) {
                    showNotification(context)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                MicroWinNotificationScheduler.schedule(context)
                pendingResult.finish()
            }
        }
    }

    private fun showNotification(context: Context) {
        val channelId = "micro_wins"
        val contentIntent = Intent(context, MainActivity::class.java).apply {
            putExtra("destination", "micro_wins")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            1002,
            contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificationMessages = listOf(
            "The day passed quite a bit. Would you like to log a tiny Micro-Win for the day?",
            "Day's winding down! Take a quick moment to celebrate a small win.",
            "Spot any Micro-Wins today? Log a tiny victory to boost your morale!",
            "Take a gentle pause—got a small Micro-Win to log for today?"
        )
        val message = notificationMessages.random()

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_stat_name)
            .setLargeIcon(BitmapFactory.decodeResource(context.resources, R.mipmap.ic_launcher))
            .setContentTitle("Log a Micro-Win ✨")
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(1002, notification)
    }
}
