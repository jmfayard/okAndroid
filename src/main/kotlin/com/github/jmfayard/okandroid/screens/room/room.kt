package com.github.jmfayard.okandroid.screens.room

import android.arch.persistence.room.*
import com.github.jmfayard.okandroid.App
import io.reactivex.Flowable

val DB: MyDatabase  by lazy {
    Room.databaseBuilder(App.ctx, MyDatabase::class.java, "room-1day").build()
}


@Dao
interface PersonDao {

    @Query("SELECT * FROM person")
    fun getAllPeople(): Flowable<List<Person>>

    @Query("DELETE FROM person")
    fun deleteAll(): Int

    @Insert
    fun insert(person: Person)

    @Delete
    fun delete(person: Person)


}


@Entity
data class Person(
        @PrimaryKey(autoGenerate = true) val uid: Long,
        val firstName: String = "",
        val lastName: String = ""
)

@Database(entities = arrayOf(Person::class), version = 1)
abstract class MyDatabase : RoomDatabase() {
    abstract fun personDao(): PersonDao
}