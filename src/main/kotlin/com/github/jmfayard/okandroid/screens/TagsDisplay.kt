package com.github.jmfayard.okandroid.screens

import android.support.annotation.LayoutRes
import com.github.jmfayard.okandroid.R
import com.marcinmoskala.kotlinandroidviewbindings.bindToText


interface TagsDisplay : IDisplay {
    var htmlContent: String
    var history: String

    companion object {
        @LayoutRes const val LAYOUT = R.layout.tags_screen
    }
}

fun SomeView.createTagsDisplay() = object : TagsDisplay {
    override var htmlContent by bindToText(R.id.htmlContent)
    override var history by bindToText(R.id.actionResult)
}






