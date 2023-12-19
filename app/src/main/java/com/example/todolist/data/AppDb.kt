package com.example.todolist.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Task::class], version = 2)
abstract class AppDb : RoomDatabase() {
    abstract fun musicDao() : TaskDao
    var onInsert = {}
    val sounds: MutableList<Task> = mutableListOf()

    companion object {
        @Volatile
        private var INSTANCE: AppDb? = null

        operator fun get(context: Context) = INSTANCE ?: synchronized(this) {
            Room.databaseBuilder(
                context.applicationContext,
                AppDb::class.java,
                "audioplayer"
            ).build().also {
                INSTANCE = it
            }
        }
    }
}
