package com.vi17.assistant

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class ViApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        // Initialize Timber logging
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        
        // Create notification channels
        createNotificationChannels()
        
        Timber.d("Vi-17 Application initialized")
    }
    
    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = getSystemService(NotificationManager::class.java)
            
            // Voice Pipeline Channel
            val voiceChannel = NotificationChannel(
                CHANNEL_VOICE_PIPELINE,
                "Voice Pipeline",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Notifications for voice pipeline and assistant activity"
            }
            notificationManager?.createNotificationChannel(voiceChannel)
            
            // Accessibility Channel
            val accessibilityChannel = NotificationChannel(
                CHANNEL_ACCESSIBILITY,
                "Accessibility",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Notifications for accessibility service"
            }
            notificationManager?.createNotificationChannel(accessibilityChannel)
            
            // Error Channel
            val errorChannel = NotificationChannel(
                CHANNEL_ERROR,
                "Errors",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Error notifications"
            }
            notificationManager?.createNotificationChannel(errorChannel)
        }
    }
    
    companion object {
        const val CHANNEL_VOICE_PIPELINE = "voice_pipeline"
        const val CHANNEL_ACCESSIBILITY = "accessibility"
        const val CHANNEL_ERROR = "error"
    }
}
