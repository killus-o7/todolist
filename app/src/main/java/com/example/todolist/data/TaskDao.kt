package com.example.todolist.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface TaskDao {
    @Query("SELECT * FROM Task")
    fun fetchTasks(): List<Task>

    @Query("SELECT * FROM Task WHERE id = :id")
    fun fetchTaskById(id: Int): Task

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertTask(vararg item: Task)

    @Update
    fun updateTask(vararg item: Task)

    @Delete
    fun delete(item: Task)
}