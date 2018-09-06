package com.github.jmfayard.screens

import androidx.annotation.LayoutRes
import com.github.jmfayard.okandroid.R
import com.marcinmoskala.kotlinandroidviewbindings.bindToText


interface TagsDisplay : IDisplay {
    var tagsContent: String
    var history: String

    companion object {
        @LayoutRes
        const val LAYOUT = R.layout.tags_screen
    }
}

fun SomeView.createTagsDisplay() = object : TagsDisplay {
    override var tagsContent by bindToText(R.id.tags_content)
    override var history by bindToText(R.id.tags_history)
}






