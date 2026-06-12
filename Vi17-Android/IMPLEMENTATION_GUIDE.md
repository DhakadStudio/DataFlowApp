# Vi-17 Implementation Guide

This guide provides step-by-step instructions for implementing the remaining components of Vi-17 based on the generated code structure.

## Overview

The Vi-17 project has been scaffolded with the following completed components:

| Component | Status | Location |
|-----------|--------|----------|
| **PART 1: Foundation & Architecture** | ✅ Complete | `app/build.gradle.kts`, `AndroidManifest.xml`, `ViApplication.kt`, `CryptoManager.kt`, `ViDatabase.kt` |
| **PART 2: Edge Light UI** | ✅ Partial | `ui/overlay/EdgeLightUI.kt`, `EdgeLightAnimator.kt`, `OverlayManager.kt` |
| **PART 3: Onboarding & Permissions** | ⏳ Next | To be implemented |
| **PART 4: Voice Pipeline** | ✅ Partial | `ai/pipeline/VoicePipeline.kt` (orchestrator) |
| **PART 5: Memory System** | ✅ Partial | `core/database/entity/Entities.kt`, `core/database/dao/Daos.kt` |
| **PART 6: Screen Reading & DataGate** | ⏳ Next | To be implemented |

---

## PART 1: Foundation & Architecture (✅ Complete)

### What's Included

1. **Gradle Configuration** (`app/build.gradle.kts`)
   - All dependencies for Kotlin, Compose, Room, Hilt, MediaPipe, ONNX Runtime, Porcupine
   - Build variants for debug and release
   - ProGuard/R8 configuration for production

2. **Android Manifest** (`AndroidManifest.xml`)
   - All 12 required permissions declared
   - Services registered (Foreground, Accessibility, MediaProjection, WakeWord)
   - Receivers for boot completion
   - Notification channels

3. **Application Class** (`ViApplication.kt`)
   - Hilt initialization
   - Timber logging setup
   - Notification channel creation

4. **Encryption** (`CryptoManager.kt`)
   - AES-256-GCM encryption using Android Keystore
   - Hardware-backed encryption support
   - Automatic key generation and management

5. **Database** (`ViDatabase.kt`)
   - Room database with SQLCipher encryption
   - Memory and Activity Log entities
   - Type converters for ByteArray

6. **Dependency Injection** (`core/di/Modules.kt`)
   - Hilt modules for Database, Security, Preferences, AI
   - Singleton providers for all major components

### Next Steps

- Verify all dependencies resolve: `./gradlew dependencies`
- Build the project: `./gradlew build`
- Run on emulator/device to verify no runtime errors

---

## PART 2: Edge Light UI (✅ Partial)

### What's Included

1. **State Management** (`EdgeLightUI.kt`)
   - `EdgeLightState` sealed class (Idle, Listening, Thinking, Speaking, Error, SosActive)
   - Design tokens (colors, dimensions, animation timings)
   - Configuration data classes

2. **Animation Engine** (`EdgeLightAnimator.kt`)
   - Compose-based animations for each state
   - Smooth easing curves (EaseInOutSine, EaseInOutCubic)
   - Infinite repeatable animations with proper timing

3. **Overlay Management** (`OverlayManager.kt`)
   - Singleton manager for all overlays
   - State-driven overlay lifecycle
   - Configuration updates

### To Implement

1. **Complete EdgeLightOverlay.kt**
   ```kotlin
   // Implement WindowManager overlay
   // - Draw gradient edges on Canvas
   // - Apply animations from EdgeLightAnimator
   // - Handle touch events (non-blocking)
   ```

2. **Complete ScreenOverlay.kt**
   ```kotlin
   // Implement screen reading overlay
   // - Semi-transparent white background (0.06 alpha)
   // - Draggable pill with "Vi is reading" text
   // - Quick action panel on tap
   ```

3. **Theme Configuration** (`ui/theme/`)
   ```kotlin
   // Create Color.kt, Type.kt, Theme.kt
   // Define Compose Material3 theme
   // Use EdgeLightTheme colors
   ```

### Implementation Steps

```kotlin
// 1. Create EdgeLightOverlay.kt
class EdgeLightOverlay(
    context: Context,
    windowManager: WindowManager,
    stateFlow: StateFlow<EdgeLightState>,
    config: EdgeLightConfig
) {
    private val animator = EdgeLightAnimator()
    private val animationValues = rememberEdgeLightAnimationValues()

    fun show() {
        // Create WindowManager overlay
        // Add left, right, bottom edge views
        // Start animations based on state
    }

    fun updateState(state: EdgeLightState) {
        // Trigger appropriate animation
        when (state) {
            EdgeLightState.Listening -> animator.animateListening(...)
            EdgeLightState.Thinking -> animator.animateThinking(...)
            // etc.
        }
    }
}

// 2. Create ScreenOverlay.kt
class ScreenOverlay(
    context: Context,
    windowManager: WindowManager,
    config: ScreenOverlayConfig
) {
    fun show() {
        // Create semi-transparent overlay
        // Add draggable pill at top center
        // Handle pill tap → show quick actions
    }
}

// 3. Update OverlayManager to use real overlays
@Singleton
class OverlayManager @Inject constructor(
    @ApplicationContext context: Context
) {
    private fun showEdgeLight() {
        edgeLightOverlay = EdgeLightOverlay(
            context, windowManager, stateFlow, config
        )
        edgeLightOverlay?.show()
    }
}
```

