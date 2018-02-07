package com.github.jmfayard.screens

import android.content.Context
import com.github.jmfayard.checkvist.CheckvistScreen
import com.github.jmfayard.okandroid.R
import com.github.jmfayard.screens.HomeId.Layout
import com.github.jmfayard.pri.PresentRenderInputScreen
import com.github.jmfayard.utils.See


@See(layout = R.layout.home_screen)
class HomeScreen : MagellanScreen<HomeDisplay>() {

    override fun createView(context: android.content.Context) =
            MagellanView(context, Layout.id, SomeView::createHomeDisplay)

    override val screenTitle: Int
        get() = R.string.app_name

    fun onItemClicked(item: HomeItem) {
        navigator.goTo(item.screen)
    }

    val ITEMS: List<ListItem> by lazy {
        listOf(
                HomeItem("CheckvistScreen", "Playground for checkvist", CheckvistScreen()),
                HomeItem("TagsScreen", "Try #android #features", TagsScreen()),
                HomeItem("present(render(userInput()))", "Reactive Presenter", PresentRenderInputScreen()),
                HomeItem("Room", "Google's component for sqlite", RoomScreen())
        )
    }

    override fun onShow(context: Context?) {
        display?.setupRecyclerView { onItemClicked(it as HomeItem) }
        display?.updateRecyclerViewData(ITEMS)
    }

}
