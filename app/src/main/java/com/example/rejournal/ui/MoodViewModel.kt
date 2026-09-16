package com.example.rejournal.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.rejournal.data.ImportantDay
import com.example.rejournal.data.MediaFileHelper
import com.example.rejournal.data.MoodEntry
import com.example.rejournal.data.MoodRepository
import com.example.rejournal.data.StreakCalculator
import com.example.rejournal.data.StreakInfo
import com.example.rejournal.notifications.ImportantDayScheduler
import com.example.rejournal.widget.AppWidgetsUpdater
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

class MoodViewModel(
    private val repository: MoodRepository,
    private val appContext: Context
) : ViewModel() {

    val allEntries: StateFlow<List<MoodEntry>> = repository.allEntries.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val allImportantDays: StateFlow<List<ImportantDay>> = repository.allImportantDays.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val streakInfo: StateFlow<StreakInfo> = allEntries.map { StreakCalculator.calculate(it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = StreakInfo(0, 0)
        )

    var selectedPhoto: com.example.rejournal.data.MediaItem? = null

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
        audioPaths: List<String> = emptyList()
    ) {
        viewModelScope.launch {
            repository.saveEntry(
                MoodEntry(
                    date = date, mood = mood, activities = activities, note = note,
                    energy = energy, productivity = productivity, stress = stress, sleep = sleep,
                    photoPaths = photoPaths, audioPaths = audioPaths
                )
            )
            AppWidgetsUpdater.updateAll(appContext)
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