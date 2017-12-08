package com.github.jmfayard.okandroid

import android.app.Application
import android.content.Context
import com.mooveit.library.Fakeit
import timber.log.Timber
import android.support.multidex.MultiDex
import com.github.jmfayard.okandroid.jobs.Jobs


class App : Application() {

    companion object {
        lateinit var ctx : Application
            private set
    }

    override fun onCreate() {
        super.onCreate()
        ctx = this

        Timber.plant(Timber.DebugTree())

        Fakeit.init()
        Jobs.initialize(this)

    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }


}
