package com.example.rejournal.widget

import android.content.Context
import androidx.glance.appwidget.updateAll

object MoodWidgetUpdater {
    suspend fun update(context: Context) {
        MoodWidget().updateAll(context)
    }
}