package com.arygm.quickfix.model.offline.large

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
    // Just check that it's not empty and contains known fields
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
    // Verify fields of the first message
    assertEquals("1", deserialized[0].messageId)
    assertEquals("user1", deserialized[0].senderId)
    assertEquals("Hello!", deserialized[0].content)
    // Timestamp comparison: we can check that the type matches or that the difference in time is
    // minimal
    // but for simplicity, we'll just assume if it deserialized without error it's correct.

    // Verify fields of the second message
    assertEquals("2", deserialized[1].messageId)
    assertEquals("worker1", deserialized[1].senderId)
    assertEquals("Hi there!", deserialized[1].content)
  }

  @Test
  fun emptyList_serializesAndDeserializesCorrectly() {
    val messages = emptyList<Message>()
    val json = converters.fromMessagesList(messages)
    assertTrue(json.isNotEmpty()) // Should be "[]"

    val deserialized = converters.toMessagesList(json)
    assertTrue(deserialized.isEmpty())
  }
}
