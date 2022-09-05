package com.connor.anonfiles.di

import com.connor.anonfiles.Repository
import com.connor.anonfiles.model.net.AnonNet
import com.connor.anonfiles.model.room.AppDatabase
import com.connor.anonfiles.tools.VTools
import com.connor.anonfiles.viewmodel.MainViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    single { AppDatabase.getDataBase(androidApplication() ) }
    single { get<AppDatabase>().fileDao() }

    single { AnonNet }

    single { Repository(get(), get()) }

    viewModel { MainViewModel(get()) }

    factory { VTools() }
}