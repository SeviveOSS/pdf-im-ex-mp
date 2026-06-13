package xyz.sevive.pdfimex.core

import org.koin.dsl.module
import xyz.sevive.pdfimex.MuPdfCleanupProvider

val androidKoinModule =
    module {
        single<PdfEngine> { MuPdfEngine() }
        single<PdfSaver> { MediaStorePdfSaver(get()) }
        single<PdfCleanupProvider> { MuPdfCleanupProvider() }
    }
