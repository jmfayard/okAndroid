package com.github.jmfayard.okandroid.screens.room

import io.reactivex.Flowable
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.toSingle
import io.reactivex.schedulers.Schedulers

class DbConnector(
        val subscribeOn: Scheduler = Schedulers.io(),
        val observeOn: Scheduler = AndroidSchedulers.mainThread()) {

    fun delete(person: Person): Single<Unit> =
            { DB.personDao().delete(person) }
                    .toSingle()
                    .subscribeOn(subscribeOn)
                    .observeOn(observeOn)

    fun insert(person: Person): Single<Unit> =
            { DB.personDao().insert(person) }
                    .toSingle()
                    .subscribeOn(subscribeOn)
                    .observeOn(observeOn)

    fun queryAll(): Flowable<List<Person>>? {
        return DB.personDao().getAllPeople()
                .subscribeOn(subscribeOn)
                .observeOn(observeOn)
    }

    fun deleteAll(): Single<Int> = { DB.personDao().deleteAll() }
            .toSingle()
            .subscribeOn(subscribeOn)
            .observeOn(observeOn)
}