@file:Suppress("NOTHING_TO_INLINE")

package com.github.jmfayard.okandroid.utils

import android.content.Intent
import android.net.Uri
import android.text.Html
import okhttp3.HttpUrl

// See https://github.com/jmfayard/codepath/blob/master/Common-Implicit-Intents.md

object Intents {
    fun viewUrl(url: okhttp3.HttpUrl) : android.content.Intent
            = android.content.Intent(android.content.Intent.ACTION_VIEW, Uri.parse(url.toString()))

    inline fun viewUrl(crossinline url: okhttp3.HttpUrl.Builder.() -> Unit) : android.content.Intent
        = com.github.jmfayard.okandroid.utils.Intents.viewUrl(HttpUrl.Builder().apply(url).build())

    fun sendEmail(subject: String? = null, email : String? = null, text: String? = null, type: String = "plain/text") =
            android.content.Intent(android.content.Intent.ACTION_SENDTO).apply {
                val uriText = buildString {
                    append("mailto:")
                    if (email != null) append(email)
                    append("?subject=")
                    if (subject != null) {
                        append(android.net.Uri.encode(subject))
                    }
                    append("&body=")
                    if (text != null) {
                        append(android.net.Uri.encode(text))
                    }

                }
                setData(android.net.Uri.parse(uriText))
            }

    fun openPlaystore(packageName : String) : android.content.Intent
            = android.content.Intent(android.content.Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName"))

    inline fun shareText(text: String): android.content.Intent = com.github.jmfayard.okandroid.utils.Intents.intentSend("text/plain") {
        putExtra(Intent.EXTRA_TEXT, text)
    }

    inline fun shareHtml(text: String): android.content.Intent = com.github.jmfayard.okandroid.utils.Intents.intentSend("text/html") {
        putExtra(Intent.EXTRA_TEXT, Html.fromHtml(text))
    }

    inline fun intentSend(type: String, crossinline builder: android.content.Intent.() -> Unit): android.content.Intent =
            android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                setType(type)
                builder()
            }

    fun capturePhoto(file: android.net.Uri): android.content.Intent = android.content.Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE).apply {
        type = "image/png"
        flags = android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION or android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP
        putExtra(android.provider.MediaStore.EXTRA_OUTPUT, file)
    }
}