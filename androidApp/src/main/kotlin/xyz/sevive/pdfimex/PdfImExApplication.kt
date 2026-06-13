package xyz.sevive.pdfimex

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import xyz.sevive.pdfimex.core.androidKoinModule
import xyz.sevive.pdfimex.core.commonKoinModule

class PdfImExApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@PdfImExApplication)
            modules(commonKoinModule, androidKoinModule)
        }
    }
}
