package com.themishkun.conduitlurker

import trikita.anvil.DSL.*


fun badge(label: String) {
    frameLayout {
        padding(dip(4))
        textView {
            text(label)
        }
    }
}