package com.hw.baselibrary

import com.trello.rxlifecycle2.LifecycleProvider
import io.reactivex.Observable

/**
 *author：Thinkpad
 *data:2019/12/7 08:52
 * kotlin的通用扩展
 */
/**
 * 扩展observable执行
 */
fun <T> Observable<T>.bindLife(lifecycleProvider: LifecycleProvider<*>): Observable<T> {
    return this.compose(lifecycleProvider.bindToLifecycle())
}

/**
 * 获取字符串的最后一位字符
 * fun          固定写法
 * String       想要扩展方法的类
 * lastChar     扩展的方法名称
 * Char         返回值类型
 * { return this.get(this.length - 1)}      方法的具体实现
 */
fun String.lastChar(): Char {
    return this.get(this.length - 1)
}

