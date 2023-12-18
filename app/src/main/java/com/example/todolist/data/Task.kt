package com.example.audioplayer.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.todolist.data.TaskStatus
import java.util.Calendar

@Entity
class Task (
    @PrimaryKey val id: String,
    val name: String,
    val status: TaskStatus,

    private val year: Int,
    private val month: Int,
    private val day: Int,
    private val hour: Int,
    private val minute: Int,
){
    fun getCalendarInstance(): Calendar = Calendar.getInstance().apply {
        set(year, month, day, hour, minute)
    }
}
