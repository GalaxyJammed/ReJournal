package com.example.rejournal.data

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import java.util.concurrent.TimeUnit

class HealthConnectSyncWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val database = AppDatabase.getDatabase(applicationContext)
            val repository = MoodRepository(
                dao = database.moodDao(),
                importantDayDao = database.importantDayDao(),
                timeCapsuleDao = database.timeCapsuleDao(),
                microWinDao = database.microWinDao()
            )
            HealthConnectSyncHelper.syncHealthData(applicationContext, repository)
            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }
    }

    companion object {
        private const val PERIODIC_WORK_NAME = "health_connect_periodic_sync"
        private const val ONE_TIME_WORK_NAME = "health_connect_one_time_sync"

        fun schedulePeriodicSync(context: Context) {
            if (!HealthConnectSyncPrefs.isEnabled(context)) {
                cancelSync(context)
                return
            }

            val syncRequest = PeriodicWorkRequestBuilder<HealthConnectSyncWorker>(
                15, TimeUnit.MINUTES
            ).build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                PERIODIC_WORK_NAME,
                ExistingPeriodicWorkPolicy.UPDATE,
                syncRequest
            )

            scheduleImmediateSync(context)
        }

        fun scheduleImmediateSync(context: Context) {
            if (!HealthConnectSyncPrefs.isEnabled(context)) return

            val syncRequest = OneTimeWorkRequestBuilder<HealthConnectSyncWorker>().build()
            WorkManager.getInstance(context).enqueueUniqueWork(
                ONE_TIME_WORK_NAME,
                ExistingWorkPolicy.REPLACE,
                syncRequest
            )
        }

        fun cancelSync(context: Context) {
            WorkManager.getInstance(context).cancelUniqueWork(PERIODIC_WORK_NAME)
            WorkManager.getInstance(context).cancelUniqueWork(ONE_TIME_WORK_NAME)
        }
    }
}
