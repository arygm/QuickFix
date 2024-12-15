package com.arygm.quickfix.model.offline.large.messaging

import com.arygm.quickfix.model.messaging.Message
import com.arygm.quickfix.model.offline.large.Converters
import com.google.firebase.Timestamp
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ChatEntityTest {

  private lateinit var converters: Converters

  @Before
  fun setUp() {
    converters = Converters()
  }

  @Test
  fun toChat_returnsCorrectChatObject() {
    // Given a list of messages
    val timestamp = Timestamp.now()
    val messagesList =
        listOf(
            Message(
                messageId = "msg1", senderId = "user1", content = "Hello", timestamp = timestamp),
            Message(
                messageId = "msg2",
                senderId = "worker1",
                content = "Hi there!",
                timestamp = timestamp))

    // Convert the messages to a JSON string using the Converters
    val messagesJson = converters.fromMessagesList(messagesList)

    // Create a ChatEntity
    val chatEntity =
        ChatEntity(
            chatId = "chat123", workeruid = "worker1", useruid = "user1", messages = messagesJson)

    // When we call toChat()
    val chat = chatEntity.toChat()

    // Then the fields should match
    assertEquals("chat123", chat.chatId)
    assertEquals("worker1", chat.workeruid)
    assertEquals("user1", chat.useruid)

    // And messages should be correctly deserialized
    assertEquals(messagesList.size, chat.messages.size)
    assertTrue(chat.messages.any { it.messageId == "msg1" && it.content == "Hello" })
    assertTrue(chat.messages.any { it.messageId == "msg2" && it.content == "Hi there!" })
  }

  @Test
  fun toChat_withEmptyMessages_returnsChatWithEmptyList() {
    // Given an empty messages JSON
    val emptyMessagesJson = converters.fromMessagesList(emptyList())

    // Create a ChatEntity
    val chatEntity =
        ChatEntity(
            chatId = "chatEmpty",
            workeruid = "workerEmpty",
            useruid = "userEmpty",
            messages = emptyMessagesJson)

    // When we call toChat()
    val chat = chatEntity.toChat()

    // Then the fields should match and messages should be empty
    assertEquals("chatEmpty", chat.chatId)
    assertEquals("workerEmpty", chat.workeruid)
    assertEquals("userEmpty", chat.useruid)
    assertTrue(chat.messages.isEmpty())
  }
}
