package com.github.jmfayard.okandroid

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.MenuItem

import com.github.jmfayard.okandroid.screens.HomeScreen
import com.github.jmfayard.okandroid.screens.TagsScreen
import com.github.jmfayard.okandroid.utils.See
import com.wealthfront.magellan.Navigator
import com.wealthfront.magellan.Screen
import com.wealthfront.magellan.ScreenLifecycleListener
import com.wealthfront.magellan.support.SingleActivity
import com.wealthfront.magellan.transitions.CircularRevealTransition
import com.wealthfront.magellan.transitions.CrossfadeTransition
import com.wealthfront.magellan.transitions.DefaultTransition

@See(layout = R.layout.main_activity)
class MainActivity : SingleActivity() {

    var rootScreen = "main"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        rootScreen = intent?.extras?.getString("screen") ?: "main"

        //FIXME handle shortcut intents
    }


    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId) {
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

    val LifeCycle  = object : ScreenLifecycleListener {
        override fun onShow(screen: Screen<*>) {
            val title = screen.getTitle(applicationContext)
            Log.i("Navigator", "onShow(screen = '$title')")

            with (supportActionBar!!) {
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
