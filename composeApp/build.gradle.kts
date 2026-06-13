import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.kmp.library)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.licensee)
    alias(libs.plugins.koin.compiler)
}

licensee {
    // a bunch of AGPL compatible licenses
    // commented out entries are used to disable the licensee "unused" warnings
//    allow("AGPL-3.0-only")
//    allow("AGPL-3.0-or-later")
//    allow("GPL-3.0-only")
//    allow("GPL-3.0-or-later")
//    allow("LGPL-2.1-only")
//    allow("LGPL-3.0-only")
//    allow("MPL-2.0")
    allow("Apache-2.0")
//    allow("MIT")
//    allow("BSD-2-Clause")
//    allow("BSD-3-Clause")
//    allow("ISC")
//    allow("X11")
//    allow("Unlicense")
//    allow("CC0-1.0")
    allowUrl("https://github.com/hypfvieh/dbus-java/blob/master/LICENSE") {
        because("MIT")
    }
    allowUrl("https://github.com/vinceglb/FileKit/blob/main/LICENSE") {
        because("MIT")
    }
    allowUrl("https://raw.githubusercontent.com/korlibs/korge/refs/heads/main/LICENSE") {
        because("Each library has its own licenses typically MIT or Public Domain, dual licensed with Apache 2.0.")
    }
    allowUrl("https://asm.ow2.io/license.html") {
        because("ASM is released under the following 3-Clause BSD License")
    }
    allowUrl("https://opensource.org/license/mit") {
        because("Obviously MIT")
    }
}

tasks.withType<Test> {
    failOnNoDiscoveredTests = false
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
            implementation(libs.compose.navigation)
            implementation(libs.lifecycle.runtime.compose)
            implementation(libs.lifecycle.viewmodel.compose)

            implementation(libs.kotlinx.coroutines)
            implementation(libs.korlibs.io)
            implementation(libs.korlibs.image)
            implementation(libs.korlibs.image.core)
            implementation(libs.filekit.core)
            implementation(libs.filekit.dialogs)
            implementation(libs.filekit.dialogs.compose)
            implementation(libs.kermit)
            implementation(project.dependencies.platform(libs.koin.bom))
            implementation(libs.koin.core)
            implementation(libs.koin.annotations)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)
        }

        androidMain.dependencies {
            implementation(libs.mupdf)
            implementation(libs.korlibs.image.android)
            implementation(libs.androidx.core.ktx)
            implementation(libs.androidx.activity.compose)
            implementation(libs.androidx.compose.ui.tooling)
            implementation(libs.koin.android)
            implementation(libs.koin.androidx.compose)
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
