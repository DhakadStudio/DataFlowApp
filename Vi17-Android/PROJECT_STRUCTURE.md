# Vi-17 Project Structure

Complete directory tree and file organization for the Vi-17 Android AI Assistant.

## Root Directory

```
Vi17-Android/
├── app/                          # Main application module
├── .gitignore                    # Git ignore rules
├── README.md                     # Project overview
├── SETUP.md                      # Setup instructions
├── IMPLEMENTATION_GUIDE.md       # Implementation steps
├── PROJECT_STRUCTURE.md          # This file
├── settings.gradle.kts           # Gradle settings
├── build.gradle.kts              # Root build file
├── local.properties              # Local configuration (not in git)
└── keystore.properties           # Signing configuration (not in git)
```

## App Module Structure

```
app/
├── build.gradle.kts              # App-level Gradle configuration
├── proguard-rules.pro            # ProGuard/R8 obfuscation rules
├── src/
│   ├── main/
│   │   ├── AndroidManifest.xml   # App manifest with permissions
│   │   ├── kotlin/com/vi17/assistant/
│   │   │   ├── MainActivity.kt
│   │   │   ├── ViApplication.kt
│   │   │   │
│   │   │   ├── core/             # Core infrastructure
│   │   │   │   ├── di/           # Hilt dependency injection
│   │   │   │   │   ├── Modules.kt
│   │   │   │   │   ├── DatabaseModule.kt
│   │   │   │   │   ├── SecurityModule.kt
│   │   │   │   │   └── AIModule.kt
│   │   │   │   │
│   │   │   │   ├── security/     # Encryption & keystore
│   │   │   │   │   └── CryptoManager.kt
│   │   │   │   │
│   │   │   │   ├── database/     # Room database
│   │   │   │   │   ├── ViDatabase.kt
│   │   │   │   │   ├── ViDatabaseCallback.kt
│   │   │   │   │   ├── entity/
│   │   │   │   │   │   ├── Entities.kt
│   │   │   │   │   │   ├── MemoryEntity.kt
│   │   │   │   │   │   └── ActivityLogEntity.kt
│   │   │   │   │   ├── dao/
│   │   │   │   │   │   ├── Daos.kt
│   │   │   │   │   │   ├── MemoryDao.kt
│   │   │   │   │   │   └── ActivityLogDao.kt
│   │   │   │   │   ├── converter/
│   │   │   │   │   │   └── ByteArrayConverter.kt
│   │   │   │   │   └── migration/
│   │   │   │   │       └── Migrations.kt
│   │   │   │   │
│   │   │   │   └── utils/        # Utilities & extensions
│   │   │   │       ├── Extensions.kt
│   │   │   │       ├── Constants.kt
│   │   │   │       └── Logger.kt
│   │   │   │
│   │   │   ├── data/             # Data layer
│   │   │   │   ├── memory/
│   │   │   │   │   ├── MemoryRepository.kt
│   │   │   │   │   ├── MemoryManager.kt
│   │   │   │   │   └── MemoryContext.kt
│   │   │   │   │
│   │   │   │   ├── permissions/
│   │   │   │   │   ├── PermissionRepository.kt
│   │   │   │   │   ├── PermissionManager.kt
│   │   │   │   │   └── PermissionState.kt
│   │   │   │   │
│   │   │   │   └── preferences/
│   │   │   │       ├── PreferencesManager.kt
│   │   │   │       ├── OnboardingPreferences.kt
│   │   │   │       └── SettingsPreferences.kt
│   │   │   │
│   │   │   ├── domain/           # Domain layer (business logic)
│   │   │   │   ├── models/
│   │   │   │   │   ├── User.kt
│   │   │   │   │   ├── Memory.kt
│   │   │   │   │   └── VoiceState.kt
│   │   │   │   │
│   │   │   │   ├── usecases/
│   │   │   │   │   ├── GetMemoryUseCase.kt
│   │   │   │   │   ├── SaveMemoryUseCase.kt
│   │   │   │   │   ├── ProcessVoiceUseCase.kt
│   │   │   │   │   └── GetScreenContentUseCase.kt
│   │   │   │   │
│   │   │   │   └── interfaces/
│   │   │   │       ├── MemoryRepository.kt
│   │   │   │       ├── PermissionRepository.kt
│   │   │   │       └── VoiceRepository.kt
│   │   │   │
│   │   │   ├── service/          # Android services
│   │   │   │   ├── ViForegroundService.kt
│   │   │   │   ├── ViAccessibilityService.kt
│   │   │   │   ├── ViMediaProjectionService.kt
│   │   │   │   ├── ViWakeWordService.kt
│   │   │   │   ├── ServiceNotification.kt
│   │   │   │   └── receiver/
│   │   │   │       └── BootCompletedReceiver.kt
│   │   │   │
│   │   │   ├── ai/               # AI/ML components
│   │   │   │   ├── pipeline/
│   │   │   │   │   ├── VoicePipeline.kt
│   │   │   │   │   ├── PipelineState.kt
│   │   │   │   │   ├── PipelineConfig.kt
│   │   │   │   │   └── PipelineResult.kt
│   │   │   │   │
│   │   │   │   ├── WakeWordManager.kt
│   │   │   │   ├── SpeechRecognitionManager.kt
│   │   │   │   ├── TTSManager.kt
│   │   │   │   ├── LLMManager.kt
│   │   │   │   ├── ViSystemPrompt.kt
│   │   │   │   │
│   │   │   │   ├── memory/
│   │   │   │   │   ├── EmbeddingManager.kt
│   │   │   │   │   ├── VectorSearch.kt
│   │   │   │   │   └── SimilarityMetrics.kt
│   │   │   │   │
│   │   │   │   └── datagate/
│   │   │   │       ├── DataGate.kt
│   │   │   │       ├── ScreenContent.kt
│   │   │   │       ├── FilterResult.kt
│   │   │   │       └── SensitivePatterns.kt
│   │   │   │
│   │   │   ├── ui/               # UI layer (Jetpack Compose)
│   │   │   │   ├── overlay/
│   │   │   │   │   ├── EdgeLightUI.kt
│   │   │   │   │   ├── EdgeLightState.kt
│   │   │   │   │   ├── EdgeLightOverlay.kt
│   │   │   │   │   ├── EdgeLightAnimator.kt
│   │   │   │   │   ├── ScreenOverlay.kt
│   │   │   │   │   └── OverlayManager.kt
│   │   │   │   │
│   │   │   │   ├── onboarding/
│   │   │   │   │   ├── OnboardingActivity.kt
│   │   │   │   │   ├── OnboardingViewModel.kt
│   │   │   │   │   ├── screens/
│   │   │   │   │   │   ├── WelcomeScreen.kt
│   │   │   │   │   │   ├── FeaturesScreen.kt
│   │   │   │   │   │   ├── PermissionScreen.kt
│   │   │   │   │   │   └── ReadyScreen.kt
│   │   │   │   │   ├── PermissionConfig.kt
│   │   │   │   │   └── OnboardingPreferences.kt
│   │   │   │   │
│   │   │   │   ├── settings/
│   │   │   │   │   ├── SettingsActivity.kt
│   │   │   │   │   ├── SettingsViewModel.kt
│   │   │   │   │   ├── screens/
│   │   │   │   │   │   ├── GeneralSettings.kt
│   │   │   │   │   │   ├── PrivacySettings.kt
│   │   │   │   │   │   ├── VoiceSettings.kt
│   │   │   │   │   │   └── AboutSettings.kt
│   │   │   │   │   └── SettingsPreferences.kt
│   │   │   │   │
│   │   │   │   ├── activity_log/
│   │   │   │   │   ├── ActivityLogScreen.kt
│   │   │   │   │   ├── ActivityLogViewModel.kt
│   │   │   │   │   └── ActivityLogItem.kt
│   │   │   │   │
│   │   │   │   ├── home/
│   │   │   │   │   ├── HomeScreen.kt
│   │   │   │   │   ├── HomeViewModel.kt
│   │   │   │   │   └── HomeComponents.kt
│   │   │   │   │
│   │   │   │   ├── theme/
│   │   │   │   │   ├── Color.kt
│   │   │   │   │   ├── Type.kt
│   │   │   │   │   ├── Theme.kt
│   │   │   │   │   └── Shapes.kt
│   │   │   │   │
│   │   │   │   ├── components/
│   │   │   │   │   ├── Button.kt
│   │   │   │   │   ├── Card.kt
│   │   │   │   │   ├── Dialog.kt
│   │   │   │   │   ├── LoadingIndicator.kt
│   │   │   │   │   └── PermissionCard.kt
│   │   │   │   │
│   │   │   │   └── navigation/
│   │   │   │       ├── Navigation.kt
│   │   │   │       └── NavGraph.kt
│   │   │   │
│   │   │   └── util/             # Utility functions
│   │   │       ├── DateUtils.kt
│   │   │       ├── StringUtils.kt
│   │   │       └── PermissionUtils.kt
│   │   │
│   │   ├── res/                  # Resources
│   │   │   ├── values/
│   │   │   │   ├── strings.xml
│   │   │   │   ├── colors.xml
│   │   │   │   ├── dimens.xml
│   │   │   │   └── styles.xml
│   │   │   ├── values-night/
│   │   │   │   ├── colors.xml
│   │   │   │   └── styles.xml
│   │   │   ├── mipmap/
│   │   │   │   ├── ic_launcher.xml
│   │   │   │   └── ic_launcher_round.xml
│   │   │   ├── drawable/
│   │   │   │   ├── ic_edge_light.xml
│   │   │   │   ├── ic_microphone.xml
│   │   │   │   └── ic_settings.xml
│   │   │   └── xml/
│   │   │       ├── accessibility_service_config.xml
│   │   │       ├── file_paths.xml
│   │   │       └── data_extraction_rules.xml
│   │   │
│   │   └── assets/
│   │       └── models/
│   │           ├── gemma3-1b.bin
│   │           ├── minilm-l6-v2.onnx
│   │           └── porcupine_params.pv
│   │
│   ├── test/                     # Unit tests
│   │   └── kotlin/com/vi17/assistant/
│   │       ├── core/
│   │       │   ├── security/
│   │       │   │   └── CryptoManagerTest.kt
│   │       │   └── database/
│   │       │       └── ViDatabaseTest.kt
│   │       ├── ai/
│   │       │   ├── pipeline/
│   │       │   │   └── VoicePipelineTest.kt
│   │       │   └── datagate/
│   │       │       └── DataGateTest.kt
│   │       └── data/
│   │           └── memory/
│   │               └── MemoryRepositoryTest.kt
│   │
│   └── androidTest/              # Instrumented tests
│       └── kotlin/com/vi17/assistant/
│           ├── ui/
│           │   ├── onboarding/
│           │   │   └── OnboardingActivityTest.kt
│           │   └── settings/
│           │       └── SettingsActivityTest.kt
│           └── service/
│               └── ViForegroundServiceTest.kt
│
└── gradle/
    └── wrapper/
        ├── gradle-wrapper.jar
        └── gradle-wrapper.properties
```

