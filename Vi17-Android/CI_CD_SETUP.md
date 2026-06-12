# GitHub Actions CI/CD Setup Guide

This guide explains how to set up **automatic APK builds** on GitHub. Every time you push code, GitHub will automatically build the APK for you.

## Overview

The CI/CD pipeline includes:

| Workflow | Trigger | Output |
|----------|---------|--------|
| **build.yml** | Push to main/develop, PR | Debug APK, Release APK, Artifacts |
| **deploy.yml** | Push tag (v*) | Release to Play Store, GitHub Release |

---

## Step 1: Create GitHub Repository

1. Go to [GitHub.com](https://github.com)
2. Create new repository: **Vi17-Android**
3. Clone locally:
   ```bash
   git clone https://github.com/YOUR_USERNAME/Vi17-Android.git
   cd Vi17-Android
   ```

## Step 2: Add Project Files

Copy all generated files to the repository:

```bash
# Copy all files from Vi17-Android-Kotlin to the cloned repo
cp -r /path/to/Vi17-Android-Kotlin/* .
git add .
git commit -m "Initial commit: Vi-17 Android project"
git push origin main
```

## Step 3: Set Up GitHub Secrets

GitHub Secrets are encrypted environment variables used in workflows.

### 3.1 Generate Signing Keystore

First, create a keystore for signing APKs:

```bash
keytool -genkey -v -keystore vi17-release.jks \
  -keyalg RSA -keysize 2048 -validity 10000 \
  -alias vi17-key \
  -storepass YOUR_STORE_PASSWORD \
  -keypass YOUR_KEY_PASSWORD
```

### 3.2 Encode Keystore to Base64

```bash
base64 -i vi17-release.jks -o keystore-base64.txt
```

Copy the contents of `keystore-base64.txt`.

### 3.3 Add Secrets to GitHub

1. Go to repository: **Settings** → **Secrets and variables** → **Actions**
2. Click **New repository secret** and add each:

| Secret Name | Value |
|-------------|-------|
| `KEYSTORE_FILE` | Base64-encoded keystore (from step 3.2) |
| `KEYSTORE_PASSWORD` | Your keystore password |
| `KEY_ALIAS` | `vi17-key` |
| `KEY_PASSWORD` | Your key password |
| `PORCUPINE_ACCESS_KEY` | Your Picovoice access key |
| `PLAY_STORE_SERVICE_ACCOUNT` | Google Play service account JSON (optional) |
| `SLACK_WEBHOOK_URL` | Slack webhook for notifications (optional) |

### Example: Adding KEYSTORE_FILE Secret

1. Click **New repository secret**
2. Name: `KEYSTORE_FILE`
3. Value: Paste the base64 content from `keystore-base64.txt`
4. Click **Add secret**

---

## Step 4: Configure Models

The workflow needs access to AI models. You have two options:

### Option A: Host Models on GitHub Releases (Recommended)

1. **Create a separate "models" repository**:
   ```bash
   git clone https://github.com/YOUR_USERNAME/Vi17-Models.git
   cd Vi17-Models
   
   # Add models
   mkdir models
   cp gemma3-1b.bin models/
   cp minilm-l6-v2.onnx models/
   cp porcupine_params.pv models/
   
   git add models/
   git commit -m "Add AI models"
   git push origin main
   ```

2. **Create a GitHub Release** with model files attached:
   - Go to Releases
   - Create new release: `v1.0-models`
   - Upload model files
   - Publish

3. **Update workflow to download models**:
   ```yaml
   - name: Download models
     run: |
       mkdir -p app/src/main/assets/models
       wget https://github.com/YOUR_USERNAME/Vi17-Models/releases/download/v1.0-models/gemma3-1b.bin -O app/src/main/assets/models/gemma3-1b.bin
       wget https://github.com/YOUR_USERNAME/Vi17-Models/releases/download/v1.0-models/minilm-l6-v2.onnx -O app/src/main/assets/models/minilm-l6-v2.onnx
       wget https://github.com/YOUR_USERNAME/Vi17-Models/releases/download/v1.0-models/porcupine_params.pv -O app/src/main/assets/models/porcupine_params.pv
   ```

### Option B: Use Git LFS (Large File Storage)

1. **Install Git LFS**:
   ```bash
   brew install git-lfs  # macOS
   # or
   sudo apt-get install git-lfs  # Linux
   ```

2. **Track large files**:
   ```bash
   git lfs install
   git lfs track "*.bin" "*.onnx"
   git add .gitattributes
   ```

3. **Commit models**:
   ```bash
   git add app/src/main/assets/models/
   git commit -m "Add AI models (LFS)"
   git push origin main
   ```

---

## Step 5: Test the Workflow

### 5.1 Trigger Build Manually

1. Go to repository → **Actions**
2. Select **Build APK** workflow
3. Click **Run workflow** → **Run workflow**
4. Wait for build to complete (5-10 minutes)

### 5.2 View Build Results

1. Click on the workflow run
2. Expand **build** job
3. Scroll down to see:
   - Build logs
   - Artifact downloads
   - Any errors

### 5.3 Download APK

1. Scroll to **Artifacts** section
2. Download **app-debug** or **app-release**
3. Extract and install on device:
   ```bash
   adb install app-debug.apk
   ```

---

## Step 6: Automate Builds on Push

Now every push triggers a build automatically:

```bash
# Make changes to code
git add .
git commit -m "Add new feature"
git push origin main
```

GitHub will automatically:
1. ✅ Build debug APK
2. ✅ Build release APK
3. ✅ Run lint checks
4. ✅ Run unit tests
5. ✅ Upload artifacts
6. ✅ Create release (if on main branch)

---

## Step 7: Deploy to Play Store (Optional)

### 7.1 Get Play Store Service Account

1. Go to [Google Play Console](https://play.google.com/console)
2. **Settings** → **API access**
3. Create new service account
4. Download JSON key file
5. Encode to base64:
   ```bash
   base64 -i service-account.json -o service-account-base64.txt
   ```

### 7.2 Add Secret

Add to GitHub Secrets:
- Name: `PLAY_STORE_SERVICE_ACCOUNT`
- Value: Base64 content from above

### 7.3 Deploy via Tag

To deploy to Play Store:

```bash
# Create and push a tag
git tag v1.0.0
git push origin v1.0.0
```

This triggers the **deploy.yml** workflow which:
- Builds release APK
- Builds App Bundle
- Uploads to Play Store (internal track)
- Creates GitHub Release

---

## Workflow Files

### build.yml (Main Build Workflow)

**Triggers**:
- Push to `main` or `develop`
- Pull requests to `main` or `develop`
- Manual trigger (workflow_dispatch)

**Jobs**:
1. **build** - Compiles debug and release APKs
2. **lint** - Runs Android lint checks
3. **test** - Runs unit tests

**Outputs**:
- Debug APK (30-day retention)
- Release APK (90-day retention)
- Lint reports
- Test results

### deploy.yml (Play Store Deployment)

**Triggers**:
- Push tag matching `v*` (e.g., `v1.0.0`)
- Manual trigger

**Jobs**:
1. Builds release APK
2. Builds App Bundle
3. Signs with keystore
4. Uploads to Play Store
5. Creates GitHub Release
6. Sends Slack notification

---

## Monitoring Builds

### View Build Status

1. Go to repository → **Actions**
2. See all workflow runs
3. Click on a run to see details

### Check Logs

1. Click on workflow run
2. Expand **build** job
3. Expand each step to see logs

### Download Artifacts

1. Click on workflow run
2. Scroll to **Artifacts** section
3. Download APK or reports

---

## Troubleshooting

| Issue | Solution |
|-------|----------|
| **Build fails: "SDK not found"** | Workflow automatically sets up SDK. Check logs for errors. |
| **"Keystore password incorrect"** | Verify secret values in Settings → Secrets. |
| **Models not downloading** | Check model URLs in workflow. Ensure files exist in release. |
| **APK not signed** | Verify KEYSTORE_FILE, KEYSTORE_PASSWORD, KEY_ALIAS, KEY_PASSWORD secrets. |
| **Play Store upload fails** | Verify PLAY_STORE_SERVICE_ACCOUNT secret and app version in build.gradle.kts. |
| **Workflow doesn't trigger** | Check branch name (main/develop) and ensure files are in `.github/workflows/`. |

---

## Environment Variables in Workflow

The workflow uses these environment variables:

```yaml
env:
  ANDROID_HOME: /opt/hostedtoolcache/android
  GRADLE_OPTS: -Xmx4g
  JAVA_VERSION: 17
```

To add custom variables, edit the workflow file:

```yaml
env:
  MY_VARIABLE: value
```

---

## Secrets Best Practices

✅ **Do**:
- Use GitHub Secrets for sensitive data
- Rotate keystore passwords regularly
- Use separate keystores for debug/release
- Keep service account keys secure

❌ **Don't**:
- Commit secrets to repository
- Share keystore files
- Hardcode passwords in code
- Use weak passwords

---

## Advanced Configuration

### Build Multiple APKs

To build APKs for different architectures:

```gradle
splits {
    abi {
        enable true
        reset()
        include 'arm64-v8a', 'armeabi-v7a'
        universalApk true
    }
}
```

### Custom Build Variants

```gradle
buildTypes {
    debug { ... }
    release { ... }
    staging { ... }
}
```

### Parallel Builds

GitHub Actions runs jobs in parallel by default. To run sequentially:

```yaml
jobs:
  build:
    runs-on: ubuntu-latest
  test:
    needs: build
    runs-on: ubuntu-latest
```

---

## Performance Optimization

### Cache Gradle Dependencies

```yaml
- uses: actions/setup-java@v4
  with:
    cache: gradle
```

This caches Gradle dependencies, reducing build time by 50%.

### Use Smaller Runners

For faster builds, use `ubuntu-latest` (default) or `ubuntu-20.04`.

### Parallel Testing

```yaml
- run: ./gradlew testDebugUnitTest --parallel
```

---

## Integration with Other Services

### Slack Notifications

Add to secrets: `SLACK_WEBHOOK_URL`

The workflow will send notifications on build success/failure.

### Email Notifications

GitHub sends automatic email notifications. Configure in:
- Settings → Notifications

### Discord Webhooks

Replace Slack webhook with Discord webhook URL.

---

## References

- [GitHub Actions Documentation](https://docs.github.com/en/actions)
- [Android Gradle Plugin](https://developer.android.com/studio/build)
- [Google Play Console](https://play.google.com/console)
- [GitHub Secrets](https://docs.github.com/en/actions/security-guides/encrypted-secrets)

---

## Next Steps

1. ✅ Create GitHub repository
2. ✅ Push project files
3. ✅ Add GitHub Secrets
4. ✅ Configure models
5. ✅ Test workflow manually
6. ✅ Push code to trigger automatic builds
7. ✅ Download APK from artifacts

**Your CI/CD pipeline is ready! Every push will now automatically build your APK. 🚀**
