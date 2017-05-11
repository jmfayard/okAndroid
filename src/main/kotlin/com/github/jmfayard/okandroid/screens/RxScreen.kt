package com.github.jmfayard.okandroid.screens

import android.content.Context
import android.view.LayoutInflater
import android.widget.Toast
import com.github.jmfayard.okandroid.R
import com.github.jmfayard.okandroid.attach
import com.github.jmfayard.okandroid.databinding.RxplaygroundBinding
import com.github.jmfayard.okandroid.databinding.RxplaygroundBinding.inflate
import com.github.jmfayard.okandroid.inflater
import com.github.jmfayard.okandroid.utils.See
import com.jakewharton.rxbinding2.view.clicks
import com.jakewharton.rxbinding2.widget.*
import com.wealthfront.magellan.BaseScreenView
import com.wealthfront.magellan.Screen
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.subscribeBy
import java.util.concurrent.TimeUnit

@See(layout = R.layout.rxplayground, java = UxEvent::class)
class RxScreen : Screen<RxView>() {
    override fun createView(context: Context) = RxView(context)

    override fun getTitle(context: Context?): String = "Rx Playground"

    private var diposable: Disposable? = null

    override fun onResume(context: Context?) {
        diposable?.dispose()
        diposable = view.uxEvents()
                .retry { t ->
                    view.toast("Error : $t")
                    true
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                onNext =  { view.toast(it.toString()) },
                onError = { view.toast("Error : $it")}
        )
    }

    override fun onPause(context: Context?) {
        diposable?.dispose()
    }
}


class RxView(context: Context) : BaseScreenView<RxScreen>(context) {

    val binding : RxplaygroundBinding = inflate(inflater, this, attach)


    fun uxEvents() : Observable<out UxEvent> {

        return Observable.merge(listOf(
                binding.rxButton.clicks().map { ClickWait },
                binding.rxCheckbox.clicks().map {
                    CheckMe(binding.rxCheckbox.isChecked)
                },
                binding.rxCRash.clicks().map {
                    if(true) { throw RuntimeException("Please handle errors correctly!!!") }
                    ErrorThrown("failed")
                },
                binding.rxTime.textChanges()
                        .debounce(500, TimeUnit.MILLISECONDS)
                        .map { seq ->
                            UpdateTime(seq.toString())
                        },
                binding.rxTime.textChanges()
                        .debounce(500, TimeUnit.MILLISECONDS)
                        .map { seq ->
                            UpdateTime(seq.toString())
                        },
                binding.rxSpinner.itemSelections()
                        .skipInitialValue()
                        .skip(1)
                        .map { position: Int ->
                            val country = binding.rxSpinner.adapter.getItem(position) as String
                            UpdateCountry(country)
                        }
        ))
    }


    fun toast(text: String) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
    }

}


sealed class UxEvent

data class UpdateTime(val content: String) : UxEvent()
data class UpdateCountry(val country: String) : UxEvent()
data class CheckMe(val checked: Boolean) : UxEvent()
object ClickWait : UxEvent()
data class ErrorThrown(val message: String) : UxEvent()




