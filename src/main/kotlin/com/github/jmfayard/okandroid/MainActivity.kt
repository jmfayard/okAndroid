package com.github.jmfayard.okandroid

import android.app.Activity
import android.content.Context
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
import android.widget.Toast
import com.github.jmfayard.okandroid.screens.TagsScreen
import timber.log.Timber


@See(layout = R.layout.all_activity)
class MainActivity : SingleActivity() {

    companion object {
        val REQUEST_ENABLE_BT = 101
    }

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


    public override fun onResume() {
        super.onResume()
        // Check to see that the Activity started due to an Android Beam
        processIntent(intent)
//        if (NfcAdapter.ACTION_NDEF_DISCOVERED == intent.action) {
//        }
    }

    public override fun onNewIntent(intent: Intent) {
        // onResume gets called after this to handle the intent
        setIntent(intent)
    }

    /**
     * Parses the NDEF Message from the intent and prints to the TextView
     */
    fun processIntent(intent: Intent?) {
        if (intent == null || intent.hasCategory(Intent.CATEGORY_LAUNCHER)) return

        Toast.makeText(this, intent.description(), Toast.LENGTH_LONG).show()
//        val rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)
////         only one message sent during the beam
//        val msg = rawMsgs[0] as NdefMessage
////         record 0 contains the MIME type, record 1 is the AAR, if present
//        val payload = String(msg.records[0].payload)
//        getNavigator().currentScreen().toast(payload)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val result = when(resultCode) {
            Activity.RESULT_CANCELED -> "RESULT_CANCELED"
            Activity.RESULT_OK -> "RESULT_OK"
            else -> "ResultCode:$resultCode"
        }
        val request = when(requestCode) {
            REQUEST_ENABLE_BT -> "REQUEST_ENABLE_BT"
            else -> "Code $requestCode"
        }
        val message = "OnActivityResult for $request : $result data=${data?.description()}"
        Timber.w(message)
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

}


