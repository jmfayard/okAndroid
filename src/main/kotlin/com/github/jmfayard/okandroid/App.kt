package com.github.jmfayard.okandroid

import android.app.Application
import com.mooveit.library.Fakeit
import timber.log.Timber

class App : Application() {

    companion object {
        lateinit var ctx : Application
            private set
    }
    override fun onCreate() {
        super.onCreate()
        ctx = this

        Timber.plant(Timber.DebugTree())

        Fakeit.init(this)
    }
}
