package com.example.rejournal.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.rejournal.data.AppDatabase
import com.example.rejournal.data.CapsuleType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return

        if (ReminderPrefs.isEnabled(context)) {
            ReminderScheduler.schedule(
                context,
                ReminderPrefs.getHour(context),
                ReminderPrefs.getMinute(context)
            )
        }

        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val db = AppDatabase.getDatabase(context)
                db.importantDayDao().getAllOnce()
                    .filter { !it.date.isBefore(LocalDate.now()) }
                    .forEach { ImportantDayScheduler.schedule(context, it.date, it.message) }

                db.timeCapsuleDao().getUndeliveredOnce()
                    .filter { it.type == CapsuleType.TIME && it.targetDate != null }
                    .forEach { TimeCapsuleScheduler.schedule(context, it.id, it.targetDate!!) }
            } finally {
                pendingResult.finish()
            }
        }
    }
}