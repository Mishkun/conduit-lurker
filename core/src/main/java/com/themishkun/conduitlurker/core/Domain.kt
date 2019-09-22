package com.themishkun.conduitlurker.core

import com.themishkun.conduitlurker.core.data.DateSerializer
import kotlinx.serialization.Serializable
import java.util.*

typealias Tag = String
typealias Slug = String

@Serializable
data class Article(
    val slug: Slug,
    val title: String,
    val description: String,
    val body: String,
    val tagList: List<Tag>,
    @Serializable(with = DateSerializer::class) val createdAt: Date,
    @Serializable(with = DateSerializer::class) val updatedAt: Date,
    val favorited: Boolean,
    val favoritesCount: Int,
    val author: Author
)

@Serializable
data class Author(
    val username: String,
    val bio: String,
    val image: String
)