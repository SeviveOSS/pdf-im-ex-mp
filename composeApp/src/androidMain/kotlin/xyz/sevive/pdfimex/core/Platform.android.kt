package xyz.sevive.pdfimex.core

import android.util.Log

actual fun log(tag: String, message: String, cause: Throwable?) {
    Log.d(tag, message, cause)
}
