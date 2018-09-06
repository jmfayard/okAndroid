package com.github.jmfayard.screens

import android.content.Context
import com.github.jmfayard.jobs.Jobs
import com.github.jmfayard.okandroid.R
import com.github.jmfayard.okandroid.toast
import com.github.jmfayard.room.DbConnector
import com.github.jmfayard.room.Person
import com.github.jmfayard.utils.PatternEditableBuilder
import com.github.jmfayard.utils.See
import com.wealthfront.magellan.Screen
import io.reactivex.rxkotlin.subscribeBy
import timber.log.Timber


@See(layout = R.layout.room_screen, java = PatternEditableBuilder::class)
class RoomScreen : Screen<RoomView>() {

    val text = """
Actions: #insert and #clear
"""

    override fun createView(context: Context) = RoomView(context)

    override fun onShow(context: Context?) {
        view.init()
        dbQuery()
    }

    override fun getTitle(context: Context): String = "Room"


    fun clickedOn(hashtag: String) {
        toast("Clicked on $hashtag")
        when (hashtag) {
            "#insert" -> insert()
            "#clear" -> delete()
            else -> toast("Hashtag $hashtag not handled")
        }
    }

    var fakeit = 1

    fun insert() {
//        val person = Person(0, Fakeit.name().firstName(), Fakeit.name().lastName())
        val person = Person(0, "First name ${fakeit++}", "Last name ${fakeit++}")
        connector.insert(person)
                .subscribeBy(
                        onSuccess = { Jobs.launchSyncNow() },
                        onError = this::showError
                )
    }

    fun delete() {
        connector.deleteAll().subscribeBy(onSuccess = this::noop, onError = this::showError)
    }

    fun dbQuery() {
        connector.queryAll()?.subscribeBy(
                onNext = { users: List<Person> ->
                    if (users.isEmpty())
                        view.slimAdapter.updateData(listOf(HeaderItem("No items yet")))
                    else
                        view.slimAdapter.updateData(users)
                },
                onError = { e -> Timber.e("DB failed $e") }
        )
    }

    val connector = DbConnector()
    fun onItemClicked(data: Person) {
        connector.delete(data).subscribeBy(onSuccess = this::noop, onError = this::showError)
    }

    private fun <T> noop(next: T) {}

    fun showError(t: Throwable) = toast("Rx throwed $t")

    fun toggleSelected(uuid: Long) {
        toast("Toggle $uuid")
    }

}









