@echo off
REM Build script for CheckTracker (Windows)
REM Usage: build.bat [target]
REM Targets: test, android-debug, android-release, clean

setlocal enabledelayedexpansion

set TARGET=%1
if "%TARGET%"=="" set TARGET=test

echo.
echo ============================================================
echo CheckTracker Build Script
echo ============================================================
echo.

if "%TARGET%"=="test" (
    echo Running Unit Tests...
    call gradlew.bat test --info
    echo.
    echo [SUCCESS] Tests completed!
    goto :end
)

if "%TARGET%"=="android-debug" (
    echo Building Android Debug APK...
    call gradlew.bat :androidApp:assembleDebug
    echo.
    if exist "androidApp\build\outputs\apk\debug\checktracker-debug.apk" (
        echo [SUCCESS] APK built!
        echo Path: androidApp\build\outputs\apk\debug\checktracker-debug.apk
    ) else (
        echo [ERROR] APK not found after build
    )
    goto :end
)

if "%TARGET%"=="android-release" (
    echo Building Android Release APK...
    call gradlew.bat :androidApp:assembleRelease
    echo.
    if exist "androidApp\build\outputs\apk\release\checktracker-release.apk" (
        echo [SUCCESS] APK built!
        echo Path: androidApp\build\outputs\apk\release\checktracker-release.apk
        echo Remember to sign the APK before publishing!
    ) else (
        echo [ERROR] APK not found after build
    )
    goto :end
)

if "%TARGET%"=="android-install" (
    echo Building and Installing Debug APK...
    call gradlew.bat :androidApp:installDebug
    echo.
    echo [SUCCESS] App installed on device/emulator
    echo Launching app...
    adb shell am start -n com.checktracker/.MainActivity
    goto :end
)

if "%TARGET%"=="clean" (
    echo Cleaning build artifacts...
    call gradlew.bat clean
    echo [SUCCESS] Build artifacts cleaned
    goto :end
)

echo Usage: build.bat [target]
echo.
echo Targets:
echo   test              - Run unit tests
echo   android-debug     - Build debug APK
echo   android-release   - Build release APK
echo   android-install   - Build and install debug APK
echo   clean             - Clean build artifacts
echo.
echo Examples:
echo   build.bat test
echo   build.bat android-debug
echo   build.bat android-install

:end
echo.
echo ============================================================
echo Done!
echo ============================================================
