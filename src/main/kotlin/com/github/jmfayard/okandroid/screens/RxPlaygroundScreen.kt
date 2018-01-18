package com.github.jmfayard.okandroid.screens

import android.content.Context
import android.widget.Toast
import com.github.jmfayard.okandroid.R
import com.github.jmfayard.okandroid.attach
import com.github.jmfayard.okandroid.databinding.RxScreenBinding
import com.github.jmfayard.okandroid.databinding.RxScreenBinding.inflate
import com.github.jmfayard.okandroid.inflater
import com.github.jmfayard.okandroid.screens.pri.debounceMs
import com.github.jmfayard.okandroid.screens.pri.miliseconds
import com.github.jmfayard.okandroid.screens.pri.seconds
import com.github.jmfayard.okandroid.screens.pri.timer
import com.github.jmfayard.okandroid.toast
import com.github.jmfayard.okandroid.utils.See
import com.jakewharton.rxbinding2.view.clicks
import com.jakewharton.rxbinding2.widget.itemSelections
import com.jakewharton.rxbinding2.widget.textChanges
import com.wealthfront.magellan.BaseScreenView
import com.wealthfront.magellan.Screen
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.Observables
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers

@See(layout = R.layout.rx_screen, java = UxEvent::class)
class RxPlaygroundScreen : Screen<RxPlaygroundView>() {
    override fun createView(context: Context) = RxPlaygroundView(context)

    override fun getTitle(context: Context): String = context.getString(R.string.rx_title)

    private var diposable: Disposable? = null

    override fun onResume(context: Context?) {
        diposable?.dispose()
        diposable = view.uxEvents()
                .retry { t ->
                    toast("Error : $t")
                    true
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                        onNext = { toast(it.toString()) },
                        onError = { toast("Error : $it") }
                )
    }

    fun rxTesting(): Observable<String> {
        return Observable.just(1, 2, 4)
                .subscribeOn(Schedulers.io())
                .flatMap { n ->
                    Observables.timer(n.seconds).map { n }
                }.observeOn(AndroidSchedulers.mainThread())
                .map { n ->
                    val thred = Thread.currentThread().name
                    "Got $n on $thred"
                }


    }

    override fun onPause(context: Context?) {
        diposable?.dispose()
    }
}


class RxPlaygroundView(context: Context) : BaseScreenView<RxPlaygroundScreen>(context) {

    val binding: RxScreenBinding = inflate(inflater, this, attach)


    fun uxEvents(): Observable<out UxEvent> {

        return Observable.merge(listOf(
                binding.rxButtonWait.clicks().map { ClickWait },
                binding.rxCheckboxCheckme.clicks().map {
                    CheckMe(binding.rxCheckboxCheckme.isChecked)
                },
                binding.rxButtonCrash.clicks().map {
                    if (true) {
                        throw RuntimeException("Please handle errors correctly!!!")
                    }
                    ErrorThrown("failed")
                },
                binding.rxEditTime.textChanges()
                        .debounceMs(500.miliseconds)
                        .map { seq ->
                            UpdateTime(seq.toString())
                        },
                binding.rxEditTime.textChanges()
                        .debounceMs(500.miliseconds)
                        .map { seq ->
                            UpdateTime(seq.toString())
                        },
                binding.rxSpinnerCountries.itemSelections()
                        .skipInitialValue()
                        .skip(1)
                        .map { position: Int ->
                            val country = binding.rxSpinnerCountries.adapter.getItem(position) as String
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




