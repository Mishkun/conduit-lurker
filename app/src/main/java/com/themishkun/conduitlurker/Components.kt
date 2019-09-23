package com.themishkun.conduitlurker

import android.graphics.Color
import trikita.anvil.DSL.*


fun badge(label: String, listener: (() -> Unit)? = null) {
    frameLayout {
        padding(dip(4))
        margin(0, 0, dip(4), 0)
        backgroundColor(Color.parseColor("#45DA45"))
        textView {
            text(label)
        }
        onClick { listener?.invoke() }
    }
}