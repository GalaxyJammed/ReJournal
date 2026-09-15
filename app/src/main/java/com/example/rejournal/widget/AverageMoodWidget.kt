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
import androidx.glance.unit.ColorProvider
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.example.rejournal.MainActivity
import com.example.rejournal.data.AppDatabase
import com.example.rejournal.data.moodEmojis
import com.example.rejournal.ui.theme.resolvedPrimaryColor


class AverageMoodWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val dao = AppDatabase.getDatabase(context).moodDao()
        val entries = dao.getAllEntriesOnce()
        val average = if (entries.isNotEmpty()) entries.map { it.mood }.average() else 0.0
        val roundedIndex = Math.round(average).toInt().coerceIn(1, 5) - 1
        val accent = resolvedPrimaryColor(context)

        provideContent {
            Column(
                modifier = GlanceModifier
                    .fillMaxWidth()
                    .background(ColorProvider(Color(0xCC1A1A1A)))
                    .clickable(actionStartActivity<MainActivity>())
            ) {
                Column(
                    modifier = GlanceModifier
                        .fillMaxWidth()
                        .background(ColorProvider(accent))
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Text(
                        "Average Mood",
                        style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 14.sp, color = ColorProvider(Color.Black))
                    )
                }
                Column(
                    modifier = GlanceModifier.fillMaxWidth().padding(12.dp),
                    horizontalAlignment = Alignment.Horizontal.CenterHorizontally
                ) {
                    if (entries.isEmpty()) {
                        Text("No entries yet", style = TextStyle(fontSize = 13.sp, color = ColorProvider(Color.White)))
                    } else {
                        Text(moodEmojis[roundedIndex], style = TextStyle(fontSize = 28.sp))
                        Text(
                            String.format("%.1f", average),
                            style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 18.sp, color = ColorProvider(Color.White))
                        )
                    }
                }
            }
        }
    }
}