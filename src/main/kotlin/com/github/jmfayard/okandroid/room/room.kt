package com.github.jmfayard.okandroid.room

import android.arch.persistence.room.*
import com.github.jmfayard.okandroid.App
import io.reactivex.Flowable

val DB: MyDatabase  by lazy {
    Room.databaseBuilder(App.ctx, MyDatabase::class.java, "room-2day").build()
}


@Dao
interface PersonDao : BaseDao<Person> {

    @Query("SELECT * FROM person")
    fun getAllPeople(): Flowable<List<Person>>

    @Query("DELETE FROM person")
    fun deleteAll(): Int

    @Query("""
select * from Person
where deleted = 0 and synced = 0""")
    fun unsyncedPeople() : List<Person>

    @Query("UPDATE `Person` SET `synced`=:synced WHERE uid=:uid")
    fun markAsSynced(uid: Long, synced: Boolean)

}




@Entity
data class Person(
        @PrimaryKey(autoGenerate = true) val uid: Long,
        val firstName: String = "",
        val lastName: String = "",
        val deleted: Boolean = false,
        val synced: Boolean = false
)

@Database(entities = arrayOf(Person::class), version = 1, exportSchema = false)
abstract class MyDatabase : RoomDatabase() {
    abstract fun persons(): PersonDao
}