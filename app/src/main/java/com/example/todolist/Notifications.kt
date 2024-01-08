package com.example.todolist

import android.Manifest
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.todolist.data.AppDb
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

private const val CHANNEL_ID = "deadline_channel"

// Function to schedule a notification for a task
fun scheduleNotification(context: Context, taskId: Int, deadline: Long) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val intent = Intent(context, NotificationReceiver::class.java).apply {
        action = "SHOW_NOTIFICATION"
        putExtra("taskId", taskId)
    }
    val pendingIntent = PendingIntent.getBroadcast(context, taskId, intent,
        PendingIntent.FLAG_IMMUTABLE)

    // Schedule the notification near the deadline
    alarmManager.set(AlarmManager.RTC, deadline, pendingIntent)
}

// Function to cancel a scheduled notification for a task
fun cancelNotification(context: Context, taskId: Int) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val intent = Intent(context, NotificationReceiver::class.java).apply {
        action = "SHOW_NOTIFICATION"
        putExtra("taskId", taskId)
    }
    val pendingIntent = PendingIntent.getBroadcast(context, taskId, intent,
        PendingIntent.FLAG_IMMUTABLE)

    // Cancel the notification
    alarmManager.cancel(pendingIntent)
}

// BroadcastReceiver to handle the notification display
class NotificationReceiver : BroadcastReceiver() {
    @OptIn(DelicateCoroutinesApi::class)
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context != null && intent != null && intent.action == "SHOW_NOTIFICATION") {
            val taskId = intent.getIntExtra("taskId", 0)

            // Retrieve task details from the Room database
            GlobalScope.launch {
                val db = AppDb[context]
                val task = db.taskDao().fetchTaskById(taskId)

                // Display notification
                showNotification(context, task.name, "Minął termin dla zadania: ${task.name}")
            }
        }
    }
}

// Function to display the notification
private fun showNotification(context: Context, title: String, content: String) {
    createNotificationChannel(context)

    val builder = NotificationCompat.Builder(context, CHANNEL_ID)
        .setSmallIcon(R.drawable.baseline_notification_important_24)
        .setContentTitle(title)
        .setContentText(content)
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)

    with(NotificationManagerCompat.from(context)) {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        notify(1, builder.build())
    }
}
private fun createNotificationChannel(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val name = "Deadline Notifications"
        val descriptionText = "Channel for upcoming deadline notifications"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
            description = descriptionText
        }

        // Register the channel with the system
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}