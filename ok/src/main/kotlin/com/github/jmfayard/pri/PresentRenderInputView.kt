package com.github.jmfayard.pri

import android.content.Context
import android.graphics.Color
import android.widget.TextView
import androidx.core.view.forEach
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.github.jmfayard.okandroid.R
import com.github.jmfayard.okandroid.inflateViewFrom
import com.github.jmfayard.screens.*
import com.wealthfront.magellan.BaseScreenView
import io.reactivex.subjects.BehaviorSubject
import net.idik.lib.slimadapter.SlimAdapter
import timber.log.Timber


// Can be generated by $ androidstrings.kt layout {FILE}
// https://github.com/jmfayard/skripts/blob/44fb949b86288bed17a6a4b4eba9db656e6a09dc/kotlin/androidstrings.kt
enum class IdFrp(override val id: Int) : HasId {
    PriLayoutAll(R.id.pri_layout_all), // RelativeLayout
    PriLayoutPrefs(R.id.pri_layout_prefs), // LinearLayout
    PriEmptyView(R.id.pri_empty_view), // TextView
    PriListTitles(R.id.pri_list_titles), // android.support.v7.widget.RecyclerView
    PriProgressLarge(R.id.pri_progress_large), // ProgressBar
    PriButtonUpdate(R.id.pri_button_update), // Button
    PriProgressSmall(R.id.pri_progress_small), // ProgressBar
    PriLabelPrefs(R.id.pri_label_prefs), // TextView
    PriButtonPrefs(R.id.pri_button_prefs), // Button
    PriLayout(R.layout.pri_screen),
    PriTitle(R.string.pri_title),
    PriItemLayout(R.layout.pri_item),
    PriItemTitle(R.id.pri_item_title),
    PriItemCardView(R.id.pri_item_cardview)
}


class PresentRenderInputView(context: Context) : BaseScreenView<PresentRenderInputScreen>(context) {

    val slimAdapter: SlimAdapter = SlimAdapter.create()

    init {
        inflateViewFrom(IdFrp.PriLayout.id)
    }

    fun setupRecyclerView(onclick: (Article) -> Unit) {
        val recyclerView: RecyclerView = findViewById(IdFrp.PriListTitles.id)
        slimAdapter.register<Article>(IdFrp.PriItemLayout.id) { item: Article, injector ->
            injector.text(IdFrp.PriItemTitle.id, item.title)
            injector.clicked(IdFrp.PriItemCardView.id) {
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

    fun updateColor(p: MviPrefs) {
        view(IdFrp.PriLayoutAll)?.setBackgroundColor(color(p.backgroundColor))
        viewGroup(IdFrp.PriLayoutAll)?.forEach {  }
        view(IdFrp.PriLayoutAll)?.children()?.filterIsInstance(TextView::class.java)?.forEach {
            it.setTextColor(color(p.fontColor))
        }
        text(IdFrp.PriLabelPrefs)?.text = "Prefs: color=${p.fontColor} highlight=${p.backgroundColor}"
    }

    fun color(name: String): Int = when (name) {
        "black" -> Color.BLACK
        "blue" -> Color.BLUE
        "red" -> Color.RED
        "white" -> Color.WHITE
        "grey" -> Color.GRAY
        "yellow" -> Color.YELLOW
        else -> {
            Timber.e("Un-expected color $name ")
            Color.CYAN
        }
    }

    fun showDialog(dialog: MviDialog, title: String, choices: List<String>, dialogResults: BehaviorSubject<DialogResult>) {
        MaterialDialog.Builder(context)
                .title(title)
                .items(choices)
                .cancelListener {
                    val result = DialogResult.DialogCancel(dialog)
                    dialogResults.onNext(result)
                }
                .itemsCallback(MaterialDialog.ListCallback { _, _, position, text ->
                    val result = DialogResult.DialogOk(dialog, text.toString())
                    dialogResults.onNext(result)
                })
                .show()
    }

}