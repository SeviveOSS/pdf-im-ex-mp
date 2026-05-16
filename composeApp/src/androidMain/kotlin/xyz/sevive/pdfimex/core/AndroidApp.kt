package xyz.sevive.pdfimex.core

import android.content.Context

@Volatile
private var _appContext: Context? = null

object AndroidApp {
    val appContext: Context
        get() = _appContext ?: throw IllegalStateException(
            "AndroidApp.appContext not initialized. Call AndroidApp.init(context) in MainActivity."
        )

    fun init(context: Context) {
        _appContext = context.applicationContext
    }
}
