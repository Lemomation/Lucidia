package com.lucidia.app

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.lucidia.app.alarm.AlarmNotificationHelper
import com.lucidia.app.data.LucidDatabase
import com.lucidia.app.data.UserPreferencesRepository

class LucidiaApplication : Application() {

    val database: LucidDatabase by lazy {
        LucidDatabase.getDatabase(this)
    }

    val preferencesRepository: UserPreferencesRepository by lazy {
        UserPreferencesRepository(this)
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = getSystemService(NotificationManager::class.java)

            // High-importance channel for lucid/WBTB alarms
            val alarmChannel = NotificationChannel(
                AlarmNotificationHelper.CHANNEL_ID_ALARM,
                "Lucid Dream Alarms",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "WBTB and Morning wake-up alarms with auto-dismissal"
                enableVibration(true)
                setSound(null, null) // Custom sound handled by AlarmService
            }

            // Low/default channel for daytime reality check reminders
            val realityChannel = NotificationChannel(
                AlarmNotificationHelper.CHANNEL_ID_REALITY_CHECK,
                "Reality Checks",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Discreet reminders to question your reality during the day"
                enableVibration(true)
            }

            notificationManager.createNotificationChannel(alarmChannel)
            notificationManager.createNotificationChannel(realityChannel)
        }
    }
}
