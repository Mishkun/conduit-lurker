package com.themishkun.conduitlurker.core.tea

import android.os.Handler
import android.os.Looper

class TeaEffectDecorator<Msg : Any, State : Any, Effect : Any>(
    private val innerFeature: TeaFeature<Msg, State, Effect>,
    private val effectHandler: ((Msg) -> Unit).(eff: Effect) -> Unit,
    private val scheduler: (() -> Unit) -> Unit
) : TeaFeature<Msg, State, Effect> by innerFeature {
    private val callerThreadHandler = Handler(Looper.myLooper())
    private val msgListener: (Msg) -> Unit = { msg: Msg ->
        callerThreadHandler.post {
            innerFeature.accept(msg)
        }
    }

    init {
        innerFeature.listenEffect { effect ->
            scheduler {
                effectHandler.invoke(msgListener, effect)
            }
        }
    }
}

fun <Msg : Any, State : Any, Effect : Any> TeaFeature<Msg, State, Effect>.wrapWithHandler(
    scheduler: (() -> Unit) -> Unit,
    effectHandler: ((Msg) -> Unit).(eff: Effect) -> Unit
) = TeaEffectDecorator(this, effectHandler, scheduler)
