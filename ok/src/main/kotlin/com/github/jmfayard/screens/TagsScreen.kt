package com.github.jmfayard.screens

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.support.v4.content.ContextCompat.startActivity
import com.afollestad.materialdialogs.MaterialDialog
import com.github.jmfayard.okandroid.*
import com.github.jmfayard.jobs.Jobs
import com.github.jmfayard.screens.TagAction.*
import com.github.jmfayard.screens.TagAction.Companion.clickedOn
import com.github.jmfayard.utils.Intents
import com.github.jmfayard.utils.PatternEditableBuilder
import com.github.jmfayard.utils.See
import com.github.jmfayard.utils.intentFor
import timber.log.Timber
import java.util.regex.Pattern


enum class TagAction {
    url, email, playtore, photo, anotherActivity,
    choose, clear, notification, dialogs,
    share, requestbin, requestnow, sync;

    companion object {
        val text = """
            HELLLO

With Intents, we can open an $url or send an $email or open the $playtore or $share content, take a $photo and are also used inside a $notification or open $anotherActivity

Widgets: $dialogs $choose

Jobs: $requestbin $requestnow $sync $clear
"""

        fun TagsScreen.clickedOn(hashtag: String) {
            val action = values().firstOrNull { it.toString() == hashtag } ?: return
            say("Clicked on $hashtag", toast = false)
            when (action) {
                choose -> materialDialog()
                clear -> display?.history = ""
                notification -> createNotification()
                dialogs -> createMagellanDialog()
                url -> launchIntent(action)
                email -> launchIntent(action)
                playtore -> launchIntent(action)
                photo -> launchIntent(action)
                anotherActivity -> launchIntent(action)
                share -> launchIntent(action)
                requestbin -> Jobs.postSoonToHttpbin()
                requestnow -> Jobs.postNowToHttpbin()
                sync -> Jobs.launchSyncNow()
            }
        }


    }


    override fun toString(): String = "#$name"

}

@See(layout = R.layout.tags_screen, java = PatternEditableBuilder::class)
class TagsScreen : MagellanScreen<TagsDisplay>() {

    override fun createView(context: Context) =
            MagellanView(context, TagsDisplay.LAYOUT, SomeView::createTagsDisplay)

    override val screenTitle: Int
        get() = R.string.rx_screen_title

    override fun onResume(context: Context?) {
        display?.tagsContent = TagAction.text
        PatternEditableBuilder()
                .addPattern(Pattern.compile("\\@(\\w+)"))
                .addPattern(Pattern.compile("#(\\w+)"), R.color.link) { hashtag ->
                    clickedOn(hashtag)
                }
                .into(activity.findViewById(R.id.tags_content))
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
        if (toast) toast(message)
        val displayOk = display ?: return
        displayOk.history = "$message\n${displayOk.history}"
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
        anotherActivity -> activity.intentFor<ReceiverActivity>("Greeting" to "Hello World")
        email -> Intents.sendEmail(email = "katogarabato1@gmail.com", text = "Que tal?", subject = "hola")
        playtore -> Intents.openPlaystore("com.whatsapp")
        share -> Intents.shareText(TagAction.text)
        photo -> Intents.capturePhoto(activity.contentFile("image", "okandroid"))
        url -> Intents.viewUrl {
            scheme("https")
            host("google.com")
        }
        else -> {
            say("Error: no intent defined for $action"); Intent()
        }
    }

    fun createNotification() {
        val context = activity.applicationContext

        context.sendNotification(id = 42) {
            setSmallIcon(R.drawable.ic_okandroid)
            setContentTitle("My notification")
            setContentText("Hello World!")
            setAutoCancel(true)

            val urlIntent = context.pendingIntent(createIntent(url))
            setContentIntent(urlIntent)

            addAction(R.drawable.ic_http_black_24dp, "google", urlIntent)

            addAction(R.drawable.ic_email_black_24dp, "email",
                    context.pendingIntent(createIntent(email)))
            addAction(R.drawable.ic_share_black_24dp, "share",
                    context.pendingIntent(createIntent(share)))


        }
    }
}






