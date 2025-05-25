package pl.parfen.blockappstudyrelease.blockservice

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import pl.parfen.blockappstudyrelease.MainActivity
import pl.parfen.blockappstudyrelease.R

class MonitoringNotificationHelper(private val context: Context) {
    companion object {
        private const val CHANNEL_ID = "monitoring_channel"
    }

    fun createNotificationChannel() {
        val channelName = context.getString(R.string.monitoring_service_channel_name)
        val channel = NotificationChannel(
            CHANNEL_ID,
            channelName,
            NotificationManager.IMPORTANCE_DEFAULT
        )
        val manager = context.getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }

    fun createForegroundNotification(): Notification {
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            Intent(context, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )
        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle(context.getString(R.string.monitoring_service_channel_name))
            .setContentText(context.getString(R.string.monitoring_service_notification))
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()
    }
}
