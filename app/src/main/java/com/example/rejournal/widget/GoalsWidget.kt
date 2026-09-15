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
import androidx.glance.layout.Column
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.example.rejournal.MainActivity
import com.example.rejournal.data.GoalProgressPrefs
import com.example.rejournal.ui.theme.resolvedPrimaryColor

class GoalsWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val activeGoals = GoalProgressPrefs.activeGoals(context)
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
                        "Current Goals",
                        style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 16.sp, color = ColorProvider(Color.Black))
                    )
                }
                Column(modifier = GlanceModifier.padding(12.dp)) {
                    if (activeGoals.isEmpty()) {
                        Text("No active goals", style = TextStyle(fontSize = 12.sp, color = ColorProvider(Color.White)))
                    } else {
                        activeGoals.forEach { goal ->
                            Text(
                                "• ${goal.title}",
                                style = TextStyle(fontSize = 12.sp, color = ColorProvider(Color.White)),
                                modifier = GlanceModifier.padding(bottom = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}