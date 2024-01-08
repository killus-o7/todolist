package com.example.todolist

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.todolist.data.AppDb
import com.example.todolist.databinding.ActivityMainBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private val b by lazy {ActivityMainBinding.inflate(layoutInflater)}
    private val db by lazy { AppDb[this] }
    private lateinit var adapter: TaskAdapter
    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(b.root)
        db.onInsert = {
            runOnUiThread {
                loadTasks()
            }
        }

        adapter = TaskAdapter (
            onEdit = { task ->
                val intent = Intent(this, AddActivity::class.java)
                intent.putExtra("id", task.id)
                startActivity(intent)
            },
            onDelete = {
                MaterialAlertDialogBuilder(this).apply {
                    setTitle("Dialog usunięcia")
                    setMessage("Czy chcesz usunąć ten dźwięk?")
                    setPositiveButton("Tak") { _, _ ->
                        GlobalScope.launch {
                            db.taskDao().delete(it)
                            runOnUiThread {
                                loadTasks()
                                cancelNotification(this@MainActivity, it.id)
                            }
                        }
                    }
                    setNegativeButton("Nie") { _, _ -> }
                }.show()
            }
        )

        b.taskRecycler.adapter = adapter
        loadTasks()

        b.fab.setOnClickListener {
            val intent = Intent(this, AddActivity::class.java)
            startActivity(intent)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    @OptIn(DelicateCoroutinesApi::class)
    private fun loadTasks() = GlobalScope.launch {
        val tasks = db.taskDao().fetchTasks()

        runOnUiThread {
            adapter.dataSet.clear()
            adapter.dataSet.addAll(tasks)
            adapter.notifyDataSetChanged()
        }
    }
}