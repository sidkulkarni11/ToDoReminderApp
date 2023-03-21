package com.sid.todoreminderapp

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.sid.todoreminderapp.model.Task
import kotlin.random.Random


class TaskBroadcastReceiver : BroadcastReceiver() {
    lateinit var notificationManager : NotificationManager
    var mess: String? = ""
    override fun onReceive(context: Context?, intent: Intent?) {
        val bun = intent?.getBundleExtra("bundle")
        val task = bun?.getSerializable("taskobj") as Task?

        sendNotf(context!!, task!!)


    }

        fun sendNotf(context: Context, task: Task) {
            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }

            notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager


            var notificationId = System.currentTimeMillis().toInt()
            var notifId =
                if (notificationId != null) notificationId.toInt() else System.currentTimeMillis()
                    .toInt()


            val pendingIntent: PendingIntent =
                PendingIntent.getActivity(
                    context, Random.nextInt(10000), intent,
                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                )

            val onCancelIntent = Intent(context, TaskBroadcastReceiver::class.java)
            val onDismissPendingIntent =
                PendingIntent.getBroadcast(
                    context,
                    Random.nextInt(10000),
                    onCancelIntent,
                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                )


            val soundUri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val vibrationArray: LongArray = longArrayOf(10, 20, 30, 40, 50)


            val contentView =
                RemoteViews("com.sid.todoreminderapp", R.layout.layout_custom_notification_template)
            contentView.setImageViewResource(R.id.image, R.drawable.ic_launcher_foreground)
            contentView.setTextViewText(R.id.title, task.taskName)
            contentView.setTextViewText(R.id.text, task.taskDescription)

            var builder = NotificationCompat.Builder(context, "101")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                // .setLargeIcon(icon)
                .setContentTitle("title")
                .setAutoCancel(true)
                // .setStyle(NotificationCompat.BigTextStyle().bigText(mess))
                .setStyle(NotificationCompat.DecoratedCustomViewStyle())
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setDeleteIntent(onDismissPendingIntent)
                .setSound(soundUri)
                .setVibrate(vibrationArray)
                .setContent(contentView)
                .setDefaults(Notification.DEFAULT_VIBRATE or Notification.DEFAULT_LIGHTS or Notification.DEFAULT_SOUND)



            createNotificationChannel(context)

            with(NotificationManagerCompat.from(context)) {
                notify(notifId.toString().toInt(), builder.build())
            }
        }

        private fun createNotificationChannel(context: Context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val name = "Name"
                val descriptionText = "Desc"
                val importance = NotificationManager.IMPORTANCE_HIGH
                val channel = NotificationChannel("101", name, importance).apply {
                    description = descriptionText
                }
                channel.setShowBadge(true);
                channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
                val notificationManager: NotificationManager =
                    context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.createNotificationChannel(channel)
            }
        }


}
