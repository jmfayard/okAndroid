package com.github.jmfayard.okandroid

import android.content.Context
import com.github.jmfayard.okandroid.screens.pri.ArticlesProvider
import com.github.jmfayard.okandroid.screens.pri.StaticArticlesProvider

fun app() : IApplicationComponent = App.applicationComponent

interface IApplicationComponent {
    val ctx: Context
    val app: App
    val articlesProvider: ArticlesProvider
}

class RealApplicationComponent(override val app: App) : IApplicationComponent {
    override val articlesProvider: ArticlesProvider = StaticArticlesProvider

    override val ctx: Context
        get() = app
}