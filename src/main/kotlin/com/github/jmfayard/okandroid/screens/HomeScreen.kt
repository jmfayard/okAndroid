package com.github.jmfayard.okandroid.screens

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.github.jmfayard.okandroid.R
import com.github.jmfayard.okandroid.databinding.ActivityStartBinding
import com.github.jmfayard.okandroid.databinding.ActivityStartBinding.inflate
import com.github.jmfayard.okandroid.databinding.ItemHomeBinding
import com.github.jmfayard.okandroid.inflater
import com.github.jmfayard.okandroid.toast
import com.github.jmfayard.okandroid.utils.See
import com.wealthfront.magellan.Screen

@See(layout = R.layout.item_home)
class HomeScreen : com.wealthfront.magellan.Screen<HomeView>() {

    override fun createView(context: android.content.Context): HomeView {
        return HomeView(context)
    }

    override fun getTitle(context: android.content.Context?): String {
        return "Ok Android"
    }

    fun onItemClicked(item: HomeItem) {
        navigator.goTo(item.screen)
    }
}

val ITEMS = listOf(
        HomeItem("TagsScreen", "Try #android #features", TagsScreen()),
        HomeItem("RegisterScreen", "Registration by SMS flow", RegisterScreen()),
        HomeItem("RxScreen", "RxJava / RxBinding", RxScreen())
)

data class HomeItem(val title: String, val description: String, val screen: Screen<*>)

class HomeAdapter(val context: Context, val items: List<HomeItem>, val onClick: (HomeItem)->Unit) : RecyclerView.Adapter<HomeHolder>() {

    val inflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeHolder {
        val binding : ItemHomeBinding = ItemHomeBinding.inflate(inflater, parent, false)
        return HomeHolder(binding)
    }

    override fun onBindViewHolder(holder: HomeHolder, position: Int) {
        holder.bind(items[position], onClick)
    }

    override fun getItemCount(): Int = items.size
}

class HomeHolder(val binding: ItemHomeBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(item: HomeItem, onClick: (HomeItem) -> Unit) {
        binding.homeItemTitle.text = item.title
        binding.homeItemDescription.text = item.description
        binding.cardView.setOnClickListener { onClick(item) }
        binding.executePendingBindings()
    }

}



class HomeView (context: android.content.Context) : com.wealthfront.magellan.BaseScreenView<HomeScreen>(context) {

    val binding : ActivityStartBinding = inflate(inflater, this, true)

    init {

        with (binding.recycler) {
            layoutManager = LinearLayoutManager(context)
            adapter = HomeAdapter(context, ITEMS) { item  : HomeItem ->
                toast("Clicked on ${item}")
                screen.onItemClicked(item)
            }
        }
    }

}