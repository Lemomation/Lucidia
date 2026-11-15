package com.lucidia.app.ui.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.lucidia.app.LucidiaApplication
import com.lucidia.app.data.LucidPreferences
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val app = application as LucidiaApplication
    private val prefsRepo = app.preferencesRepository

    val preferences: StateFlow<LucidPreferences> = prefsRepo.preferencesFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = LucidPreferences()
        )

    fun setAutoDismissSeconds(seconds: Int) {
        viewModelScope.launch {
            prefsRepo.setAutoDismissSeconds(seconds)
        }
    }

    fun setTwilightFilter(enabled: Boolean, intensity: Int = 70) {
        viewModelScope.launch {
            prefsRepo.setTwilightFilter(enabled, intensity)
        }
    }

    fun setVolumeCrescendo(enabled: Boolean) {
        viewModelScope.launch {
            prefsRepo.setVolumeCrescendo(enabled)
        }
    }

    fun setRealityCheckInterval(hours: Int) {
        viewModelScope.launch {
            prefsRepo.setRealityCheckInterval(hours)
        }
    }
}
