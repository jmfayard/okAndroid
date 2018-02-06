package com.github.jmfayard

import android.annotation.SuppressLint
import android.content.Context

interface AndroidCommonComponent {
    val ctx: Context
    val isRunningTest: Boolean

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var instance: AndroidCommonComponent
    }
}

fun androidCommon(): AndroidCommonComponent =
        AndroidCommonComponent.instance
