package com.github.jmfayard.okandroid.screens.mvi

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.github.jmfayard.okandroid.R
import com.github.jmfayard.okandroid.screens.ListItem
import com.github.jmfayard.okandroid.screens.RecyclerViewDisplay
import com.github.jmfayard.okandroid.screens.SomeView
import com.github.jmfayard.okandroid.screens.ViewCallback
import com.marcinmoskala.kotlinandroidviewbindings.bindToClick
import com.marcinmoskala.kotlinandroidviewbindings.bindToText
import com.marcinmoskala.kotlinandroidviewbindings.bindToVisibility
import net.idik.lib.slimadapter.SlimAdapter

interface MviDisplay : RecyclerViewDisplay {
    var showEmptyView : Boolean
    var showProgressLarge: Boolean
    var showProgressSmall : Boolean
    var onUpdateList: ViewCallback
    var ctaLabel: String
    var ctaEnabled: Boolean

    companion object {
        val LAYOUT = R.layout.mvi_screen
    }
}

fun SomeView.createMviDisplay() = object : MviDisplay {
    override var loading: Boolean = true

    lateinit var slimAdapter: SlimAdapter

    override fun setupRecyclerView(onclick: (ListItem) -> Unit) {
        val recyclerView: RecyclerView = findViewById(R.id.mvi_list_titles)
        slimAdapter = SlimAdapter.create()
                .register<Article>(R.layout.mvi_item) { item: Article, injector ->
                    injector.text(R.id.mvi_item_title, item.title)
                    injector.clicked(R.id.mvi_item_title) {
                        onclick(item)
                    }
                }
                .attachTo(recyclerView)
        recyclerView.adapter = slimAdapter
        recyclerView.layoutManager = LinearLayoutManager(context)
    }

    override fun recyclerViewData(): List<ListItem>? {
        return slimAdapter.data as? List<ListItem>
    }

    override fun updateRecyclerViewData(items: List<ListItem>) {
        slimAdapter.updateData(items)
    }

    override var showEmptyView: Boolean by bindToVisibility(R.id.mvi_empty_view)

    override var showProgressLarge: Boolean by bindToVisibility(R.id.mvi_progress_large)
    override var showProgressSmall: Boolean by bindToVisibility(R.id.mvi_progress_small)

    override var onUpdateList: ViewCallback by bindToClick(R.id.mvi_button_update)

    override var ctaLabel: String by bindToText(R.id.mvi_button_update)
    override var ctaEnabled: Boolean = true
}