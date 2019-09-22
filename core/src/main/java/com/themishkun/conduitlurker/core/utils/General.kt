package com.themishkun.conduitlurker.core.utils


fun <T> List<(T) -> Unit>.notifyAll(msg: T) = forEach { listener -> listener.invoke(msg) }

sealed class LoadableData<out T> {
    abstract val data: T?

    object Loading : LoadableData<Nothing>() {
        override val data = null
    }

    object Error : LoadableData<Nothing>() {
        override val data = null
    }

    data class Success<T>(override val data: T) : LoadableData<T>()
}

fun <T> List<T>.pop(): List<T> = subList(0, lastIndex - 1)