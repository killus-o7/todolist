package com.example.todolist.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Task::class], version = 3)
abstract class AppDb : RoomDatabase() {
    abstract fun taskDao() : TaskDao
    var onInsert = {}

    companion object {
        @Volatile
        private var INSTANCE: AppDb? = null

        operator fun get(context: Context) = INSTANCE ?: synchronized(this) {
            Room.databaseBuilder(
                context.applicationContext,
                AppDb::class.java,
                "tasks"
            ).build().also {
                INSTANCE = it
            }
        }
    }
}