## Key Files by Functionality

### Security & Encryption
- `core/security/CryptoManager.kt` - AES-256-GCM encryption
- `core/database/ViDatabase.kt` - SQLCipher encrypted database

### Database & Storage
- `core/database/ViDatabase.kt` - Room database setup
- `core/database/entity/Entities.kt` - Memory and Activity Log entities
- `core/database/dao/Daos.kt` - Database access objects
- `data/preferences/` - DataStore preferences

### Voice Processing
- `ai/pipeline/VoicePipeline.kt` - Main voice orchestrator
- `ai/WakeWordManager.kt` - Wake word detection (Porcupine)
- `ai/SpeechRecognitionManager.kt` - Speech-to-text
- `ai/TTSManager.kt` - Text-to-speech
- `ai/LLMManager.kt` - Language model inference

### Memory & Context
- `data/memory/MemoryRepository.kt` - Memory persistence
- `data/memory/MemoryManager.kt` - Memory operations
- `ai/memory/EmbeddingManager.kt` - Vector embeddings
- `ai/memory/VectorSearch.kt` - Similarity search

### Privacy & Security
- `ai/datagate/DataGate.kt` - Privacy filter
- `ai/datagate/ScreenContent.kt` - Screen data model
- `ai/datagate/SensitivePatterns.kt` - Pattern matching

