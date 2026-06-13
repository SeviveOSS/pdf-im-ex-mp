package xyz.sevive.pdfimex.core

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import xyz.sevive.pdfimex.MainViewModel

val commonKoinModule =
    module {
        viewModelOf(::MainViewModel)
    }
