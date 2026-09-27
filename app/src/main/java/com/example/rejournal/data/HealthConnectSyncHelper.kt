package com.example.rejournal.data

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.ActiveCaloriesBurnedRecord
import androidx.health.connect.client.records.ExerciseSessionRecord
import androidx.health.connect.client.records.SleepSessionRecord
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.request.AggregateRequest
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import com.example.rejournal.notifications.HealthConnectNotificationHelper
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.util.Locale

object HealthConnectSyncHelper {

    val REQUIRED_PERMISSIONS = setOf(
        HealthPermission.getReadPermission(StepsRecord::class),
        HealthPermission.getReadPermission(ExerciseSessionRecord::class),
        HealthPermission.getReadPermission(SleepSessionRecord::class),
        HealthPermission.getReadPermission(ActiveCaloriesBurnedRecord::class),
        HealthPermission.PERMISSION_READ_HEALTH_DATA_IN_BACKGROUND
    )

    fun getSdkStatus(context: Context): Int {
        return HealthConnectClient.getSdkStatus(context)
    }

    fun isSdkAvailable(context: Context): Boolean {
        return getSdkStatus(context) == HealthConnectClient.SDK_AVAILABLE
    }

    fun openHealthConnectInPlayStore(context: Context) {
        val providerPackageName = "com.google.android.apps.healthdata"
        val uriString = "market://details?id=$providerPackageName&url=healthconnect%3A%2F%2Fonboarding"
        try {
            context.startActivity(
                Intent(Intent.ACTION_VIEW).apply {
                    setPackage("com.android.vending")
                    data = Uri.parse(uriString)
                    putExtra("overlay", true)
                    putExtra("callerId", context.packageName)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
            )
        } catch (_: Exception) {
            try {
                context.startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/details?id=$providerPackageName")
                    ).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                )
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
    }

    suspend fun hasPermissions(context: Context): Boolean {
        if (!isSdkAvailable(context)) return false
        val client = HealthConnectClient.getOrCreate(context)
        val granted = client.permissionController.getGrantedPermissions()
        val basicPermissions = setOf(
            HealthPermission.getReadPermission(StepsRecord::class),
            HealthPermission.getReadPermission(ExerciseSessionRecord::class),
            HealthPermission.getReadPermission(SleepSessionRecord::class),
            HealthPermission.getReadPermission(ActiveCaloriesBurnedRecord::class)
        )
        return granted.containsAll(basicPermissions)
    }

    suspend fun syncHealthData(context: Context, repository: MoodRepository): Int {
        if (!HealthConnectSyncPrefs.isEnabled(context) || !hasPermissions(context)) return 0

        val client = HealthConnectClient.getOrCreate(context)
        val zone = ZoneId.systemDefault()
        val now = Instant.now()
        val today = LocalDate.now(zone)
        val startOfToday = today.atStartOfDay(zone).toInstant()

        var newWinsCount = 0

        // 1. Sync Step Count Milestones
        if (HealthConnectSyncPrefs.isStepsSyncEnabled(context)) {
            try {
                val stepRequest = AggregateRequest(
                    metrics = setOf(StepsRecord.COUNT_TOTAL),
                    timeRangeFilter = TimeRangeFilter.between(startOfToday, now)
                )
                val response = client.aggregate(stepRequest)
                val totalSteps = response[StepsRecord.COUNT_TOTAL] ?: 0L
                val stepGoal = HealthConnectSyncPrefs.getStepGoal(context)

                val milestoneKey = "${today}_${stepGoal}"
                val syncedMilestones = HealthConnectSyncPrefs.getSyncedStepMilestones(context)

                if (totalSteps >= stepGoal && !syncedMilestones.contains(milestoneKey)) {
                    val formattedSteps = String.format(Locale.getDefault(), "%,d", totalSteps)
                    val winTitle = "👟 Reached $formattedSteps steps today!"
                    repository.saveMicroWin(MicroWin(title = winTitle, date = today))
                    HealthConnectSyncPrefs.addSyncedStepMilestone(context, milestoneKey)
                    HealthConnectNotificationHelper.showGoalReachedNotification(context, winTitle)
                    newWinsCount++
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // 2. Sync Workouts / Exercise Sessions
        if (HealthConnectSyncPrefs.isWorkoutsSyncEnabled(context)) {
            try {
                val startTime = now.minus(Duration.ofDays(7))
                val exerciseRequest = ReadRecordsRequest(
                    recordType = ExerciseSessionRecord::class,
                    timeRangeFilter = TimeRangeFilter.between(startTime, now)
                )
                val response = client.readRecords(exerciseRequest)
                val syncedRecordIds = HealthConnectSyncPrefs.getSyncedRecordIds(context)

                for (record in response.records) {
                    val id = record.metadata.id
                    if (syncedRecordIds.contains(id)) continue

                    val durationMinutes = Duration.between(record.startTime, record.endTime).toMinutes()
                    val activityName = getExerciseName(record.exerciseType)
                    val recordDate = record.startTime.atZone(zone).toLocalDate()

                    val titleText = if (!record.title.isNullOrBlank()) {
                        "${activityName}: ${record.title} (${durationMinutes} mins)"
                    } else {
                        "${activityName} completed (${durationMinutes} mins)"
                    }

                    repository.saveMicroWin(MicroWin(title = titleText, date = recordDate))
                    HealthConnectSyncPrefs.addSyncedRecordId(context, id)
                    HealthConnectNotificationHelper.showGoalReachedNotification(context, titleText)
                    newWinsCount++
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // 3. Sync Sleep Sessions
        if (HealthConnectSyncPrefs.isSleepSyncEnabled(context)) {
            try {
                val startTime = now.minus(Duration.ofDays(7))
                val sleepRequest = ReadRecordsRequest(
                    recordType = SleepSessionRecord::class,
                    timeRangeFilter = TimeRangeFilter.between(startTime, now)
                )
                val response = client.readRecords(sleepRequest)
                val syncedRecordIds = HealthConnectSyncPrefs.getSyncedRecordIds(context)

                for (record in response.records) {
                    val id = record.metadata.id
                    if (syncedRecordIds.contains(id)) continue

                    val sleepDuration = Duration.between(record.startTime, record.endTime)
                    val hours = sleepDuration.toHours()
                    val minutes = sleepDuration.toMinutes() % 60
                    val sleepDate = record.endTime.atZone(zone).toLocalDate()

                    val titleText = "😴 Logged ${hours}h ${minutes}m of sleep"

                    repository.saveMicroWin(MicroWin(title = titleText, date = sleepDate))
                    HealthConnectSyncPrefs.addSyncedRecordId(context, id)
                    HealthConnectNotificationHelper.showGoalReachedNotification(context, titleText)
                    newWinsCount++
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // 4. Sync Active Calories Burned
        if (HealthConnectSyncPrefs.isCaloriesSyncEnabled(context)) {
            try {
                val calorieRequest = AggregateRequest(
                    metrics = setOf(ActiveCaloriesBurnedRecord.ACTIVE_CALORIES_TOTAL),
                    timeRangeFilter = TimeRangeFilter.between(startOfToday, now)
                )
                val response = client.aggregate(calorieRequest)
                val activeEnergy = response[ActiveCaloriesBurnedRecord.ACTIVE_CALORIES_TOTAL]
                val activeCalories = activeEnergy?.inKilocalories?.toInt() ?: 0
                val calorieGoal = HealthConnectSyncPrefs.getCalorieGoal(context)

                val milestoneKey = "${today}_${calorieGoal}"
                val syncedMilestones = HealthConnectSyncPrefs.getSyncedCalorieMilestones(context)

                if (activeCalories >= calorieGoal && !syncedMilestones.contains(milestoneKey)) {
                    val formattedCalories = String.format(Locale.getDefault(), "%,d", activeCalories)
                    val winTitle = "🔥 Burned $formattedCalories active kcal today!"
                    repository.saveMicroWin(MicroWin(title = winTitle, date = today))
                    HealthConnectSyncPrefs.addSyncedCalorieMilestone(context, milestoneKey)
                    HealthConnectNotificationHelper.showGoalReachedNotification(context, winTitle)
                    newWinsCount++
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        HealthConnectSyncPrefs.setLastSyncedTime(context, System.currentTimeMillis())
        return newWinsCount
    }

    private fun getExerciseName(type: Int): String {
        return when (type) {
            ExerciseSessionRecord.EXERCISE_TYPE_RUNNING -> "🏃 Run"
            ExerciseSessionRecord.EXERCISE_TYPE_BIKING -> "🚴 Ride"
            ExerciseSessionRecord.EXERCISE_TYPE_SWIMMING_POOL,
            ExerciseSessionRecord.EXERCISE_TYPE_SWIMMING_OPEN_WATER -> "🏊 Swim"
            ExerciseSessionRecord.EXERCISE_TYPE_WALKING -> "🚶 Walk"
            ExerciseSessionRecord.EXERCISE_TYPE_HIKING -> "🥾 Hike"
            ExerciseSessionRecord.EXERCISE_TYPE_YOGA -> "🧘 Yoga"
            ExerciseSessionRecord.EXERCISE_TYPE_WEIGHTLIFTING,
            ExerciseSessionRecord.EXERCISE_TYPE_CALISTHENICS -> "🏋️ Strength Workout"
            else -> "🏋️ Workout"
        }
    }
}
