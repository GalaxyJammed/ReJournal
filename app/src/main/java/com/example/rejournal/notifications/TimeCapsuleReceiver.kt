package com.example.rejournal.notifications

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.rejournal.MainActivity
import com.example.rejournal.R
import com.example.rejournal.data.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate

class TimeCapsuleReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val capsuleId = intent.getLongExtra("capsuleId", -1L)
        if (capsuleId == -1L) return

        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val dao = AppDatabase.getDatabase(context).timeCapsuleDao()
                val capsule = dao.getUndeliveredOnce().find { it.id == capsuleId } ?: return@launch
                dao.insert(capsule.copy(delivered = true, deliveredDate = LocalDate.now()))
                showNotification(context, capsule.message)
            } finally {
                pendingResult.finish()
            }
        }
    }

    private fun showNotification(context: Context, message: String) {
        val contentIntent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context, 0, contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val notification = NotificationCompat.Builder(context, "time_capsules")
            .setSmallIcon(R.drawable.ic_stat_name)
            .setContentTitle("A Time Capsule has arrived")
            .setContentText(message)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(("capsule_" + message.hashCode()).hashCode(), notification)
    }
}