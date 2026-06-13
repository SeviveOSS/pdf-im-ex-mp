package xyz.sevive.pdfimex

import android.app.Application
import xyz.sevive.pdfimex.core.di.initKoin

class PdfImExApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        initKoin(this)
    }
}
