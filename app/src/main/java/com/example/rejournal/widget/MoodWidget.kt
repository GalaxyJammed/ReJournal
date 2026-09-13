package com.example.rejournal.widget

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.color.ColorProvider
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.example.rejournal.MainActivity
import com.example.rejournal.data.AppDatabase
import com.example.rejournal.data.StreakCalculator
import java.time.LocalDate

class MoodWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val dao = AppDatabase.getDatabase(context).moodDao()
        val entries = dao.getAllEntriesOnce()
        val loggedToday = entries.any { it.date == LocalDate.now() }
        val streak = StreakCalculator.calculate(entries).currentStreak

        provideContent {
            Row(
                modifier = GlanceModifier
                    .fillMaxSize()
                    .background(ColorProvider(day = Color(0xFFFFF3E0), night = Color(0xFFFFF3E0)))
                    .padding(12.dp)
                    .clickable(actionStartActivity<MainActivity>()),
                verticalAlignment = Alignment.Vertical.CenterVertically
            ) {
                Column(modifier = GlanceModifier.defaultWeight()) {
                    Text(
                        text = if (loggedToday) "✅ Logged today" else "⭕ Not logged yet",
                        style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    )
                    if (!loggedToday) {
                        Text(
                            text = "Tap to check in",
                            style = TextStyle(fontSize = 11.sp)
                        )
                    }
                }
                Column(horizontalAlignment = Alignment.Horizontal.CenterHorizontally) {
                    Text("🔥 $streak", style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 18.sp))
                    Text("day streak", style = TextStyle(fontSize = 10.sp))
                }
            }
        }
    }
}