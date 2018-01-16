package com.github.jmfayard.okandroid.screens.mvi

import android.app.LauncherActivity
import com.github.jmfayard.okandroid.screens.ListItem
import io.reactivex.Observable.just
import io.reactivex.schedulers.Schedulers
import java.io.Serializable
import java.util.concurrent.TimeUnit

data class Article(
        val title: String
) : Serializable, ListItem

class ArticlesProvider {

    val titles = listOf(
            "Casting a \$20M Mirror for the World’s Largest Telescope ",
            "Nvidia press conference live at CES 2018",
            "Apple shareholders push for study of phone addiction in children",
            "Products Over Projects"
    )

    fun getArticles()=
            just(titles.map { Article(it) })
                .singleOrError()
                .subscribeOn(Schedulers.io())
                .delaySubscription(1500, TimeUnit.MILLISECONDS)

}
