package com.github.jmfayard.pri

import android.view.View
import android.view.ViewGroup
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable


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


fun View.children(): List<View> {
  if (this !is ViewGroup) return emptyList()
  val result = mutableListOf<View>()
  for (i in 0 until childCount) {
    val child = getChildAt(i)
    result += child
    result.addAll(child.children())
  }
  return result
}