package com.github.jmfayard.okandroid.screens

import android.content.Context
import com.github.jmfayard.okandroid.R
import com.github.jmfayard.okandroid.inflateViewFrom
import com.github.jmfayard.okandroid.utils.See
import com.wealthfront.magellan.BaseScreenView
import com.wealthfront.magellan.Screen

@See(layout = R.layout.airbnb_login_screen)
class AirbnbScreen : Screen<AirbnbView>() {
    override fun createView(context: Context) = AirbnbView(context)

    override fun getTitle(context: Context): String = context.getString(R.string.airbnb_title)
}


class AirbnbView(context: Context) : BaseScreenView<AirbnbScreen>(context) {

    init {
        inflateViewFrom(R.layout.airbnb_login_screen)
    }

}