package com.lucidia.app.alarm

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.lucidia.app.MainActivity

object AlarmNotificationHelper {
    const val CHANNEL_ID_ALARM = "lucidia_alarm_channel"
    const val CHANNEL_ID_REALITY_CHECK = "lucidia_reality_channel"
    const val NOTIFICATION_ID_ALARM = 1001
    const val NOTIFICATION_ID_REALITY = 1002

    fun buildAlarmNotification(
        context: Context,
        title: String,
        message: String,
        autoDismissSeconds: Int
    ): Notification {
        val openAppIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingOpen = PendingIntent.getActivity(
            context,
            0,
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val dismissIntent = Intent(context, AlarmService::class.java).apply {
            action = AlarmService.ACTION_DISMISS
        }
        val pendingDismiss = PendingIntent.getService(
            context,
            1,
            dismissIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val autoDismissNotice = if (autoDismissSeconds > 0) {
            "Auto-dismisses in ${autoDismissSeconds}s"
        } else {
            "Manual dismiss required"
        }

        return NotificationCompat.Builder(context, CHANNEL_ID_ALARM)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle(title)
            .setContentText("$message ($autoDismissNotice)")
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setOngoing(true)
            .setAutoCancel(false)
            .setContentIntent(pendingOpen)
            .addAction(
                android.R.drawable.ic_menu_close_clear_cancel,
                "Dismiss Now",
                pendingDismiss
            )
            .build()
    }
}
