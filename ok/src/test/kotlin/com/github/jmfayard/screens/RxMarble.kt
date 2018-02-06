package com.github.jmfayard.screens

import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.schedulers.TestScheduler
import java.lang.RuntimeException
import java.util.concurrent.TimeUnit


const val TICK = 1000L

fun <T> marble(marbles: String, scheduler: TestScheduler, tickMs: Long = TICK, operation: (Int) -> T): Observable<T> {
    return RxMarble(marbles, true, tickMs, 100L, scheduler).create().map { operation(it) }
}

fun TestScheduler.advanceByFrame(nb: Int, tickMs: Long = TICK): TestScheduler {
    this.advanceTimeBy(tickMs * nb, TimeUnit.MILLISECONDS)
    return this
}


class RxMarble(val marbles: String, val hot: Boolean, val tickMs: Long, val max: Long, val scheduler: Scheduler) {

    init {
        check(marbles.all { it in RxMarble.NUMBERS || it in OTHERS }) { "Invalid marble: $marbles" }
        check(marbles.filter { it == '^' }.count() <= 1) { "Multiplate subscriptions in $marbles" }
        check(marbles.filter { it == '#' || it == '|' }.count() <= 1) { "Multiple terminal events in $marbles" }
        for (c in listOf('|', '^', '#')) {
            check(marbles.filter { it == c }.count() <= 1) { "Multiple values for '$c'" }
        }
    }

    fun firstFrame(): Long {
        val first = if (hot) marbles.indexOfFirst { it == '^' } else -1
        return if (first == -1) 0L else first.toLong()
    }

    fun lastFrame(): Long {
        val end = marbles.indexOfFirst { it == '#' || it == '|' }
        return if (end == -1) max else end.toLong()
    }

    fun completion(): Observable<Int> {
        val end = marbles.firstOrNull { it == '#' || it == '|' }
        return when (end) {
            null -> Observable.never()
            '#' -> Observable.error(Error)
            '|' -> Observable.empty()
            else -> TODO("invalid completion ${completion()}")
        }
    }

    fun create(): Observable<Int> {
        return Observable.merge(Observable.interval(tickMs, TimeUnit.MILLISECONDS, scheduler)
                .take(lastFrame())
                .skip(firstFrame())
                .map { l -> marbles.getOrNull(l.toInt()) ?: "-" }
                .filter { it in NUMBERS }
                .map { "$it".toInt() },
                completion())
    }

    companion object {
        private val NUMBERS = '0'..'9'
        private val OTHERS = listOf('|', '^', '#', '-')
    }

    object Error : RuntimeException("error")
}


