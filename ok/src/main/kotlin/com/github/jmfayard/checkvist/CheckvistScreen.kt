package com.github.jmfayard.checkvist

import android.annotation.TargetApi
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.support.customtabs.CustomTabsIntent
import com.afollestad.materialdialogs.MaterialDialog
import com.evernote.android.job.v14.PlatformAlarmServiceExact.createIntent
import com.github.jmfayard.checkvist.CheckvistAction.*
import com.github.jmfayard.checkvist.CheckvistAction.Companion.clickedOn
import com.github.jmfayard.okandroid.R
import com.github.jmfayard.okandroid.pendingIntent
import com.github.jmfayard.okandroid.toast
import com.github.jmfayard.screens.*
import com.github.jmfayard.utils.Intents
import com.github.jmfayard.utils.PatternEditableBuilder
import timber.log.Timber
import java.util.regex.Pattern


enum class CheckvistAction {
    chrome, permission,
    start, stop
    ;

    override fun toString(): String = "#$name"

    companion object {
        val text = """
Check $permission - Launch ${chrome} -
Overlay: ${start} ${stop}
"""

        fun CheckvistScreen.clickedOn(hashtag: String) {
            val action = values().firstOrNull { it.toString() == hashtag } ?: return
            say("Clicked on $hashtag", toast = false)
            when (action) {
                chrome -> playWithGoogleChrome()
                permission -> checkOverlayPermission()
                start -> overlayService(start = true)
                stop -> overlayService(start = false)
            }
        }


    }


}

val CODE_DRAW_OVER_OTHER_APP_PERMISSION = 2084;

class CheckvistScreen : MagellanScreen<TagsDisplay>() {

    override fun createView(context: Context) =
            MagellanView(context, TagsDisplay.LAYOUT, SomeView::createTagsDisplay)

    override val screenTitle: Int
        get() = R.string.rx_screen_title

    override fun onResume(context: Context?) {
        display?.tagsContent = CheckvistAction.text
        PatternEditableBuilder()
                .addPattern(Pattern.compile("\\@(\\w+)"))
                .addPattern(Pattern.compile("#(\\w+)"), R.color.link) { hashtag ->
                    clickedOn(hashtag)
                }
                .into(activity.findViewById(R.id.tags_content))
    }

    @TargetApi(Build.VERSION_CODES.M)
    fun checkOverlayPermission(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(activity)) {

            toast("Permission not granted, opening the settings")
            //If the draw over permission is not available open the settings screen
            //to grant the permission.
            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:${activity.packageName}"))
            activity.startActivityForResult(intent, CODE_DRAW_OVER_OTHER_APP_PERMISSION)
            return false
        } else {
            return true
        }
    }

    fun overlayService(start: Boolean) {
        if (!checkOverlayPermission()) return
        val intent = Intent(activity, ChatHeadService::class.java)
        if (start) {
            activity.startService(intent)
        } else {
            activity.stopService(intent)
        }
    }

    fun playWithGoogleChrome() {
        val pendingIntent = activity.pendingIntent(
                Intents.shareText("Hello World"),
                3434090,
                0,
                Bundle.EMPTY)

        val url = "https://m.checkvist.com/app/list/649516"
        val color = activity.resources.getColor(R.color.accent)
        val customTabsIntent = CustomTabsIntent.Builder()
                .addDefaultShareMenuItem()
                .addMenuItem("Share Hello World", pendingIntent)
                .enableUrlBarHiding()
                .setToolbarColor(color)
                .build()

        customTabsIntent.launchUrl(activity, Uri.parse(url));
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


}






