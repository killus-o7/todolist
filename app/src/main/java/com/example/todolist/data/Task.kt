package com.example.todolist.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.todolist.data.TaskStatus
import java.util.Calendar

@Entity
class Task (
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    var name: String = "",
    var desc: String = "",
    var isDone: Boolean = false,
    var date: Long = 0
)
