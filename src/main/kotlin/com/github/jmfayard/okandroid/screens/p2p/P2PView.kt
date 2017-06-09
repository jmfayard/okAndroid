package com.github.jmfayard.okandroid.screens.p2p

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import com.github.jmfayard.okandroid.*
import com.github.jmfayard.okandroid.databinding.TagsScreenBinding
import com.github.jmfayard.okandroid.screens.TagsScreen
import com.github.jmfayard.okandroid.utils.Intents
import com.github.jmfayard.okandroid.utils.PatternEditableBuilder
import com.marcinmoskala.kotlinandroidviewbindings.bindToText
import com.wealthfront.magellan.BaseScreenView
import java.util.regex.Pattern

class P2PView(context: Context) : BaseScreenView<P2PScreen>(context) {


    val binding: TagsScreenBinding = TagsScreenBinding.inflate(inflater, this, attach)
    var htmlContent by binding.htmlContent.bindToText()
    var history by binding.actionResult.bindToText()

    fun setupTags() {
        PatternEditableBuilder()
                .addPattern(Pattern.compile("\\@(\\w+)"))
                .addPattern(Pattern.compile("#(\\w+)"), android.graphics.Color.BLUE) { hashtag ->
                    screen.clickedOn(hashtag)
                }
                .into(binding.htmlContent)
    }

}
