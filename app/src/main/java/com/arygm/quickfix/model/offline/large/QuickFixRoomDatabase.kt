package com.arygm.quickfix.model.offline.large

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.arygm.quickfix.model.offline.large.categories.CategoryDao
import com.arygm.quickfix.model.offline.large.categories.CategoryEntity
import com.arygm.quickfix.model.offline.large.messaging.ChatDao
import com.arygm.quickfix.model.offline.large.messaging.ChatEntity

@Database(entities = [ChatEntity::class, CategoryEntity::class], version = 2)
@TypeConverters(Converters::class)
abstract class QuickFixRoomDatabase : RoomDatabase() {
    abstract fun chatDao(): ChatDao
    abstract fun categoryDao(): CategoryDao

    companion object {
        @Volatile
        private var INSTANCE: QuickFixRoomDatabase? = null

        fun getInstance(context: Context): QuickFixRoomDatabase {
            return INSTANCE
                ?: synchronized(this) {
                    Room.databaseBuilder(
                        context.applicationContext, QuickFixRoomDatabase::class.java, "app_database"
                    )
                        .fallbackToDestructiveMigration() // Remove for production, add migrations instead
                        .build()
                        .also { INSTANCE = it }
                }
        }
    }
}
