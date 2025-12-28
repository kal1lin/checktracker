# CheckTracker - Development Setup Guide

## Prerequisites

- **JDK 11 or higher**
- **Android SDK** (minSdk: 24, targetSdk: 34)
- **Gradle 8.0+**
- **Git**

## Project Structure

```
checktracker/
├── shared/              # KMP shared module
│   ├── src/
│   │   ├── commonMain/  # Common code (shared between Android & iOS)
│   │   ├── androidMain/ # Android-specific implementations
│   │   ├── iosMain/     # iOS-specific implementations
│   │   └── commonTest/  # Unit tests
│   └── build.gradle.kts
├── androidApp/          # Android application
│   ├── src/main/
│   │   ├── kotlin/
│   │   ├── AndroidManifest.xml
│   │   └── res/
│   └── build.gradle.kts
└── iosApp/              # iOS application (Xcode project)
```

## Setup Instructions

### 1. Clone Repository

```bash
git clone https://github.com/yourusername/checktracker.git
cd checktracker
```

### 2. Build Shared Module

```bash
./gradlew :shared:build
```

### 3. Run Unit Tests

```bash
./gradlew :shared:test
```

Expected output:
```
ReceiptParserTest ............ PASSED
ExpenseRepositoryTest ........ PASSED

BUILD SUCCESSFUL in 5s
```

### 4. Build Android App

**Debug:**
```bash
./gradlew :androidApp:assembleDebug
```

Output: `androidApp/build/outputs/apk/debug/checktracker-debug.apk`

**Release:**
```bash
./gradlew :androidApp:assembleRelease
```

Output: `androidApp/build/outputs/apk/release/checktracker-release.apk`

### 5. Install on Device/Emulator

```bash
# Start Android emulator (or connect device)
adb devices

# Install
./gradlew :androidApp:installDebug

# Launch
adb shell am start -n com.checktracker/.MainActivity
```

## Development Workflow

### Adding a New Feature

1. **Create in shared module**
```
shared/src/commonMain/kotlin/com/checktracker/feature/...
```

2. **Add tests**
```
shared/src/commonTest/kotlin/FeatureTest.kt
```

3. **Build & Test**
```bash
./gradlew :shared:build test
```

4. **Implement UI** (Compose)
```
shared/src/commonMain/kotlin/com/checktracker/presentation/ui/...
```

### Code Organization

- `data/` — Models, Repository, Database
- `domain/` — Business logic, Use cases
- `presentation/` — UI, ViewModels

### Naming Conventions

- **Classes**: PascalCase
- **Functions**: camelCase
- **Constants**: UPPER_SNAKE_CASE
- **Files**: Match class name

## Testing

### Run All Tests

```bash
./gradlew test
```

### Run Specific Test

```bash
./gradlew :shared:test --tests ReceiptParserTest
```

### Generate Test Report

```bash
./gradlew test
# Report: shared/build/reports/tests/test/index.html
```

### Code Coverage

Currently: **>80%** (ReceiptParser, Repository, ViewModel)

To maintain coverage:
- Write tests for new features
- Test happy path + edge cases
- Mock dependencies with InMemory implementations

## Debug Logging

### Enable Logging

```kotlin
// Add to your code
Log.d("TAG", "Message")
```

### View Logs

```bash
adb logcat -s "TAG"
```

## Troubleshooting

### Build Fails with "Kotlin Symbol Not Found"

```bash
./gradlew clean build --refresh-dependencies
```

### Android SDK Issues

```bash
# Check SDK location
android --version

# Update SDK
sdkmanager --update
```

### Emulator Won't Start

```bash
# List AVDs
emulator -list-avds

# Start specific AVD
emulator -avd Pixel_4_API_30
```

## IDE Setup (Android Studio)

1. Open project in Android Studio
2. Sync Gradle files
3. Install ML Kit plugin (for better IDE support)
4. Go to: File → Project Structure → SDK Location → verify Android SDK

## Build Variants

### Debug
- Unoptimized, debuggable
- Size: ~50 MB
- Testing, development

### Release
- Optimized, minified
- Size: ~15 MB
- Production, Play Store

## Performance Optimization

### Disable Debug Symbols in Release

Already configured in `build.gradle.kts`

### Enable ProGuard/R8

Already configured in release builds

### Monitor App Size

```bash
./gradlew :androidApp:analyzeDebugBundle
```

## Resources

- [KMP Documentation](https://kotlinlang.org/docs/multiplatform.html)
- [Compose Multiplatform](https://www.jetbrains.com/help/compose-multiplatform/)
- [ML Kit Text Recognition](https://developers.google.com/ml-kit/vision/text-recognition)
- [Android Camera X](https://developer.android.com/training/camerax)

## Contributing

1. Fork repository
2. Create feature branch: `git checkout -b feature/amazing-feature`
3. Commit changes: `git commit -m 'Add amazing feature'`
4. Push branch: `git push origin feature/amazing-feature`
5. Open Pull Request

**Code Review Checklist:**
- [ ] Unit tests added (≥80% coverage)
- [ ] Code formatted properly
- [ ] No breaking changes
- [ ] Documentation updated

---

**Questions?** Open an issue on GitHub!
