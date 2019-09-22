package com.themishkun.conduitlurker.core

import android.net.Uri
import android.util.Log
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.FuelManager
import com.github.kittinunf.fuel.core.Headers
import com.github.kittinunf.fuel.core.Request
import com.themishkun.conduitlurker.core.tea.TeaHandlerFeature
import com.themishkun.conduitlurker.core.tea.wrapWithHandler
import com.themishkun.conduitlurker.core.utils.IoScheduler
import com.themishkun.conduitlurker.core.utils.LoadableData
import kotlinx.serialization.json.Json
import kotlinx.serialization.list

private const val FUEL_TAG = "Fuel"

val feature by lazy {
    val baseUrl = Uri.parse("https://conduit.productionready.io/api")
    fun endpoint(path: String) = baseUrl.buildUpon().appendPath(path)
    val json = Json.nonstrict
    FuelManager.instance.baseHeaders = mapOf(Headers.CONTENT_TYPE to "application/json")

    TeaHandlerFeature(
        initialState = AppState(
            screen = Screen.Feed(tag = null, listState = LoadableData.Loading),
            backStack = emptyList()
        ),
        reducer = ::reducer
    ).wrapWithHandler(
        IoScheduler(),
        Effect.LoadFeed
    ) { eff ->
        when (eff) {
            Effect.LoadFeed -> {
                val articles = Fuel.get {
                    endpoint("articles")
                }.execute(
                    deserializer = {
                        val response = json.parseJson(it)
                        val articles = response.jsonObject.getArray("articles")
                        val deserialized = json.fromJson(Article.serializer().list, articles)
                        LoadableData.Success(deserialized)
                    },
                    onError = { LoadableData.Error }
                )
                invoke(Msg.FeedMsg.OnFeedLoaded(articles))
            }
            is Effect.LoadFeedByTag -> {
                val articles = Fuel.get {
                    endpoint("articles").appendQueryParameter("tag", eff.tag)
                }.execute(
                    deserializer = {
                        val response = json.parseJson(it)
                        val articles = response.jsonObject.getArray("articles")
                        val deserialized = json.fromJson(Article.serializer().list, articles)
                        LoadableData.Success(deserialized)
                    },
                    onError = { LoadableData.Error }
                )
                invoke(Msg.FeedMsg.OnFeedLoaded(articles))
            }
            is Effect.LoadArticle -> {
                val article = Fuel.get {
                    endpoint("articles").appendPath(eff.slug)
                }.execute(
                    deserializer = {
                        val response = json.parseJson(it)
                        val article = response.jsonObject.getArray("article")
                        val deserialized = json.fromJson(Article.serializer(), article)
                        LoadableData.Success(deserialized)
                    },
                    onError = { LoadableData.Error }
                )
                invoke(Msg.ReadMsg.OnArticleLoaded(article))
            }
        }
    }
}

private inline fun Fuel.get(uriBuilder: () -> Uri.Builder) = get(uriBuilder().build().toString())

private inline fun <T> Request.execute(
    deserializer: (String) -> T,
    onError: (Throwable) -> T
): T {
    Log.d(FUEL_TAG, toString())
    val (_, response, result) = responseString()
    Log.d(FUEL_TAG, response.toString())
    return result.fold(
        success = deserializer,
        failure = { onError(it.exception) }
    )
}