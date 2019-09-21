package com.themishkun.conduitlurker.core.utils

import java.util.concurrent.Executors

class ThreadPoolScheduler : (() -> Unit) -> Unit {
    private val executor =
        Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors())

    override fun invoke(task: () -> Unit) {
        executor.execute(task)
    }
}