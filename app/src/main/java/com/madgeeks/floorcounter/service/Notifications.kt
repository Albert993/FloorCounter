package com.madgeeks.floorcounter.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import androidx.core.app.NotificationCompat
import com.madgeeks.floorcounter.Utils.Constants
import com.madgeeks.floorcounter.R
import java.util.*

class Notifications(private val notifContext: Context) {
    private var id: Int = UUID.randomUUID().hashCode()

    private val notificationManager: NotificationManager by lazy {
        notifContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    fun create(pendingIntent: PendingIntent, input: String): Notification {
        // Channel
        val channel1 = NotificationChannel(
            Constants.channelID,
            "Floors counter channel",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "My shitty channel"
        }

        notificationManager.createNotificationChannel(channel1)

        // notif
        return NotificationCompat.Builder(notifContext, Constants.channelID)
            .setContentTitle(Constants.foregroundServiceNotificationTitle)
            .setContentText(input)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .build()
    }
}