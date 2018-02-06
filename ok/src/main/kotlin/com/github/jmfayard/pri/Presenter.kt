package com.github.jmfayard.pri

import com.github.jmfayard.okandroid.R
import com.github.jmfayard.pri.MviDialog.*
import com.tbruyelle.rxpermissions2.Permission
import io.reactivex.Observable
import io.reactivex.Observable.*
import io.reactivex.rxkotlin.merge
import java.util.concurrent.TimeUnit

class MainViewModel(
        val articles: Observable<List<Article>>,
        val updateButtonIsEnabled: Observable<Boolean>,
        val emptyViewIsVisible: Observable<Boolean>,
        val progressIsVisible: Observable<Boolean>,
        val smallProgressIsVisible: Observable<Boolean>,
        val updateButtonText: Observable<Int>,
        val startDetailActivitySignals: Observable<Article>,
        val permissionSignal: Observable<Permission>,
        val preferences: Observable<MviPrefs>,
        val dialogCmds: Observable<MviDialog>
) {
  fun toDebugModel() = MainViewModel(
          articles.printEvents("articles"),
          updateButtonIsEnabled.printEvents("articles"),
          emptyViewIsVisible.printEvents("emptyViewIsVisible"),
          progressIsVisible.printEvents("progressIsVisible"),
          smallProgressIsVisible.printEvents("smallProgressIsVisible"),
          updateButtonText.printEvents("updateButtonText"),
          startDetailActivitySignals.printEvents("startDetailActivitySignals"),
          permissionSignal.printEvents("permissionSignal"),
          preferences.printEvents("preferences"),
          dialogCmds.printEvents("dialogCmds")

  )
}



fun present(
        updateButtonClicks: Observable<Unit>,
        articleClicks: Observable<Article>,
        articlesProvider: ArticlesProvider,
        permissionProvider: PermissionProvider,
        prefsButtonClicks: Observable<Unit>,
        dialogResults: Observable<DialogResult>
): MainViewModel {
  // Internal states


  val getArticlesWithStartAndEnd = articlesProvider.fetchArticles()
    .markStartAndEnd()


  // View states
  val permissionSignal = updateButtonClicks
          .compose(permissionProvider.requestPermissions())
          .publish().autoConnect()


  val getArticlesEvents = permissionSignal
          .filter { it.granted }
          .flatMapWithDrop(getArticlesWithStartAndEnd)
          .publish().autoConnect()

  val isDownloadingArticles = just(false).concatWith(
    getArticlesEvents.map { it.isRunning() }
  )

  val downloadedArticles = getArticlesEvents
    .filter { it is SingleEvent.Result }
    .map { (it as SingleEvent.Result).data }

  val articles = concat(
    just(emptyList<Article>()),
    downloadedArticles
  )

  val hasArticles = articles.map { it.isNotEmpty() }



  val updateButtonIsEnabled = isDownloadingArticles.map { it.not() }

  val emptyViewIsVisible = combineLatest(isDownloadingArticles, hasArticles) { downloadingArticles, hasArticle ->
    !hasArticle && !downloadingArticles
  }

  val progressIsVisible = combineLatest(isDownloadingArticles, hasArticles) { downloadingArticles, hasArticle ->
    downloadingArticles && !hasArticle
  }.distinctUntilChanged()

  val smallProgressIsVisible = combineLatest(isDownloadingArticles, hasArticles) { downloadingArticles, hasArticle ->
    downloadingArticles && hasArticle
  }

  val updateButtonText = isDownloadingArticles.map { downloading ->
    if (downloading) R.string.updating else R.string.update
  }

  val startDetailActivitySignals = articleClicks.withLatestFrom(articles) { article, _ ->
    article
  }


  val dialogEvents = dialogResults.share()

  val refreshPrefs = articles.map { DialogResult.DialogCancel(PrefsMain) }.delay(100, TimeUnit.MILLISECONDS)

  val preferences = listOf(dialogEvents, refreshPrefs).merge()
          .printEvents("updatePrefs")
          .scan(MviPrefs(), {
            prefs, result ->
            nextPref(prefs, result)
          }
          )

  val dialogCmds = listOf(
          dialogEvents.flatMap {
            val next = nextDialog(it)
            if (next == null) empty() else just(next)
          },
          prefsButtonClicks.map { PrefsMain }
  ).merge()



  return MainViewModel(
          articles = articles,
          updateButtonIsEnabled = updateButtonIsEnabled,
          emptyViewIsVisible = emptyViewIsVisible,
          progressIsVisible = progressIsVisible,
          smallProgressIsVisible = smallProgressIsVisible,
          updateButtonText = updateButtonText,
          startDetailActivitySignals = startDetailActivitySignals,
          permissionSignal = permissionSignal,
          preferences = preferences,
          dialogCmds = dialogCmds
  )
}

fun nextDialog(it: DialogResult) : MviDialog? {
  return if (it is DialogResult.DialogOk) {
    when {
      it.dialog != PrefsMain -> null
      it.value == "font" -> PrefsFontColor
      it.value == "background" -> PrefsBackgroundColor
      else -> null
    }
  } else {
    null
  }
}

fun nextPref(prefs: MviPrefs, r: DialogResult) : MviPrefs {
  return if (r !is DialogResult.DialogOk) prefs
  else when (r.dialog){
    PrefsMain -> if (r.value == "reset") MviPrefs() else prefs
    PrefsFontColor -> prefs.copy(fontColor = r.value)
    PrefsBackgroundColor -> prefs.copy(backgroundColor = r.value)
  }
}