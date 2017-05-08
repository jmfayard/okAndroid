package com.github.jmfayard.okandroid.screens

import android.content.Context
import android.view.LayoutInflater
import com.wealthfront.magellan.BaseScreenView
import com.wealthfront.magellan.Screen

class DetailScreen : com.wealthfront.magellan.Screen<DetailView>() {

    override fun createView(context: android.content.Context): DetailView {
        return DetailView(context)
    }

    override fun getTitle(context: android.content.Context?): String {
        return "Detail Screen"
    }

}

class DetailView internal constructor(context: android.content.Context) : com.wealthfront.magellan.BaseScreenView<DetailScreen>(context) {

    init {
        android.view.LayoutInflater.from(context).inflate(com.github.jmfayard.okandroid.R.layout.detail, this, true)
        //DetailBinding.inflate(LayoutInflater.from(context), this, true);
    }

}