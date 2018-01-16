package com.github.jmfayard.okandroid.screens

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.support.annotation.IdRes
import android.support.annotation.LayoutRes
import android.view.LayoutInflater
import android.widget.TextView
import com.github.jmfayard.okandroid.App
import com.github.jmfayard.okandroid.R
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty


/*** usage:
 *
 * fun pinDialog(
onOk: (pin: String) -> Unit,
onCancel: () -> Unit,
amount: MonetaryAmount,
recipient: String
) = DialogCreator { activity ->

if (isRunningUnitTests) return@DialogCreator DummyDialog

val display = object : DisplayDialog(activity, R.layout.nfc_confirm_pin) {
var transferAmount: String by bindToText(R.id.transfer_amount)
var recipient: String by bindToText(R.id.transfer_recipient)
var pinEntry: String by bindToText(R.id.card_pin)
}

display.transferAmount = moneyFormatter.format(amount)
display.recipient = recipient

display.createBuilder()
.setNegativeButton("cancel") { _, _ -> onCancel() }
.setOnCancelListener { onCancel() }
.setPositiveButton("Pay Now") { _, _ -> onOk(display.pinEntry) }
.show()
}


 */

abstract class DisplayDialog(val activity: Activity, @LayoutRes layout: Int) : IDisplay {
    val view = LayoutInflater.from(activity).inflate(layout, null)

    fun bindToText(@IdRes textView: Int) = DialogViewBinding(lazy { view.findViewById<TextView>(textView) })

    fun createBuilder(): AlertDialog.Builder {
        return AlertDialog.Builder(activity, R.style.AppTheme)
                .setView(view)
    }
}

class DialogViewBinding(lazyViewProvider: Lazy<TextView>) : ReadWriteProperty<Any?, String> {

    val view by lazyViewProvider

    override fun getValue(thisRef: Any?, property: KProperty<*>): String {
        return view.text.toString()
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: String) {
        view.text = value
    }
}


object DummyDialog : Dialog(App.ctx) {
    override fun show() {}
    override fun hide() {}
    override fun setOnDismissListener(listener: DialogInterface.OnDismissListener?) {}
    override fun dismiss() {}
    override fun cancel() {}
    override fun isShowing(): Boolean = false
}