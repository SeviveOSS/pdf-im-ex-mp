package xyz.sevive.pdfimex.core

import org.koin.dsl.module
import xyz.sevive.pdfimex.JvmPdfCleanupProvider

val jvmKoinModule =
    module {
        single<PdfEngine> { PdfBoxEngine() }
        single<PdfSaver> { FileKitPdfSaver() }
        single<PdfCleanupProvider> { JvmPdfCleanupProvider() }
    }
