package com.lucidia.app.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface DreamDao {

    @Query("SELECT * FROM dreams ORDER BY timestamp DESC")
    fun getAllDreams(): Flow<List<DreamEntity>>

    @Query("SELECT * FROM dreams WHERE id = :id LIMIT 1")
    suspend fun getDreamById(id: Long): DreamEntity?

    @Query("SELECT * FROM dreams WHERE title LIKE '%' || :query || '%' OR content LIKE '%' || :query || '%'")
    fun searchDreams(query: String): Flow<List<DreamEntity>>

    @Query("SELECT COUNT(*) FROM dreams")
    fun getDreamCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM dreams WHERE lucidityLevel >= 2")
    fun getLucidDreamCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDream(dream: DreamEntity): Long

    @Update
    suspend fun updateDream(dream: DreamEntity)

    @Delete
    suspend fun deleteDream(dream: DreamEntity)
}
