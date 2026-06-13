package xyz.sevive.pdfimex.core.di

import android.content.Context
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.dsl.bind
import org.koin.dsl.module
import org.koin.plugin.module.dsl.single
import xyz.sevive.pdfimex.MuPdfCleanupProvider
import xyz.sevive.pdfimex.core.MediaStorePdfSaver
import xyz.sevive.pdfimex.core.MuPdfEngine
import xyz.sevive.pdfimex.core.PdfCleanupProvider
import xyz.sevive.pdfimex.core.PdfEngine
import xyz.sevive.pdfimex.core.PdfSaver

val androidKoinModule =
    module {
        single<MuPdfEngine>() bind PdfEngine::class
        single<MediaStorePdfSaver>() bind PdfSaver::class
        single<MuPdfCleanupProvider>() bind PdfCleanupProvider::class
    }

// place `startKoin` here so the compiler plugin can scan for missing dependencies
fun initKoin(context: Context) {
    startKoin {
        androidLogger()
        androidContext(context)
        modules(commonKoinModule, androidKoinModule)
    }
}
