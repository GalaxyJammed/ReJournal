package com.example.rejournal.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED && ReminderPrefs.isEnabled(context)) {
            ReminderScheduler.schedule(
                context,
                ReminderPrefs.getHour(context),
                ReminderPrefs.getMinute(context)
            )
        }
    }
}