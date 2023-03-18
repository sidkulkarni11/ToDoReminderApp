package com.sid.todoreminderapp

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.Intent.getIntent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.sid.todoreminderapp.model.Task


class TaskBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        val bun =intent?.getBundleExtra("bundle")
        val task = bun?.getSerializable("taskobj") as Task?

        var notificationId = System.currentTimeMillis().toInt()
        var contentTitle = "To App Reminder"
        var contentText = ""
        var channelId = ""

        if(task != null){
            notificationId = task.taskId.toInt() ?: notificationId
            contentTitle = task.taskName ?: contentTitle
            contentText = task.taskDescription ?: contentText
            channelId = task.taskId.toString()
        }





        val builder = NotificationCompat.Builder(context!!, channelId)
            .setSmallIcon(R.drawable.ic_baseline_notifications_24)
            .setContentTitle(contentTitle)
            .setContentText(contentText)
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        task?.taskId?.let { notificationManager.notify(it.toInt(), builder.build()) }
    }
}