package com.github.jmfayard.okandroid

import android.os.Bundle
import android.util.Log
import android.view.MenuItem

import com.github.jmfayard.okandroid.screens.HomeScreen
import com.github.jmfayard.okandroid.utils.See
import com.wealthfront.magellan.Navigator
import com.wealthfront.magellan.Screen
import com.wealthfront.magellan.ScreenLifecycleListener
import com.wealthfront.magellan.support.SingleActivity
import com.wealthfront.magellan.transitions.DefaultTransition
import android.databinding.adapters.TextViewBindingAdapter.setText
import android.nfc.NdefMessage
import android.nfc.NfcAdapter
import android.os.Parcelable
import android.widget.TextView
import android.content.Intent
import com.github.jmfayard.okandroid.screens.TagsScreen


@See(layout = R.layout.all_activity)
class MainActivity : SingleActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.all_activity)
        handleShortcutIntents()
    }

    fun handleShortcutIntents() {
        // Overwriting root screen with magellan https://github.com/wealthfront/magellan/issues/41
        val screen = intent?.extras?.getString("screen") ?: "main"
        if (screen == "tags") {
            getNavigator().resetWithRoot(this, TagsScreen())
        }
    }


    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> getNavigator().replace(HomeScreen())
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    override fun createNavigator(): Navigator {
        val navigator = Navigator.withRoot(HomeScreen())
                .transition(DefaultTransition())
                .loggingEnabled(true)
                .build()
        navigator.addLifecycleListener(LifeCycle)
        return navigator
    }

    val LifeCycle = object : ScreenLifecycleListener {
        override fun onShow(screen: Screen<*>) {
            val title = screen.getTitle(applicationContext)
            Log.i("Navigator", "onShow(screen = '$title')")

            with(supportActionBar!!) {
                val firstScreen = screen is HomeScreen
                setDisplayUseLogoEnabled(firstScreen)
                setDisplayHomeAsUpEnabled(!firstScreen)
            }
        }

        override fun onHide(screen: Screen<*>) {
            val title = screen.getTitle(applicationContext)
            Log.i("Navigator", "onHide(screen = '$title')")
        }
    }

}
