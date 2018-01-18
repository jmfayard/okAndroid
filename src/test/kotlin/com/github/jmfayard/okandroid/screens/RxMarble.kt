package com.github.jmfayard.okandroid.screens

import android.icu.text.Collator.ReorderCodes.OTHERS
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.schedulers.TestScheduler
import java.lang.RuntimeException
import java.util.concurrent.TimeUnit



fun <T> T.debug(name: String): T {
    println("DEBUG: ${name} = ${toString()}")
    return this
}

fun <T> List<T>.debugList(name: String): List<T> {
    println("<List $name with $size elements>")
    forEachIndexed { i, t ->
        println("$name[$i] : $t")
    }
    println("</List $name>")
    return this
}

inline fun <K, V> Map<K, V>.printMap(name: String): Map<K, V> {
    println("<Map name=$name size=$size>")
    for ((k, v) in this) {
        println("$name[$k] : $v")
    }
    println("</Map name=$name size=$size>")
    return this
}


const val TICK = 1000L

fun <T> marble(marbles: String, scheduler: TestScheduler, operation: (Int) -> T ) : Observable<T> {
    return RxMarble(marbles, true, TICK, 100L, scheduler).create().map { operation(it) }
}

fun TestScheduler.advanceByFrame(nb: Int): TestScheduler {
    this.advanceTimeBy(TICK*nb, TimeUnit.MILLISECONDS)
    return this
}


class RxMarble(val marbles: String, val hot: Boolean, val tickMs: Long, val max: Long, val scheduler: Scheduler) {

    init {
        check(marbles.all { it in RxMarble.NUMBERS || it in OTHERS }) { "Invalid marble: $marbles" }
        check(marbles.filter { it == '^' }.count() <= 1) { "Multiplate subscriptions in $marbles" }
        check(marbles.filter { it == '#' || it == '|' }.count() <= 1) { "Multiple terminal events in $marbles" }
        for (c in listOf('|','^', '#')) {
            check(marbles.filter { it == c }.count() <= 1) { "Multiple values for '$c'" }
        }
    }

    fun firstFrame() : Long {
        val first = if (hot) marbles.indexOfFirst { it == '^' } else -1
        return if (first == -1) 0L else first.toLong()
    }
    fun lastFrame() : Long {
        val end = marbles.indexOfFirst { it == '#' || it == '|'}
        return if (end == -1)  max else end.toLong()
    }

    fun completion(): Observable<Int> {
        val end = marbles.firstOrNull { it == '#' || it == '|'}
        return when(end) {
            null ->  Observable.never()
            '#' ->  Observable.error(Error)
            '|' -> Observable.empty()
            else -> TODO("invalid completion ${completion()}")
        }
    }

    fun create() : Observable<Int> {
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
        private val OTHERS = listOf('|','^', '#', '-')
    }
    object Error : RuntimeException("error")
}


