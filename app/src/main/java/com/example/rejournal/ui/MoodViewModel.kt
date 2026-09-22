package com.example.rejournal.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.rejournal.data.CapsuleType
import com.example.rejournal.data.ImportantDay
import com.example.rejournal.data.MediaFileHelper
import com.example.rejournal.data.MoodEntry
import com.example.rejournal.data.MoodRepository
import com.example.rejournal.data.StreakCalculator
import com.example.rejournal.data.StreakInfo
import com.example.rejournal.data.TimeCapsule
import com.example.rejournal.notifications.ImportantDayScheduler
import com.example.rejournal.notifications.TimeCapsuleScheduler
import com.example.rejournal.widget.AppWidgetsUpdater
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import android.Manifest
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.example.rejournal.data.CalendarSyncHelper
import com.example.rejournal.data.CalendarSyncPrefs
import com.example.rejournal.data.GoalProgressCalculator
import com.example.rejournal.data.GoalProgressPrefs
import com.example.rejournal.data.MediaItem
import java.time.YearMonth

class MoodViewModel(
    private val repository: MoodRepository,
    private val appContext: Context
) : ViewModel() {

    val allEntries: StateFlow<List<MoodEntry>> = repository.allEntries.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = emptyList()
    )

    val allImportantDays: StateFlow<List<ImportantDay>> = repository.allImportantDays.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = emptyList()
    )

    val allTimeCapsules: StateFlow<List<TimeCapsule>> = repository.allTimeCapsules.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = emptyList()
    )

    val pendingCapsules: StateFlow<List<TimeCapsule>> = allTimeCapsules.map { list ->
        list.filter { it.delivered && !it.opened }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = emptyList()
    )

    val streakInfo: StateFlow<StreakInfo> = allEntries.map { StreakCalculator.calculate(it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = StreakInfo(0, 0)
        )

    val activeGoalsCount = MutableStateFlow(0)

    val timeCapsulesCount: StateFlow<Int> = allTimeCapsules.map { it.size }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = 0
    )

    val photoAlbumCount: StateFlow<Int> = allEntries.map { list ->
        list.sumOf { it.photoPaths.size }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = 0
    )

    val voiceMemosCount: StateFlow<Int> = allEntries.map { list ->
        list.sumOf { it.audioPaths.size }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = 0
    )

    val importantDaysCount: StateFlow<Int> = allImportantDays.map { it.size }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = 0
    )

    val favoriteDaysCount: StateFlow<Int> = allEntries.map { list ->
        list.count { it.isFavorite }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0
    )

    var selectedPhoto: MediaItem? = null
    var selectedPositiveMemory: MoodEntry? = null

    init {
        refreshGoalStatus()
        viewModelScope.launch {
            allEntries.collect {
                refreshGoalStatus()
            }
        }
    }

    fun refreshGoalStatus() {
        viewModelScope.launch {
            GoalProgressCalculator.checkAndCompleteActiveGoals(appContext, allEntries.value)
            activeGoalsCount.value = GoalProgressPrefs.activeCount(appContext)
        }
    }

    fun saveEntry(
        date: LocalDate,
        mood: Int,
        activities: List<String>,
        note: String,
        energy: Int = 3,
        productivity: Int = 3,
        stress: Int = 3,
        sleep: Int = 3,
        photoPaths: List<String> = emptyList(),
        audioPaths: List<String> = emptyList(),
        isFavorite: Boolean = false
    ) {
        viewModelScope.launch {
            repository.saveEntry(
                MoodEntry(
                    date = date, mood = mood, activities = activities, note = note,
                    energy = energy, productivity = productivity, stress = stress, sleep = sleep,
                    photoPaths = photoPaths, audioPaths = audioPaths, isFavorite = isFavorite
                )
            )
            checkMoodCapsules(mood)
            AppWidgetsUpdater.updateAll(appContext)
        }
    }

    private suspend fun checkMoodCapsules(loggedMood: Int) {
        val undelivered = repository.getUndeliveredCapsulesOnce()
        undelivered.filter { it.type == CapsuleType.MOOD && it.targetMood == loggedMood }
            .forEach { capsule ->
                repository.saveTimeCapsule(capsule.copy(delivered = true, deliveredDate = LocalDate.now()))
            }
    }

    fun deleteEntry(entry: MoodEntry) {
        viewModelScope.launch {
            entry.photoPaths.forEach { MediaFileHelper.deleteFile(it) }
            entry.audioPaths.forEach { MediaFileHelper.deleteFile(it) }
            repository.deleteEntry(entry)
            AppWidgetsUpdater.updateAll(appContext)
        }
    }

    fun removeTagEverywhere(tag: String) {
        viewModelScope.launch {
            repository.removeTagFromAllEntries(tag)
        }
    }

    fun saveImportantDay(date: LocalDate, message: String) {
        viewModelScope.launch {
            repository.saveImportantDay(ImportantDay(date = date, message = message))
            ImportantDayScheduler.schedule(appContext, date, message)
        }
    }

    fun deleteImportantDay(day: ImportantDay) {
        viewModelScope.launch {
            ImportantDayScheduler.cancel(appContext, day.date)
            repository.deleteImportantDay(day)
        }
    }

    fun createMoodCapsule(targetMood: Int, message: String) {
        viewModelScope.launch {
            repository.saveTimeCapsule(
                TimeCapsule(
                    type = CapsuleType.MOOD,
                    targetMood = targetMood,
                    message = message,
                    createdDate = LocalDate.now()
                )
            )
        }
    }

    fun createTimeCapsule(daysFromNow: Int, message: String) {
        viewModelScope.launch {
            val targetDate = LocalDate.now().plusDays(daysFromNow.toLong())
            val capsule = TimeCapsule(
                type = CapsuleType.TIME,
                targetDate = targetDate,
                message = message,
                createdDate = LocalDate.now()
            )
            repository.saveTimeCapsule(capsule)
            val saved = repository.getUndeliveredCapsulesOnce()
                .filter { it.type == CapsuleType.TIME && it.targetDate == targetDate }
                .maxByOrNull { it.id }
            saved?.let { TimeCapsuleScheduler.schedule(appContext, it.id, targetDate) }
        }
    }

    fun deleteTimeCapsule(capsule: TimeCapsule) {
        viewModelScope.launch {
            if (capsule.type == CapsuleType.TIME) {
                TimeCapsuleScheduler.cancel(appContext, capsule.id)
            }
            repository.deleteTimeCapsule(capsule)
        }
    }

    fun dismissCapsule(capsule: TimeCapsule) {
        viewModelScope.launch {
            repository.saveTimeCapsule(capsule.copy(opened = true))
        }
    }

    suspend fun getEntryForDate(date: LocalDate): MoodEntry? = repository.getEntryForDate(date)
    suspend fun getImportantDayForDate(date: LocalDate): ImportantDay? = repository.getImportantDayForDate(date)

    fun importBackup(uri: android.net.Uri, onResult: (Int) -> Unit) {
        viewModelScope.launch {
            val imported = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
                com.example.rejournal.data.BackupHelper.importFromZip(appContext, uri)
            }
            imported.forEach { repository.saveEntry(it) }
            AppWidgetsUpdater.updateAll(appContext)
            onResult(imported.size)
        }
    }

    fun hasCalendarPermission(): Boolean =
        ContextCompat.checkSelfPermission(appContext, Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED

    fun syncCalendarNow(calendarIds: Set<Long>, onResult: (Int) -> Unit) {
        viewModelScope.launch {
            if (!hasCalendarPermission()) {
                onResult(0)
                return@launch
            }
            val events = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
                CalendarSyncHelper.eventsForRestOfMonth(appContext, calendarIds)
            }
            var added = 0
            events.forEach { occurrence ->
                val existing = repository.getImportantDayForDate(occurrence.date)
                if (existing == null) {
                    repository.saveImportantDay(ImportantDay(date = occurrence.date, message = occurrence.title))
                    ImportantDayScheduler.schedule(appContext, occurrence.date, occurrence.title)
                    added++
                }
            }
            CalendarSyncPrefs.setLastSyncedMonth(appContext, YearMonth.now())
            onResult(added)
        }
    }

    fun autoSyncCalendar() {
        val selectedIds = CalendarSyncPrefs.getSelectedCalendarIds(appContext)
        if (selectedIds.isEmpty() || !hasCalendarPermission()) return
        syncCalendarNow(selectedIds) { }
    }

    class Factory(
        private val repository: MoodRepository,
        private val appContext: Context
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return MoodViewModel(repository, appContext) as T
        }
    }
}