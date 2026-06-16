import org.jetbrains.kotlin.gradle.dsl.JvmTarget

// AGP 9+ has built-in Kotlin support, so no separate kotlin-android plugin is applied.
plugins {
    id("com.android.application") version "9.2.1"
}

android {
    namespace = "dev.mobilewright.testapp"
    compileSdk = 37

    defaultConfig {
        applicationId = "dev.mobilewright.testapp"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.19.0")
    androidTestImplementation("androidx.test.ext:junit:1.3.0")
    androidTestImplementation("androidx.test:runner:1.7.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.7.0")
    androidTestImplementation("io.github.kakaocup:kakao:3.7.0")
}
