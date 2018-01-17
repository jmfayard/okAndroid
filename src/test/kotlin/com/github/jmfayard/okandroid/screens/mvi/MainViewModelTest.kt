package com.github.jmfayard.okandroid.screens.mvi

import com.tbruyelle.rxpermissions2.Permission
import io.kotlintest.matchers.shouldBe
import io.kotlintest.specs.StringSpec
import io.reactivex.Observable
import io.reactivex.Observable.just
import io.reactivex.schedulers.TestScheduler

class MainViewModelTest : StringSpec() { init {


    val testScheduler = TestScheduler()


    "on update click - progress is updated" {


        val model = present(
                updateButtonClicks = just(Unit).delaySubscription(1.seconds, testScheduler),
                articleClicks = Observable.never(),
                articlesProvider = testArticlesProvider("article1", "article2"),
                permissionProvider = testPermissionProvider(granted = true)
        )

        val progressIsVisible = model.progressIsVisible.test()
        testScheduler.advanceTimeTo(20.seconds)

        model.permissionSignal.test().assertNoValues()
        progressIsVisible.assertValues(false, true, false)
    }

    "on article click - opens article" {

        val model = present(
                updateButtonClicks = just(Unit).delaySubscription(1.seconds, testScheduler),
                articleClicks = just(Article("article2")).delaySubscription(10.seconds, testScheduler),
                articlesProvider = testArticlesProvider("article1", "article2"),
                permissionProvider = testPermissionProvider(granted = true)
        )

        val startDetailActivitySignals = model.startDetailActivitySignals.test()
        val progressIsVisible = model.progressIsVisible.test()
        val emptyViewIsVisible = model.emptyViewIsVisible.test()

        testScheduler.advanceTimeTo(20.seconds)

        model.permissionSignal.test().assertNoValues()
        startDetailActivitySignals.assertResult(Article("article2"))
        progressIsVisible.values().last() shouldBe false
        emptyViewIsVisible.values().last() shouldBe false
    }

    "when permission not granted, do nothing" {
        val model = present(
                updateButtonClicks = just(Unit).delaySubscription(1.seconds, testScheduler),
                articleClicks = Observable.never(),
                articlesProvider = testArticlesProvider("article1", "article2"),
                permissionProvider = testPermissionProvider(granted = true)
        )
        model.emptyViewIsVisible.test().run {
            values() shouldBe listOf(true)
        }
        model.articles.test().run {
            values().last() shouldBe emptyList<Article>()
        }
    }

}
}