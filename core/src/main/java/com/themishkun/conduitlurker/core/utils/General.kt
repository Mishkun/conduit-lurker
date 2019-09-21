package com.themishkun.conduitlurker.core.utils


fun <T> List<(T) -> Unit>.notifyAll(msg: T) = forEach { listener -> listener.invoke(msg) }
