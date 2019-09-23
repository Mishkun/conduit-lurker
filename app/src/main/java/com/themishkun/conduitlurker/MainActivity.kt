package com.themishkun.conduitlurker

import android.graphics.Color
import android.os.Bundle
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.LinearLayout.HORIZONTAL
import android.widget.LinearLayout.VERTICAL
import androidx.appcompat.app.AppCompatActivity
import com.themishkun.conduitlurker.core.*
import com.themishkun.conduitlurker.core.utils.LoadableData
import trikita.anvil.Anvil
import trikita.anvil.DSL.*
import trikita.anvil.RenderableView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(object : RenderableView(this) {
            override fun view() {
                when (val screen = feature.currentState.screen) {
                    is Screen.Feed -> feed(screen)
                    is Screen.Read -> read(screen)
                }
            }

            private fun feed(screen: Screen.Feed) {
                scrollView {
                    linearLayout {
                        orientation(VERTICAL)
                        backgroundColor(Color.parseColor("#FFFFFF"))

                        screen.tag?.let { tag ->
                            linearLayout {
                                padding(dip(16))
                                backgroundColor(Color.parseColor("#FEFEFE"))
                                orientation(HORIZONTAL)

                                textView {
                                    width(0)
                                    weight(1f)
                                    text("Feed by tag")
                                }
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
                            is LoadableData.Success -> for (article in list.data) {
                                item(article)
                                view {
                                    width(MATCH_PARENT)
                                    backgroundColor(Color.parseColor("#333333"))
                                }
                            }
                        }
                    }
                }
            }

            private fun read(screen: Screen.Read) {
                linearLayout {
                    orientation(VERTICAL)
                    padding(dip(16))

                    when (val article = screen.article) {
                        LoadableData.Loading -> {
                            progressBar {
                                width(dip(32))
                                height(dip(32))
                                indeterminate(true)
                            }
                        }
                        LoadableData.Error -> {
                            textView {
                                layoutGravity(CENTER)
                                textColor(Color.parseColor("#333333"))
                                text("There is nothing here, try again")
                            }
                        }
                        is LoadableData.Success -> {
                            textView {
                                text(article.data.title)
                            }
                            textView {
                                text(article.data.body)
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
                    linearLayout {
                        orientation(HORIZONTAL)
                        for (tag in article.tagList) {
                            badge(tag) { feature.accept(Msg.FeedMsg.OnArticleTagClick(tag)) }
                        }
                    }
                    onClick { feature.accept(Msg.FeedMsg.OnArticleClick(article.slug)) }
                }
            }
        })
        feature.listenState {
            Anvil.render()
        }
        feature.listenEffect { eff ->
            when (eff) {
                Effect.CloseApp -> finish()
            }
        }
    }

    override fun onBackPressed() {
        feature.accept(Msg.OnBack)
    }
}
