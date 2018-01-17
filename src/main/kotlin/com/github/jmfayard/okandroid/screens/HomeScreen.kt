package com.github.jmfayard.okandroid.screens

import android.content.Context
import com.github.jmfayard.okandroid.R
import com.github.jmfayard.okandroid.room.RoomScreen
import com.github.jmfayard.okandroid.screens.HomeId.Layout
import com.github.jmfayard.okandroid.screens.mvi.PresentRenderInputScreen
import com.github.jmfayard.okandroid.utils.See


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
                HeaderItem("Current Stuff"),
                HomeItem("TagsScreen", "Try #android #features", TagsScreen()),
                HomeItem("present(render(userInput()))", "Reactive Presenter", PresentRenderInputScreen()),
                HomeItem("Room", "Google's component for sqlite", RoomScreen()),
                HomeItem("RegisterScreen", "Registration by SMS flow", RegisterScreen()),
                HeaderItem("Old Stuff"),
                HomeItem("RxScreen", "RxJava / RxBinding", RxPlaygroundScreen())
        )
    }

    override fun onShow(context: Context?) {
        display?.setupRecyclerView { onItemClicked(it as HomeItem) }
        display?.updateRecyclerViewData(ITEMS)
    }

}
