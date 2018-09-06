package com.github.jmfayard.screens;

import android.content.Context;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import com.wealthfront.magellan.Screen;
import com.wealthfront.magellan.ScreenView;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.BehaviorSubject;
import kotlin.Unit;

/**
 * Base magellan screen to apply the functional reactive architecture described in
 *
 * See <a href="https://rongi.github.io/kotlin-blog/rx-presenter.html">Implementing your presenter with Rx or Functional Reactive architecture for Android applications</a>
 *
 * Your presenter is a pure function that returns a data class of observables
 *
 * It contains the logic of your app and can be easily tested
 *
 * In onRender you should subscribe to each of those observables and
 * apply the desired side effects on your UX
 *
 * Written in java because of cross-reference problem
 * https://stackoverflow.com/questions/43786012/cross-references-in-type-parameters
 */
public abstract class ReactiveScreen<V extends ViewGroup & ScreenView> extends Screen<V> {

    private CompositeDisposable disposables = new CompositeDisposable();

    /** Title of the screen **/
    public abstract @StringRes
    int screenTitle();

    /** Create the view
     * <pre>
Typically:

<code>override fun createView(context: Context) = MyView(context)</code>

where

<code>
    class MyView(context: Context) : BaseScreenView<MyScreen>(context) {
          init { inflateViewFrom(Layout.id) }
    }
</pre>

     * **/
    @Override
    protected abstract @NonNull
    V createView(@NonNull Context context);

    /** Replace onShow() in a reactive screen.
     * this is where you setup <code>render(present(userInput))</code> **/
    protected abstract void onRender(@NonNull Context context);

    /** Replace onHide() in a reactive screen **/
    protected void onDispose(@NonNull Context context) {

    }

    protected final void autoDispose(Disposable disposable) {
        disposables.add(disposable);
    }

    /** Create a hot observable of clicks on this view.  **/
    protected final Observable<Unit> clicks(@IdRes Integer view) {
        final BehaviorSubject<Unit> clicks = BehaviorSubject.create();
        listeners.put(view, clicks);
        getActivity().findViewById(view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clicks.onNext(Unit.INSTANCE);
            }
        });
        return clicks;
    }

    /** Create a hot observable of clicks on this view.  **/
    protected final Observable<Unit> clicks(HasId hasId) {
        return clicks(hasId.getId());
    }

    /** see screenTitle() **/
    @Override
    public final String getTitle(Context context) {
        return context.getString(screenTitle());
    }


    /** Replaced by onRender() in a reactive screen **/
    @Override
    protected final void onShow(Context context) {
        disposables = new CompositeDisposable();
        onRender(context);
    }


    private SparseArray<BehaviorSubject<Unit>> listeners = new SparseArray<>();

    /** Replaced by onDispose in a reactive screen **/
    @Override
    protected final void onHide(Context context) {
        onDispose(context);
        disposables.dispose();
        for (int i = 0; i < listeners.size(); i++) {
            listeners.valueAt(i).onComplete();
        }
    }

}