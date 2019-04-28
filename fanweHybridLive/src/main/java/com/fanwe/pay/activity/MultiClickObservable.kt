package com.hn.library.extension

import android.os.Looper
import android.view.View
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.MainThreadDisposable

/**
 *
 * Copyright (C) 2018,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：HnMall_V2.0
 * 类描述：多个控件点击事件的observable
 * 创建人：kevinxie
 * 创建时间：2018/4/24 18:08
 * 修改人：
 * 修改时间：2018/4/24 18:08
 * 修改备注：
 * Version:  1.0.0
 *
 */
class MultiClickObservable(private val view: List<View>) : Observable<View>() {

    override fun subscribeActual(observer: Observer<in View>) {
        if (!checkMainThread(observer)) {
            return
        }
        val listener = Listener(view, observer)
        observer.onSubscribe(listener)
        view.map {
            it.setOnClickListener(listener)
        }
    }

    private fun checkMainThread(observer: Observer<*>): Boolean {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            observer.onError(IllegalStateException(
                    "Expected to be called on the main thread but was " + Thread.currentThread().name))
            return false
        }
        return true
    }

    internal class Listener(private val views: List<View>, private val observer: Observer<in View>) : MainThreadDisposable(), View.OnClickListener {

        override fun onClick(v: View) {
            if (!isDisposed) {
                observer.onNext(v)
            }
        }

        override fun onDispose() {
            views.map {
                it.setOnClickListener(null)
            }
        }
    }
}