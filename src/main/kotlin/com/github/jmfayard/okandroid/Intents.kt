@file:Suppress("NOTHING_TO_INLINE")

package com.github.jmfayard.okandroid

import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.text.Html
import okhttp3.HttpUrl

// See https://github.com/jmfayard/codepath/blob/master/Common-Implicit-Intents.md

object Intents {
    fun viewUrl(url: HttpUrl) : Intent
            = Intent(Intent.ACTION_VIEW, Uri.parse(url.toString()))

    inline fun viewUrl(crossinline url: HttpUrl.Builder.() -> Unit) : Intent
        = viewUrl(HttpUrl.Builder().apply(url).build())

    fun sendEmail(subject: String? = null, email : String? = null, text: String? = null, type: String = "plain/text") =
            Intent(Intent.ACTION_SENDTO).apply {
                val uriText = buildString {
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
                setData(Uri.parse(uriText))
            }

    fun openPlaystore(packageName : String) : Intent
            = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName"))

    inline fun shareText(text: String): Intent = intentSend("text/plain") {
        putExtra(android.content.Intent.EXTRA_TEXT, text)
    }

    inline fun shareHtml(text: String): Intent = intentSend("text/html") {
        putExtra(android.content.Intent.EXTRA_TEXT, Html.fromHtml(text))
    }

    inline fun intentSend(type: String, crossinline builder: Intent.() -> Unit): Intent =
            Intent(Intent.ACTION_SEND).apply {
                setType(type)
                builder()
            }

    fun capturePhoto(file: Uri): Intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
        type = "image/png"
        flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_CLEAR_TOP
        putExtra(MediaStore.EXTRA_OUTPUT, file)
    }
}