package com.github.jmfayard.okandroid.screens

import android.content.Context
import android.view.LayoutInflater
import com.wealthfront.magellan.BaseScreenView
import com.wealthfront.magellan.Screen

class AirbnbScreen : Screen<AirbnbView>() {
    override fun createView(context: Context) = AirbnbView(context)

    override fun getTitle(context: Context?): String = "Airbnb Login"
}


class AirbnbView(context: Context) : com.wealthfront.magellan.BaseScreenView<AirbnbScreen>(context) {

    init {
        android.view.LayoutInflater.from(context).inflate(com.github.jmfayard.okandroid.R.layout.airbnb_login, this, true)
    }

}