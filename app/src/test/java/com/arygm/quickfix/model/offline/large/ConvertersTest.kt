package com.arygm.quickfix.model.offline.large

import com.arygm.quickfix.model.category.Scale
import com.arygm.quickfix.model.category.Subcategory
import com.arygm.quickfix.model.messaging.Message
import com.google.firebase.Timestamp
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ConvertersTest {

  private lateinit var converters: Converters

  @Before
  fun setUp() {
    converters = Converters()
  }

  @Test
  fun fromMessagesList_serializesCorrectly() {
    val timestamp = Timestamp.now()
    val messages =
        listOf(
            Message(messageId = "1", senderId = "user1", content = "Hello!", timestamp = timestamp),
            Message(
                messageId = "2",
                senderId = "worker1",
                content = "Hi there!",
                timestamp = timestamp))

    val json = converters.fromMessagesList(messages)
    assertTrue(json.isNotEmpty())
    assertTrue(json.contains("user1"))
    assertTrue(json.contains("worker1"))
    assertTrue(json.contains("Hello!"))
    assertTrue(json.contains("Hi there!"))
  }

  @Test
  fun toMessagesList_deserializesCorrectly() {
    val timestamp = Timestamp.now()
    val messages =
        listOf(
            Message(messageId = "1", senderId = "user1", content = "Hello!", timestamp = timestamp),
            Message(
                messageId = "2",
                senderId = "worker1",
                content = "Hi there!",
                timestamp = timestamp))

    val json = converters.fromMessagesList(messages)
    val deserialized = converters.toMessagesList(json)

    assertEquals(messages.size, deserialized.size)
    assertEquals("1", deserialized[0].messageId)
    assertEquals("user1", deserialized[0].senderId)
    assertEquals("Hello!", deserialized[0].content)
    assertEquals("2", deserialized[1].messageId)
    assertEquals("worker1", deserialized[1].senderId)
    assertEquals("Hi there!", deserialized[1].content)
  }

  @Test
  fun fromSubcategoryList_serializesCorrectly() {
    val subcategories =
        listOf(
            Subcategory(id = "1", name = "Sub1", category = "Cat1", tags = listOf("Tag1", "Tag2")),
            Subcategory(id = "2", name = "Sub2", category = "Cat2", tags = listOf("Tag3")))

    val json = converters.fromSubcategoryList(subcategories)
    assertTrue(json.isNotEmpty())
    assertTrue(json.contains("Sub1"))
    assertTrue(json.contains("Sub2"))
    assertTrue(json.contains("Tag1"))
    assertTrue(json.contains("Tag3"))
  }

  @Test
  fun toSubcategoryList_deserializesCorrectly() {
    val subcategories =
        listOf(
            Subcategory(id = "1", name = "Sub1", category = "Cat1", tags = listOf("Tag1", "Tag2")),
            Subcategory(id = "2", name = "Sub2", category = "Cat2", tags = listOf("Tag3")))

    val json = converters.fromSubcategoryList(subcategories)
    val deserialized = converters.toSubcategoryList(json)

    assertEquals(subcategories.size, deserialized.size)
    assertEquals("1", deserialized[0].id)
    assertEquals("Sub1", deserialized[0].name)
    assertEquals("Cat1", deserialized[0].category)
    assertEquals(listOf("Tag1", "Tag2"), deserialized[0].tags)
    assertEquals("2", deserialized[1].id)
    assertEquals("Sub2", deserialized[1].name)
    assertEquals("Cat2", deserialized[1].category)
    assertEquals(listOf("Tag3"), deserialized[1].tags)
  }

  @Test
  fun fromScale_serializesCorrectly() {
    val scale = Scale(longScale = "Large", shortScale = "L")

    val json = converters.fromScale(scale)
    assertTrue(json.isNotEmpty())
    assertTrue(json.contains("Large"))
    assertTrue(json.contains("L"))
  }

  @Test
  fun toScale_deserializesCorrectly() {
    val scale = Scale(longScale = "Large", shortScale = "L")

    val json = converters.fromScale(scale)
    val deserialized = converters.toScale(json)

    assertEquals(scale.longScale, deserialized?.longScale)
    assertEquals(scale.shortScale, deserialized?.shortScale)
  }

  @Test
  fun emptyScale_serializesAndDeserializesCorrectly() {
    val scale: Scale? = null
    val json = converters.fromScale(scale)
    assertTrue(json.isNotEmpty()) // Should serialize to "null" for a nullable field

    val deserialized = converters.toScale(json)
    assertEquals(null, deserialized) // Should return null
  }

  @Test
  fun emptySubcategoryList_serializesAndDeserializesCorrectly() {
    val subcategories = emptyList<Subcategory>()
    val json = converters.fromSubcategoryList(subcategories)
    assertTrue(json.isNotEmpty()) // Should serialize to "[]"

    val deserialized = converters.toSubcategoryList(json)
    assertTrue(deserialized.isEmpty())
  }
}
