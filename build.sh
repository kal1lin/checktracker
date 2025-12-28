#!/bin/bash

# Build script for CheckTracker
# Usage: ./build.sh [target]
# Targets: test, android-debug, android-release, clean

set -e

# Colors for output
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Functions
print_header() {
    echo -e "${BLUE}═══════════════════════════════════════${NC}"
    echo -e "${BLUE}$1${NC}"
    echo -e "${BLUE}═══════════════════════════════════════${NC}"
}

print_success() {
    echo -e "${GREEN}✓ $1${NC}"
}

print_warning() {
    echo -e "${YELLOW}⚠ $1${NC}"
}

# Main build script
TARGET=${1:-test}

case $TARGET in
    test)
        print_header "Running Unit Tests"
        ./gradlew test --info
        print_success "Tests completed"
        ;;
    
    android-debug)
        print_header "Building Android Debug APK"
        ./gradlew :androidApp:assembleDebug
        APK_PATH="androidApp/build/outputs/apk/debug/checktracker-debug.apk"
        if [ -f "$APK_PATH" ]; then
            print_success "APK built: $APK_PATH"
            ls -lh "$APK_PATH"
        fi
        ;;
    
    android-release)
        print_header "Building Android Release APK"
        ./gradlew :androidApp:assembleRelease
        APK_PATH="androidApp/build/outputs/apk/release/checktracker-release.apk"
        if [ -f "$APK_PATH" ]; then
            print_success "APK built: $APK_PATH"
            ls -lh "$APK_PATH"
        fi
        print_warning "Remember to sign the APK before publishing"
        ;;
    
    android-install)
        print_header "Building and Installing Debug APK"
        ./gradlew :androidApp:installDebug
        print_success "App installed on device/emulator"
        print_header "Launching app..."
        adb shell am start -n com.checktracker/.MainActivity
        ;;
    
    clean)
        print_header "Cleaning build artifacts"
        ./gradlew clean
        print_success "Build artifacts cleaned"
        ;;
    
    *)
        echo "Usage: ./build.sh [target]"
        echo ""
        echo "Targets:"
        echo "  test              - Run unit tests"
        echo "  android-debug     - Build debug APK"
        echo "  android-release   - Build release APK"
        echo "  android-install   - Build and install debug APK"
        echo "  clean             - Clean build artifacts"
        echo ""
        echo "Examples:"
        echo "  ./build.sh test"
        echo "  ./build.sh android-debug"
        echo "  ./build.sh android-install"
        exit 1
        ;;
esac

echo ""
print_success "Build completed!"
