package com.github.jmfayard.okandroid.screens

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.support.v4.content.ContextCompat.startActivity
import android.widget.TextView
import com.afollestad.materialdialogs.MaterialDialog
import com.github.jmfayard.okandroid.*
import com.github.jmfayard.okandroid.databinding.TagsScreenBinding
import com.github.jmfayard.okandroid.databinding.TagsScreenBinding.inflate
import com.github.jmfayard.okandroid.screens.TagAction.Companion.clickedOn
import com.github.jmfayard.okandroid.utils.Intents
import com.github.jmfayard.okandroid.utils.PatternEditableBuilder
import com.github.jmfayard.okandroid.utils.See
import com.marcinmoskala.kotlinandroidviewbindings.bindToText
import com.wealthfront.magellan.BaseScreenView
import com.wealthfront.magellan.Screen
import timber.log.Timber
import java.util.regex.Pattern


enum class TagAction {
    url, email, playtore, photo, anotherActivity,
    choose, clear, notification, dialogs,
    share;

    companion object {
        val text = """
With Intents, we can open an $url or send an $email or open the $playtore or $share content, take a $photo and are also used inside a $notification or open $anotherActivity

Widgets: $dialogs $choose

-- $clear
"""

        fun TagsScreen.clickedOn(hashtag: String) {
            val action = values().firstOrNull { it.toString() == hashtag } ?: return
            say("Clicked on $hashtag", toast = false)
            when (action) {
                choose -> materialDialog()
                clear -> view.history = ""
                notification -> createNotification()
                dialogs -> createMagellanDialog()
                url -> launchIntent(action)
                email -> launchIntent(action)
                playtore -> launchIntent(action)
                photo -> launchIntent(action)
                anotherActivity -> launchIntent(action)
                share -> launchIntent(action)
            }
        }


    }


    override fun toString(): String = "#$name"

}

@See(layout = R.layout.tags_screen, java = PatternEditableBuilder::class)
class TagsScreen : Screen<TagsView>() {

    override fun createView(context: Context) = TagsView(context)

    override fun getTitle(context: Context): String = context.getString(R.string.rx_screen_title)

    override fun onResume(context: Context?) {
        view.setHtmlcontentAndSetupTags(TagAction.text)
    }


    fun materialDialog() {
        val items = listOf("Twitter", "Facebook", "Hacker News")
        MaterialDialog.Builder(activity)
                .title("You waste more time on:")
                .items(items)
                .itemsCallback { _, _, which, text ->
                    say("Your choice: $text (option #$which)")
                }
                .show()
    }


    fun say(message: String, toast: Boolean = true) {
        Timber.i("SAY: $message")
        if (view == null) return
        if (toast) toast(message)
        view.history += "\n" + message
    }

    fun createMagellanDialog() = buildDialog {
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


    fun launchIntent(action: TagAction) {
        val intent = createIntent(action)
        say(intent.description())
        if (intent.resolveActivity(activity.packageManager) != null) {
            startActivity(activity, intent, android.os.Bundle())
        } else {
            say("Can not handle this intent")
        }
    }

    fun createIntent(action: TagAction): Intent = when (action) {
        TagAction.anotherActivity -> Intent(activity, ReceiverActivity::class.java).apply {
            putExtra("Greeting", "Hello World")
        }
        TagAction.email -> Intents.sendEmail(email = "katogarabato1@gmail.com", text = "Que tal?", subject = "hola")
        TagAction.playtore -> Intents.openPlaystore("com.whatsapp")
        TagAction.share -> Intents.shareText(TagAction.text)
        TagAction.photo -> Intents.capturePhoto(activity.contentFile("image", "okandroid"))
        TagAction.url -> Intents.viewUrl {
            scheme("https")
            host("google.com")
        }
        else -> { say("Error: no intent defined for $action") ; Intent() }
    }

    fun createNotification() {
        val context = activity.applicationContext

        context.sendNotification(id = 42) {
            setSmallIcon(R.drawable.ic_okandroid)
            setContentTitle("My notification")
            setContentText("Hello World!")
            setAutoCancel(true)

            val urlIntent = context.pendingIntent(createIntent(TagAction.url))
            setContentIntent(urlIntent)

            addAction(R.drawable.ic_http_black_24dp, "google", urlIntent)

            addAction(R.drawable.ic_email_black_24dp, "email",
                    context.pendingIntent(createIntent(TagAction.email)))
            addAction(R.drawable.ic_share_black_24dp, "share",
                    context.pendingIntent(createIntent(TagAction.share)))


        }
    }
}


class TagsView(context: Context) : BaseScreenView<TagsScreen>(context) {


    val binding: TagsScreenBinding = inflate(inflater, this, attach)
    var htmlContent by binding.htmlContent.bindToText()
    var history by binding.actionResult.bindToText()

    fun setupTags() {
        PatternEditableBuilder()
                .addPattern(Pattern.compile("\\@(\\w+)"))
                .addPattern(Pattern.compile("#(\\w+)"), R.color.link) { hashtag ->
                    screen.clickedOn(hashtag)
                }
                .into(findViewById<TextView>(R.id.htmlContent))
    }

    fun setHtmlcontentAndSetupTags(content: String) {
        htmlContent = content
        setupTags()
    }


}









