# ProGuard rules for Vi-17 Assistant

# Keep all Kotlin classes
-keep class kotlin.** { *; }
-keepclassmembers class kotlin.** { *; }

# Keep Hilt-generated classes
-keep class dagger.hilt.** { *; }
-keep class * extends dagger.hilt.android.lifecycle.HiltViewModel { *; }
-keepclasseswithmembernames class * {
    @dagger.hilt.* <fields>;
}

# Keep Compose classes
-keep class androidx.compose.** { *; }
-keepclassmembers class androidx.compose.** { *; }

# Keep Room database classes
-keep class * extends androidx.room.RoomDatabase { *; }
-keep @androidx.room.Entity class * { *; }
-keepclassmembers class * extends androidx.room.Dao { *; }

# Keep Retrofit classes
-keep class retrofit2.** { *; }
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}

# Keep Gson classes
-keep class com.google.gson.** { *; }
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# Keep OkHttp classes
-keep class okhttp3.** { *; }
-keepclasseswithmembernames class okhttp3.** { *; }

# Keep coroutines
-keep class kotlinx.coroutines.** { *; }
-keepclassmembers class kotlinx.coroutines.** { *; }

# Keep data classes
-keep class * {
    @kotlin.Metadata <methods>;
}

# Keep BuildConfig
-keep class **.BuildConfig { *; }

# Keep R classes
-keepclassmembers class **.R$* {
    public static <fields>;
}

# Remove logging in release builds
-assumenosideeffects class timber.log.Timber {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
}

# Optimization
-optimizationpasses 5
-dontusemixedcaseclassnames
-verbose

# Preserve line numbers for debugging
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile
