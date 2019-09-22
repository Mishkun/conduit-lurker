package com.themishkun.conduitlurker

import android.graphics.Color
import android.os.Bundle
import android.widget.LinearLayout.HORIZONTAL
import android.widget.LinearLayout.VERTICAL
import androidx.appcompat.app.AppCompatActivity
import com.themishkun.conduitlurker.core.*
import com.themishkun.conduitlurker.core.Screen.Feed
import com.themishkun.conduitlurker.core.tea.TeaHandlerFeature
import com.themishkun.conduitlurker.core.tea.wrapWithHandler
import com.themishkun.conduitlurker.core.utils.IoScheduler
import com.themishkun.conduitlurker.core.utils.LoadableData
import trikita.anvil.DSL.*
import trikita.anvil.RenderableView

class MainActivity : AppCompatActivity() {

    private val feature = TeaHandlerFeature(
        initialState = AppState(
            screen = Feed(tag = null, listState = LoadableData.Loading),
            backStack = emptyList()
        ),
        reducer = ::reducer
    ).wrapWithHandler(
        IoScheduler(),
        Effect.LoadFeed
    ) {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(object : RenderableView(this) {
            override fun view() {
                when (val screen = feature.currentState.screen) {
                    is Feed -> feed(screen)
                    is Screen.Read -> TODO()
                }
            }

            private fun feed(screen: Feed) {
                linearLayout {
                    orientation(VERTICAL)
                    padding(dip(16))
                    backgroundColor(Color.parseColor("#FFFFFF"))

                    screen.tag?.let { tag ->
                        linearLayout {
                            backgroundColor(Color.parseColor("#FEFEFE"))
                            orientation(HORIZONTAL)

                            textView { text("Feed by tag") }
                            badge(tag)
                        }
                    }

                    when (val list = screen.listState) {
                        LoadableData.Loading -> frameLayout {
                            progressBar {
                                width(dip(32))
                                height(dip(32))
                                indeterminate(true)
                            }
                        }
                        LoadableData.Error -> frameLayout {
                            padding(dip(32))

                            textView {
                                layoutGravity(CENTER)
                                textColor(Color.parseColor("#333333"))
                                text("There is nothing here, try again")
                            }
                        }
                        is LoadableData.Success -> linearLayout {
                            for (article in list.data) {
                                item(article)
                            }
                        }
                    }
                }
            }

            private fun item(article: Article) {
                linearLayout {
                    padding(dip(16))
                    orientation(VERTICAL)

                    textView {
                        text(article.title)
                    }
                }
            }
        })
    }
}
