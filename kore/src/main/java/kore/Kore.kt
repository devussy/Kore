package kore

import androidx.lifecycle.ViewModel
import com.jakewharton.rxrelay2.PublishRelay
import com.jakewharton.rxrelay2.Relay
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject


abstract class Kore<ACTION, VIEW_DATA : BaseViewData> : ViewModel() {

    var lastViewData: VIEW_DATA? = null
        private set

    private val stopActionStream = PublishSubject.create<Any>()
    private val actionStream: Relay<ACTION> = PublishRelay.create<ACTION>().toSerialized()
    private val viewDataStream: PublishSubject<VIEW_DATA> by lazy {
        val viewDataStream = PublishSubject.create<VIEW_DATA>()
        actionStream
            .observeOn(Schedulers.newThread())
            .doOnNext {
                if (BuildConfig.DEBUG) {
                    System.out.println("--> [Action] $it")
                }
            }
            .scan(initialViewData, reducer)
            .skip(1)
            .doOnError {
                if (BuildConfig.DEBUG) {
                    System.out.println("--x [Error] $it")
                }
                it.printStackTrace()
            }
            .takeUntil(stopActionStream)
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext {
                if (BuildConfig.DEBUG) {
                    System.out.println("<-- [ViewData] $it")
                }
                lastViewData = it
            }
            .retry()
            .subscribe(viewDataStream)

        viewDataStream
    }

    private val initialViewData by lazy { createInitialViewData() }

    private val reducer by lazy { reduce() }


    override fun onCleared() {
        System.out.println("Clear")
        stopActionStream.onNext(0)
        stopActionStream.onComplete()
        viewDataStream.onComplete()
        super.onCleared()
    }

    fun bind(
        actions: List<Observable<out ACTION>>,
        subscriber: (viewData: VIEW_DATA) -> Unit
    ): Disposable {
        bind(actions)

        return bind(subscriber)
    }

    fun bind(action: Single<out ACTION>) {
        action.subscribe(actionStream)
    }

    fun bind(actions: List<Observable<out ACTION>>) {
        Observable.merge(actions).subscribe(actionStream)
    }

    fun bind(subscriber: (viewData: VIEW_DATA) -> Unit): Disposable =
        viewDataStream.subscribe(subscriber)

    fun acceptAction(action: ACTION) = actionStream.accept(action)


    abstract fun createInitialViewData(): VIEW_DATA

    abstract fun reduce(): Reducer<ACTION, VIEW_DATA>
}

interface BaseViewData

typealias Reducer<ACTION, VIEW_DATA> = (viewData: VIEW_DATA, action: ACTION) -> VIEW_DATA

typealias Observer<VIEW_DATA> = (viewData: VIEW_DATA) -> Unit

typealias Ignore = Unit
