package com.github.jmfayard.okandroid.screens

import android.content.Context
import android.view.LayoutInflater
import com.github.jmfayard.okandroid.R
import com.github.jmfayard.okandroid.inflater
import com.github.jmfayard.okandroid.utils.See
import com.wealthfront.magellan.BaseScreenView
import com.wealthfront.magellan.Screen

@See(layout = R.layout.detail)
class DetailScreen : Screen<DetailView>() {

    override fun createView(context: Context): DetailView {
        return DetailView(context)
    }

    override fun getTitle(context: Context?): String {
        return "Detail Screen"
    }

}

class DetailView internal constructor(context: Context) : BaseScreenView<DetailScreen>(context) {

    init {
        inflater.inflate(R.layout.detail, this, true)
        //DetailBinding.inflate(LayoutInflater.from(context), this, true);
    }

}