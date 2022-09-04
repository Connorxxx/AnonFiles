package com.connor.anonfiles.model.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(version = 1, entities = [FileData::class])
abstract class AppDatabase : RoomDatabase() {

    abstract fun fileDao(): FileDao

    companion object {
        private var instance: AppDatabase? = null

        @Synchronized
        fun getDataBase(context: Context): AppDatabase {
            instance?.let {
                return it
            }
            return Room.databaseBuilder(context.applicationContext,
                AppDatabase::class.java, "app_database").build().apply {
                instance = this
            }
        }
    }
}