package kotterknife

import android.app.Activity
import android.support.annotation.IdRes
import android.view.View
import android.widget.AdapterView
import android.widget.Spinner
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

fun Activity.bindToItemSelected(@IdRes editTextId: Int): ReadWriteProperty<Any?, SpinnerListener>
        = bindToItemSelected { findViewById(editTextId) }

fun Spinner.bindToItemSelected(): ReadWriteProperty<Any?, SpinnerListener>
        = bindToItemSelected { this }

private fun bindToItemSelected(viewProvider: () -> Spinner): ReadWriteProperty<Any?, SpinnerListener>
        = SpinnerBinding(lazy(viewProvider))

typealias SpinnerListener = (Int, Any) -> Unit

private class SpinnerBinding(lazyViewProvider: Lazy<Spinner>) : ReadWriteProperty<Any?, SpinnerListener> {

    val spinner by lazyViewProvider

    var function: SpinnerListener? = null

    override fun getValue(thisRef: Any?, property: KProperty<*>): SpinnerListener {
        return function ?: noop
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: SpinnerListener) {
        setUpListener()
        function = value
    }

    fun setUpListener() {
        if (function == null) {
            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {
                    //
                }

                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    function?.invoke(position, spinner.adapter.getItem(position))
                }

            }
        }
    }

    companion object {
        val noop: SpinnerListener = { _, _ -> }
    }
}