package com.github.jmfayard.okandroid

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.support.annotation.VisibleForTesting
import com.mooveit.library.Fakeit
import timber.log.Timber
import android.support.multidex.MultiDex
import com.github.jmfayard.okandroid.jobs.Jobs


open class App : Application() {

    companion object {

        @JvmStatic
        lateinit var applicationComponent: IApplicationComponent
            private set

        @VisibleForTesting /** Can be called in unit tests **/
        fun setupApplicationComponent(applicationComponent: IApplicationComponent) {
            this.applicationComponent = applicationComponent
        }

    }

    @SuppressLint("VisibleForTests")
    override fun onCreate() {
        super.onCreate()
        setupApplicationComponent(
                buildAppComponent()
        )

        Timber.plant(Timber.DebugTree())

        Fakeit.init()
        Jobs.initialize(this)

    }

    /** Can be overriden in instrumentation test **/
    open fun buildAppComponent() : IApplicationComponent
            = RealApplicationComponent(this)


    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }


}
