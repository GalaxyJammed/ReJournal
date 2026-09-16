package com.example.rejournal.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import java.time.LocalDate
import java.util.Calendar

object ImportantDayScheduler {

    fun schedule(context: Context, date: LocalDate, message: String) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) return

        val trigger = Calendar.getInstance().apply {
            set(date.year, date.monthValue - 1, date.dayOfMonth, 9, 0, 0)
            set(Calendar.MILLISECOND, 0)
        }
        if (trigger.timeInMillis <= System.currentTimeMillis()) return

        val pendingIntent = buildPendingIntent(context, date, message)
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, trigger.timeInMillis, pendingIntent)
    }

    fun cancel(context: Context, date: LocalDate) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(buildPendingIntent(context, date, ""))
    }

    private fun buildPendingIntent(context: Context, date: LocalDate, message: String): PendingIntent {
        val intent = Intent(context, ImportantDayReceiver::class.java).apply {
            putExtra("message", message)
            putExtra("date", date.toString())
        }
        val requestCode = date.toEpochDay().toInt()
        return PendingIntent.getBroadcast(
            context, requestCode, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}