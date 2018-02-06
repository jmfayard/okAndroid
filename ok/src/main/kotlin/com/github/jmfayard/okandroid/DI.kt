package com.github.jmfayard.okandroid

import android.content.Context
import com.github.jmfayard.AndroidCommonComponent
import com.github.jmfayard.CommonComponent
import com.github.jmfayard.ILogger
import com.github.jmfayard.okandroid.screens.pri.ArticlesProvider
import com.github.jmfayard.okandroid.screens.pri.StaticArticlesProvider
import com.github.jmfayard.room.RoomComponent
import timber.log.Timber

class DI(
        override val app: App
) : OkComponent, RoomComponent, CommonComponent, AndroidCommonComponent {

    override val logger = object: ILogger {
        override fun log(message: String) = Timber.i(message)
        override fun warn(message: String) = Timber.w(message)
    }

    override val isRunningTest: Boolean = false
    override val articlesProvider: ArticlesProvider = StaticArticlesProvider

    override val ctx: Context
        get() = app
}