package com.github.jmfayard.screens

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.jmfayard.okandroid.R
import com.github.jmfayard.screens.HomeId.*
import com.github.jmfayard.utils.See
import com.wealthfront.magellan.Screen
import net.idik.lib.slimadapter.SlimAdapter

interface ListItem

@See(layout = R.layout.home_item_card)
data class HomeItem(val title: String, val description: String, val screen: Screen<*>) : ListItem

@See(layout = R.layout.home_item_section)
data class HeaderItem(val title: String) : ListItem



enum class HomeId(override val id: Int) : HasId {
    Recycler(R.id.recycler), // android.support.v7.widget.RecyclerView
    Layout(R.layout.home_screen), // Layout home_screen.xml
    CardView(R.id.card_view), // android.support.v7.widget.CardView
    HomeItemTitle(R.id.home_item_title), // TextView
    HomeItemDescription(R.id.home_item_description), // TextView
    LayoutItem(R.layout.home_item_card), // Layout home_item_card.xml
    LayoutSection(R.layout.home_item_section)
}


interface HomeDisplay : RecyclerViewDisplay

fun SomeView.createHomeDisplay() = object : HomeDisplay {
    lateinit var slimAdapter: SlimAdapter

    override var loading: Boolean = false

    override fun setupRecyclerView(onclick: (ListItem) -> Unit) {
        val recyclerView: RecyclerView = findViewById(Recycler.id)
        slimAdapter = SlimAdapter.create()
                .register<HomeItem>(LayoutItem.id) { data: HomeItem, injector ->
                    injector.text(HomeItemTitle.id, data.title)
                            .text(HomeItemDescription.id, data.description)
                            .clicked(CardView.id, { _ -> onclick(data) })
                }
                .register<HeaderItem>(LayoutSection.id) { item: HeaderItem, injector ->
                    injector.text(HomeItemTitle.id, item.title)
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