# Vi-17 Android Setup Guide

This guide walks you through setting up the Vi-17 Android project in Android Studio and preparing it for development and deployment.

## Prerequisites

Before starting, ensure you have:

- **Android Studio** 2024.1 or later ([download](https://developer.android.com/studio))
- **Android SDK** API 34 (target), API 24+ (minimum)
- **Kotlin** 1.9+
- **Gradle** 8.0+
- **Java** 17+ JDK
- **Git** for version control
- **Picovoice Account** for wake word API key (free tier available)

## Step 1: Project Setup in Android Studio

### 1.1 Clone the Repository
```bash
git clone <your-repo-url>
cd Vi17-Android
```

### 1.2 Open in Android Studio
```bash
# macOS
open -a "Android Studio" .

# Linux
android-studio .

# Windows
start android-studio.exe .
```

### 1.3 Wait for Gradle Sync
Android Studio will automatically sync Gradle files. If it doesn't:
- **File** → **Sync Now**
- If errors persist: **File** → **Invalidate Caches** → **Invalidate and Restart**

## Step 2: Configure Local Properties

Create `local.properties` in the project root:

```properties
sdk.dir=/Users/yourname/Library/Android/sdk
ndk.dir=/Users/yourname/Library/Android/sdk/ndk/25.1.8937393
porcupine.access.key=YOUR_PICOVOICE_ACCESS_KEY
```

### Finding Your SDK Path

**macOS/Linux:**
```bash
echo $ANDROID_HOME
# or
ls ~/Library/Android/sdk
```

**Windows:**
```cmd
echo %ANDROID_HOME%
```

## Step 3: Download AI Models

Vi-17 requires three AI models. Download and place them in `app/src/main/assets/models/`:

### 3.1 Gemma 3 1B (LLM)
- **Source**: [Google AI Studio](https://ai.google.dev/studio)
- **File**: `gemma3-1b.bin` (~2.5 GB)
- **Purpose**: On-device language model
- **Steps**:
  1. Go to Google AI Studio
  2. Create a new project
  3. Download Gemma 3 1B quantized model
  4. Place in `app/src/main/assets/models/gemma3-1b.bin`

### 3.2 MiniLM-L6-v2 (Embeddings)
- **Source**: [Hugging Face](https://huggingface.co/sentence-transformers/all-MiniLM-L6-v2)
- **File**: `minilm-l6-v2.onnx` (~22 MB)
- **Purpose**: Vector embeddings for memory search
- **Steps**:
  1. Download ONNX model from Hugging Face
  2. Place in `app/src/main/assets/models/minilm-l6-v2.onnx`

### 3.3 Porcupine Wake Word Model
- **Source**: [Picovoice Console](https://console.picovoice.ai)
- **File**: `porcupine_params.pv` (~100 KB)
- **Purpose**: "Hey Vi" wake word detection
- **Steps**:
  1. Create free account at Picovoice
  2. Download access key (add to `local.properties`)
  3. Download model file
  4. Place in `app/src/main/assets/models/porcupine_params.pv`

### 3.4 Verify Assets
```bash
ls -lh app/src/main/assets/models/
# Should show:
# gemma3-1b.bin (~2.5 GB)
# minilm-l6-v2.onnx (~22 MB)
# porcupine_params.pv (~100 KB)
```

## Step 4: Configure Build Variants

### 4.1 Select Build Variant
- **Build** → **Select Build Variant**
- Choose: `debugArm64` or `releaseArm64`

### 4.2 Configure Signing (Release Only)
Create `keystore.properties`:
```properties
storeFile=/path/to/keystore.jks
storePassword=your_store_password
keyAlias=your_key_alias
keyPassword=your_key_password
```

Update `app/build.gradle.kts`:
```kotlin
signingConfigs {
    release {
        storeFile = file(keystoreProperties["storeFile"])
        storePassword = keystoreProperties["storePassword"]
        keyAlias = keystoreProperties["keyAlias"]
        keyPassword = keystoreProperties["keyPassword"]
    }
}
```

## Step 5: Build and Run

### 5.1 Debug Build
```bash
./gradlew assembleDebug
```

Or in Android Studio:
- **Build** → **Make Project**

### 5.2 Run on Emulator
1. **Tools** → **Device Manager** → Create virtual device (API 34)
2. **Run** → **Run 'app'** (or press Shift+F10)

### 5.3 Run on Physical Device
1. Enable Developer Mode: Settings → About Phone → Tap Build Number 7 times
2. Enable USB Debugging: Settings → Developer Options → USB Debugging
3. Connect device via USB
4. **Run** → **Run 'app'**

### 5.4 View Logs
```bash
./gradlew logcat
# or in Android Studio: View → Tool Windows → Logcat
```

## Step 6: Grant Permissions on First Launch

When the app starts, follow the onboarding flow:

1. **Welcome Screen** - Tap "Let's set up Vi"
2. **Features Screen** - Swipe through 3 cards
3. **Notifications** - Tap "Allow Notifications"
4. **Overlay Permission** - Tap "Enable Overlay" → Grant in Settings
5. **Accessibility Service** - Tap "Enable Screen Reading" → Grant in Settings
6. **Microphone** - Tap "Allow Microphone"
7. **Ready Screen** - Tap "Open Vi"

## Step 7: Test Voice Pipeline

### 7.1 Enable Microphone
- Settings → Apps → Vi-17 → Permissions → Microphone → Allow

### 7.2 Test Wake Word
- Say "Hey Vi" near your device
- Edge light should pulse indigo (LISTENING state)

### 7.3 Test Voice Response
- After wake word: "What time is it?"
- Vi should respond with current time
- Edge light should breathe cyan (SPEAKING state)

## Step 8: Release Build

### 8.1 Generate Signed APK
```bash
./gradlew assembleRelease
```

Output: `app/build/outputs/apk/release/app-release.apk`

### 8.2 Generate App Bundle (for Play Store)
```bash
./gradlew bundleRelease
```

Output: `app/build/outputs/bundle/release/app-release.aab`

## Step 9: Play Store Submission

### 9.1 Create Developer Account
- Go to [Google Play Console](https://play.google.com/console)
- Create account ($25 one-time fee)

### 9.2 Create App
1. **Create app** → Enter app name
2. **Choose app type**: Application
3. **Select category**: Productivity or Tools

### 9.3 Upload Release
1. **Release** → **Production** → **Create release**
2. Upload `app-release.aab`
3. Fill in release notes

### 9.4 Complete Store Listing
- **App details** → Upload screenshots, icon, description
- **Content rating** → Fill questionnaire
- **Pricing & distribution** → Set price, countries

### 9.5 Submit for Review
- **Review** → Check all requirements
- **Submit** → Wait for review (typically 1-3 hours)

## Troubleshooting

| Issue | Solution |
|-------|----------|
| **Gradle sync fails** | File → Invalidate Caches → Invalidate and Restart |
| **"SDK not found"** | Check `local.properties` SDK path |
| **Models not loading** | Verify files in `app/src/main/assets/models/` |
| **Permission denied** | Grant in Settings → Apps → Vi-17 → Permissions |
| **Microphone not working** | Check RECORD_AUDIO permission, restart app |
| **Edge light not showing** | Grant SYSTEM_ALERT_WINDOW permission |
| **Accessibility service crashes** | Check accessibility service config in manifest |
| **Database encryption error** | Ensure Android API 18+ |
| **Build fails with "Out of memory"** | Increase Gradle heap: `org.gradle.jvmargs=-Xmx4g` |

## Development Tips

### Enable Logging
```kotlin
// In ViApplication.kt
if (BuildConfig.DEBUG) {
    Timber.plant(Timber.DebugTree())
}
```

### View Database
```bash
# Using Android Studio Device File Explorer
# Navigate to: /data/data/com.vi17.assistant/databases/vi_database
```

### Profile Performance
- **Profiler** → **CPU** → Record trace
- **Profiler** → **Memory** → Check for leaks

### Debug Accessibility Service
- Settings → Accessibility → Vi-17
- Enable logging in `ViAccessibilityService.kt`

## Architecture Overview

```
UI Layer (Compose)
    ↓
ViewModel (State Management)
    ↓
Use Cases (Business Logic)
    ↓
Repository (Data Abstraction)
    ↓
Data Sources (Room, Preferences, Network)
```

## Key Files

| File | Purpose |
|------|---------|
| `ViApplication.kt` | App initialization, notification channels |
| `MainActivity.kt` | Entry point |
| `OnboardingActivity.kt` | Permission flow |
| `ViForegroundService.kt` | Background service |
| `ViAccessibilityService.kt` | Screen reading |
| `EdgeLightOverlay.kt` | UI overlay |
| `VoicePipeline.kt` | Voice processing |
| `MemoryRepository.kt` | Memory management |
| `DataGate.kt` | Privacy filter |

## Next Steps

1. Customize theme colors in `ui/theme/Color.kt`
2. Implement voice pipeline managers
3. Add memory system functionality
4. Test on real device
5. Prepare Play Store submission

## Support

For issues or questions:
- Check Android documentation: https://developer.android.com
- Picovoice support: https://picovoice.ai/support
- MediaPipe docs: https://ai.google.dev/edge/mediapipe

---

**Happy coding! Vi-17 is ready for development.**
