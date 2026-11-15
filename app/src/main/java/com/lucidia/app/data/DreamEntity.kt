package com.lucidia.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "dreams")
data class DreamEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    val lucidityLevel: Int = 0, // 0 = Normal, 1 = Semi-Lucid, 2 = Fully Lucid
    val clarityScore: Int = 3,  // 1 to 5
    val mood: String = "Neutral", // Peaceful, Euphoric, Bizarre, Intense, Anxious
    val tags: List<String> = emptyList(), // e.g. ["#flying", "#ocean", "#space"]
    val characters: List<String> = emptyList(), // e.g. ["guide", "friend", "stranger"]
    val locations: List<String> = emptyList() // e.g. ["beach", "childhood home", "space station"]
)
