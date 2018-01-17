package com.github.jmfayard.okandroid.screens.mvi

import com.github.jmfayard.okandroid.screens.ListItem
import io.reactivex.Observable.just
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import java.io.Serializable

data class Article(
        val title: String
) : Serializable, ListItem

interface ArticlesProvider {
    fun fetchArticles(): Single<List<Article>>
}


object StaticArticlesProvider : ArticlesProvider {

    val titles = listOf(
            "Casting a \$20M Mirror for the World’s Largest Telescope ",
            "Nvidia press conference live at CES 2018",
            "Apple shareholders push for study of phone addiction in children",
            "Products Over Projects"
    )

    override fun fetchArticles() =
            just(titles.map { Article(it) })
                    .singleOrError()
                    .subscribeOn(Schedulers.io())
                    .delaySubscription(1500.miliseconds)

}

fun testArticlesProvider(vararg title: String): ArticlesProvider =
        object : ArticlesProvider {
            override fun fetchArticles(): Single<List<Article>>
                    = Single.just(title.map { Article(it) })
        }