---

## PART 3: Onboarding & Permission Flow

### Architecture

```
OnboardingActivity
├── OnboardingViewModel (tracks state, permissions)
├── WelcomeScreen
├── FeaturesScreen (swipeable cards)
├── PermissionScreen (reusable)
│   ├── NotificationPermission
│   ├── OverlayPermission
│   ├── AccessibilityPermission
│   ├── MicrophonePermission
├── ReadyScreen
└── PermissionManager (requests permissions)
```

### Implementation

```kotlin
// 1. Create OnboardingViewModel.kt
@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val permissionManager: PermissionManager,
    private val onboardingPreferences: OnboardingPreferences
) : ViewModel() {
    private val _currentStep = MutableStateFlow(0)
    val currentStep: StateFlow<Int> = _currentStep.asStateFlow()

    private val _grantedPermissions = MutableStateFlow(setOf<String>())
    val grantedPermissions: StateFlow<Set<String>> = _grantedPermissions.asStateFlow()

    fun requestPermission(permission: String) {
        viewModelScope.launch {
            val granted = permissionManager.requestPermission(permission)
            if (granted) {
                _grantedPermissions.value += permission
            }
        }
    }

    fun completeOnboarding() {
        viewModelScope.launch {
            onboardingPreferences.setOnboardingComplete(true)
        }
    }
}

// 2. Create OnboardingActivity.kt
@AndroidEntryPoint
class OnboardingActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Vi17Theme {
                OnboardingFlow()
            }
        }
    }
}

// 3. Create screen composables
@Composable
fun WelcomeScreen(onNext: () -> Unit) {
    ScreenContainer {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Meet Vi", style = MaterialTheme.typography.headlineLarge)
            Text("Your private AI assistant")
            Button(onClick = onNext) { Text("Let's set up Vi →") }
        }
    }
}

// 4. Create PermissionManager.kt
@Singleton
class PermissionManager @Inject constructor(
    @ApplicationContext context: Context
) {
    private val context = context

    suspend fun requestPermission(permission: String): Boolean {
        // Use ActivityResultContracts to request permission
        // Return true if granted
    }
}

// 5. Create OnboardingPreferences.kt
@Singleton
class OnboardingPreferences @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    suspend fun isOnboardingComplete(): Boolean {
        return dataStore.data.map { preferences ->
            preferences[ONBOARDING_COMPLETE] ?: false
        }.first()
    }

    suspend fun setOnboardingComplete(complete: Boolean) {
        dataStore.edit { preferences ->
            preferences[ONBOARDING_COMPLETE] = complete
        }
    }

    companion object {
        private val ONBOARDING_COMPLETE = booleanPreferencesKey("onboarding_complete")
    }
}
```

---

## PART 4: Voice Pipeline (✅ Partial)

### What's Included

- **VoicePipeline.kt**: Main orchestrator with state machine
- **PipelineState**: Sealed class for pipeline states
- **SpeechResult**, **LLMResult**: Result types

### To Implement

1. **WakeWordManager.kt**
   ```kotlin
   class WakeWordManager @Inject constructor(
       @ApplicationContext context: Context
   ) {
       private val porcupine = Porcupine.Builder()
           .setAccessKey(BuildConfig.PORCUPINE_ACCESS_KEY)
           .setKeywords(arrayOf(Porcupine.BuiltInKeyword.HEY_GOOGLE))
           .build(context)

       val wakeWordEvents: Flow<Boolean> = flow {
           while (true) {
               val result = porcupine.process(audioFrame)
               if (result) emit(true)
           }
       }
   }
   ```

2. **SpeechRecognitionManager.kt**
   ```kotlin
   class SpeechRecognitionManager @Inject constructor(
       @ApplicationContext context: Context
   ) {
       private val recognizer = SpeechRecognizer.createSpeechRecognizer(context)

       val speechResults: Flow<SpeechResult> = flow {
           recognizer.setRecognitionListener(object : RecognitionListener {
               override fun onPartialResults(results: Bundle) {
                   val partial = results.getStringArrayList(...)?.first() ?: \"\"
                   emit(SpeechResult.Partial(partial))
               }
               override fun onResults(results: Bundle) {
                   val final = results.getStringArrayList(...)?.first() ?: \"\"
                   emit(SpeechResult.Final(final, 0.9f))
               }
               override fun onError(error: Int) {
                   emit(SpeechResult.Error(error, \"Speech error\"))
               }
           })
       }
   }
   ```

