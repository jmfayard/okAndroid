package com.github.jmfayard.okandroid.screens

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.github.jmfayard.okandroid.R
import com.github.jmfayard.okandroid.inflateViewFrom
import com.github.jmfayard.okandroid.utils.See
import com.wealthfront.magellan.BaseScreenView
import com.wealthfront.magellan.Screen
import kotterknife.bindView
import net.idik.lib.slimadapter.SlimAdapter

interface ListItem

@See(layout = R.layout.home_item_card)
data class HomeItem(val title: String, val description: String, val screen: Screen<*>) : ListItem

@See(layout = R.layout.home_item_section)
data class HeaderItem(val title: String) : ListItem


@See(layout = R.layout.home_screen)
class HomeScreen : com.wealthfront.magellan.Screen<HomeView>() {

    override fun createView(context: android.content.Context) = HomeView(context)

    override fun getTitle(context: android.content.Context?) = "Ok Android"

    fun onItemClicked(item: HomeItem) {
        navigator.goTo(item.screen)
    }

    val ITEMS: List<ListItem> = listOf(
            HeaderItem("Current Stuff"),
            HomeItem("TagsScreen", "Try #android #features", TagsScreen()),
            HomeItem("RegisterScreen", "Registration by SMS flow", RegisterScreen()),
            HeaderItem("Old Stuff"),
            HomeItem("RxScreen", "RxJava / RxBinding", RxScreen())
    )

    override fun onShow(context: Context?) {
        view.slimAdapter.updateData(ITEMS)
    }

}


class HomeView(context: android.content.Context) : BaseScreenView<HomeScreen>(context) {

    val list: RecyclerView by bindView(R.id.recycler)

    val slimAdapter by lazy {
        SlimAdapter.create()
                .register<HomeItem>(R.layout.home_item_card) { data: HomeItem, injector ->
                    injector.text(R.id.home_item_title, data.title)
                            .text(R.id.home_item_description, data.description)
                            .clicked(R.id.card_view, { _ -> screen.onItemClicked(data) })
                }
                .register<HeaderItem>(R.layout.home_item_section) { item: HeaderItem, injector ->
                    injector.text(R.id.home_item_title, item.title)
                }
                .attachTo(list)
    }

    init {
        inflateViewFrom(R.layout.home_screen)
        with(list) {
            layoutManager = LinearLayoutManager(context)
            adapter = slimAdapter
        }
    }

}