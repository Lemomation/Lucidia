package com.lucidia.app.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "lucidia_prefs")

data class LucidPreferences(
    val wbtbAlarmHour: Int = 4,
    val wbtbAlarmMinute: Int = 25,
    val wbtbEnabled: Boolean = true,
    val wakeupAlarmHour: Int = 7,
    val wakeupAlarmMinute: Int = 30,
    val wakeupEnabled: Boolean = false,
    val autoDismissSeconds: Int = 15, // Auto-dismiss time in seconds (0 = off)
    val volumeCrescendo: Boolean = true,
    val twilightWarmFilterEnabled: Boolean = true,
    val twilightWarmthIntensity: Int = 70, // 0 to 100%
    val realityCheckIntervalHours: Int = 2,
    val streakDays: Int = 5,
    val streakWeekBits: Int = 0b0011111 // 7-day bitmask for the circles
)

class UserPreferencesRepository(private val context: Context) {

    private object PreferencesKeys {
        val WBTB_HOUR = intPreferencesKey("wbtb_hour")
        val WBTB_MINUTE = intPreferencesKey("wbtb_minute")
        val WBTB_ENABLED = booleanPreferencesKey("wbtb_enabled")
        val WAKEUP_HOUR = intPreferencesKey("wakeup_hour")
        val WAKEUP_MINUTE = intPreferencesKey("wakeup_minute")
        val WAKEUP_ENABLED = booleanPreferencesKey("wakeup_enabled")
        val AUTO_DISMISS_SECONDS = intPreferencesKey("auto_dismiss_seconds")
        val VOLUME_CRESCENDO = booleanPreferencesKey("volume_crescendo")
        val TWILIGHT_ENABLED = booleanPreferencesKey("twilight_enabled")
        val TWILIGHT_INTENSITY = intPreferencesKey("twilight_intensity")
        val REALITY_INTERVAL = intPreferencesKey("reality_interval")
        val STREAK_DAYS = intPreferencesKey("streak_days")
        val STREAK_WEEK_BITS = intPreferencesKey("streak_week_bits")
    }

    val preferencesFlow: Flow<LucidPreferences> = context.dataStore.data.map { preferences ->
        LucidPreferences(
            wbtbAlarmHour = preferences[PreferencesKeys.WBTB_HOUR] ?: 4,
            wbtbAlarmMinute = preferences[PreferencesKeys.WBTB_MINUTE] ?: 25,
            wbtbEnabled = preferences[PreferencesKeys.WBTB_ENABLED] ?: true,
            wakeupAlarmHour = preferences[PreferencesKeys.WAKEUP_HOUR] ?: 7,
            wakeupAlarmMinute = preferences[PreferencesKeys.WAKEUP_MINUTE] ?: 30,
            wakeupEnabled = preferences[PreferencesKeys.WAKEUP_ENABLED] ?: false,
            autoDismissSeconds = preferences[PreferencesKeys.AUTO_DISMISS_SECONDS] ?: 15,
            volumeCrescendo = preferences[PreferencesKeys.VOLUME_CRESCENDO] ?: true,
            twilightWarmFilterEnabled = preferences[PreferencesKeys.TWILIGHT_ENABLED] ?: true,
            twilightWarmthIntensity = preferences[PreferencesKeys.TWILIGHT_INTENSITY] ?: 70,
            realityCheckIntervalHours = preferences[PreferencesKeys.REALITY_INTERVAL] ?: 2,
            streakDays = preferences[PreferencesKeys.STREAK_DAYS] ?: 5,
            streakWeekBits = preferences[PreferencesKeys.STREAK_WEEK_BITS] ?: 0b0011111
        )
    }

    suspend fun updateWbtbAlarm(hour: Int, minute: Int, enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[PreferencesKeys.WBTB_HOUR] = hour
            prefs[PreferencesKeys.WBTB_MINUTE] = minute
            prefs[PreferencesKeys.WBTB_ENABLED] = enabled
        }
    }

    suspend fun updateWakeupAlarm(hour: Int, minute: Int, enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[PreferencesKeys.WAKEUP_HOUR] = hour
            prefs[PreferencesKeys.WAKEUP_MINUTE] = minute
            prefs[PreferencesKeys.WAKEUP_ENABLED] = enabled
        }
    }

    suspend fun setAutoDismissSeconds(seconds: Int) {
        context.dataStore.edit { prefs ->
            prefs[PreferencesKeys.AUTO_DISMISS_SECONDS] = seconds
        }
    }

    suspend fun setTwilightFilter(enabled: Boolean, intensity: Int = 70) {
        context.dataStore.edit { prefs ->
            prefs[PreferencesKeys.TWILIGHT_ENABLED] = enabled
            prefs[PreferencesKeys.TWILIGHT_INTENSITY] = intensity
        }
    }

    suspend fun setVolumeCrescendo(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[PreferencesKeys.VOLUME_CRESCENDO] = enabled
        }
    }

    suspend fun setRealityCheckInterval(hours: Int) {
        context.dataStore.edit { prefs ->
            prefs[PreferencesKeys.REALITY_INTERVAL] = hours
        }
    }
}
