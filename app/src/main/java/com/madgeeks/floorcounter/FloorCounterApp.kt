package com.madgeeks.floorcounter

import android.app.Application
import com.madgeeks.floorcounter.di.appModule
import com.madgeeks.floorcounter.di.viewModelModules
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.core.module.Module
import org.koin.dsl.module

class FloorCounterApp: Application() {
    override fun onCreate() {
        super.onCreate()

        // Dependency injection setup
        startKoin{
            androidLogger(Level.NONE)
            androidContext(this@FloorCounterApp)
            val modules = mutableListOf<Module>()
            modules.add(appModule)
            modules.add(viewModelModules)
            modules(modules)
        }
    }
}