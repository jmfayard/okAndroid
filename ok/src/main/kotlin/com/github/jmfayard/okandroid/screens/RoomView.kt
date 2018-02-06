package com.github.jmfayard.okandroid.screens

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import com.github.jmfayard.okandroid.R
import com.github.jmfayard.okandroid.attach
import com.github.jmfayard.okandroid.databinding.RoomScreenBinding
import com.github.jmfayard.okandroid.inflater
import com.github.jmfayard.okandroid.utils.PatternEditableBuilder
import com.github.jmfayard.room.Person
import com.marcinmoskala.kotlinandroidviewbindings.bindToText
import com.wealthfront.magellan.BaseScreenView
import net.idik.lib.slimadapter.SlimAdapter
import java.util.regex.Pattern.compile

class RoomView(context: Context) : BaseScreenView<RoomScreen>(context) {


    val binding: RoomScreenBinding = RoomScreenBinding.inflate(inflater, this, attach)
    var htmlContent by binding.htmlContent.bindToText()

    fun init() {
        htmlContent = screen.text
        setupTags()
        with(binding.recycler) {
            adapter = slimAdapter
            layoutManager = LinearLayoutManager(context)
        }
        slimAdapter.updateData(listOf(HeaderItem("No items yet")))

    }

    val slimAdapter by lazy {
        SlimAdapter.create()
                .register<Person>(R.layout.room_item) { data: Person, injector ->
                    injector.text(R.id.room_item_description, data.uid.toString())
                            .text(R.id.room_item_title, "${data.firstName} - ${data.lastName}")
                            .clicked(R.id.card_view, { _ -> screen.onItemClicked(data) })
                }
                .register<HeaderItem>(R.layout.home_item_section) { item: HeaderItem, injector ->
                    injector.text(R.id.home_item_title, item.title)
                }
                .attachTo(binding.recycler)
    }

    fun setupTags() {
        PatternEditableBuilder()
                .addPattern(compile("\\@(\\w+)"))
                .addPattern(compile("#(\\w+)"), android.graphics.Color.BLUE) { hashtag ->
                    screen.clickedOn(hashtag)
                }
                .into(binding.htmlContent)
    }


}