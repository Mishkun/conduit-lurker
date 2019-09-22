package com.themishkun.conduitlurker.core.data

import kotlinx.serialization.*
import kotlinx.serialization.internal.StringDescriptor
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.getOrSet

@Serializer(forClass = Date::class)
class DateSerializer : KSerializer<Date> {

    private val formatLink = ThreadLocal<SimpleDateFormat>()
    private val format
        get() = formatLink.getOrSet {
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
        }

    override val descriptor: SerialDescriptor = StringDescriptor.withName("Date from timestamp")

    override fun deserialize(decoder: Decoder): Date {
        val timestamp = decoder.decodeString()
        return format.parse(timestamp)
    }

    override fun serialize(encoder: Encoder, obj: Date) {
        val timestamp = format.format(obj)
        encoder.encodeString(timestamp)
    }
}