package com.github.jmfayard.okandroid.room

import android.arch.persistence.room.*
import android.content.Context
import com.github.jmfayard.okandroid.App
import com.github.jmfayard.okandroid.R
import com.github.jmfayard.okandroid.app
import com.github.jmfayard.okandroid.room.Chinook.ALBUMS
import com.github.jmfayard.okandroid.room.Chinook.ARTISTS
import com.github.jmfayard.okandroid.room.Chinook.CUSTOMERS
import com.github.jmfayard.okandroid.room.Chinook.EMPLOYEES
import com.github.jmfayard.okandroid.room.Chinook.GENRES
import com.github.jmfayard.okandroid.room.Chinook.MEDIA_TYPE
import com.github.jmfayard.okandroid.room.Chinook.PLAYLISTS
import com.github.jmfayard.okandroid.room.Chinook.PLAYLIST_TRACK
import com.github.jmfayard.okandroid.room.Chinook.TRACKS
import io.reactivex.Flowable
import java.io.File

object Chinook {

    val DB: ChinookDatabase  by lazy {
        Room.databaseBuilder(app().ctx, ChinookDatabase::class.java, "chinook.db").build()
    }
    /*** See http://www.sqlitetutorial.net/sqlite-sample-database/ **/
    const val SAMPLE_DATABASE = R.raw.chinook

    const val ALBUMS = "albums"
    const val TRACKS = "tracks"
    const val ARTISTS = "artists"
    const val CUSTOMERS = "customers"
    const val EMPLOYEES = "employees"
    const val GENRES = "genreS"
    const val MEDIA_TYPE = "media_types"
    const val PLAYLIST_TRACK = "playlist_track"
    const val PLAYLISTS = "playlists"

    fun copyFromResources(context: Context): File {
        val file = context.getDatabasePath("chinook.db")
        file.delete()
        val inputStream = context.resources.openRawResource(SAMPLE_DATABASE).buffered()
        file.outputStream().use { fileOutputStream ->
            while (inputStream.copyTo(fileOutputStream) > 0) {}
        }
        inputStream.close()
        println("File ${file.absolutePath} has length ${file.length()}")
        return file
    }

}

@Database(version = 1, exportSchema = false,
        entities = arrayOf(
    Album::class, Artist::class, Customer::class, Employee::class,
    Genre::class, MediaType::class, Playlist::class, PlaylistTrack::class, Track::class
))
abstract class ChinookDatabase : RoomDatabase() {
    abstract fun queries(): QueriesDao
    abstract fun albums(): AlbumsDao
    abstract fun customers(): CustomerDao
    abstract fun employees(): EmployeeDao
    abstract fun genres(): GenreDao
    abstract fun mediatypes(): MediaTypeDao
    abstract fun playlists(): PlaylistDao
    abstract fun playlistsTracks(): PlayListTrackDao
    abstract fun artists(): ArtistDao
    abstract fun tracks(): TrackDao
}


@Dao interface QueriesDao {


    @Query("""
select distinct t.TrackId, t.Name, t.Composer, t.UnitPrice, mt.Name as MediaType
 from tracks t
inner join media_types mt on mt.MediaTypeId = t.MediaTypeId
inner join genres g on t.GenreId = g.GenreId
inner join playlist_track pt on pt.TrackId = t.TrackId
inner join playlists p on p.PlaylistId = pt.PlaylistId
where p.Name = "Music" and g.Name = :genreName
        """)
    fun musicTracksOfGenre(genreName: String): List<TrackData>

    @Query("SELECT * FROM $TRACKS")
    fun tracks(): List<Track>

    @Query("SELECT * FROM $CUSTOMERS")
    fun customers(): List<Customer>

    @Query("SELECT * FROM $EMPLOYEES")
    fun employees(): List<Employee>

    @Query("SELECT * FROM $ARTISTS")
    fun artists(): List<Artist>

    @Query("SELECT * FROM $PLAYLISTS")
    fun playlists(): List<Playlist>

    @Query("SELECT * from $GENRES")
    fun genres(): List<Genre>


}

@Dao interface AlbumsDao : BaseDao<Album> {
    @Query("SELECT * FROM $ALBUMS")
    fun selectAll(): Flowable<List<Album>>
}
@Dao interface ArtistDao: BaseDao<Album>
@Dao interface CustomerDao: BaseDao<Customer>
@Dao interface EmployeeDao: BaseDao<Employee>
@Dao interface GenreDao: BaseDao<Genre>
@Dao interface MediaTypeDao: BaseDao<MediaType>
@Dao interface PlaylistDao: BaseDao<Playlist>
@Dao interface PlayListTrackDao: BaseDao<PlaylistTrack>

@Dao interface TrackDao: BaseDao<Track>


data class TrackData(
        val TrackId: Long,
        val Name: String,
        val Composer: String?,
        val UnitPrice: Float,
        val MediaType: String
)

@Entity(tableName = ALBUMS)
data class Album(
        @PrimaryKey(autoGenerate = true) val AlbumId: Long,
        val Title: String = "",
        val ArtistId: Long = 0
)

@Entity(tableName = ARTISTS)
data class Artist(
        @PrimaryKey(autoGenerate = true) val ArtistId: Long,
        val Name: String = ""
)

@Entity(tableName = CUSTOMERS)
data class Customer(
        @PrimaryKey(autoGenerate = true) val CustomerId: Long,
        val FirstName: String = "",
        val LastName: String = "",
        val Email: String = ""
)

@Entity(tableName = EMPLOYEES)
data class Employee(
        @PrimaryKey(autoGenerate = true) val EmployeeId: Long,
        val FirstName: String = "",
        val LastName: String = "",
        val Title: String = ""
)

@Entity(tableName = GENRES)
data class Genre(
        @PrimaryKey(autoGenerate = true) val GenreId: Long,
        val Name: String = ""
)

@Entity(tableName = MEDIA_TYPE)
data class MediaType(
        @PrimaryKey(autoGenerate = true) val MediaTypeId: Long,
        val Name: String = ""
)

@Entity(tableName = PLAYLIST_TRACK,
        primaryKeys = arrayOf("PlaylistId", "TrackId"),
        foreignKeys = arrayOf(
                ForeignKey(entity = Playlist::class, parentColumns = arrayOf("PlaylistId"), childColumns = arrayOf("PlaylistId")),
                ForeignKey(entity = Track::class, parentColumns = arrayOf("TrackId"), childColumns = arrayOf("TrackId"))
        ))
data class PlaylistTrack(
        val PlaylistId: Long,
        val TrackId: Long
)

@Entity(tableName = PLAYLISTS)
data class Playlist(
        @PrimaryKey(autoGenerate = true) val PlaylistId: Long,
        val Name: String = ""
)

@Entity(tableName = TRACKS,
        foreignKeys = arrayOf(
                ForeignKey(entity = Album::class, parentColumns = arrayOf("AlbumId"), childColumns = arrayOf("AlbumId")),
                ForeignKey(entity = MediaType::class, parentColumns = arrayOf("MediaTypeId"), childColumns = arrayOf("MediaTypeId")),
                ForeignKey(entity = Genre::class, parentColumns = arrayOf("GenreId"), childColumns = arrayOf("GenreId"))
))
data class Track(
        @PrimaryKey(autoGenerate = true) val TrackId: Long,
        val Name: String = "",
        val AlbumId: Long?,
        val MediaTypeId: Long?,
        val GenreId: Long?,
        val Composer: String?,
        val Milliseconds: Long,
        val Bytes: Long?,
        val UnitPrice: Float
)





