package com.github.jmfayard.okandroid

import android.content.Context
import com.github.jmfayard.okandroid.screens.pri.ArticlesProvider

fun ok() : OkComponent = OkComponent.instance

interface OkComponent {
    val ctx: Context
    val app: App
    val articlesProvider: ArticlesProvider

    companion object {
        lateinit var instance: OkComponent
    }
}

