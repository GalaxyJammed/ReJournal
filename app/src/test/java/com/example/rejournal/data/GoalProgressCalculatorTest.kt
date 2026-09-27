package com.example.rejournal.data

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

class GoalProgressCalculatorTest {

    @Test
    fun `streak metric should only count entries since goal start date`() {
        val today = LocalDate.now()
        val yesterday = today.minusDays(1)
        val dayBefore = today.minusDays(2)

        val entries = listOf(
            MoodEntry(date = dayBefore, mood = 3, activities = emptyList(), note = ""),
            MoodEntry(date = yesterday, mood = 3, activities = emptyList(), note = ""),
            MoodEntry(date = today, mood = 3, activities = emptyList(), note = "")
        )

        val definition = GoalDefinition(
            id = "test_streak",
            title = "Test Streak",
            description = "Test",
            category = GoalCategory.HABITS,
            metric = GoalMetric.STREAK,
            target = 7
        )

        val stateStartedToday = GoalState(isActive = true, startDate = today, completions = 0, attempts = 0)
        val progressToday = GoalProgressCalculator.currentProgress(definition, stateStartedToday, entries)
        assertEquals(1, progressToday)

        val stateStarted2DaysAgo = GoalState(isActive = true, startDate = dayBefore, completions = 0, attempts = 0)
        val progressOld = GoalProgressCalculator.currentProgress(definition, stateStarted2DaysAgo, entries)
        assertEquals(3, progressOld)
    }

    @Test
    fun `entries logged metric should only count entries since goal start date`() {
        val today = LocalDate.now()
        val yesterday = today.minusDays(1)

        val entries = listOf(
            MoodEntry(date = yesterday, mood = 3, activities = emptyList(), note = ""),
            MoodEntry(date = today, mood = 3, activities = emptyList(), note = "")
        )

        val definition = GoalDefinition(
            id = "test_entries",
            title = "Test Entries",
            description = "Test",
            category = GoalCategory.HABITS,
            metric = GoalMetric.ENTRIES_LOGGED,
            target = 10
        )

        val stateStartedToday = GoalState(isActive = true, startDate = today, completions = 0, attempts = 0)
        val progress = GoalProgressCalculator.currentProgress(definition, stateStartedToday, entries)

        assertEquals(1, progress)
    }

    @Test
    fun `microwins metric should only count wins added after startMicroWinId`() {
        val today = LocalDate.now()
        val microWins = listOf(
            MicroWin(id = 1, title = "Old Win 1", date = today.minusDays(1)),
            MicroWin(id = 2, title = "Old Win 2", date = today),
            MicroWin(id = 3, title = "New Win 1", date = today),
            MicroWin(id = 4, title = "New Win 2", date = today)
        )

        val definition = GoalDefinition(
            id = "growth_microwins_5",
            title = "Log 5 Micro-Wins",
            description = "Collect 5 micro-wins",
            category = GoalCategory.GROWTH,
            metric = GoalMetric.MICRO_WINS,
            target = 5
        )

        val stateStartedAfterWin2 = GoalState(
            isActive = true,
            startDate = today,
            completions = 0,
            attempts = 0,
            startMicroWinId = 2
        )

        val progress = GoalProgressCalculator.currentProgress(
            definition = definition,
            state = stateStartedAfterWin2,
            entries = emptyList(),
            microWins = microWins
        )

        assertEquals(2, progress)
    }
}
