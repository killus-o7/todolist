package com.example.todolist

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.service.autofill.CharSequenceTransformation
import android.text.Editable
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.TimePicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.set
import com.example.todolist.data.AppDb
import com.example.todolist.data.Task
import com.example.todolist.databinding.ActivityAddBinding
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AddActivity : AppCompatActivity() {
    private val b by lazy { ActivityAddBinding.inflate(layoutInflater)}
    private val db by lazy { AppDb[this] }
    private var date = Calendar.getInstance()

    private var task = Task()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(b.root)

        val taskId = intent.getIntExtra("id", -1)
        if (taskId != -1) {
            GlobalScope.launch {
                task = db.taskDao().fetchTaskById(taskId)
                setValues(task.name, task.desc, task.date)
            }
        }

        b.deadlineDateButton.setOnClickListener { showDatePickerDialog() }
        b.deadlineTimeButton.setOnClickListener { showTimePickerDialog() }

        b.saveButton.setOnClickListener {
            val title = b.taskNameEditText.text.toString()
            val desc = b.taskDescriptionEditText.text.toString()

            if (title.isEmpty() || desc.isEmpty()){
                Toast.makeText(this, "Któreś z pól jest puste !!!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            GlobalScope.launch {
                if (taskId == -1) db.taskDao().insertSong(
                    Task(name = title, desc = desc, date = date.timeInMillis)
                ) else db.taskDao().updateSong(
                    Task(id = taskId, name = title, desc = desc, date = date.timeInMillis)
                )
                db.onInsert()
            }
            finish()
        }
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                date.set(Calendar.YEAR, year)
                date.set(Calendar.MONTH, month)
                date.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                b.deadlineDateButton.text = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
                    .format(date.time)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    private fun showTimePickerDialog() {
        val calendar = Calendar.getInstance()
        val timePickerDialog = TimePickerDialog(
            this,
            { _: TimePicker?, hourOfDay: Int, minute: Int ->
                date.set(Calendar.HOUR_OF_DAY, hourOfDay)
                date.set(Calendar.MINUTE, minute)
                b.deadlineTimeButton.text = SimpleDateFormat("HH:mm ", Locale.getDefault())
                    .format(date.time)
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        )
        timePickerDialog.show()
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun setValues(name: String, desc: String, taskDate: Long){
        runOnUiThread {
            date.timeInMillis = taskDate

            b.taskNameEditText.setText(name)
            b.taskDescriptionEditText.setText(desc)
            b.deadlineDateButton.text = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
                .format(date.time)
            b.deadlineTimeButton.text = SimpleDateFormat("HH:mm ", Locale.getDefault())
                .format(date.time)
        }
    }
}
