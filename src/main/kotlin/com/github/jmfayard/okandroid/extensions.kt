package com.github.jmfayard.okandroid

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.NfcAdapter
import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.v4.app.NotificationCompat
import android.support.v4.content.FileProvider.getUriForFile
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import com.wealthfront.magellan.BaseScreenView
import com.wealthfront.magellan.Screen
import io.reactivex.Scheduler
import io.reactivex.SingleTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.schedulers.TestScheduler
import java.io.File


typealias MagellanScreen = Screen<*>?
typealias MagellanView = BaseScreenView<*>

val MagellanScreen.ioThread : Scheduler
    get() = if (isRunningTest) TestScheduler() else Schedulers.io()

val MagellanScreen.mainThread : Scheduler
    get() = if (isRunningTest) TestScheduler() else AndroidSchedulers.mainThread()


val MagellanView.attach: Boolean
    get() = true


fun <T> onMainThread(operation: (T) -> Unit): SingleTransformer<T, T> {
    return SingleTransformer { single ->
        single.observeOn(AndroidSchedulers.mainThread())
                .map { value ->
                    operation(value)
                    value
                }
                .observeOn(Schedulers.io())
    }
}


fun MagellanView.inflateViewFrom(@LayoutRes layoutRes: Int): View =
        LayoutInflater.from(context).inflate(layoutRes, this, true)


val BaseScreenView<*>.inflater: LayoutInflater
    get() = LayoutInflater.from(context)


fun toast(message: String, long: Boolean = false) {
    if (isRunningTest) return
    val length = if (long) Toast.LENGTH_LONG else Toast.LENGTH_SHORT
    // use the applicationContext so that the toast is shown even if the screen is not in the foreground
    Toast.makeText(applicationContext(), message, length).show()
}

private fun applicationContext(): Context =
        App.ctx

// https://stackoverflow.com/questions/28550370/how-to-detect-whether-android-app-is-running-ui-test-with-espresso
val isRunningTest: Boolean by lazy {
    try {
        Class.forName("android.support.test.espresso.Espresso")
        return@lazy true
    } catch (e: ClassNotFoundException) {
    }
    try {
        Class.forName("io.kotlintest.specs.StringSpec")
        return@lazy true
    } catch (e: ClassNotFoundException) {
    }
    false
}

fun Intent.description(): String = buildString {
    append(this@description.toString())
    val data = extras?.keySet().orEmpty().map { key ->
        key to extras?.get(key)
    }.toMap()
    if (data.isNotEmpty()) append(data.toString())
    if (ndefRecords().isNotEmpty()) {
        ndefRecords().forEach { append("Record ${String(it.payload)}${String(it.type)}\n") }
    }
}

fun Intent.ndefRecords(): List<NdefRecord> {
    val rawMessages = getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES) ?: return emptyList()
    return rawMessages.flatMap { (it as NdefMessage).records.toList() }
}

inline fun Context.buildNotification(operation: NotificationCompat.Builder.() -> Unit): Notification =
        NotificationCompat.Builder(this).apply { operation() }.build()

inline fun Context.sendNotification(id: Int, notificationBuilder: NotificationCompat.Builder.() -> Unit): Notification =
        buildNotification(notificationBuilder).also { notification -> notificationManager.notify(id, notification) }

val Context.notificationManager: NotificationManager
    get() = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

fun Context.pendingIntent(
        intent: Intent,
        requestCode: Int = System.currentTimeMillis().toInt(),
        flag: Int = PendingIntent.FLAG_UPDATE_CURRENT,
        options: Bundle = Bundle()
): PendingIntent =
        PendingIntent.getActivity(this, requestCode, intent, flag, options)


fun Context.contentFile(directory: String, filename: String): Uri {
    val file = File(filesDir, directory).resolve(filename)
    val contentUri = getUriForFile(this, packageName, file)
    Log.i("File", contentUri.toString())
    return contentUri
}

