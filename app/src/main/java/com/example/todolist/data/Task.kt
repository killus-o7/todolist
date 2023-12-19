package com.example.todolist.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.todolist.data.TaskStatus
import java.util.Calendar

@Entity
class Task (
    @PrimaryKey val id: String,
    val name: String,
    val status: TaskStatus,
    val date: String
){

}
