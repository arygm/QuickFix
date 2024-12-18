package com.arygm.quickfix.model.offline.large

import androidx.room.TypeConverter
import com.arygm.quickfix.model.category.Scale
import com.arygm.quickfix.model.category.Subcategory
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

  @TypeConverter
  fun fromSubcategoryList(subcategories: List<Subcategory>): String {
    return gson.toJson(subcategories)
  }

  @TypeConverter
  fun toSubcategoryList(json: String): List<Subcategory> {
    val type = object : TypeToken<List<Subcategory>>() {}.type
    return gson.fromJson(json, type)
  }

  @TypeConverter
  fun fromScale(scale: Scale?): String {
    return gson.toJson(scale)
  }

  @TypeConverter
  fun toScale(json: String): Scale? {
    val type = object : TypeToken<Scale?>() {}.type
    return gson.fromJson(json, type)
  }
}
