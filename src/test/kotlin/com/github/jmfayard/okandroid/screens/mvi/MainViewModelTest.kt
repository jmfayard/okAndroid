package com.github.jmfayard.okandroid.screens.mvi

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import io.kotlintest.matchers.shouldBe
import io.kotlintest.specs.StringSpec
import io.reactivex.Observable
import io.reactivex.Observable.just
import io.reactivex.Single
import io.reactivex.schedulers.TestScheduler
import java.util.concurrent.TimeUnit

class MainViewModelTest : StringSpec() { init {


    val testScheduler = TestScheduler()

    val articlesProvider: ArticlesProvider = mock()


    "on update click - progress is updated" {


        val model = present(
                updateButtonClicks = just(Unit).delaySubscription(1, TimeUnit.SECONDS, testScheduler),
                articleClicks = Observable.never(),
                articlesProvider = testArticlesProvider("article1", "article2")
        )

        val progressIsVisible = model.progressIsVisible.test()
        testScheduler.advanceTimeTo(20, TimeUnit.SECONDS)

        progressIsVisible.assertValues(false, true, false)
    }

    "on article click - opens article" {
        whenever(articlesProvider.fetchArticles()).thenReturn(Single.just(listOf(
                Article("article1"),
                Article("article2")
        )))

        val model = present(
                updateButtonClicks = just(Unit).delaySubscription(1, TimeUnit.SECONDS, testScheduler),
                articleClicks = just(1).delaySubscription(10, TimeUnit.SECONDS, testScheduler),
                articlesProvider = articlesProvider
        )

        val startDetailActivitySignals = model.startDetailActivitySignals.test()
        val progressIsVisible = model.progressIsVisible.test()
        val emptyViewIsVisible = model.emptyViewIsVisible.test()

        testScheduler.advanceTimeTo(20, TimeUnit.SECONDS)

        startDetailActivitySignals.assertResult(Article("article2"))
        progressIsVisible.values().last() shouldBe false
        emptyViewIsVisible.values().last() shouldBe false
    }

}
}