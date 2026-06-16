package dev.mobilewright.testapp

// Forces the Kotlin plugin to compile Kotlin into the app APK so kotlin-stdlib
// (CollectionsKt etc.) is present at runtime for androidx.startup initializers.
internal object Noop {
    val marker: List<Int> = listOf(1, 2, 3)
}
