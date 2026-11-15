package com.lucidia.app.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat

class AlarmReceiver : BroadcastReceiver() {

    companion object {
        const val EXTRA_ALARM_TYPE = "extra_alarm_type"
        const val EXTRA_AUTO_DISMISS_SECONDS = "extra_auto_dismiss_seconds"
        const val EXTRA_ALARM_TITLE = "extra_alarm_title"

        const val TYPE_WBTB = "WBTB"
        const val TYPE_WAKEUP = "WAKEUP"
        const val TYPE_REALITY_CHECK = "REALITY_CHECK"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val alarmType = intent.getStringExtra(EXTRA_ALARM_TYPE) ?: TYPE_WBTB
        val autoDismiss = intent.getIntExtra(EXTRA_AUTO_DISMISS_SECONDS, 15)
        val title = intent.getStringExtra(EXTRA_ALARM_TITLE) ?: "Lucid Dream Alarm"

        val serviceIntent = Intent(context, AlarmService::class.java).apply {
            putExtra(EXTRA_ALARM_TYPE, alarmType)
            putExtra(EXTRA_AUTO_DISMISS_SECONDS, autoDismiss)
            putExtra(EXTRA_ALARM_TITLE, title)
        }

        ContextCompat.startForegroundService(context, serviceIntent)
    }
}
