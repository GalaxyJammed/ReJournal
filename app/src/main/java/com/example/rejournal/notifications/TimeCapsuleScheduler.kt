package com.example.rejournal.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import java.time.LocalDate
import java.util.Calendar

object TimeCapsuleScheduler {

    fun schedule(context: Context, capsuleId: Long, date: LocalDate) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) return

        val trigger = Calendar.getInstance().apply {
            set(date.year, date.monthValue - 1, date.dayOfMonth, 0, 0, 1)
            set(Calendar.MILLISECOND, 0)
        }
        if (trigger.timeInMillis <= System.currentTimeMillis()) return

        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, trigger.timeInMillis, buildPendingIntent(context, capsuleId))
    }

    fun cancel(context: Context, capsuleId: Long) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(buildPendingIntent(context, capsuleId))
    }

    private fun buildPendingIntent(context: Context, capsuleId: Long): PendingIntent {
        val intent = Intent(context, TimeCapsuleReceiver::class.java).apply {
            putExtra("capsuleId", capsuleId)
        }
        return PendingIntent.getBroadcast(
            context, capsuleId.toInt(), intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}