package com.themishkun.conduitlurker.core

import com.themishkun.conduitlurker.core.Msg.FeedMsg
import com.themishkun.conduitlurker.core.Msg.ReadMsg
import com.themishkun.conduitlurker.core.Screen.Feed
import com.themishkun.conduitlurker.core.Screen.Read
import com.themishkun.conduitlurker.core.utils.LoadableData
import com.themishkun.conduitlurker.core.utils.pop


data class AppState(val screen: Screen, val backStack: List<Screen>)

sealed class Screen {
    data class Feed(val tag: Tag?, val listState: LoadableData<List<Article>>) : Screen()
    data class Read(val article: LoadableData<Article>) : Screen()
}

sealed class Msg {
    sealed class FeedMsg : Msg() {
        data class OnFeedLoaded(val listResponse: LoadableData<List<Article>>) : FeedMsg()
        data class OnArticleClick(val articleSlug: Slug) : FeedMsg()
        data class OnArticleTagClick(val articleTag: Tag) : FeedMsg()
    }

    sealed class ReadMsg : Msg() {
        data class OnArticleLoaded(val articleResponse: LoadableData<Article>) : ReadMsg()
        data class OnArticleTagClick(val articleTag: Tag) : ReadMsg()
    }

    object OnBack : Msg()
}

sealed class Effect {
    object LoadFeed : Effect()
    data class LoadFeedByTag(val tag: Tag) : Effect()
    data class LoadArticle(val slug: Slug) : Effect()
    object CloseApp : Effect()
}

fun reducer(msg: Msg, state: AppState): Pair<AppState, Set<Effect>> = when {
    msg is FeedMsg && state.screen is Feed -> feedReducer(msg, state, state.screen)
    msg is ReadMsg && state.screen is Read -> readReducer(msg, state, state.screen)
    msg is Msg.OnBack -> closeScreen(state)
    else -> state to emptySet()
}

internal fun feedReducer(msg: FeedMsg, state: AppState, screen: Feed): Pair<AppState, Set<Effect>> =
    when (msg) {
        is FeedMsg.OnFeedLoaded -> state.copy(screen = screen.copy(listState = msg.listResponse)) to emptySet()
        is FeedMsg.OnArticleClick -> state.copy(
            screen = Read(article = LoadableData.Loading),
            backStack = state.backStack.plus(state.screen)
        ) to setOf(Effect.LoadArticle(msg.articleSlug))
        is FeedMsg.OnArticleTagClick -> openTagFeed(msg.articleTag, state)
    }

internal fun readReducer(msg: ReadMsg, state: AppState, screen: Read): Pair<AppState, Set<Effect>> =
    when (msg) {
        is ReadMsg.OnArticleTagClick -> openTagFeed(msg.articleTag, state)
        is ReadMsg.OnArticleLoaded -> state.copy(screen = screen.copy(msg.articleResponse)) to emptySet()
    }

private fun closeScreen(
    state: AppState
): Pair<AppState, Set<Effect>> = if (state.backStack.isNotEmpty()) {
    state.copy(screen = state.backStack.last(), backStack = state.backStack.pop()) to emptySet()
} else {
    state to setOf(Effect.CloseApp)
}

private fun openTagFeed(
    tag: Tag,
    state: AppState
): Pair<AppState, Set<Effect>> = state.copy(
    screen = Feed(tag = tag, listState = LoadableData.Loading),
    backStack = state.backStack.plus(state.screen)
) to setOf(Effect.LoadFeedByTag(tag))
