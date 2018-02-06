@file:Suppress("NOTHING_TO_INLINE")

package com.github.jmfayard.okandroid.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.Intent.*
import android.net.Uri
import android.provider.MediaStore
import android.text.Html
import androidx.net.toUri
import androidx.os.bundleOf
import okhttp3.HttpUrl

// See https://github.com/jmfayard/codepath/blob/master/Common-Implicit-Intents.md

inline fun <reified T : Activity> Context.intentFor(vararg pairs: Pair<String, Any?>): Intent {
    return Intent(this, T::class.java)
            .apply { putExtras(bundleOf(*pairs)) }
}

fun intentFor(action: String, vararg pairs: Pair<String, Any?>): Intent {
    val intent : Intent = Intent(action)
    val data = pairs.toMap()
    val type = data["type"] as? String
    val uri = data["uri"] as? String
    val extras = data.filterKeys { it !in listOf("type", "uri") }.map { it.key to it.value }.toTypedArray()
    if (type!= null) intent.type = type
    if (uri != null) intent.data = uri.toUri()
    intent.putExtras(bundleOf(*extras))
    return intent
}



object Intents {
    fun viewUrl(url: okhttp3.HttpUrl): Intent = Intent(ACTION_VIEW, Uri.parse(url.toString()))

    inline fun viewUrl(crossinline url: okhttp3.HttpUrl.Builder.() -> Unit): Intent = viewUrl(HttpUrl.Builder().apply(url).build())

    fun sendEmail(subject: String? = null, email: String? = null, text: String? = null, type: String = "plain/text") =
            intentFor(ACTION_SENDTO, "uri" to emailUri(email, subject, text))

    private fun emailUri(email: String?, subject: String?, text: String?): String {
        return buildString {
            append("mailto:")
            if (email != null) append(email)
            append("?subject=")
            if (subject != null) {
                append(Uri.encode(subject))
            }
            append("&body=")
            if (text != null) {
                append(Uri.encode(text))
            }

        }
    }

    fun openPlaystore(packageName: String): Intent =
            intentFor(ACTION_VIEW, "uri" to "market://details?id=$packageName")

    inline fun shareText(text: String): Intent =
            intentFor(ACTION_SEND, "type" to "text/plain", EXTRA_TEXT to text)

    inline fun shareHtml(text: String): Intent =
            intentFor(ACTION_SEND, "type" to "text/html", EXTRA_TEXT to Html.fromHtml(text))

    fun capturePhoto(file: android.net.Uri): Intent =
            intentFor(MediaStore.ACTION_IMAGE_CAPTURE, MediaStore.EXTRA_OUTPUT to file)
}