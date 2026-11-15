package com.lucidia.app.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.lucidia.app.LucidiaApplication
import com.lucidia.app.alarm.AlarmReceiver
import com.lucidia.app.alarm.LucidAlarmManager
import com.lucidia.app.data.LucidPreferences
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val app = application as LucidiaApplication
    private val prefsRepo = app.preferencesRepository
    private val alarmManager = LucidAlarmManager(application)

    val preferences: StateFlow<LucidPreferences> = prefsRepo.preferencesFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = LucidPreferences()
        )

    fun toggleWbtbAlarm(enabled: Boolean) {
        val current = preferences.value
        viewModelScope.launch {
            prefsRepo.updateWbtbAlarm(current.wbtbAlarmHour, current.wbtbAlarmMinute, enabled)
            if (enabled) {
                alarmManager.scheduleAlarm(
                    requestCode = 100,
                    hour = current.wbtbAlarmHour,
                    minute = current.wbtbAlarmMinute,
                    alarmType = AlarmReceiver.TYPE_WBTB,
                    autoDismissSeconds = current.autoDismissSeconds,
                    title = "WBTB Alarm (Lucid Anchor)"
                )
            } else {
                alarmManager.cancelAlarm(100)
            }
        }
    }

    fun toggleWakeupAlarm(enabled: Boolean) {
        val current = preferences.value
        viewModelScope.launch {
            prefsRepo.updateWakeupAlarm(current.wakeupAlarmHour, current.wakeupAlarmMinute, enabled)
            if (enabled) {
                alarmManager.scheduleAlarm(
                    requestCode = 101,
                    hour = current.wakeupAlarmHour,
                    minute = current.wakeupAlarmMinute,
                    alarmType = AlarmReceiver.TYPE_WAKEUP,
                    autoDismissSeconds = 0, // Morning wake-up typically requires user attention
                    title = "Wake-up Alarm"
                )
            } else {
                alarmManager.cancelAlarm(101)
            }
        }
    }

    fun setWbtbTime(hour: Int, minute: Int) {
        val current = preferences.value
        viewModelScope.launch {
            prefsRepo.updateWbtbAlarm(hour, minute, current.wbtbEnabled)
            if (current.wbtbEnabled) {
                alarmManager.scheduleAlarm(
                    requestCode = 100,
                    hour = hour,
                    minute = minute,
                    alarmType = AlarmReceiver.TYPE_WBTB,
                    autoDismissSeconds = current.autoDismissSeconds,
                    title = "WBTB Alarm (Lucid Anchor)"
                )
            }
        }
    }

    fun setWakeupTime(hour: Int, minute: Int) {
        val current = preferences.value
        viewModelScope.launch {
            prefsRepo.updateWakeupAlarm(hour, minute, current.wakeupEnabled)
            if (current.wakeupEnabled) {
                alarmManager.scheduleAlarm(
                    requestCode = 101,
                    hour = hour,
                    minute = minute,
                    alarmType = AlarmReceiver.TYPE_WAKEUP,
                    autoDismissSeconds = 0,
                    title = "Wake-up Alarm"
                )
            }
        }
    }
}
