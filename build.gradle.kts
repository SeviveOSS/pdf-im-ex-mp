// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.compose.multiplatform) apply false
    alias(libs.plugins.android.kmp.library) apply false
    id("org.jmailen.kotlinter") version "5.5.0" apply false
}

subprojects {
    apply(plugin = "org.jmailen.kotlinter")
}
