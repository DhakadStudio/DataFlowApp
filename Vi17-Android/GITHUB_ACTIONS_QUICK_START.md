# GitHub Actions Quick Start (5 Minutes)

Get automatic APK builds on GitHub in just 5 minutes!

## ⚡ Quick Setup

### Step 1: Create Keystore (2 minutes)

```bash
keytool -genkey -v -keystore vi17-release.jks \
  -keyalg RSA -keysize 2048 -validity 10000 \
  -alias vi17-key \
  -storepass mypassword \
  -keypass mypassword \
  -dname "CN=Vi17,O=Vi17,C=IN"
```

### Step 2: Encode Keystore (30 seconds)

```bash
# macOS/Linux
base64 vi17-release.jks | pbcopy

# Linux (without pbcopy)
base64 vi17-release.jks > keystore-base64.txt
cat keystore-base64.txt
```

### Step 3: Add GitHub Secrets (2 minutes)

1. Go to: **GitHub Repo** → **Settings** → **Secrets and variables** → **Actions**
2. Click **New repository secret** for each:

```
KEYSTORE_FILE = (paste base64 content)
KEYSTORE_PASSWORD = mypassword
KEY_ALIAS = vi17-key
KEY_PASSWORD = mypassword
PORCUPINE_ACCESS_KEY = (your Picovoice key)
```

### Step 4: Push to GitHub (30 seconds)

```bash
git add .
git commit -m "Add CI/CD"
git push origin main
```

✅ **Done!** GitHub will now build APK automatically.

---

## 🎯 What Happens Next

1. **GitHub detects push** → Triggers workflow
2. **Workflow runs** → Builds APK (5-10 minutes)
3. **APK ready** → Download from Actions → Artifacts
4. **Automatic release** → Created on main branch

---

## 📥 Download APK

1. Go to: **GitHub Repo** → **Actions**
2. Click latest workflow run
3. Scroll down to **Artifacts**
4. Download **app-debug** or **app-release**

---

## 🔄 Every Push = Automatic Build

```bash
# Make changes
git add .
git commit -m "New feature"
git push origin main

# ✅ APK builds automatically on GitHub!
```

---

## 🚀 Deploy to Play Store (Optional)

```bash
# Create a release tag
git tag v1.0.0
git push origin v1.0.0

# ✅ Automatically deploys to Play Store!
```

---

## ⚙️ Secrets Reference

| Secret | Example | Where to Get |
|--------|---------|--------------|
| `KEYSTORE_FILE` | Base64 string | `base64 vi17-release.jks` |
| `KEYSTORE_PASSWORD` | mypassword | Same as keytool password |
| `KEY_ALIAS` | vi17-key | Same as keytool alias |
| `KEY_PASSWORD` | mypassword | Same as keytool key password |
| `PORCUPINE_ACCESS_KEY` | abc123xyz | [Picovoice Console](https://console.picovoice.ai) |

---

## 🐛 Troubleshooting

**Build fails?**
- Check GitHub Actions logs: Repo → Actions → Latest run
- Verify all secrets are added correctly
- Ensure branch is `main` or `develop`

**APK not downloading?**
- Wait for workflow to complete (green checkmark)
- Scroll down in workflow run to Artifacts section
- Try different browser if download fails

**Models missing?**
- Models are optional for first build
- Update workflow to download from URL or Git LFS
- See CI_CD_SETUP.md for detailed model setup

---

## 📚 Full Documentation

For detailed setup, see: **CI_CD_SETUP.md**

---

**That's it! Your APKs will now build automatically on every push! 🎉**
