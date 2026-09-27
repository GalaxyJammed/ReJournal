package com.example.rejournal.notifications

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.core.app.NotificationCompat
import com.example.rejournal.R
import com.example.rejournal.data.AvailableUpdate

object UpdateNotificationHelper {
    private const val CHANNEL_ID = "app_updates"
    private const val NOTIFICATION_ID = 2025

    fun showNotification(context: Context, update: AvailableUpdate) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(update.releaseUrl))
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_stat_name)
            .setLargeIcon(BitmapFactory.decodeResource(context.resources, R.mipmap.ic_launcher))
            .setContentTitle("New Version (${update.version}) Available")
            .setContentText("A new update for ReJournal is available on GitHub. Tap to view.")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("A new update (${update.version}) for ReJournal is available on GitHub. Tap to view the release and download.")
            )
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, notification)
    }
}
