package com.github.jmfayard.okandroid.room

import io.reactivex.Flowable
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


class DbConnector(
        val subscribeOn: Scheduler = Schedulers.io(),
        val observeOn: Scheduler = AndroidSchedulers.mainThread()) {

    fun delete(person: Person): Single<Unit> =
            Single.fromCallable { DB.albums().delete(person) }
                    .subscribeOn(subscribeOn)
                    .observeOn(observeOn)

    fun insert(person: Person): Single<Unit> =
            Single.fromCallable { DB.albums().insert(person) }
                    .subscribeOn(subscribeOn)
                    .observeOn(observeOn)

    fun queryAll(): Flowable<List<Person>>? {
        return DB.albums().getAllPeople()
                .subscribeOn(subscribeOn)
                .observeOn(observeOn)
    }

    fun deleteAll(): Single<Int> =
            Single.fromCallable { DB.albums().deleteAll() }
                    .subscribeOn(subscribeOn)
                    .observeOn(observeOn)
}