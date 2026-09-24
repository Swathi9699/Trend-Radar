package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.ChallengeEntity
import com.example.data.model.CreatedMemeEntity
import com.example.data.model.SavedTrendEntity
import com.example.data.model.TrendVoteEntity

@Database(
    entities = [
        SavedTrendEntity::class,
        CreatedMemeEntity::class,
        TrendVoteEntity::class,
        ChallengeEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun trendDao(): TrendDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "viraltrend_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
