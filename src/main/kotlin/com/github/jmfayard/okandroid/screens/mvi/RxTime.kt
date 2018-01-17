package com.github.jmfayard.okandroid.screens.mvi

import io.reactivex.*
import io.reactivex.schedulers.TestScheduler
import java.util.concurrent.TimeUnit.*


fun TestScheduler.advanceTimeBy(delay: Long) = this.advanceTimeBy(delay, MILLISECONDS)

fun TestScheduler.advanceTimeTo(delay: Long) = this.advanceTimeTo(delay, MILLISECONDS)

fun <T> Flowable<T>.delaySubscription(delay: Long, scheduler: Scheduler) =
        this.delaySubscription(delay, MILLISECONDS, scheduler)

fun <T> Single<T>.delaySubscription(delay: Long, scheduler: Scheduler) =
        this.delaySubscription(delay, MILLISECONDS, scheduler)

fun <T> Maybe<T>.delaySubscription(delay: Long, scheduler: Scheduler) =
        this.delaySubscription(delay, MILLISECONDS, scheduler)

fun <T> Observable<T>.delaySubscription(delay: Long, scheduler: Scheduler) =
        this.delaySubscription(delay, MILLISECONDS, scheduler)


val Int.miliseconds get() = MINUTES.toMillis(this.toLong())
val Int.seconds get() = SECONDS.toMillis(this.toLong())
val Int.minutes get() = MINUTES.toMillis(this.toLong())
