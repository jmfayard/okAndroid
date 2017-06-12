package com.github.jmfayard.okandroid.screens

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.support.v4.content.ContextCompat.startActivity
import com.github.jmfayard.okandroid.*
import com.github.jmfayard.okandroid.databinding.TagsScreenBinding
import com.github.jmfayard.okandroid.databinding.TagsScreenBinding.inflate
import com.github.jmfayard.okandroid.utils.Intents
import com.github.jmfayard.okandroid.utils.PatternEditableBuilder
import com.github.jmfayard.okandroid.utils.See
import com.marcinmoskala.kotlinandroidviewbindings.bindToText
import com.wealthfront.magellan.BaseScreenView
import com.wealthfront.magellan.Screen
import timber.log.Timber
import java.util.regex.Pattern.compile


@See(layout = R.layout.tags_screen, java = PatternEditableBuilder::class)
class TagsScreen : Screen<TagsView>() {

    val text = """
With Intents, we can open an #url or send an #email or open the #playstore or #share content, take a #photo and are also used inside a #notification or open another #activity

Magellan has support for #dialogs

--

You can #clear history
"""
    val intentHashtags = listOf("#url", "#email", "#playstore", "#share", "#photo", "#activity")

    override fun createView(context: Context) = TagsView(context)

    override fun getTitle(context: Context): String = context.getString(R.string.rx_screen_title)

    override fun onResume(context: Context?) {
        view.htmlContent = text
        view.setupTags()
    }


    fun clickedOn(hashtag: String) {
        say("Clicked on $hashtag", toast = false)
        when (hashtag) {
            "#clear" -> view.history = ""
            "#notification" -> view.createNotification()
            "#dialogs" -> createMagellanDialog()
            in intentHashtags -> view.launchIntent(view.createIntent(hashtag))
            else -> say("Hashtag $hashtag not handled")
        }
    }


    fun say(message: String, toast: Boolean = true) {
        Timber.i("SAY: $message")
        if (view == null) return
        if (toast) toast(message)
        view.history += "\n" + message
    }

    private fun createMagellanDialog() = buildDialog {
        fun show(message: String) {
            say("Dialog Result: $message")
            toast(message)
        }
        setTitle("This is a magellan Dialog")
        setMessage("You can either approve or dismiss it")
        setNeutralButton("Neither") { _, _ ->
            show("Whatever")
        }
        setNegativeButton("Dismiss") { _, _ ->
            show("Dismissed!")
        }
        setPositiveButton("Approve") { _, _ ->
            show("Approved!")
        }
    }


    // TODO: should be added to a base class
    fun buildDialog(builder: AlertDialog.Builder.() -> Unit) {
        showDialog { activity ->
            AlertDialog.Builder(activity).apply { builder() }.show()
        }
    }


}


class TagsView(context: Context) : BaseScreenView<TagsScreen>(context) {


    val binding: TagsScreenBinding = inflate(inflater, this, attach)
    var htmlContent by binding.htmlContent.bindToText()
    var history by binding.actionResult.bindToText()

    fun setupTags() {
        PatternEditableBuilder()
                .addPattern(compile("\\@(\\w+)"))
                .addPattern(compile("#(\\w+)"), android.graphics.Color.BLUE) { hashtag ->
                    screen.clickedOn(hashtag)
                }
                .into(binding.htmlContent)
    }

    fun createIntent(hashtag: String): Intent = when (hashtag) {
        "#activity" -> Intent(context, ReceiverActivity::class.java).apply {
            putExtra("Greeting", "Hello World")
        }
        "#email" -> Intents.sendEmail(email = "katogarabato1@gmail.com", text = "Que tal?", subject = "hola")
        "#playstore" -> Intents.openPlaystore("com.whatsapp")
        "#share" -> Intents.shareText(screen.text)
        "#photo" -> Intents.capturePhoto(context.contentFile("image", "okandroid"))
        "#url" -> Intents.viewUrl {
            scheme("https")
            host("google.com")
        }
        else -> Intent()
    }

    fun createNotification() {
        context.sendNotification(id = 42) {
            setSmallIcon(R.drawable.ic_okandroid)
            setContentTitle("My notification")
            setContentText("Hello World!")
            setAutoCancel(true)

            val urlIntent = context.pendingIntent(createIntent("#url"))
            setContentIntent(urlIntent)

            addAction(R.drawable.ic_http_black_24dp, "google", urlIntent)

            addAction(R.drawable.ic_email_black_24dp, "email",
                    context.pendingIntent(createIntent("#email")))
            addAction(R.drawable.ic_share_black_24dp, "share",
                    context.pendingIntent(createIntent("#share")))


        }
    }

    fun launchIntent(intent: Intent) {
        history += intent.description() + "\n"
        if (intent.resolveActivity(context.packageManager) != null) {
            startActivity(context, intent, android.os.Bundle())
        } else {
            screen.say("Can not handle this intent")
        }
    }
}









