package com.github.jmfayard.okandroid

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.github.jmfayard.screens.HomeScreen
import com.github.jmfayard.screens.TagsScreen
import com.github.jmfayard.utils.See
import com.wealthfront.magellan.Navigator
import com.wealthfront.magellan.Screen
import com.wealthfront.magellan.ScreenLifecycleListener
import com.wealthfront.magellan.support.SingleActivity
import com.wealthfront.magellan.transitions.DefaultTransition
import timber.log.Timber


@See(layout = R.layout.all_activity)
class MainActivity : SingleActivity() {

    companion object {
        val REQUEST_ENABLE_BT = 101
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (this as AppCompatActivity).setContentView(R.layout.all_activity)
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
            else -> Toast.makeText(this, intent.description(), Toast.LENGTH_LONG).show()
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val result = when (resultCode) {
            Activity.RESULT_CANCELED -> "RESULT_CANCELED"
            Activity.RESULT_OK -> "RESULT_OK"
            else -> "ResultCode:$resultCode"
        }
        val request = when (requestCode) {
            REQUEST_ENABLE_BT -> "REQUEST_ENABLE_BT"
            else -> "Code $requestCode"
        }
        val message = "OnActivityResult for $request : $result data=${data?.description()}"
        Timber.w(message)
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

}


