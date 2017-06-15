package com.github.jmfayard.okandroid

import android.app.Activity
import android.content.BroadcastReceiver
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
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NdefRecord
import android.nfc.NfcAdapter
import android.support.v4.content.LocalBroadcastManager
import android.widget.Toast
import com.github.jmfayard.okandroid.MainActivity.Companion.REQUEST_ENABLE_BT
import com.github.jmfayard.okandroid.screens.TagsScreen
import com.github.jmfayard.okandroid.screens.p2p.DATA_EXCHANGE
import com.github.jmfayard.okandroid.screens.p2p.P2PScreen
import com.github.jmfayard.okandroid.screens.p2p.handoverAdapter
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
        listenForDataExchange()
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(applicationContext).unregisterReceiver(receiver)
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == DATA_EXCHANGE) {
                val message = intent.getStringExtra("message")
                Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun listenForDataExchange() {
        LocalBroadcastManager.getInstance(applicationContext).registerReceiver(
                receiver, IntentFilter(DATA_EXCHANGE))
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


    public override fun onNewIntent(intent: Intent) {
        // onResume gets called after this to handle the intent
        setIntent(intent)
    }

    public override fun onResume() {
        super.onResume()
        // Check to see that the Activity started due to an Android Beam
        when {
            intent == null -> return
            intent.hasCategory(Intent.CATEGORY_LAUNCHER) -> return
            intent.action == NfcAdapter.ACTION_NDEF_DISCOVERED -> handleNfcHandover()
            else -> Toast.makeText(this, intent.description(), Toast.LENGTH_LONG).show()
        }
    }



    /**
     * Parses the NDEF Message from the intent and prints to the TextView
     */
    fun handleNfcHandover() {
        val records: List<NdefRecord> = intent?.ndefRecords() ?: emptyList()
        if (records.isEmpty()) return

        val handoverData = try {
            val text = String(records.first().payload)
             handoverAdapter.fromJson(text.substring(3))
        } catch(e: Exception) {
            Toast.makeText(this, intent.description(), Toast.LENGTH_LONG).show()
            return
        }
        val screen: P2PScreen
        if (getNavigator().currentScreen() is P2PScreen) {
            screen = getNavigator().currentScreen() as P2PScreen
        } else {
            screen = P2PScreen.from(this)
            getNavigator().showNow(screen)
        }
        screen.startLookingFor(handoverData)

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


