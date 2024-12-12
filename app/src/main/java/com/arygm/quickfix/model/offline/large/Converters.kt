package com.arygm.quickfix.model.offline.large

import androidx.room.TypeConverter
import com.arygm.quickfix.model.messaging.Message
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromMessagesList(messages: List<Message>): String {
        return gson.toJson(messages)
    }

    @TypeConverter
    fun toMessagesList(messagesJson: String): List<Message> {
        val type = object : TypeToken<List<Message>>() {}.type
        return gson.fromJson(messagesJson, type)
    }
}