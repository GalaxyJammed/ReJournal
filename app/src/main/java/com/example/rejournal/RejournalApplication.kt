package com.example.rejournal

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.rejournal.data.AppDatabase

class RejournalApplication : Application() {
    val database: AppDatabase by lazy { AppDatabase.getDatabase(this) }

    var isUnlockedThisSession by mutableStateOf(false)

    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(
                NotificationChannel("daily_reminder", "Daily Mood Reminder", NotificationManager.IMPORTANCE_DEFAULT)
                    .apply { description = "Reminds you to log your mood each day" }
            )
            manager.createNotificationChannel(
                NotificationChannel("important_days", "Important Days", NotificationManager.IMPORTANCE_HIGH)
                    .apply { description = "Notifies you on days you've marked as important" }
            )
            manager.createNotificationChannel(
                NotificationChannel("time_capsules", "Time Capsules", NotificationManager.IMPORTANCE_HIGH)
                    .apply { description = "Notifies you when a scheduled time capsule arrives" }
            )
        }
    }
}