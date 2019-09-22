package com.themishkun.conduitlurker.core.tea

import android.os.Handler
import android.os.Looper

class TeaEffectDecorator<Msg : Any, State : Any, Effect : Any>(
    private val innerFeature: TeaFeature<Msg, State, Effect>,
    private val effectHandler: ((Msg) -> Unit).(eff: Effect) -> Unit,
    initialEffect: Effect,
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
        scheduler {
            effectHandler.invoke(msgListener, initialEffect)
        }
    }
}

fun <Msg : Any, State : Any, Effect : Any> TeaFeature<Msg, State, Effect>.wrapWithHandler(
    scheduler: (() -> Unit) -> Unit,
    initialEffect: Effect,
    effectHandler: ((Msg) -> Unit).(eff: Effect) -> Unit
) = TeaEffectDecorator(
    innerFeature = this,
    effectHandler = effectHandler,
    initialEffect = initialEffect,
    scheduler = scheduler
)
