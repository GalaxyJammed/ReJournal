package com.example.rejournal.widget

import android.content.Context
import androidx.glance.appwidget.updateAll

object AppWidgetsUpdater {
    suspend fun updateAll(context: Context) {
        TodayMoodWidget().updateAll(context)
        AverageMoodWidget().updateAll(context)
        GoalsWidget().updateAll(context)
    }
}