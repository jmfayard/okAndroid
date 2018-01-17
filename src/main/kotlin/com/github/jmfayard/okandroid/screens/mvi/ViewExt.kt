package com.github.jmfayard.okandroid.screens.mvi

import android.view.View
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.BehaviorSubject


fun <T> Observable<T>.render(onNext: (T) -> Unit): Disposable {
  return this.observeOn(AndroidSchedulers.mainThread()).subscribe(onNext)
}


fun View.onClick(function: () -> Unit) {
  this.setOnClickListener {
    function.invoke()
  }
}

var View.visible: Boolean
  get() {
    return this.visibility == View.VISIBLE
  }
  set(visible: Boolean) {
    this.visibility = if (visible) View.VISIBLE else View.INVISIBLE
  }
