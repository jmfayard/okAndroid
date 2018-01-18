package com.github.jmfayard.okandroid.screens.mvi

import android.Manifest
import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.github.jmfayard.okandroid.R
import com.github.jmfayard.okandroid.app
import com.github.jmfayard.okandroid.inflateViewFrom
import com.github.jmfayard.okandroid.screens.*
import com.github.jmfayard.okandroid.screens.mvi.IdFrp.*
import com.github.jmfayard.okandroid.toast
import com.tbruyelle.rxpermissions2.Permission
import com.wealthfront.magellan.BaseScreenView
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import net.idik.lib.slimadapter.SlimAdapter
import timber.log.Timber


class PresentRenderInputScreen(
        val provider: ArticlesProvider = app().articlesProvider
) : ReactiveScreen<PresentRenderInputView>() {

    override fun screenTitle(): Int = MviTitle.id

    override fun createView(context: Context): PresentRenderInputView = PresentRenderInputView(context)

    val articleClicks = BehaviorSubject.create<Article>()


    override fun onRender(context: Context) {
        display?.setupRecyclerView {
            articleClicks.onNext(it)
        }
        val model: MainViewModel = present(
                updateButtonClicks = clicks(MviButtonUpdate),
                articleClicks = articleClicks,
                articlesProvider = provider,
                permissionProvider = permissionProvider(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_CONTACTS),
                prefsButtonClicks = Observable.never<Unit>(),
                dialogResults = Observable.never<DialogResult>()
        )
        renderModel(model.toDebugModel())


    }


    fun renderModel(model: MainViewModel) {

        with(model) {
            articles.render { display?.updateRecyclerViewData(it) }
            updateButtonIsEnabled.render { button(MviButtonUpdate)?.isEnabled = it }
            emptyViewIsVisible.render { v(MviEmptyView)?.visible = it }
            progressIsVisible.render { v(MviProgressLarge)?.visible = it }
            smallProgressIsVisible.render { v(MviProgressSmall)?.visible = it }
            updateButtonText.render { text(MviButtonUpdate)?.text = app().ctx.getString(it) }
            startDetailActivitySignals.render { toast("You clicked on $it") }
            permissionSignal.render { p : Permission ->
                Timber.i("permissionSignal: $p")
                if (!p.granted) toast("We need permission ${p.name} to continue")
            }
            dialogCmds.render { Timber.e("Dialogs: $it") }
            preferences.render { Timber.e("Preferences: $it") }
            Unit
        }
    }



}



// Can be generated by $ androidstrings.kt layout {FILE}
// https://github.com/jmfayard/skripts/blob/44fb949b86288bed17a6a4b4eba9db656e6a09dc/kotlin/androidstrings.kt
enum class IdFrp(override val id: Int) : HasId {
    Layout(R.layout.mvi_screen),
    MviTitle(R.string.mvi_title),
    MviItemLayout(R.layout.mvi_item),
    MviEmptyView(R.id.mvi_empty_view),
    MviProgressLarge(R.id.mvi_progress_large),
    MviProgressSmall(R.id.mvi_progress_small),
    MviButtonUpdate(R.id.mvi_button_update),
    MviListTitles(R.id.mvi_list_titles),
    MviItemTitle(R.id.mvi_item_title)
}


class PresentRenderInputView(context: Context) : BaseScreenView<PresentRenderInputScreen>(context) {

    val slimAdapter: SlimAdapter = SlimAdapter.create()

    init {
        inflateViewFrom(Layout.id)
    }

    fun setupRecyclerView(onclick: (Article) -> Unit) {
        val recyclerView: RecyclerView = findViewById(MviListTitles.id)
        slimAdapter.register<Article>(MviItemLayout.id) { item: Article, injector ->
            injector.text(MviItemTitle.id, item.title)
            injector.clicked(MviItemTitle.id) {
                onclick(item)
            }
        }.attachTo(recyclerView)
        recyclerView.adapter = slimAdapter
        recyclerView.layoutManager = LinearLayoutManager(context)
    }

    fun recyclerViewData(): List<ListItem>? {
        return slimAdapter.data as? List<ListItem>
    }

    fun updateRecyclerViewData(items: List<ListItem>) {
        slimAdapter.updateData(items)
    }

}


