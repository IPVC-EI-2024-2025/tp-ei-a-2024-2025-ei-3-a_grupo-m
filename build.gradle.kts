buildscript {
    repositories {
        google()
        mavenCentral()
    }
}

// This block might already be present in your settings.gradle.kts
// instead of the project-level build.gradle.kts
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.kapt) apply false
}