package com.github.jmfayard.okandroid.screens


interface RecyclerViewDisplay : IDisplay {
    var loading: Boolean
    fun setupRecyclerView(onclick: (ListItem) -> Unit)
    fun recyclerViewData(): List<ListItem>?
    fun updateRecyclerViewData(items: List<ListItem>)
}

data class TestRecyclerViewDisplay(
        override var loading: Boolean = false,
        var data: List<ListItem> = emptyList())
    : RecyclerViewDisplay {
    var isSetup: Boolean = false

    override fun setupRecyclerView(onclick: (ListItem) -> Unit) {
        isSetup = true
    }

    override fun recyclerViewData(): List<ListItem> = data

    override fun updateRecyclerViewData(items: List<ListItem>) {
        data = items
    }
}