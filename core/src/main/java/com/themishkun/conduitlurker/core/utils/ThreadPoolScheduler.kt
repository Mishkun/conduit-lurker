package com.themishkun.conduitlurker.core.utils

import java.util.concurrent.Executors

private typealias Scheduler = (() -> Unit) -> Unit
private typealias Task = () -> Unit

class ComputationScheduler : Scheduler {
    private val executor =
        Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors())

    override fun invoke(task: Task) {
        executor.execute(task)
    }
}

class IoScheduler : Scheduler {
    private val executor =
        Executors.newCachedThreadPool()

    override fun invoke(task: Task) {
        executor.execute(task)
    }
}