package com.github.jmfayard.okandroid

import android.os.Bundle

import com.github.jmfayard.okandroid.screens.HomeScreen
import com.github.jmfayard.okandroid.screens.TagsScreen
import com.wealthfront.magellan.Navigator
import com.wealthfront.magellan.support.SingleActivity

class MainActivity : SingleActivity() {

    var rootScreen = "main"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        rootScreen = intent?.extras?.getString("screen") ?: "main"
        //FIXME handle shortcut intents
    }

    override fun createNavigator(): Navigator {
        return Navigator.withRoot(HomeScreen()).loggingEnabled(true).build()
    }

}
