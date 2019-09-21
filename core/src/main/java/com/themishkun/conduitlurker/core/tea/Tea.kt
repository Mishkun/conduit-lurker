package com.themishkun.conduitlurker.core.tea

import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import com.themishkun.conduitlurker.core.utils.notifyAll


interface Disposable {
    fun dispose()
}

interface TeaFeature<Msg : Any, State : Any, Effect : Any> {
    val currentState: State
    fun accept(msg: Msg)
    fun listenState(listener: (state: State) -> Unit): Disposable
    fun listenEffect(listener: (eff: Effect) -> Unit): Disposable
}

class TeaHandlerFeature<Msg : Any, State : Any, Effect : Any>(
    initialState: State,
    private val reducer: (Msg, State) -> Pair<State, Set<Effect>>
) : TeaFeature<Msg, State, Effect> {
    private val reduceThread = HandlerThread("reducer thread")
    private val reduceHandler = Handler(reduceThread.looper)
    private val callerThreadHandler = Handler(Looper.myLooper())
    private val stateListeners = mutableListOf<(state: State) -> Unit>()
    private val effectListeners = mutableListOf<(state: Effect) -> Unit>()

    override var currentState: State = initialState
        private set

    override fun accept(msg: Msg) {
        val state = currentState
        reduceHandler.post {
            val (newState, effects) = reducer(msg, state)
            callerThreadHandler.post {
                currentState = newState
                stateListeners.notifyAll(newState)
                effects.forEach { effect ->
                    effectListeners.notifyAll(effect)
                }
            }
        }
    }

    override fun listenState(listener: (state: State) -> Unit): Disposable {
        stateListeners.add(listener)
        return object : Disposable {
            override fun dispose() {
                stateListeners.remove(listener)
            }
        }
    }

    override fun listenEffect(listener: (eff: Effect) -> Unit): Disposable {
        effectListeners.add(listener)
        return object : Disposable {
            override fun dispose() {
                effectListeners.remove(listener)
            }
        }
    }
}

