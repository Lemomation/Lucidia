package com.lucidia.app.ui.journal

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.lucidia.app.LucidiaApplication
import com.lucidia.app.data.DreamEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class JournalViewModel(application: Application) : AndroidViewModel(application) {

    private val app = application as LucidiaApplication
    private val dreamDao = app.database.dreamDao()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val dreams: StateFlow<List<DreamEntity>> = dreamDao.getAllDreams()
        .combine(_searchQuery) { list, query ->
            if (query.isBlank()) list
            else list.filter {
                it.title.contains(query, ignoreCase = true) ||
                it.content.contains(query, ignoreCase = true) ||
                it.tags.any { tag -> tag.contains(query, ignoreCase = true) }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun onSearchQueryChanged(newQuery: String) {
        _searchQuery.value = newQuery
    }

    fun addDream(
        title: String,
        content: String,
        lucidityLevel: Int,
        clarityScore: Int,
        tags: List<String>,
        characters: List<String>,
        locations: List<String>
    ) {
        viewModelScope.launch {
            val newDream = DreamEntity(
                title = title.ifBlank { "Untitled Dream" },
                content = content,
                lucidityLevel = lucidityLevel,
                clarityScore = clarityScore,
                tags = tags,
                characters = characters,
                locations = locations
            )
            dreamDao.insertDream(newDream)
        }
    }

    fun deleteDream(dream: DreamEntity) {
        viewModelScope.launch {
            dreamDao.deleteDream(dream)
        }
    }
}
