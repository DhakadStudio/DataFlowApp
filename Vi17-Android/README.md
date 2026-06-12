# Vi-17: Production-Ready Android AI Assistant

**Vi-17** is a privacy-first, on-device AI assistant for Android built with **Kotlin** and **Jetpack Compose**. It features an edge-light UI, screen awareness, voice pipeline, encrypted memory, and full app control—like having Claude Code as a native Android assistant.

## Overview

This project implements the complete Vi-17 vision across 12 architectural components:

| Part | Component | Description |
|------|-----------|-------------|
| 1 | **Foundation & Architecture** | Gradle setup, AndroidManifest, Hilt DI, Room Database, CryptoManager |
| 2 | **Edge Light UI** | Animated overlay with state-driven animations (Listening, Thinking, Speaking, Error) |
| 3 | **Onboarding & Permissions** | 7-screen permission flow with privacy-first messaging |
| 4 | **Voice Pipeline** | STT → LLM → TTS with streaming and memory integration |
| 5 | **Memory System** | Encrypted episodic, semantic, and contextual memory with vector search |
| 6 | **Screen Reading & DataGate** | AccessibilityService with privacy filter (blocks banking/sensitive apps) |
| 7 | **Settings & Activity Log** | User-facing controls and audit trail |
| 8 | **Advanced Features** | Biometric auth, app control, notifications |
| 9 | **Testing & QA** | Unit tests, integration tests, performance benchmarks |
| 10 | **Deployment & CI/CD** | GitHub Actions, signed APK builds, Play Store submission |
| 11 | **Documentation** | Architecture decision records, API docs, troubleshooting |
| 12 | **Maintenance & Monitoring** | Crash reporting, analytics, performance monitoring |

---

## Tech Stack

| Layer | Technology | Purpose |
|-------|-----------|---------|
| **Language** | Kotlin 1.9+ | Type-safe, concise Android development |
| **UI Framework** | Jetpack Compose | Modern declarative UI |
| **Architecture** | MVVM + Clean Architecture | Separation of concerns, testability |
| **DI** | Hilt | Compile-time dependency injection |
| **Async** | Coroutines + Flow | Non-blocking, reactive programming |
| **Database** | Room + SQLCipher | Encrypted local persistence |
| **Security** | Android Keystore (AES-256-GCM) | Hardware-backed encryption |
| **Services** | Foreground Service + AccessibilityService + MediaProjection | Background operation, screen reading, screen capture |
| **AI/LLM** | MediaPipe LLM Inference API | On-device model inference (Gemma 3 1B) |
| **STT** | Android SpeechRecognizer (V1), Whisper (V2) | Speech-to-text |
| **TTS** | Android TextToSpeech (V1), Piper (V2) | Text-to-speech |
| **Wake Word** | Picovoice Porcupine | "Hey Vi" detection |
| **Embeddings** | MiniLM-L6 via ONNX Runtime | Vector search for memory |
| **Build** | Gradle with KTS | Build automation |

---

## Project Structure

```
com.vi17.assistant/
├── core/
│   ├── security/              # Keystore, encryption, CryptoManager
│   ├── database/              # Room, SQLCipher, DAO, entities
│   ├── di/                    # Hilt modules
│   └── utils/                 # Extensions, constants
├── data/
│   ├── memory/                # MemoryRepository, MemoryEntity
│   ├── permissions/           # PermissionRepository, DataGate
│   └── preferences/           # DataStore preferences
├── domain/
│   ├── models/                # Data classes
│   ├── usecases/              # Business logic
│   └── interfaces/            # Repository contracts
├── service/
│   ├── ViForegroundService.kt         # Main persistent service
│   ├── ViAccessibilityService.kt      # Screen reading
│   ├── ViMediaProjectionService.kt    # Screen capture
│   └── ViWakeWordService.kt           # Wake word detection
├── ai/
│   ├── pipeline/              # STT → LLM → TTS pipeline
│   ├── llm/                   # MediaPipe LLM wrapper
│   ├── memory/                # Vector search, embedding
│   └── datagate/              # DataGate privacy filter
├── ui/
│   ├── overlay/               # Edge light, floating UI
│   ├── onboarding/            # First-run setup
│   ├── settings/              # All settings screens
│   ├── permissions/           # Permission explanation screens
│   ├── activity_log/          # What Vi did log
│   └── theme/                 # Design system
└── MainActivity.kt
```