3. **TTSManager.kt**
   ```kotlin
   class TTSManager @Inject constructor(
       @ApplicationContext context: Context
   ) {
       private val tts = TextToSpeech(context) { status ->
           if (status == TextToSpeech.SUCCESS) {
               tts.language = Locale(\"en\", \"IN\")
           }
       }

       suspend fun speak(text: String) {
           tts.speak(text, TextToSpeech.QUEUE_ADD, null)
       }
   }
   ```

4. **LLMManager.kt**
   ```kotlin
   class LLMManager @Inject constructor(
       @ApplicationContext context: Context
   ) {
       private val llm = LlmInference.createFromOptions(
           context,
           LlmInference.LlmInferenceOptions.builder()
               .setModelPath(\"models/gemma3-1b.bin\")
               .build()
       )

       suspend fun processStream(
           input: String,
           systemPrompt: String
       ): Flow<LLMResult> = flow {
           val response = llm.generateResponse(systemPrompt + input)
           response.split(\" \").forEach { word ->
               emit(LLMResult.Chunk(word + \" \"))
           }
           emit(LLMResult.Complete(response))
       }
   }
   ```

---

## PART 5: Memory System (✅ Partial)

### What's Included

- **MemoryEntity.kt**: Room entity with encryption
- **MemoryDao.kt**: Database operations
- **ActivityLogEntity.kt**: Audit trail
- **ActivityLogDao.kt**: Activity log operations

### To Implement

1. **EmbeddingManager.kt**
   ```kotlin
   @Singleton
   class EmbeddingManager @Inject constructor(
       @ApplicationContext context: Context
   ) {
       private val ort = OrtEnvironment.getEnvironment()
       private val session = ort.createSession(\"models/minilm-l6-v2.onnx\")

       suspend fun encodeText(text: String): FloatArray {
           // Tokenize text
           // Run through ONNX model
           // Return embedding vector
       }
   }
   ```

2. **MemoryRepository.kt**
   ```kotlin
   @Singleton
   class MemoryRepository @Inject constructor(
       private val memoryDao: MemoryDao,
       private val embeddingManager: EmbeddingManager,
       private val cryptoManager: CryptoManager
   ) {
       suspend fun saveMemory(
           content: String,
           type: MemoryType,
           context: MemoryContext
       ) {
           val encrypted = cryptoManager.encrypt(content)
           val embedding = embeddingManager.encodeText(content)
           val entity = MemoryEntity(
               content = encrypted.ciphertext.toString(),
               contentVector = embedding,
               type = type
           )
           memoryDao.insertMemory(entity)
       }

       suspend fun searchMemory(query: String, topK: Int = 5): List<MemoryEntity> {
           val queryEmbedding = embeddingManager.encodeText(query)
           val allMemories = memoryDao.getAllMemories()
           return allMemories
               .sortedByDescending { cosineSimilarity(queryEmbedding, it.contentVector) }
               .take(topK)
       }
   }
   ```

3. **MemoryManager.kt**
   ```kotlin
   @Singleton
   class MemoryManager @Inject constructor(
       private val memoryRepository: MemoryRepository,
       private val activityLogDao: ActivityLogDao
   ) {
       suspend fun retrieveRelevantMemory(query: String): String {
           val memories = memoryRepository.searchMemory(query, topK = 5)
           return memories.joinToString(\"\\n\") { \"- ${it.content}\" }
       }

       suspend fun saveConversation(userInput: String, viResponse: String) {
           memoryRepository.saveMemory(
               content = \"User: $userInput\\nVi: $viResponse\",
               type = MemoryType.EPISODIC,
               context = MemoryContext()
           )
       }
   }
   ```

---

## PART 6: Screen Reading & DataGate

### Architecture

```
ViAccessibilityService
├── Captures screen content
├── Sends to DataGate
└── Emits ScreenContent

DataGate (Privacy Filter)
├── Blocks banking apps
├── Filters OTPs, passwords, card numbers
├── Filters Aadhaar, PAN
└── Returns FilterResult

ViMediaProjectionService
└── Captures screen for visual context
```

### Implementation

