package com.github.jmfayard.okandroid.screens

import android.app.AlertDialog
import android.content.Context
import android.support.v4.content.ContextCompat.startActivity
import com.github.jmfayard.okandroid.*
import com.github.jmfayard.okandroid.databinding.AndroidFeaturesBinding
import com.github.jmfayard.okandroid.utils.*
import com.wealthfront.magellan.BaseScreenView
import com.wealthfront.magellan.DialogCreator
import com.wealthfront.magellan.Screen
import java.util.regex.Pattern.*



class TagsScreen : Screen<TagsView>() {

    val text = """
With Intents, we can open an #url or send an #email or open the #playstore or #share content, take a #photo and are also used inside a #notification

Magellan has support for #dialogs

--

You can #clear history
"""
    val intentHashtags = listOf("#url", "#email", "#playstore", "#share", "#photo")

    override fun createView(context: Context) = TagsView(context)

    override fun getTitle(context: Context?): String = "Android Features"

    override fun onResume(context: Context?) {
        view.setupWidgets()
    }


    fun clickedOn(hashtag: String) {
        view.log("Clicked on $hashtag")
        when (hashtag) {
            "#clear" -> view.clearHistory()
            "#notification" -> view.createNotification(another = false)
            "#dialogs" -> createMagellanDialog()
            in intentHashtags -> view.launchIntent(view.createIntent(hashtag))
            else -> view.toast("Hashtag $hashtag not handled")
        }
    }

    private fun createMagellanDialog() = buildDialog {
        setTitle("This is a magellan Dialog")
        setMessage("You can either approve or dismiss it")
        setNeutralButton("Neither") { _, _ ->
            view.toast("Whatever")
        }
        setNegativeButton("Dismiss") { _, _ ->
            view.toast("Dismissed!")
        }
        setPositiveButton("Approve") { _, _ ->
            view.toast("Approved!")
        }
    }


    // TODO: should be added to a base class
    fun buildDialog(builder : AlertDialog.Builder.() -> Unit) {
        showDialog { activity ->
            AlertDialog.Builder(activity).apply { builder() }.show()
        }
    }
}





class TagsView(context: Context) : BaseScreenView<TagsScreen>(context) {

    val binding = AndroidFeaturesBinding.inflate(inflater, this, true)

    fun setupWidgets() {
        binding.htmlContent.text = screen.text

        PatternEditableBuilder()
                .addPattern(compile("\\@(\\w+)"))
                .addPattern(compile("#(\\w+)"), android.graphics.Color.BLUE) { hashtag ->
                    screen.clickedOn(hashtag)
                }
                .into(binding.htmlContent)
    }

    fun createIntent(hashtag: String): android.content.Intent = when (hashtag) {
        "#email" -> Intents.sendEmail(email = "katogarabato1@gmail.com", text = "Que tal?", subject = "hola")
        "#playstore" -> Intents.openPlaystore("com.whatsapp")
        "#share" -> Intents.shareText(screen.text)
        "#photo" -> Intents.capturePhoto(context.contentFile("image", "okandroid"))
        "#url" -> Intents.viewUrl {
            scheme("https")
            host("google.com")
        }
        else -> android.content.Intent()
    }


    fun clearHistory() {
        binding.actionResult.text = ""
    }

    fun log(message: String) {
        binding.actionResult.text = "$message\n${binding.actionResult.text}"

    }

    fun createNotification(another: Boolean) {
        context.sendNotification(id = 42) {
            setSmallIcon(com.github.jmfayard.okandroid.R.drawable.ic_okandroid)
            setContentTitle("My notification")
            setContentText("Hello World!")
            setAutoCancel(true)

            val urlIntent = context.pendingIntent(createIntent("#url"))
            setContentIntent(urlIntent)
            addAction(com.github.jmfayard.okandroid.R.drawable.ic_http_black_24dp, "google", urlIntent)

            if (another) {
//                addAction(R.drawable.ic_playstore, "playstore",
//                        context.pendingIntent(createIntent("#playstore")))
                addAction(com.github.jmfayard.okandroid.R.drawable.ic_visibility_black_24dp, "photo",
                        context.pendingIntent(createIntent("#photo")))
            } else {
                addAction(com.github.jmfayard.okandroid.R.drawable.ic_email_black_24dp, "email",
                        context.pendingIntent(createIntent("#email")))
                addAction(com.github.jmfayard.okandroid.R.drawable.ic_share_black_24dp, "share",
                        context.pendingIntent(createIntent("#share")))

            }

        }
    }

    fun launchIntent(intent: android.content.Intent) {
        if (intent.resolveActivity(context.packageManager) != null) {
            startActivity(context, intent, android.os.Bundle())
        } else {
            toast("Can not handle this intent")
        }
    }
}









