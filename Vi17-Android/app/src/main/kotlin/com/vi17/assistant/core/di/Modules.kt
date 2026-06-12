package com.vi17.assistant.core.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.SharedPreferencesMigration
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.vi17.assistant.core.database.ViDatabase
import com.vi17.assistant.core.security.CryptoManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import timber.log.Timber
import javax.inject.Singleton

/**
 * Hilt dependency injection modules for Vi-17.
 */

// ============================================================================
// DATABASE MODULE
// ============================================================================

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideViDatabase(
        @ApplicationContext context: Context,
        cryptoManager: CryptoManager
    ): ViDatabase {
        Timber.d("Providing ViDatabase")
        return ViDatabase.getInstance(context, cryptoManager)
    }

    @Singleton
    @Provides
    fun provideMemoryDao(database: ViDatabase) =
        database.memoryDao()

    @Singleton
    @Provides
    fun provideActivityLogDao(database: ViDatabase) =
        database.activityLogDao()
}

// ============================================================================
// SECURITY MODULE
// ============================================================================

@Module
@InstallIn(SingletonComponent::class)
object SecurityModule {

    @Singleton
    @Provides
    fun provideCryptoManager(): CryptoManager {
        Timber.d("Providing CryptoManager")
        return CryptoManager()
    }
}

// ============================================================================
// PREFERENCES MODULE
// ============================================================================

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = "vi_preferences",
    produceMigrations = { context ->
        listOf(
            SharedPreferencesMigration(context, "vi_prefs")
        )
    }
)

@Module
@InstallIn(SingletonComponent::class)
object PreferencesModule {

    @Singleton
    @Provides
    fun providePreferencesDataStore(
        @ApplicationContext context: Context
    ): DataStore<Preferences> {
        Timber.d("Providing DataStore<Preferences>")
        return context.dataStore
    }
}

// ============================================================================
// AI MODULE
// ============================================================================

@Module
@InstallIn(SingletonComponent::class)
object AIModule {

    @Singleton
    @Provides
    fun provideWakeWordManager(@ApplicationContext context: Context): WakeWordManager {
        Timber.d("Providing WakeWordManager")
        return WakeWordManager(context)
    }

    @Singleton
    @Provides
    fun provideSpeechRecognitionManager(@ApplicationContext context: Context): SpeechRecognitionManager {
        Timber.d("Providing SpeechRecognitionManager")
        return SpeechRecognitionManager(context)
    }

    @Singleton
    @Provides
    fun provideTTSManager(@ApplicationContext context: Context): TTSManager {
        Timber.d("Providing TTSManager")
        return TTSManager(context)
    }

    @Singleton
    @Provides
    fun provideLLMManager(@ApplicationContext context: Context): LLMManager {
        Timber.d("Providing LLMManager")
        return LLMManager(context)
    }
}

// ============================================================================
// PLACEHOLDER CLASSES (to be implemented in respective files)
// ============================================================================

class WakeWordManager(context: Context)
class SpeechRecognitionManager(context: Context)
class TTSManager(context: Context)
class LLMManager(context: Context)
