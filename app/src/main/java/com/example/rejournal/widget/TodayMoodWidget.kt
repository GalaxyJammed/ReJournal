package com.example.rejournal.widget

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.action.ActionParameters
import androidx.glance.action.actionParametersOf
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.unit.ColorProvider
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.example.rejournal.data.AppDatabase
import com.example.rejournal.data.MoodEntry
import com.example.rejournal.data.StreakCalculator
import com.example.rejournal.data.MoodAppearancePrefs
import com.example.rejournal.ui.theme.resolvedPrimaryColor
import java.time.LocalDate


private val moodKey = ActionParameters.Key<Int>("mood_value")

class TodayMoodWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val dao = AppDatabase.getDatabase(context).moodDao()
        val entries = dao.getAllEntriesOnce()
        val todayEntry = entries.find { it.date == LocalDate.now() }
        val streak = StreakCalculator.calculate(entries).currentStreak
        val accent = resolvedPrimaryColor(context)
        val activeEmojis = MoodAppearancePrefs.getActiveEmojis(context)

        provideContent {
            Column(
                modifier = GlanceModifier
                    .fillMaxWidth()
                    .background(ColorProvider(Color(0xCC1A1A1A)))
            ) {
                Row(
                    modifier = GlanceModifier
                        .fillMaxWidth()
                        .background(ColorProvider(accent))
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.Vertical.CenterVertically
                ) {
                    Text(
                        "How's your mood?",
                        style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 14.sp, color = ColorProvider(Color.Black)),
                        modifier = GlanceModifier.defaultWeight()
                    )
                    Text("🔥 $streak", style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 14.sp, color = ColorProvider(Color.Black)))
                }
                Row(
                    modifier = GlanceModifier.fillMaxWidth().padding(12.dp),
                    horizontalAlignment = Alignment.Horizontal.CenterHorizontally
                ) {
                    activeEmojis.forEachIndexed { index, emoji ->
                        val moodValue = index + 1
                        val selected = todayEntry?.mood == moodValue
                        Box(
                            modifier = GlanceModifier
                                .size(40.dp)
                                .padding(2.dp)
                                .background(if (selected) ColorProvider(accent) else ColorProvider(Color.Transparent))
                                .clickable(actionRunCallback<SetMoodAction>(actionParametersOf(moodKey to moodValue))),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(emoji, style = TextStyle(fontSize = 20.sp))
                        }
                    }
                }
            }
        }
    }
}

class SetMoodAction : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        val mood = parameters[moodKey] ?: return
        val dao = AppDatabase.getDatabase(context).moodDao()
        val today = LocalDate.now()
        val existing = dao.getEntryForDate(today)
        val entry = existing?.copy(mood = mood)
            ?: MoodEntry(date = today, mood = mood, activities = emptyList(), note = "")
        dao.insert(entry)
        AppWidgetsUpdater.updateAll(context)
    }
}