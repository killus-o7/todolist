package com.example.audioplayer.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface TaskDao {
    @Query("SELECT * FROM Task")
    fun fetchSongs(): List<Task>

    @Query("SELECT * FROM Task WHERE id = :id")
    fun fetchSongByName(id: Int): Task

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSong(vararg item: Task)

    @Delete
    fun delete(item: Task)
}