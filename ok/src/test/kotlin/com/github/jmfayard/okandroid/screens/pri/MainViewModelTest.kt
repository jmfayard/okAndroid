package com.github.jmfayard.okandroid.screens.pri

import com.github.jmfayard.okandroid.screens.advanceByFrame
import com.github.jmfayard.okandroid.screens.marble
import com.github.jmfayard.okandroid.screens.pri.DialogResult.*
import com.github.jmfayard.okandroid.screens.pri.MviDialog.*
import io.kotlintest.matchers.haveSize
import io.kotlintest.matchers.shouldBe
import io.kotlintest.specs.StringSpec
import io.reactivex.Observable
import io.reactivex.Observable.*
import io.reactivex.schedulers.TestScheduler

class MainViewModelTest : StringSpec() { init {


    val scheduler = TestScheduler()
    val noInts = emptyList<Int>()

    fun <T> Observable<T>.delay(seconds: Int) = this.delaySubscription(seconds.toLong(), java.util.concurrent.TimeUnit.SECONDS, scheduler)


    "Dialogs" {
        /** Build parameters **/
        val results = listOf(
                DialogOk(PrefsMain, "font"), DialogOk(PrefsFontColor, "red"),
                DialogOk(PrefsMain, "background"), DialogOk(PrefsBackgroundColor, "blue"),
                DialogOk(PrefsMain, "reset"),
                DialogCancel(PrefsMain)
        )
        val prefsButtonClicks =
                marble("-0-------0------0----0----", scheduler) { Unit }
        val dialogResults =
                marble("---0--1-----2-3----4---5--", scheduler) { results[it] }

        /** Call our pure functions **/
        val model: MainViewModel = present(
                never(), never(), testArticlesProvider(), testPermissionProvider(true),
                prefsButtonClicks, dialogResults
        )
                .toDebugModel() // lot of output please


        /*** Subscribe to all sources at once **/
        val testDialogCmds = model.dialogCmds.test()
        val testPreferences = model.preferences.distinctUntilChanged().test()
        val otherTests = listOf(model.updateButtonIsEnabled.test(),
                model.permissionSignal.test(), model.smallProgressIsVisible.test(),
                model.emptyViewIsVisible.test())

        /** Time-Travel Machine **/
        scheduler.advanceByFrame(100)

        /** Test output. Don't forget to check for errors **/
        with(testDialogCmds) {
            assertNoErrors()
            values() shouldBe listOf(PrefsMain, PrefsFontColor, PrefsMain, PrefsBackgroundColor, PrefsMain, PrefsMain)
        }

        with(testPreferences) {
            assertNoErrors()
            values().map { it.backgroundColor } shouldBe listOf("white", "white", "blue", "white")
            values().map { it.fontColor } shouldBe listOf("black", "red", "red", "black")
        }

        otherTests.forEach { it.assertNoErrors() }


    }

    "Refresh prefs after articles are loaded" {
        /** Build parameters **/
        val results = listOf(
                DialogOk(PrefsMain, "font"), DialogOk(PrefsFontColor, "red")
        )
        val prefsButtonClicks =
                marble("-0----------", scheduler) { Unit }
        val dialogResults: Observable<DialogResult> =
                marble("---0--1-----", scheduler) { results[it] }
        val articlesClick =
                marble("---------0--", scheduler) { Unit }

        /** Call our pure functions **/
        val model: MainViewModel = present(
                articlesClick, never(),
                testArticlesProvider("article1", "article2"), testPermissionProvider(true),
                prefsButtonClicks, dialogResults
        )
                .toDebugModel() // lot of output please


        /*** Subscribe to all sources at once **/
        val testPreferences = model.preferences.test()
        val otherTests = listOf(model.updateButtonIsEnabled.test(),
                model.permissionSignal.test(), model.smallProgressIsVisible.test(),
                model.emptyViewIsVisible.test(), model.dialogCmds.test())

        /** Time-Travel Machine **/
        scheduler.advanceByFrame(20)
        testPreferences.assertNoErrors()
        testPreferences.values().map { it.fontColor } shouldBe listOf("black", "black", "red")
    }

    "on update click - progress is updated" {


        val model: MainViewModel = present(
                updateButtonClicks = just(Unit).delay(1),
                articleClicks = never(),
                articlesProvider = testArticlesProvider("article1", "article2"),
                permissionProvider = testPermissionProvider(granted = true),
                prefsButtonClicks = never<Unit>(),
                dialogResults = never<DialogResult>()
        )

        val progressIsVisible = model.progressIsVisible.test()
        scheduler.advanceTimeTo(20.seconds)

        model.permissionSignal.test().assertNoValues()
        progressIsVisible.assertValues(false, true, false)
    }

    "on article click - opens article" {

        val model = present(
                updateButtonClicks = just(Unit).delay(1),
                articleClicks = just(Article("article2")).delay(10),
                articlesProvider = testArticlesProvider("article1", "article2"),
                permissionProvider = testPermissionProvider(granted = true),
                prefsButtonClicks = never<Unit>(),
                dialogResults = never<DialogResult>()
        )

        val startDetailActivitySignals = model.startDetailActivitySignals.test()
        val progressIsVisible = model.progressIsVisible.test()
        val emptyViewIsVisible = model.emptyViewIsVisible.test()

        scheduler.advanceTimeTo(20.seconds)

        model.permissionSignal.test().assertNoValues()
        startDetailActivitySignals.assertResult(Article("article2"))
        progressIsVisible.values().last() shouldBe false
        emptyViewIsVisible.values().last() shouldBe false
    }

    "when permission not granted, do nothing" {
        val model = present(
                updateButtonClicks = just(Unit).delay(1),
                articleClicks = never(),
                articlesProvider = testArticlesProvider("article1", "article2"),
                permissionProvider = testPermissionProvider(granted = true),
                prefsButtonClicks = never<Unit>(),
                dialogResults = never<DialogResult>()
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
