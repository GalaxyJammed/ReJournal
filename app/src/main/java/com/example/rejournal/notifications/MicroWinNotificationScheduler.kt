package com.example.rejournal.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import java.time.LocalDate
import java.util.Calendar
import kotlin.random.Random

object MicroWinNotificationScheduler {
    private const val REQUEST_CODE = 1002

    fun schedule(context: Context) {
        if (!MicroWinNotificationPrefs.isEnabled(context)) return

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val now = System.currentTimeMillis()
        val existingTrigger = MicroWinNotificationPrefs.getScheduledTriggerTime(context)

        val pendingIntent = buildPendingIntent(context)

        if (existingTrigger > now) {
            setAlarm(alarmManager, existingTrigger, pendingIntent)
            return
        }

        val todayStr = LocalDate.now().toString()
        val lastDate = MicroWinNotificationPrefs.getLastNotificationDate(context)

        val calNow = Calendar.getInstance()
        val currentMinOfDay = calNow.get(Calendar.HOUR_OF_DAY) * 60 + calNow.get(Calendar.MINUTE)

        val targetCal = Calendar.getInstance()
        val startMinWindow = 15 * 60
        val endMinWindow = 21 * 60

        if (lastDate == todayStr) {
            val randomMin = Random.nextInt(startMinWindow, endMinWindow)
            targetCal.add(Calendar.DAY_OF_YEAR, 1)
            targetCal.set(Calendar.HOUR_OF_DAY, randomMin / 60)
            targetCal.set(Calendar.MINUTE, randomMin % 60)
            targetCal.set(Calendar.SECOND, 0)
            targetCal.set(Calendar.MILLISECOND, 0)
        } else {
            if (currentMinOfDay < startMinWindow) {
                val randomMin = Random.nextInt(startMinWindow, endMinWindow)
                targetCal.set(Calendar.HOUR_OF_DAY, randomMin / 60)
                targetCal.set(Calendar.MINUTE, randomMin % 60)
                targetCal.set(Calendar.SECOND, 0)
                targetCal.set(Calendar.MILLISECOND, 0)
            } else if (currentMinOfDay < endMinWindow - 1) {
                val randomMin = Random.nextInt(currentMinOfDay + 1, endMinWindow)
                targetCal.set(Calendar.HOUR_OF_DAY, randomMin / 60)
                targetCal.set(Calendar.MINUTE, randomMin % 60)
                targetCal.set(Calendar.SECOND, 0)
                targetCal.set(Calendar.MILLISECOND, 0)
            } else {
                val randomMin = Random.nextInt(startMinWindow, endMinWindow)
                targetCal.add(Calendar.DAY_OF_YEAR, 1)
                targetCal.set(Calendar.HOUR_OF_DAY, randomMin / 60)
                targetCal.set(Calendar.MINUTE, randomMin % 60)
                targetCal.set(Calendar.SECOND, 0)
                targetCal.set(Calendar.MILLISECOND, 0)
            }
        }

        val triggerTime = targetCal.timeInMillis
        MicroWinNotificationPrefs.setScheduledTriggerTime(context, triggerTime)
        setAlarm(alarmManager, triggerTime, pendingIntent)
    }

    fun cancel(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(buildPendingIntent(context))
        MicroWinNotificationPrefs.setScheduledTriggerTime(context, 0L)
    }

    private fun setAlarm(alarmManager: AlarmManager, triggerTime: Long, pendingIntent: PendingIntent) {
        if (canScheduleExact(alarmManager)) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerTime,
                pendingIntent
            )
        } else {
            alarmManager.setAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerTime,
                pendingIntent
            )
        }
    }

    private fun canScheduleExact(alarmManager: AlarmManager): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            alarmManager.canScheduleExactAlarms()
        } else {
            true
        }
    }

    private fun buildPendingIntent(context: Context): PendingIntent {
        val intent = Intent(context, MicroWinReceiver::class.java)
        return PendingIntent.getBroadcast(
            context,
            REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}
