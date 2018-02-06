package com.github.jmfayard.okandroid

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.support.multidex.MultiDex
import com.github.jmfayard.AndroidCommonComponent
import com.github.jmfayard.CommonComponent
import com.github.jmfayard.okandroid.jobs.Jobs
import com.github.jmfayard.room.RoomComponent
import com.mooveit.library.Fakeit
import timber.log.Timber


open class App : Application() {


    @SuppressLint("VisibleForTests")
    override fun onCreate() {
        super.onCreate()

        val component = DI(this)
        CommonComponent.instance = component
        AndroidCommonComponent.instance = component
        RoomComponent.instance = component
        OkComponent.instance = component

        Timber.plant(Timber.DebugTree())

        Fakeit.init()
        Jobs.initialize(this)

    }


    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }


}
