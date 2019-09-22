package com.themishkun.conduitlurker

import android.graphics.Color
import trikita.anvil.DSL.*


fun badge(label: String) {
    frameLayout {
        padding(dip(4))
        margin(0, 0, dip(4), 0)
        backgroundColor(Color.parseColor("#45DA45"))
        textView {
            text(label)
        }
    }
}