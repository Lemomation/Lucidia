package com.lucidia.app.alarm

import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AlarmService : Service() {

    companion object {
        const val ACTION_DISMISS = "com.lucidia.app.action.DISMISS_ALARM"
    }

    private var wakeLock: PowerManager.WakeLock? = null
    private var ringtone: Ringtone? = null
    private var vibrator: Vibrator? = null
    private var autoDismissJob: Job? = null
    private val serviceScope = CoroutineScope(Dispatchers.Main)

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_DISMISS) {
            stopAlarm()
            return START_NOT_STICKY
        }

        val alarmType = intent?.getStringExtra(AlarmReceiver.EXTRA_ALARM_TYPE) ?: AlarmReceiver.TYPE_WBTB
        val autoDismissSeconds = intent?.getIntExtra(AlarmReceiver.EXTRA_AUTO_DISMISS_SECONDS, 15) ?: 15
        val title = intent?.getStringExtra(AlarmReceiver.EXTRA_ALARM_TITLE) ?: "WBTB Alarm"

        // Acquire WakeLock to ensure device stays awake during alarm and auto-dismissal
        val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        wakeLock = powerManager.newWakeLock(
            PowerManager.PARTIAL_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP,
            "Lucidia:AlarmWakeLock"
        ).apply {
            acquire((autoDismissSeconds + 5) * 1000L)
        }

        // Start Foreground Notification
        val notification = AlarmNotificationHelper.buildAlarmNotification(
            context = this,
            title = title,
            message = if (alarmType == AlarmReceiver.TYPE_WBTB) {
                "Stay still and maintain lucidity awareness"
            } else {
                "Time to record your dreams"
            },
            autoDismissSeconds = autoDismissSeconds
        )
        startForeground(AlarmNotificationHelper.NOTIFICATION_ID_ALARM, notification)

        // Play gentle chime / alarm sound
        startSoundAndVibration()

        // Configure Auto-Dismissal Timer
        if (autoDismissSeconds > 0) {
            autoDismissJob?.cancel()
            autoDismissJob = serviceScope.launch {
                delay(autoDismissSeconds * 1000L)
                // Auto dismiss without user needing to move!
                stopAlarm()
            }
        }

        return START_STICKY
    }

    private fun startSoundAndVibration() {
        try {
            val alertUri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

            ringtone = RingtoneManager.getRingtone(applicationContext, alertUri)?.apply {
                audioAttributes = AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    isLooping = true
                    volume = 0.6f // Moderate gentle volume for lucid waking
                }
                play()
            }

            vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
                vibratorManager.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            }

            // Gentle pulsing vibration pattern: wait 500ms, vibrate 400ms, wait 1000ms
            val pattern = longArrayOf(0, 400, 1000, 400, 1000)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createWaveform(pattern, 0))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(pattern, 0)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun stopAlarm() {
        autoDismissJob?.cancel()
        try {
            ringtone?.stop()
            vibrator?.cancel()
            wakeLock?.let {
                if (it.isHeld) it.release()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    override fun onDestroy() {
        stopAlarm()
        super.onDestroy()
    }
}
