package com.lucidia.app.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [DreamEntity::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class LucidDatabase : RoomDatabase() {

    abstract fun dreamDao(): DreamDao

    companion object {
        @Volatile
        private var INSTANCE: LucidDatabase? = null

        fun getDatabase(context: Context): LucidDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LucidDatabase::class.java,
                    "lucidia_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
