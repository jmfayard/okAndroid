package com.github.jmfayard.okandroid.screens.mvi

import android.content.Context
import android.widget.Button
import com.github.jmfayard.okandroid.R
import com.github.jmfayard.okandroid.app
import com.github.jmfayard.okandroid.screens.MagellanScreen
import com.github.jmfayard.okandroid.screens.MagellanView
import com.github.jmfayard.okandroid.screens.SomeView
import com.github.jmfayard.okandroid.toast
import io.reactivex.subjects.BehaviorSubject
import timber.log.Timber


class MviScreen(
        val provider: ArticlesProvider = app().articlesProvider
) : MagellanScreen<MviDisplay>() {

    override fun createView(context: Context)
            = MagellanView(context, MviDisplay.LAYOUT, SomeView::createMviDisplay)

    override val screenTitle: Int = R.string.mvi_title

    val articleClicks = BehaviorSubject.create<Int>()


    override fun onShow(context: Context) {
        display?.setupRecyclerView { toast("Hello $it") }
        val mvi_button_update: Button = activity.findViewById(R.id.mvi_button_update)
        val model = present(
                updateButtonClicks = mvi_button_update.clicks(),
                articleClicks = articleClicks,
                articlesProvider = provider
        )
        render(model)

    }

    fun render(model: MainViewModel) {
        with(model) {
            articles.render { display?.updateRecyclerViewData(it) }
            updateButtonIsEnabled.render { Timber.e("enable button: $it") }
            emptyViewIsVisible.render { display?.showEmptyView = it }
            progressIsVisible.render { display?.showProgressLarge = it }
            smallProgressIsVisible.render { display?.showProgressSmall = it }
            updateButtonText.render { display?.ctaLabel = app().ctx.getString(it) }
            startDetailActivitySignals.render {
                toast("Open details")
            }
        }
    }

}