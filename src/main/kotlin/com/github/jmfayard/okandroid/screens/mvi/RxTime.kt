package com.github.jmfayard.okandroid.screens.mvi

import io.reactivex.*
import io.reactivex.rxkotlin.Flowables
import io.reactivex.rxkotlin.Observables
import io.reactivex.rxkotlin.Singles
import io.reactivex.schedulers.Schedulers.computation
import io.reactivex.schedulers.TestScheduler
import java.util.concurrent.TimeUnit.*


fun TestScheduler.advanceTimeBy(delay: Long): TestScheduler =
        this.apply { advanceTimeBy(delay, MILLISECONDS) }

fun TestScheduler.advanceTimeTo(delay: Long): TestScheduler =
        this.apply { advanceTimeTo(delay, MILLISECONDS) }

fun <T> Flowable<T>.delaySubscription(delay: Long, scheduler: Scheduler = computation()): Flowable<T> =
        this.delaySubscription(delay, MILLISECONDS, scheduler)

fun <T> Single<T>.delaySubscription(delay: Long, scheduler: Scheduler = computation()): Single<T> =
        this.delaySubscription(delay, MILLISECONDS, scheduler)

fun <T> Maybe<T>.delaySubscription(delay: Long, scheduler: Scheduler = computation()): Maybe<T> =
        this.delaySubscription(delay, MILLISECONDS, scheduler)

fun <T> Observable<T>.delaySubscription(delay: Long, scheduler: Scheduler = computation()): Observable<T> =
        this.delaySubscription(delay, MILLISECONDS, scheduler)

fun <T> Observable<T>.debounceMs(delay: Long, scheduler: Scheduler = computation()): Observable<T> =
        this.debounce(delay, MILLISECONDS, scheduler)

fun Singles.timer(delay: Long, scheduler: Scheduler = computation()): Single<Long> =
        Single.timer(delay, MILLISECONDS, scheduler)

fun Observables.timer(delay: Long, scheduler: Scheduler = computation()): Observable<Long> =
        Observable.timer(delay, MILLISECONDS, scheduler)

fun Observables.interval(delay: Long, scheduler: Scheduler = computation()): Observable<Long> =
        Observable.interval(delay, MILLISECONDS, scheduler)

fun Flowables.interval(delay: Long, scheduler: Scheduler = computation()): Flowable<Long> =
        Flowable.interval(delay, MILLISECONDS, scheduler)

val Int.miliseconds get() = MILLISECONDS.toMillis(this.toLong())
val Int.seconds get() = SECONDS.toMillis(this.toLong())
val Int.minutes get() = MINUTES.toMillis(this.toLong())
