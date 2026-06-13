package xyz.sevive.pdfimex.core.di

import org.koin.core.context.startKoin
import org.koin.dsl.bind
import org.koin.dsl.module
import org.koin.plugin.module.dsl.single
import xyz.sevive.pdfimex.JvmPdfCleanupProvider
import xyz.sevive.pdfimex.core.FileKitPdfSaver
import xyz.sevive.pdfimex.core.PdfBoxEngine
import xyz.sevive.pdfimex.core.PdfCleanupProvider
import xyz.sevive.pdfimex.core.PdfEngine
import xyz.sevive.pdfimex.core.PdfSaver

val jvmKoinModule =
    module {
        single<PdfBoxEngine>() bind PdfEngine::class
        single<FileKitPdfSaver>() bind PdfSaver::class
        single<JvmPdfCleanupProvider>() bind PdfCleanupProvider::class
    }

fun initKoin() {
    startKoin {
        modules(commonKoinModule, jvmKoinModule)
    }
}
