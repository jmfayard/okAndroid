package com.github.jmfayard.screens

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import com.github.jmfayard.okandroid.R
import com.github.jmfayard.okandroid.inflateViewFrom
import com.github.jmfayard.utils.PatternEditableBuilder
import com.github.jmfayard.room.Person
import com.marcinmoskala.kotlinandroidviewbindings.bindToText
import com.wealthfront.magellan.BaseScreenView
import net.idik.lib.slimadapter.SlimAdapter
import java.util.regex.Pattern.compile

class RoomView(context: Context) : BaseScreenView<RoomScreen>(context) {
    init {
        inflateViewFrom(R.layout.room_screen)
    }

//    val binding: RoomScreenBinding = RoomScreenBinding.inflate(inflater, this, attach)
    var htmlContent by bindToText(R.id.rom_content)

    fun init() {
        val recycler = findViewById<RecyclerView>(R.id.room_recycler)
        val content = findViewById<TextView>(R.id.rom_content)
        htmlContent = screen.text
        setupTags(content)
        with(recycler) {
            adapter = slimAdapter.attachTo(recycler)
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
    }

    fun setupTags(textView: TextView) {
        PatternEditableBuilder()
                .addPattern(compile("\\@(\\w+)"))
                .addPattern(compile("#(\\w+)"), android.graphics.Color.BLUE) { hashtag ->
                    screen.clickedOn(hashtag)
                }
                .into(textView)
    }


}