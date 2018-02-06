package com.github.jmfayard.okandroid.screens

import android.content.Context
import com.github.jmfayard.okandroid.R
import com.github.jmfayard.okandroid.inflateViewFrom
import com.github.jmfayard.okandroid.utils.See
import com.wealthfront.magellan.BaseScreenView
import com.wealthfront.magellan.Screen

@See(layout = R.layout.detail_screen)
class DetailScreen : Screen<DetailView>() {

    override fun createView(context: Context): DetailView = DetailView(context)

    override fun getTitle(context: Context): String = context.getString(R.string.detail_title)

}

class DetailView (context: Context) : BaseScreenView<DetailScreen>(context) {

    init {
        inflateViewFrom(R.layout.detail_screen)
    }

}