```kotlin
// 1. Create ViAccessibilityService.kt
@AndroidEntryPoint
class ViAccessibilityService : AccessibilityService() {
    @Inject lateinit var dataGate: DataGate

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        when (event.eventType) {
            AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED,
            AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED -> {
                val screenContent = captureScreenContent(event)
                val filtered = dataGate.filter(screenContent)
                emitScreenContent(filtered)
            }
        }
    }

    private fun captureScreenContent(event: AccessibilityEvent): ScreenContent {
        val packageName = event.packageName.toString()
        val visibleText = extractVisibleText(rootInActiveWindow)
        return ScreenContent(
            packageName = packageName,
            visibleText = visibleText,
            timestamp = System.currentTimeMillis()
        )
    }
}

// 2. Create DataGate.kt
@Singleton
class DataGate @Inject constructor() {
    private val blockedApps = setOf(
        \"com.phonepe.app\",
        \"net.one97.paytm\",
        \"com.google.android.apps.nbu.paisa.user\",
        \"com.whatsapp\"
    )

    fun filter(screenContent: ScreenContent): FilterResult {
        // Check if app is blocked
        if (screenContent.packageName in blockedApps) {
            return FilterResult.Blocked(\"Banking app - blocked for privacy\")
        }

        // Filter sensitive patterns
        val filtered = screenContent.visibleText.filter { node ->
            !isSensitiveContent(node.text)
        }

        return FilterResult.Allowed(
            screenContent.copy(visibleText = filtered)
        )
    }

    private fun isSensitiveContent(text: String): Boolean {
        val patterns = listOf(
            Regex(\"\\\\b\\\\d{4,8}\\\\b\"),  // OTP
            Regex(\"\\\\b\\\\d{4}[\\\\s-]\\\\d{4}[\\\\s-]\\\\d{4}[\\\\s-]\\\\d{4}\\\\b\"),  // Card
            Regex(\"[A-Z]{5}\\\\d{4}[A-Z]{1}\")  // PAN
        )
        return patterns.any { it.containsMatchIn(text) }
    }
}

// 3. Create ScreenContent.kt
data class ScreenContent(
    val packageName: String,
    val activityName: String,
    val visibleText: List<TextNode>,
    val timestamp: Long,
    val isFiltered: Boolean = false,
    val blockedReason: String? = null
)

// 4. Create FilterResult.kt
sealed class FilterResult {
    data class Allowed(val content: ScreenContent) : FilterResult()
    data class Blocked(val reason: String) : FilterResult()
}
```

---

## PART 7-12: Advanced Features

### Remaining Components

| Part | Component | Priority |
|------|-----------|----------|
| 7 | Settings & Activity Log UI | Medium |
| 8 | Biometric Auth, App Control | Low |
| 9 | Testing & QA | High |
| 10 | CI/CD & Deployment | High |
| 11 | Documentation & ADRs | Medium |
| 12 | Monitoring & Analytics | Low |

### Implementation Order

1. **Complete PART 3** (Onboarding) - Required for first launch
2. **Complete PART 4** (Voice Pipeline) - Core functionality
3. **Complete PART 6** (Screen Reading) - Privacy-critical
4. **Add PART 7** (Settings UI) - User control
5. **Add PART 9** (Tests) - Quality assurance
6. **Add PART 10** (CI/CD) - Deployment

---

## Testing Strategy

### Unit Tests
```kotlin
// Test CryptoManager
@Test
fun testEncryptionDecryption() {
    val plaintext = \"secret data\"
    val encrypted = cryptoManager.encrypt(plaintext)
    val decrypted = cryptoManager.decrypt(encrypted)
    assertEquals(plaintext, decrypted)
}

// Test DataGate
@Test
fun testBankingAppBlocked() {
    val screenContent = ScreenContent(
        packageName = \"com.phonepe.app\",
        visibleText = listOf(TextNode(\"OTP: 1234\"))
    )
    val result = dataGate.filter(screenContent)
    assertTrue(result is FilterResult.Blocked)
}
```

### Integration Tests
```kotlin
// Test Voice Pipeline
@Test
fun testVoicePipeline() {
    val pipeline = VoicePipeline(...)
    pipeline.initialize()
    // Simulate wake word
    // Verify state transitions
    // Verify response generation
}
```

---

## Deployment Checklist

- [ ] All tests passing (unit + integration)
- [ ] Proguard/R8 rules configured
- [ ] Signing keys generated
- [ ] Privacy policy written
- [ ] Screenshots prepared
- [ ] Store listing completed
- [ ] Beta testing on 10+ devices
- [ ] Crash reporting configured
- [ ] Analytics integrated
- [ ] Play Store submission

---

## References

- [Android Documentation](https://developer.android.com)
- [Jetpack Compose](https://developer.android.com/jetpack/compose)
- [Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-overview.html)
- [Room Database](https://developer.android.com/training/data-storage/room)
- [Hilt Dependency Injection](https://developer.android.com/training/dependency-injection/hilt-android)
- [MediaPipe LLM Inference](https://ai.google.dev/edge/mediapipe/solutions/genai/llm_inference)
- [Picovoice Porcupine](https://picovoice.ai/platform/porcupine/)

---

**Next Step**: Start implementing PART 3 (Onboarding) following the code examples above.
