package com.github.jmfayard.okandroid

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v4.content.ContextCompat.startActivity
import com.github.jmfayard.okandroid.databinding.AndroidFeaturesBinding
import com.wealthfront.magellan.BaseScreenView
import com.wealthfront.magellan.Screen
import com.github.jmfayard.okandroid.utils.PatternEditableBuilder
import java.util.regex.Pattern


class AndroidFeaturesScreen : Screen<AndroidFeaturesView>() {

    val text = """
With Intents, we can open an #url or send an #email or open the #playstore or #share content, take a #photo

They are also used inside a #notification

--

You can #clear history
"""

    override fun createView(context: Context) = AndroidFeaturesView(context)

    override fun getTitle(context: Context?): String = "Android Features"

    fun clickedOn(hashtag: String) {
        view.log("Clicked on $hashtag")
        when (hashtag) {
            "#clear" -> view.clearHistory()
            "#notification" -> view.createNotification(another = false)
            in listOf("#url", "#email", "#playstore", "#share", "#photo") -> view.launchIntent(view.createIntent(hashtag))
            else -> view.toast("Hashtag $hashtag not handled")
        }
    }

    override fun onResume(context: Context?) {
        view.setupWidgets()
    }
}


class AndroidFeaturesView(context: Context) : BaseScreenView<AndroidFeaturesScreen>(context) {

    val binding = AndroidFeaturesBinding.inflate(inflater, this, true)


    fun setupWidgets() {
        binding.htmlContent.text = screen.text

        PatternEditableBuilder()
                .addPattern(Pattern.compile("\\@(\\w+)"))
                .addPattern(Pattern.compile("#(\\w+)"), Color.BLUE) { hashtag ->
                    screen.clickedOn(hashtag)
                }
                .into(binding.htmlContent)
    }

    fun createIntent(hashtag: String): Intent = when (hashtag) {
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


    fun clearHistory() {
        binding.actionResult.text = ""
    }

    fun log(message: String) {
        binding.actionResult.text = "$message\n${binding.actionResult.text}"

    }

    fun createNotification(another: Boolean) {
        context.sendNotification(id = 42) {
            setSmallIcon(R.drawable.ic_okandroid)
            setContentTitle("My notification")
            setContentText("Hello World!")
            setAutoCancel(true)

            val urlIntent = context.pendingIntent(createIntent("#url"))
            setContentIntent(urlIntent)
            addAction(R.drawable.ic_http_black_24dp, "google", urlIntent)

            if (another) {
//                addAction(R.drawable.ic_playstore, "playstore",
//                        context.pendingIntent(createIntent("#playstore")))
                addAction(R.drawable.ic_visibility_black_24dp, "photo",
                        context.pendingIntent(createIntent("#photo")))
            } else {
                addAction(R.drawable.ic_email_black_24dp, "email",
                        context.pendingIntent(createIntent("#email")))
                addAction(R.drawable.ic_share_black_24dp, "share",
                        context.pendingIntent(createIntent("#share")))

            }

        }
    }

    fun launchIntent(intent: Intent) {
        if (intent.resolveActivity(context.packageManager) != null) {
            startActivity(context, intent, Bundle())
        } else {
            toast("Can not handle this intent")
        }
    }


}









