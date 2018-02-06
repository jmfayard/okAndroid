package com.github.jmfayard.pri

import com.github.jmfayard.screens.ListItem
import com.tbruyelle.rxpermissions2.Permission
import io.reactivex.Observable.just
import io.reactivex.ObservableTransformer
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import java.io.Serializable
import java.util.concurrent.ThreadLocalRandom

enum class MviDialog {
    PrefsMain, PrefsFontColor, PrefsBackgroundColor
}

data class MviPrefs(
        val fontColor: String = "black",
        val backgroundColor: String = "white"
)

data class Article(
        val title: String
) : Serializable, ListItem

interface ArticlesProvider {
    fun fetchArticles(): Single<List<Article>>
}

interface PermissionProvider {
    fun <T> requestPermissions(): ObservableTransformer<T, Permission>
}

fun testPermissionProvider(granted: Boolean) = object  : PermissionProvider {
    override fun <T> requestPermissions() = ObservableTransformer<T, Permission> {
        upstream ->
        upstream.map { Permission("permission", granted) }
    }

}
object StaticArticlesProvider : ArticlesProvider {

    val titles = listOf(
            "Casting a \$20M Mirror for the World’s Largest Telescope ",
            "Nvidia press conference live at CES 2018",
            "Apple shareholders push for study of phone addiction in children",
            "Products Over Projects",
            "Cloud AutoML: Making AI accessible to every business",
            "Do Things that don't scale",
            "Bitcoin drops below \$10K after three days of cryptocurrency correction"
    )

    override fun fetchArticles() =
            just(randomArticles())
                    .singleOrError()
                    .subscribeOn(Schedulers.io())
                    .delaySubscription(1500.miliseconds)

    fun randomArticles() =
            titles.filter {
                ThreadLocalRandom.current().nextInt(5) < 3
            }.map { Article(it) }

}

fun testArticlesProvider(vararg title: String): ArticlesProvider =
        object : ArticlesProvider {
            override fun fetchArticles(): Single<List<Article>> = Single.just(title.map { Article(it) })
        }