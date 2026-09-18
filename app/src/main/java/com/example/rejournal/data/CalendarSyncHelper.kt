package com.example.rejournal.data

import android.content.Context
import android.provider.CalendarContract
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId

data class CalendarInfo(val id: Long, val displayName: String, val accountName: String)
data class CalendarEventOccurrence(val date: LocalDate, val title: String)

object CalendarSyncHelper {

    fun listCalendars(context: Context): List<CalendarInfo> {
        val result = mutableListOf<CalendarInfo>()
        val projection = arrayOf(
            CalendarContract.Calendars._ID,
            CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,
            CalendarContract.Calendars.ACCOUNT_NAME
        )
        context.contentResolver.query(
            CalendarContract.Calendars.CONTENT_URI, projection, null, null, null
        )?.use { cursor ->
            while (cursor.moveToNext()) {
                result.add(
                    CalendarInfo(
                        id = cursor.getLong(0),
                        displayName = cursor.getString(1) ?: "Unnamed calendar",
                        accountName = cursor.getString(2) ?: ""
                    )
                )
            }
        }
        return result
    }

    fun eventsForRestOfMonth(context: Context, calendarIds: Set<Long>): List<CalendarEventOccurrence> {
        if (calendarIds.isEmpty()) return emptyList()

        val zone = ZoneId.systemDefault()
        val startMillis = LocalDate.now().atStartOfDay(zone).toInstant().toEpochMilli()
        val endMillis = YearMonth.now().atEndOfMonth().plusDays(1).atStartOfDay(zone).toInstant().toEpochMilli()

        val uri = CalendarContract.Instances.CONTENT_URI.buildUpon()
            .appendPath(startMillis.toString())
            .appendPath(endMillis.toString())
            .build()

        val projection = arrayOf(
            CalendarContract.Instances.BEGIN,
            CalendarContract.Instances.TITLE,
            CalendarContract.Instances.CALENDAR_ID,
            CalendarContract.Instances.ALL_DAY
        )

        val result = mutableListOf<CalendarEventOccurrence>()
        context.contentResolver.query(uri, projection, null, null, null)?.use { cursor ->
            val beginIndex = cursor.getColumnIndexOrThrow(CalendarContract.Instances.BEGIN)
            val titleIndex = cursor.getColumnIndexOrThrow(CalendarContract.Instances.TITLE)
            val calendarIdIndex = cursor.getColumnIndexOrThrow(CalendarContract.Instances.CALENDAR_ID)

            while (cursor.moveToNext()) {
                val calendarId = cursor.getLong(calendarIdIndex)
                if (calendarId !in calendarIds) continue

                val beginMillis = cursor.getLong(beginIndex)
                val title = cursor.getString(titleIndex)?.takeIf { it.isNotBlank() } ?: continue
                val date = java.time.Instant.ofEpochMilli(beginMillis).atZone(zone).toLocalDate()

                if (date.isAfter(LocalDate.now())) {
                    result.add(CalendarEventOccurrence(date = date, title = title))
                }
            }
        }
        return result
    }
}