### UI & Overlay
- `ui/overlay/EdgeLightUI.kt` - Edge light state & theme
- `ui/overlay/EdgeLightOverlay.kt` - Overlay implementation
- `ui/overlay/EdgeLightAnimator.kt` - Animations
- `ui/overlay/OverlayManager.kt` - Overlay coordination

### Onboarding & Permissions
- `ui/onboarding/OnboardingActivity.kt` - Onboarding flow
- `ui/onboarding/OnboardingViewModel.kt` - State management
- `ui/onboarding/screens/` - Individual screens
- `data/permissions/PermissionManager.kt` - Permission handling

### Services
- `service/ViForegroundService.kt` - Background service
- `service/ViAccessibilityService.kt` - Screen reading
- `service/ViMediaProjectionService.kt` - Screen capture
- `service/ViWakeWordService.kt` - Wake word service

### Dependency Injection
- `core/di/Modules.kt` - Hilt modules
- `ViApplication.kt` - App initialization

## Build Configuration

### Gradle Files
- `app/build.gradle.kts` - App dependencies and build config
- `settings.gradle.kts` - Project settings and repositories
- `build.gradle.kts` - Root build file (if needed)

### Configuration Files
- `local.properties` - Local SDK/NDK paths (not in git)
- `keystore.properties` - Signing configuration (not in git)
- `proguard-rules.pro` - Code obfuscation rules

