package com.github.jmfayard.okandroid

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.v4.app.NotificationCompat
import android.support.v4.content.FileProvider.getUriForFile
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import com.wealthfront.magellan.BaseScreenView
import java.io.File


val BaseScreenView<*>.attach: Boolean
    get() = true

val BaseScreenView<*>.dontAttach: Boolean
    get() = false

fun BaseScreenView<*>.inflateViewFrom(@LayoutRes layoutRes: Int): View =
        LayoutInflater.from(context).inflate(layoutRes, this, true)


val BaseScreenView<*>.inflater: LayoutInflater
    get() = LayoutInflater.from(context)

fun BaseScreenView<*>.toast(s: String) = Toast.makeText(context, s, Toast.LENGTH_SHORT).show()

fun BaseScreenView<*>.longToast(s: String) = Toast.makeText(context, s, Toast.LENGTH_LONG).show()


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

