package com.github.jmfayard.okandroid

import android.content.Context
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import com.github.jmfayard.room.Chinook
import com.github.jmfayard.room.Chinook.ARTISTS
import com.github.jmfayard.room.Chinook.CUSTOMERS
import com.github.jmfayard.room.Chinook.EMPLOYEES
import com.github.jmfayard.room.Chinook.GENRES
import com.github.jmfayard.room.Chinook.PLAYLISTS
import com.github.jmfayard.room.Chinook.TRACKS
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import timber.log.Timber.v
import kotlin.system.measureTimeMillis

@RunWith(AndroidJUnit4::class)
class RoomTest {

    companion object


    @Rule
    @JvmField
    var activityTestRule = ActivityTestRule(MainActivity::class.java)

    val context: Context get() = activityTestRule.activity.applicationContext

    @Test fun initDB() {
        val file = Chinook.copyFromResources(context)
        v("File ${file.absolutePath} has length ${file.length()}")

        val albumsDao = Chinook.DB.albums()
        albumsDao.selectAll()
                .take(1)
                .doAfterNext { albums ->
                    v("Albums: " + albums.firstFive())
                    v("Found ${albums.size} albums")
                }
                .test()
                .awaitTerminalEvent()

        val queries = Chinook.DB.queries()
        queries.tracks().run {    v( "Found $size elems in $TRACKS:   " + firstFive()) }
        queries.customers().run { v( "Found $size elems in $CUSTOMERS:" + firstFive()) }
        queries.employees().run { v( "Found $size elems in $EMPLOYEES:" + firstFive()) }
        queries.artists().run {   v( "Found $size elems in $ARTISTS:  " + firstFive()) }
        queries.playlists().run { v( "Found $size elems in $PLAYLISTS:" + firstFive()) }
        queries.genres().run {    v( "Found $size elems in $GENRES: "   +   firstFive()) }


        queries.musicTracksOfGenre("Rock").run { v("Super complex query: Found $size elements" + firstFive()) }

        val time = measureTimeMillis {  queries.musicTracksOfGenre("Rock") }
        v("Super complex query has run in $time miliseconds")

    }

}

private fun <T> List<T>.firstFive() : String = take(5).joinToString(separator = "\n", prefix = "\n")