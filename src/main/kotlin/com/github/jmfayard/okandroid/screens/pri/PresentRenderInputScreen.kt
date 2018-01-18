package com.github.jmfayard.okandroid.screens.pri

import android.Manifest
import android.content.Context
import com.github.jmfayard.okandroid.app
import com.github.jmfayard.okandroid.screens.*
import com.github.jmfayard.okandroid.screens.pri.IdFrp.*
import com.github.jmfayard.okandroid.screens.pri.MviDialog.*
import com.github.jmfayard.okandroid.toast
import com.tbruyelle.rxpermissions2.Permission
import io.reactivex.subjects.BehaviorSubject
import timber.log.Timber


class PresentRenderInputScreen(
        val provider: ArticlesProvider = app().articlesProvider
) : ReactiveScreen<PresentRenderInputView>() {

    override fun screenTitle(): Int = PriTitle.id

    override fun createView(context: Context): PresentRenderInputView = PresentRenderInputView(context)

    val articleClicks = BehaviorSubject.create<Article>()

    val dialogResults = BehaviorSubject.create<DialogResult>()

    override fun onRender(context: Context) {
        display?.setupRecyclerView {
            articleClicks.onNext(it)
        }
        val model: MainViewModel = present(
                updateButtonClicks = clicks(PriButtonUpdate),
                prefsButtonClicks = clicks(PriButtonPrefs),
                articleClicks = articleClicks,
                articlesProvider = provider,
                permissionProvider = permissionProvider(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_CONTACTS),
                dialogResults = dialogResults
        )
        renderModel(model.toDebugModel())


    }


    fun renderModel(model: MainViewModel) {

        with(model) {
            articles.render { display?.updateRecyclerViewData(it) }
            updateButtonIsEnabled.render { button(PriButtonUpdate)?.isEnabled = it }
            emptyViewIsVisible.render { v(PriEmptyView)?.visible = it }
            progressIsVisible.render { v(PriProgressLarge)?.visible = it }
            smallProgressIsVisible.render { v(PriProgressSmall)?.visible = it }
            updateButtonText.render { text(PriButtonUpdate)?.text = app().ctx.getString(it) }
            startDetailActivitySignals.render { toast("You clicked on $it") }
            permissionSignal.render { p: Permission ->
                Timber.i("permissionSignal: $p")
                if (!p.granted) toast("We need permission ${p.name} to continue")
            }
            dialogCmds.render {
                createDialog(it)
            }
            preferences.render { p: MviPrefs ->
                view?.updateColor(p)
                Timber.w("Updated prefs: $p")
            }
            Unit
        }
    }

    fun createDialog(dialog: MviDialog) {
        val title = when (dialog) {
            PrefsMain -> "Update Preferences"
            PrefsFontColor -> "Font Color"
            PrefsBackgroundColor -> "Background Color"
        }
        val choices = when (dialog) {
            PrefsMain -> listOf("font", "background", "reset")
            PrefsFontColor -> listOf("black", "blue", "red")
            PrefsBackgroundColor -> listOf("white", "grey", "yellow")
        }
        view?.showDialog(dialog, title, choices, dialogResults)

    }


}
