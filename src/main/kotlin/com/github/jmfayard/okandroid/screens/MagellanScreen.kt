package com.github.jmfayard.okandroid.screens

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.support.annotation.IdRes
import android.support.annotation.LayoutRes
import android.support.annotation.VisibleForTesting
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import com.wealthfront.magellan.BaseScreenView
import com.wealthfront.magellan.Navigator
import com.wealthfront.magellan.Screen


/** A *Display* is a set of properties that provides via KotlinAndroidViewBindings
 * a simple API for the various TextView, EditText, Buttons...
 *
 * It's the generic paramter of [MagellanScreen] and [MagellanView]
 *
 * This interface can be implemented by a data class for the unit tests
 *
 * See [README.md](https://github.com/jmfayard/android-kotlin-magellan)
 * **/
interface IDisplay

/** Used to define extension methods SomeScreen.() -> Unit */
typealias SomeScreen = com.wealthfront.magellan.Screen<*>

/** Used to define extension methods SomeView.() -> Unit */
typealias SomeView = com.wealthfront.magellan.BaseScreenView<*>

/** <S : Screen<*>> **/
val <S : SomeView> Screen<S>.display: S?
    get() = view


/** Base class for all our screens. Used a [MagellanView] as its view. **/
abstract class MagellanScreen<Display : IDisplay> : Screen<MagellanView<Display>>() {

    val display: Display? get() = testDisplay ?: view?.display

    private var testDisplay: Display? = null

    abstract val screenTitle: Int

    override final fun getTitle(context: Context): String {
        return context.getString(screenTitle)
    }

    /*** Used in unit tests. Calls onShow(). [display] should be a data class implementing Display. [navigator] and [activity] are mocked objects ***/
    @VisibleForTesting
    fun setupForTests(display: Display, navigator: Navigator, activity: Activity): Display {
        this.navigator = navigator
        this.activity = activity
        this.testDisplay = display
        onShow(activity)
        return display
    }

    @VisibleForTesting
    var keyboardShownProgrammatically = false

    fun hideSoftKeyboard(@IdRes editText: Int) {
        keyboardShownProgrammatically = true
        val widget = view?.findViewById<EditText>(editText) ?: return
        val context = view?.context ?: return
        view.context.hideSoftKeyboard(widget)
    }

    fun showSoftKeyboard() {
        keyboardShownProgrammatically = true
        view?.context?.showSoftKeyboard(view.rootView)
    }
}


/*** All [MagellanScreen] use this class as view. No subclassing required **/
@SuppressLint("ViewConstructor")
class MagellanView<Display : IDisplay>(context: Context, @LayoutRes val layout: Int, setup: MagellanView<Display>.() -> Display)
    : BaseScreenView<MagellanScreen<Display>>(context) {
    val display: Display

    init {
        inflate(context, layout, this)
        display = setup()
    }

}


/** Buttons onClickListener are abstracted by a lambda ()->Unit. This gives a name to this type**/
typealias ViewCallback = () -> Unit

/** An ViewCallback that does nothing **/
val NOOP: ViewCallback = {}


fun Context.hideSoftKeyboard(view: View?) {
    if (view != null && view.requestFocus()) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.rootView.windowToken, 0) // InputMethodManager.SHOW_IMPLICIT
    }
}

fun Context.showSoftKeyboard(view: View?) {
    if (view != null && view.requestFocus()) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(view, 0) //InputMethodManager.SHOW_IMPLICIT)
    }
}
