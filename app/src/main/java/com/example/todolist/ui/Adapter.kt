package com.example.todolist

import android.graphics.Color
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.graphics.drawable.toDrawable
import androidx.recyclerview.widget.RecyclerView
import com.example.todolist.data.Task
import com.example.todolist.databinding.TaskItemBinding
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class TaskAdapter(
    private val onEdit: (Task) -> Unit,
    private val onDelete: (Task) -> Unit
) : RecyclerView.Adapter<TaskAdapter.ViewHolder>() {
    var dataSet: MutableList<Task> = mutableListOf()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        TaskItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(dataSet[position], onEdit, onDelete)
    override fun getItemCount() = dataSet.size

    class ViewHolder(private val b: TaskItemBinding) : RecyclerView.ViewHolder(b.root) {

        @RequiresApi(Build.VERSION_CODES.O)
        fun bind(entry: Task, onEdit: (Task) -> Unit, onRemove: (Task) -> Unit) {
            b.apply {
                taskTitle.text = entry.name
                taskDesc.text = entry.desc
                taskDate.text = formatTaskDeadline(LocalDateTime.ofInstant(Instant.ofEpochMilli(entry.date), ZoneId.systemDefault()))
                editBtn.setOnClickListener { onEdit(entry) }
                deleteBtn.setOnClickListener { onRemove(entry) }
            }
        }

        @RequiresApi(Build.VERSION_CODES.O)
        private fun formatTaskDeadline(deadline: LocalDateTime): String {
            val now = LocalDateTime.now()

            return when {
                deadline.isBefore(now) -> "Po terminie !!!"
                deadline.toLocalDate() == now.toLocalDate() -> "Dziś, ${formatHour(deadline)}"
                deadline.toLocalDate() == now.plusDays(1).toLocalDate() -> "Jutro, ${formatHour(deadline)}"
                else -> formatFullDate(deadline)
            }
        }
        @RequiresApi(Build.VERSION_CODES.O)
        fun formatFullDate(dateTime: LocalDateTime): String {
            val formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm")
            return dateTime.format(formatter)
        }
        @RequiresApi(Build.VERSION_CODES.O)
        private fun formatHour(dateTime: LocalDateTime): String {
            val formatter = DateTimeFormatter.ofPattern("HH:mm")
            return dateTime.format(formatter)
        }
    }
}