package com.github.jmfayard.okandroid.screens

import android.content.Context
import android.view.LayoutInflater
import com.github.jmfayard.okandroid.R
import com.github.jmfayard.okandroid.attach
import com.github.jmfayard.okandroid.inflater
import com.github.jmfayard.okandroid.utils.See
import com.wealthfront.magellan.BaseScreenView
import com.wealthfront.magellan.Screen

@See(layout = R.layout.airbnb_login)
class AirbnbScreen : Screen<AirbnbView>() {
    override fun createView(context: Context) = AirbnbView(context)

    override fun getTitle(context: Context?): String = "Airbnb Login"
}


class AirbnbView(context: Context) : com.wealthfront.magellan.BaseScreenView<AirbnbScreen>(context) {

    init {
        inflater.inflate(R.layout.airbnb_login, this, attach)
    }

}