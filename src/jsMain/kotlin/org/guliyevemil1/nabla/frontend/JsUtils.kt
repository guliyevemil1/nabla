package org.guliyevemil1.nabla.frontend

import kotlinx.html.Tag

class Observable<T>(t: T) {
    private val observers: MutableList<(T) -> Unit> = mutableListOf()

    var value: T = t
        set(value) {
            field = value
            observers.forEach { it(value) }
        }

    fun map(f: (T) -> T) {
        value = f(value)
    }

    fun apply(f: (T) -> Unit) {
        f(value)
        observers.forEach { it(value) }
    }

    fun observe(observe: (T) -> Unit) {
        observers.add(observe)
        observe(value)
    }
}

fun <T> Tag.observe(v: Observable<T>) {
    v.observe {
        this.text(v.value.toString())
    }
}
