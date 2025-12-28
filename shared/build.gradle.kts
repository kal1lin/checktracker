plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization") version "1.9.20"
    id("com.android.library")
    id("org.jetbrains.compose")
}

kotlin {
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "11"
            }
        }
    }
    
    iosX64()
    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        commonMain {
            dependencies {
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material3)
                implementation(compose.materialIconsExtended)
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.0")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
                implementation("io.insert-koin:koin-core:3.5.0")
                implementation("io.insert-koin:koin-test:3.5.0")
                implementation("app.cash.sqldelight:runtime:2.0.0")
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.5.0")
            }
        }
        
        androidMain {
            dependencies {
                implementation("androidx.activity:activity-compose:1.8.0")
                implementation("androidx.camera:camera-core:1.3.0")
                implementation("androidx.camera:camera-camera2:1.3.0")
                implementation("androidx.camera:camera-lifecycle:1.3.0")
                implementation("androidx.camera:camera-view:1.3.0")
                implementation("com.google.mlkit:text-recognition-russian:16.0.0")
                implementation("androidx.datastore:datastore-preferences:1.0.0")
                implementation("app.cash.sqldelight:android-driver:2.0.0")
            }
        }
        
        iosMain {
            dependencies {
                implementation("app.cash.sqldelight:native-driver:2.0.0")
            }
        }
        
        commonTest {
            dependencies {
                implementation(kotlin("test"))
                implementation("io.insert-koin:koin-test:3.5.0")
            }
        }
    }
}

android {
    namespace = "com.checktracker"
    compileSdk = 34
    defaultConfig {
        minSdk = 24
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}