---

## Key Features

### 1. Edge Light UI
Instead of a floating orb, Vi-17 displays glowing lines along the phone's edges:
- **IDLE**: Invisible
- **LISTENING**: Soft pulse (indigo #1A0A3D with electric blue shimmer #4A90E2)
- **THINKING**: Faster pulse, violet (#7B2FBE)
- **SPEAKING**: Breathing animation (cyan-white #A8EDEA)
- **ERROR**: Red flash (#FF3B3B)
- **SOS ACTIVE**: Rapid red pulse

### 2. Privacy-First Architecture
- **DataGate**: Blocks banking apps, OTPs, passwords, payment info
- **Encrypted Memory**: AES-256-GCM with Android Keystore
- **No Cloud**: All processing on-device
- **Audit Trail**: User can view and delete all activity

### 3. Voice Pipeline
```
User: "Hey Vi"
  ↓
Wake Word Detected (Porcupine)
  ↓
Edge Light → LISTENING
  ↓
Speech Recognition (Android SpeechRecognizer)
  ↓
Memory Retrieval (parallel, streaming)
  ↓
DataGate Filter (privacy check)
  ↓
LLM Processing (MediaPipe, Gemma 3 1B)
  ↓
Edge Light → SPEAKING
  ↓
Text-to-Speech Response
  ↓
Edge Light → IDLE
```

### 4. Memory System
Three types of encrypted memory:
- **Episodic**: Conversation history, recent interactions
- **Semantic**: Facts about user ("prefers short answers", "works at IIT Madras")
- **Contextual**: What was on screen, active app, time/location

Vector search via MiniLM embeddings for semantic retrieval.

### 5. Onboarding Flow
7 screens with progressive permission requests:
1. Welcome (animated edge light)
2. Features (3 swipeable cards)
3. Notifications
4. Overlay Permission
5. Accessibility Service (most sensitive)
6. Microphone
7. Ready! (celebration animation)

---

## Setup Instructions

### Prerequisites
- **Android Studio** 2024.1 or later
- **Android SDK** API 24+ (minimum), API 34+ (target)
- **Kotlin** 1.9+
- **Gradle** 8.0+
- **Java** 17+

### Step 1: Clone and Open in Android Studio
```bash
git clone <your-repo-url>
cd Vi17-Android
open -a "Android Studio" .
```

### Step 2: Configure Local Properties
Create `local.properties`:
```properties
sdk.dir=/path/to/Android/sdk
ndk.dir=/path/to/Android/ndk
```

### Step 3: Download Models
Place the following in `app/src/main/assets/models/`:
- `gemma3-1b.bin` (MediaPipe LLM model)
- `minilm-l6-v2.onnx` (Embedding model)
- `porcupine_params.pv` (Wake word model)

### Step 4: Build and Run
```bash
./gradlew build
./gradlew installDebug
```

Or use Android Studio: **Build → Make Project** → **Run → Run 'app'**

### Step 5: Grant Permissions
On first launch, follow the onboarding flow to grant:
- Microphone
- Overlay (Display over other apps)
- Accessibility Service
- Notifications

---

## Permissions Declared

| Permission | Purpose | SDK Level |
|-----------|---------|-----------|
| `RECORD_AUDIO` | Microphone for voice input | 1 |
| `FOREGROUND_SERVICE` | Background service | 5 |
| `FOREGROUND_SERVICE_MICROPHONE` | Mic in foreground service | 31 |
| `FOREGROUND_SERVICE_SPECIAL_USE` | Special use foreground service | 31 |
| `SYSTEM_ALERT_WINDOW` | Edge light overlay | 1 |
| `BIND_ACCESSIBILITY_SERVICE` | Screen reading | 4 |
| `RECEIVE_BOOT_COMPLETED` | Start on device boot | 1 |
| `VIBRATE` | Haptic feedback | 1 |
| `READ_CONTACTS` | Optional: contact context | 1 |
| `ACCESS_FINE_LOCATION` | Optional: location context | 1 |
| `POST_NOTIFICATIONS` | Push notifications | 31 |
| `USE_BIOMETRIC` | Biometric auth | 28 |
| `USE_FINGERPRINT` | Fingerprint auth (legacy) | 23 |

**NOT Requested**: `READ_SMS`, `READ_CALL_LOG`, `CAMERA` (unless explicit user action)

---

## File Structure by Phase

### PART 1: Foundation & Architecture
```
app/build.gradle.kts              # Gradle dependencies, build config
settings.gradle.kts               # Project settings
AndroidManifest.xml               # Permissions, services, receivers
src/main/kotlin/com/vi17/assistant/
  ├── ViApplication.kt            # Hilt Application class
  ├── core/
  │   ├── di/
  │   │   ├── DatabaseModule.kt
  │   │   ├── SecurityModule.kt
  │   │   └── AIModule.kt
  │   ├── database/
  │   │   ├── ViDatabase.kt
  │   │   └── ViDatabaseCallback.kt
  │   ├── security/
  │   │   └── CryptoManager.kt
  │   └── utils/
  │       ├── Extensions.kt
  │       └── Constants.kt
```

### PART 2: Edge Light UI
```
src/main/kotlin/com/vi17/assistant/
  ├── ui/overlay/
  │   ├── EdgeLightOverlay.kt
  │   ├── EdgeLightState.kt
  │   ├── EdgeLightAnimator.kt
  │   ├── ScreenOverlay.kt
  │   └── OverlayManager.kt
  ├── ui/theme/
  │   ├── Color.kt
  │   ├── Type.kt
  │   └── Theme.kt
```

### PART 3: Onboarding & Permissions
```
src/main/kotlin/com/vi17/assistant/
  ├── ui/onboarding/
  │   ├── OnboardingActivity.kt
  │   ├── OnboardingViewModel.kt
  │   ├── screens/
  │   │   ├── WelcomeScreen.kt
  │   │   ├── FeaturesScreen.kt
  │   │   ├── PermissionScreen.kt
  │   │   └── ReadyScreen.kt
  │   ├── PermissionConfig.kt
  │   └── OnboardingPreferences.kt
  ├── data/permissions/
  │   └── PermissionManager.kt
```

### PART 4: Voice Pipeline
```
src/main/kotlin/com/vi17/assistant/
  ├── ai/pipeline/
  │   ├── VoicePipeline.kt
  │   ├── PipelineState.kt
  │   └── PipelineConfig.kt
  ├── ai/
  │   ├── WakeWordManager.kt
  │   ├── SpeechRecognitionManager.kt
  │   ├── TTSManager.kt
  │   ├── LLMManager.kt
  │   └── ViSystemPrompt.kt
  ├── service/
  │   ├── ViForegroundService.kt
  │   ├── ViWakeWordService.kt
  │   └── ServiceNotification.kt
```

### PART 5: Memory System
```
src/main/kotlin/com/vi17/assistant/
  ├── data/memory/
  │   ├── MemoryRepository.kt
  │   ├── MemoryManager.kt
  │   └── MemoryContext.kt
  ├── core/database/
  │   ├── entity/
  │   │   ├── MemoryEntity.kt
  │   │   └── ActivityLogEntity.kt
  │   ├── dao/
  │   │   ├── MemoryDao.kt
  │   │   └── ActivityLogDao.kt
  ├── ai/memory/
  │   ├── EmbeddingManager.kt
  │   └── VectorSearch.kt
```

### PART 6: Screen Reading & DataGate
```
src/main/kotlin/com/vi17/assistant/
  ├── service/
  │   ├── ViAccessibilityService.kt
  │   └── ViMediaProjectionService.kt
  ├── data/permissions/
  │   ├── DataGate.kt
  │   ├── ScreenContent.kt
  │   └── FilterResult.kt
```

---

## Build & Deployment

### Debug Build
```bash
./gradlew assembleDebug
```
Output: `app/build/outputs/apk/debug/app-debug.apk`

### Release Build (Signed)
```bash
./gradlew assembleRelease \
  -Pandroid.injected.signing.store.file=<keystore_path> \
  -Pandroid.injected.signing.store.password=<password> \
  -Pandroid.injected.signing.key.alias=<alias> \
  -Pandroid.injected.signing.key.password=<password>
```
Output: `app/build/outputs/apk/release/app-release.apk`

### Play Store Submission
1. Generate signed APK (above)
2. Create Bundle: `./gradlew bundleRelease`
3. Upload to Google Play Console
4. Configure store listing, screenshots, privacy policy
5. Submit for review

---

## Testing

### Unit Tests
```bash
./gradlew testDebugUnitTest
```

### Instrumented Tests (on device/emulator)
```bash
./gradlew connectedAndroidTest
```

### Test Coverage
```bash
./gradlew testDebugUnitTestCoverage
```

---

## Architecture Patterns

### MVVM + Clean Architecture
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

### Dependency Injection (Hilt)
```kotlin
@HiltViewModel
class MyViewModel @Inject constructor(
  private val repository: MyRepository
) : ViewModel() {
  // ...
}
```

### Reactive Streams (Flow)
```kotlin
val uiState: StateFlow<UiState> = repository.data
  .map { /* transform */ }
  .stateIn(viewModelScope, SharingStarted.Lazily, initialValue)
```

---

## Configuration

### Build Variants
- **Debug**: Development build with logging
- **Release**: Optimized production build

### Flavors (Optional)
```gradle
flavorDimensions "environment"
productFlavors {
  dev { dimension "environment" }
  prod { dimension "environment" }
}
```

### ProGuard/R8 Rules
See `proguard-rules.pro` for obfuscation and optimization rules.

---

## Troubleshooting

| Issue | Solution |
|-------|----------|
| **Gradle sync fails** | Invalidate caches: File → Invalidate Caches → Invalidate and Restart |
| **Permission denied (Accessibility)** | Grant in Settings → Accessibility → Vi-17 |
| **Edge light not showing** | Check SYSTEM_ALERT_WINDOW permission in Settings → Apps → Permissions |
| **STT not working** | Ensure RECORD_AUDIO permission granted; check device language settings |
| **LLM model not loading** | Verify model file in `assets/models/gemma3-1b.bin` |
| **Memory encryption error** | Check Android Keystore availability (API 18+) |
| **Foreground service crash** | Verify notification channel created in `ServiceNotification.kt` |

---

## Performance Optimization

### Memory Management
- Use `WeakReference` for listeners
- Clear caches on low memory
- Profile with Android Profiler

### Battery Optimization
- Use `WorkManager` for background tasks
- Batch network requests
- Optimize sensor polling intervals

### Startup Time
- Lazy-load heavy modules
- Use `@HiltViewModel` for deferred initialization
- Profile with Startup Library

---

## Security Best Practices

### Encryption
- All sensitive data encrypted with AES-256-GCM
- Keys stored in Android Keystore (hardware-backed if available)
- Never hardcode secrets

### Network
- Use HTTPS only
- Certificate pinning for API calls
- Validate SSL certificates

### Code Obfuscation
- Enable R8/ProGuard in release builds
- Keep API contracts unobfuscated
- Test obfuscated builds before release

---

## Next Steps

1. **Clone this repository** into Android Studio
2. **Download AI models** and place in `assets/models/`
3. **Configure signing keys** for release builds
4. **Run onboarding flow** to test permissions
5. **Test voice pipeline** with "Hey Vi" wake word
6. **Submit to Play Store** following guidelines

---

## References

- [Android Documentation](https://developer.android.com/)
- [Jetpack Compose](https://developer.android.com/jetpack/compose)
- [Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-overview.html)
- [Hilt Dependency Injection](https://developer.android.com/training/dependency-injection/hilt-android)
- [Room Persistence Library](https://developer.android.com/training/data-storage/room)
- [Android Security & Privacy](https://developer.android.com/privacy-and-security)
- [MediaPipe LLM Inference](https://ai.google.dev/edge/mediapipe/solutions/genai/llm_inference)
- [Picovoice Porcupine](https://picovoice.ai/platform/porcupine/)

---

**Built with ❤️ for privacy-first Android development.**
