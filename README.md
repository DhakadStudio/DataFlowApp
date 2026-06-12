# Vi-17 Android AI Assistant

A **production-ready, privacy-first Android AI assistant** built with Kotlin and Jetpack Compose.

## 🎯 Features

- **Edge Light UI** - Animated gradient borders for visual feedback
- **Voice Pipeline** - Wake word detection → Speech recognition → LLM → Text-to-speech
- **Encrypted Memory** - Episodic, semantic, and contextual memory with vector search
- **Privacy-First** - DataGate filter blocks sensitive data from banking apps
- **On-Device AI** - Gemma 3 1B LLM, MiniLM embeddings, Porcupine wake word
- **Accessibility** - Screen reading with privacy controls
- **CI/CD** - Automatic APK builds on GitHub push

## 📋 Tech Stack

- **Language**: Kotlin 1.9+
- **UI**: Jetpack Compose + NativeWind
- **Database**: Room + SQLCipher (encrypted)
- **DI**: Hilt
- **AI/ML**: MediaPipe LLM, ONNX Runtime, Picovoice
- **Security**: AES-256-GCM, Android Keystore
- **CI/CD**: GitHub Actions

## 🚀 Quick Start

### 1. Clone Repository
```bash
git clone https://github.com/DhakadStudio/DataFlowApp.git
cd DataFlowApp/Vi17-Android
```

### 2. Open in Android Studio
```bash
open -a "Android Studio" .
```

### 3. Configure
- See `SETUP.md` for detailed setup instructions
- Add Picovoice access key to `local.properties`
- Download AI models to `app/src/main/assets/models/`

### 4. Build
```bash
./gradlew assembleDebug
```

## 📚 Documentation

| Document | Purpose |
|----------|---------|
| **README.md** | Project overview (this file) |
| **SETUP.md** | Step-by-step Android Studio setup |
| **GITHUB_ACTIONS_QUICK_START.md** | 5-minute CI/CD setup |
| **CI_CD_SETUP.md** | Detailed CI/CD configuration |
| **IMPLEMENTATION_GUIDE.md** | Code implementation steps |
| **PROJECT_STRUCTURE.md** | Directory organization |

## 🔧 Project Structure

```
Vi17-Android/
├── app/
│   ├── build.gradle.kts
│   ├── src/main/
│   │   ├── AndroidManifest.xml
│   │   ├── kotlin/com/vi17/assistant/
│   │   │   ├── core/          # Security, database, DI
│   │   │   ├── ai/            # Voice pipeline, memory, privacy
│   │   │   ├── ui/            # Compose screens and overlays
│   │   │   ├── service/       # Android services
│   │   │   └── data/          # Repositories and preferences
│   │   └── assets/models/     # AI models
│   ├── test/                  # Unit tests
│   └── androidTest/           # Instrumented tests
├── .github/workflows/
│   ├── build.yml              # Automatic APK builds
│   └── deploy.yml             # Play Store deployment
├── SETUP.md
├── IMPLEMENTATION_GUIDE.md
└── PROJECT_STRUCTURE.md
```

## 🔐 Security

- ✅ **Encryption**: AES-256-GCM with Android Keystore
- ✅ **Database**: SQLCipher encrypted storage
- ✅ **Privacy**: DataGate filter blocks sensitive data
- ✅ **Permissions**: Minimal permissions, user-controlled
- ✅ **Secrets**: GitHub Secrets for CI/CD

## 🤖 AI Components

### Wake Word Detection
- **Porcupine** by Picovoice
- "Hey Vi" wake word
- Always-on detection

### Speech Recognition
- Android SpeechRecognizer
- Real-time partial transcripts
- Confidence scoring

### Language Model
- **Gemma 3 1B** by Google
- On-device inference
- Context-aware responses

### Text-to-Speech
- Android TextToSpeech
- Multiple language support
- Natural voice synthesis

### Memory & Context
- **MiniLM-L6-v2** embeddings
- Vector similarity search
- Episodic/semantic/contextual memory

## 🔄 CI/CD Pipeline

### Automatic Builds
Every push to `master` triggers:
- ✅ Debug APK build
- ✅ Release APK build
- ✅ Lint checks
- ✅ Unit tests
- ✅ Artifact upload

### Play Store Deployment
Push a tag (`v1.0.0`) to:
- ✅ Build App Bundle
- ✅ Sign with keystore
- ✅ Upload to Play Store
- ✅ Create GitHub Release

## 📥 Download APK

1. Go to **Actions** tab
2. Click latest workflow run
3. Download from **Artifacts**

## 🛠️ Development

### Build Debug APK
```bash
./gradlew assembleDebug
```

### Build Release APK
```bash
./gradlew assembleRelease
```

### Run Tests
```bash
./gradlew test
```

### Run Lint
```bash
./gradlew lint
```

## 📱 Permissions

The app requires these permissions:

| Permission | Purpose |
|-----------|---------|
| `RECORD_AUDIO` | Microphone for voice input |
| `SYSTEM_ALERT_WINDOW` | Edge light overlay |
| `BIND_ACCESSIBILITY_SERVICE` | Screen reading |
| `MEDIA_PROJECTION` | Screen capture for context |
| `POST_NOTIFICATIONS` | Push notifications |
| `INTERNET` | API calls (optional) |

## 🎯 Next Steps

1. **Setup**: Follow `SETUP.md`
2. **Implement**: Follow `IMPLEMENTATION_GUIDE.md`
3. **Test**: Write unit and integration tests
4. **Deploy**: Configure GitHub Secrets and push
5. **Release**: Create tag to deploy to Play Store

## 📞 Support

- **Documentation**: See docs in this directory
- **Android Docs**: https://developer.android.com
- **Picovoice**: https://picovoice.ai/support
- **MediaPipe**: https://ai.google.dev/edge/mediapipe

## 📄 License

This project is provided as-is for development and educational purposes.

---

**Built with ❤️ for privacy-first AI on Android**
