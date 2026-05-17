import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.kmp.library)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.kotlin.compose)
}

kotlin {
    android {
        namespace = "xyz.sevive.pdfimex.shared"
        compileSdk = 36
        minSdk = 28
        androidResources { enable = false }
        withHostTest {}
    }

    jvm()

    sourceSets {
        commonMain.dependencies {
            implementation(libs.compose.runtime)
            implementation(libs.compose.material3)
            implementation(libs.compose.ui)
            implementation(libs.compose.foundation)
            implementation(libs.kotlinx.coroutines)
            implementation(libs.korlibs.io)
            implementation(libs.korlibs.image)
            implementation(libs.korlibs.image.core)
            implementation(libs.filekit.core)
            implementation(libs.filekit.dialogs)
            implementation(libs.filekit.dialogs.compose)
            implementation(libs.kermit)
        }

        androidMain.dependencies {
            implementation(libs.mupdf)
            implementation(libs.korlibs.image.android)
            implementation(libs.androidx.core.ktx)
            implementation(libs.androidx.activity.compose)
            implementation(libs.androidx.compose.ui.tooling)
            implementation(libs.androidx.lifecycle.viewmodel.ktx)
            implementation(libs.androidx.lifecycle.viewmodel.compose)
        }

        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)

            implementation(libs.pdfbox)
            implementation(libs.korlibs.image.jvm)
            implementation(libs.kotlinx.coroutines.swing)
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test.core)
            implementation(libs.kotlin.test.annotations.common)
            implementation(libs.kotlin.test.junit)
        }
    }
}

compose.desktop {
    application {
        mainClass = "xyz.sevive.pdfimex.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Msi)
            packageName = "xyz.sevive.pdfimex"
            packageVersion = "0.1.0"
        }
    }
}

