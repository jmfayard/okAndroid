package com.github.jmfayard.okandroid.screens

import com.github.jmfayard.okandroid.screens.pri.advanceTimeBy
import com.github.jmfayard.okandroid.screens.pri.seconds
import io.kotlintest.matchers.beGreaterThan
import io.kotlintest.matchers.beLessThan
import io.kotlintest.matchers.should
import io.kotlintest.matchers.shouldBe
import io.kotlintest.specs.StringSpec
import io.reactivex.Scheduler
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import io.reactivex.schedulers.TestScheduler
import kotlin.system.measureTimeMillis

class RxPlaygroundScreenTest : StringSpec() { init {


    val screen = RxPlaygroundScreen()


    "RxJavaPlugin use Schedulers.trampoline()" {
        configureRxWithTrampolineScheduler()

        val duration = measureTimeMillis {
            val test = screen.rxTesting().test()

            test.awaitTerminalEvent() // <-- very important

            test.values().size shouldBe 3
        }
        duration should beGreaterThan(4000L)

    }

    "RxjavaPlugin use TestScheduler" {
        val testScheduler = configureRxWithTestScheduler()

        val duration = measureTimeMillis {

            val test = screen.rxTesting().test()

            testScheduler.advanceTimeBy(5.seconds)

            test.assertComplete()

            test.values().size shouldBe 3
        }

        duration should beLessThan(100L)

    }


}

}


fun configureRxWithTrampolineScheduler() {
    val trampoline: Scheduler = Schedulers.trampoline()
    RxJavaPlugins.reset()
    RxAndroidPlugins.reset()
    RxJavaPlugins.setComputationSchedulerHandler { trampoline }
    RxJavaPlugins.setIoSchedulerHandler { trampoline }
    RxAndroidPlugins.setInitMainThreadSchedulerHandler { trampoline }
}


fun configureRxWithTestScheduler(): TestScheduler {
    val testScheduler = TestScheduler()
    RxJavaPlugins.reset()
    RxAndroidPlugins.reset()
    RxJavaPlugins.setComputationSchedulerHandler { testScheduler }
    RxJavaPlugins.setIoSchedulerHandler { testScheduler }
    RxAndroidPlugins.setInitMainThreadSchedulerHandler { testScheduler }
    return testScheduler
}