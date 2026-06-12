package com.vi17.assistant

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.core.content.getSystemService
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

/**
 * Application class for Vi-17.
 * Initializes Hilt, logging, and notification channels.
 */
@HiltAndroidApp
class ViApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // Initialize logging
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        // Create notification channels
        createNotificationChannels()

        Timber.d("Vi-17 Application initialized")
    }

    /**
     * Create notification channels for Android 8.0+
     */
    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = getSystemService<NotificationManager>()
                ?: return

            // Foreground Service Channel
            val foregroundChannel = NotificationChannel(
                CHANNEL_FOREGROUND_SERVICE,
                "Vi is Active",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Vi-17 is running in the background"
                enableVibration(false)
                setSound(null, null)
            }

            // Voice Interaction Channel
            val voiceChannel = NotificationChannel(
                CHANNEL_VOICE_INTERACTION,
                "Voice Interactions",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notifications about voice interactions"
                enableVibration(true)
            }

            // Memory & Context Channel
            val memoryChannel = NotificationChannel(
                CHANNEL_MEMORY,
                "Memory & Context",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Updates about memory and context"
                enableVibration(false)
            }

            // Alerts & Errors Channel
            val alertsChannel = NotificationChannel(
                CHANNEL_ALERTS,
                "Alerts & Errors",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Important alerts and error notifications"
                enableVibration(true)
            }

            notificationManager.createNotificationChannels(
                listOf(foregroundChannel, voiceChannel, memoryChannel, alertsChannel)
            )

            Timber.d("Notification channels created")
        }
    }

    companion object {
        const val CHANNEL_FOREGROUND_SERVICE = "vi_foreground_service"
        const val CHANNEL_VOICE_INTERACTION = "vi_voice_interaction"
        const val CHANNEL_MEMORY = "vi_memory"
        const val CHANNEL_ALERTS = "vi_alerts"
    }
}