## Resource Organization

### Values
- `strings.xml` - String resources
- `colors.xml` - Color definitions
- `dimens.xml` - Dimension values
- `styles.xml` - Style definitions

### Drawables
- `drawable/` - Vector drawables and images
- `mipmap/` - App icons

### XML Configuration
- `accessibility_service_config.xml` - Accessibility service settings
- `file_paths.xml` - File provider paths
- `data_extraction_rules.xml` - Backup rules

## Testing Structure

### Unit Tests
- `test/` directory for local unit tests
- Test classes mirror source structure
- Use JUnit 4 and Mockito

### Instrumented Tests
- `androidTest/` directory for device/emulator tests
- Test classes mirror source structure
- Use Espresso for UI testing

## Assets

### Models
- `assets/models/gemma3-1b.bin` - LLM model (~2.5 GB)
- `assets/models/minilm-l6-v2.onnx` - Embedding model (~22 MB)
- `assets/models/porcupine_params.pv` - Wake word model (~100 KB)

---

## Naming Conventions

### Kotlin Classes
- Activities: `*Activity.kt` (e.g., `OnboardingActivity.kt`)
- ViewModels: `*ViewModel.kt` (e.g., `OnboardingViewModel.kt`)
- Managers: `*Manager.kt` (e.g., `MemoryManager.kt`)
- Repositories: `*Repository.kt` (e.g., `MemoryRepository.kt`)
- Services: `*Service.kt` (e.g., `ViForegroundService.kt`)
- DAOs: `*Dao.kt` (e.g., `MemoryDao.kt`)
- Entities: `*Entity.kt` (e.g., `MemoryEntity.kt`)

### Composables
- Screens: `*Screen.kt` (e.g., `WelcomeScreen.kt`)
- Components: `*Component.kt` or `*Item.kt` (e.g., `PermissionCard.kt`)
- Dialogs: `*Dialog.kt` (e.g., `ConfirmDialog.kt`)

### Resources
- Strings: `snake_case` (e.g., `app_name`, `permission_microphone`)
- Colors: `snake_case` (e.g., `color_primary`, `color_error`)
- Dimensions: `snake_case` (e.g., `edge_width`, `corner_radius`)

---

## Import Organization

All Kotlin files should organize imports as follows:

```kotlin
// 1. Android framework
import android.content.Context
import android.view.WindowManager

// 2. AndroidX
import androidx.compose.runtime.Composable
import androidx.room.Entity

// 3. Jetpack libraries
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel

// 4. Third-party libraries
import com.google.dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

// 5. Project imports
import com.vi17.assistant.core.security.CryptoManager
import com.vi17.assistant.ui.overlay.EdgeLightState

// 6. Kotlin stdlib
import kotlinx.coroutines.flow.StateFlow
```

---

**This structure ensures scalability, maintainability, and clear separation of concerns across the Vi-17 codebase.**
