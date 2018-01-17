package com.github.jmfayard.okandroid.screens.mvi

import com.github.jmfayard.okandroid.R
import com.tbruyelle.rxpermissions2.Permission
import io.reactivex.Observable
import io.reactivex.Observable.concat
import io.reactivex.Observable.just
import timber.log.Timber

class MainViewModel(
  val articles: Observable<List<Article>>,
  val updateButtonIsEnabled: Observable<Boolean>,
  val emptyViewIsVisible: Observable<Boolean>,
  val progressIsVisible: Observable<Boolean>,
  val smallProgressIsVisible: Observable<Boolean>,
  val updateButtonText: Observable<Int>,
  val startDetailActivitySignals: Observable<Article>,
  val permissionSignal: Observable<Permission>
) {
  fun debug() = MainViewModel(
          articles.debug("articles"),
          updateButtonIsEnabled.debug("articles"),
          emptyViewIsVisible.debug("emptyViewIsVisible"),
          progressIsVisible.debug("progressIsVisible"),
          smallProgressIsVisible.debug("smallProgressIsVisible"),
          updateButtonText.debug("updateButtonText"),
          startDetailActivitySignals.debug("startDetailActivitySignals"),
          permissionSignal.debug("permissionSignal")
  )
}


fun <T> Observable<T>.debug(name: String) : Observable<T>
        = this.doOnEach { Timber.i("$name -> $it") }

fun present(
        updateButtonClicks: Observable<Unit>,
        articleClicks: Observable<Article>,
        articlesProvider: ArticlesProvider,
        permissionProvider: PermissionProvider
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

  return MainViewModel(
    articles = articles,
    updateButtonIsEnabled = updateButtonIsEnabled,
    emptyViewIsVisible = emptyViewIsVisible,
    progressIsVisible = progressIsVisible,
    smallProgressIsVisible = smallProgressIsVisible,
    updateButtonText = updateButtonText,
    startDetailActivitySignals = startDetailActivitySignals,
    permissionSignal = permissionSignal
  )
}

