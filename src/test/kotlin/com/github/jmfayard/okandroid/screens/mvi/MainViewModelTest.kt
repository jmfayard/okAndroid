package com.github.jmfayard.okandroid.screens.mvi

import io.kotlintest.matchers.shouldBe
import io.kotlintest.specs.StringSpec
import io.reactivex.Observable
import io.reactivex.Observable.just
import io.reactivex.schedulers.TestScheduler

class MainViewModelTest : StringSpec() { init {


    val testScheduler = TestScheduler()


    "on update click - progress is updated" {


        val model = present(
                articlesProvider = testArticlesProvider("article1", "article2"),
                updateButtonClicks = just(Unit).delaySubscription(1.seconds, testScheduler),
                articleClicks = Observable.never()
        )

        val progressIsVisible = model.progressIsVisible.test()
        testScheduler.advanceTimeTo(20.seconds)

        progressIsVisible.assertValues(false, true, false)
    }

    "on article click - opens article" {

        val model = present(
                articlesProvider = testArticlesProvider("article1", "article2"),
                updateButtonClicks = just(Unit).delaySubscription(1.seconds, testScheduler),
                articleClicks = just(Article("article2")).delaySubscription(10.seconds, testScheduler)
        )

        val startDetailActivitySignals = model.startDetailActivitySignals.test()
        val progressIsVisible = model.progressIsVisible.test()
        val emptyViewIsVisible = model.emptyViewIsVisible.test()

        testScheduler.advanceTimeTo(20.seconds)

        startDetailActivitySignals.assertResult(Article("article2"))
        progressIsVisible.values().last() shouldBe false
        emptyViewIsVisible.values().last() shouldBe false
    }

}
}