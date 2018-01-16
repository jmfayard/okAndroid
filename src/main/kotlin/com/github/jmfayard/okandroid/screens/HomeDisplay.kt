package com.github.jmfayard.okandroid.screens

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.github.jmfayard.okandroid.R
import net.idik.lib.slimadapter.SlimAdapter

interface HomeDisplay : RecyclerViewDisplay {
    companion object {
        val LAYOUT = R.layout.home_screen
    }
}

fun SomeView.createHomeDisplay() = object : HomeDisplay {
    lateinit var slimAdapter: SlimAdapter

    override var loading: Boolean = false // by bindToVisibility(R.id.home_loading)

    override fun setupRecyclerView(onclick: (ListItem) -> Unit) {
        val recyclerView: RecyclerView = findViewById(R.id.recycler)
        slimAdapter = SlimAdapter.create()
                .register<HomeItem>(R.layout.home_item_card) { data: HomeItem, injector ->
                    injector.text(R.id.home_item_title, data.title)
                            .text(R.id.home_item_description, data.description)
                            .clicked(R.id.card_view, { _ -> onclick(data) })
                }
                .register<HeaderItem>(R.layout.home_item_section) { item: HeaderItem, injector ->
                    injector.text(R.id.home_item_title, item.title)
                }
                .attachTo(recyclerView)
        recyclerView.adapter = slimAdapter
        recyclerView.layoutManager = LinearLayoutManager(context)
        //  slimAdapter.updateData(Item.Loading)

    }

    override fun recyclerViewData(): List<ListItem>? {
        return slimAdapter.data as? List<ListItem>
    }

    override fun updateRecyclerViewData(items: List<ListItem>) {
        slimAdapter.updateData(items)
    }

}