package xyz.sevive.pdfimex.core.di

import org.koin.dsl.module
import org.koin.plugin.module.dsl.viewModel
import xyz.sevive.pdfimex.MainViewModel

val commonKoinModule =
    module {
        viewModel<MainViewModel>()
    }
