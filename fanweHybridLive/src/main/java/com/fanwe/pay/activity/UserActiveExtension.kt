package com.hn.library.extension


import android.view.View
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.TimeUnit

/**

 * 项目名称：
 * 类描述：扩展工具类
 * 创建人：mj
 * 创建时间：2017/11/16 11:23
 *
 */

/**
 * 刷新页面，activity或者fragment，示例：
 * refreshMap[MyAddressActivity::class.java.simpleName] = true
 */
val refreshMap = mutableMapOf<String, Boolean>()

/**-------------------------------------------      自定义处理点击事件的绑定       ----------------------------------------------------------*/
/**
 * 设置需要过滤狂点的点击事件
 *
 * @param views
 * @return
 */
fun setThrottleClickListener(vararg views: View): Observable<View> {
    return MultiClickObservable(views.toList())
            .throttleFirst(500, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
}

/**
 * 订阅
 */
val  publicSubject=PublishSubject.create<Any>()
