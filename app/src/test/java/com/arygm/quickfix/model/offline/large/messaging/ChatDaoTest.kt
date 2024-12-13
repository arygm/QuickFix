package com.arygm.quickfix.model.offline.large.messaging

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.arygm.quickfix.model.offline.large.QuickFixRoomDatabase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ChatDaoTest {

  private lateinit var database: QuickFixRoomDatabase
  private lateinit var chatDao: ChatDao

  @Before
  fun setup() {
    // Create an in-memory database for testing
    database =
        Room.inMemoryDatabaseBuilder(
                ApplicationProvider.getApplicationContext(), QuickFixRoomDatabase::class.java)
            .allowMainThreadQueries()
            .build() // Use main thread for simplicity in tests

    chatDao = database.chatDao()
  }

  @After
  fun teardown() {
    database.close()
  }

  @Test
  fun insertAndGetChatById() = runBlocking {
    val chat =
        ChatEntity(chatId = "chat1", workeruid = "worker1", useruid = "user1", messages = "[]")

    // Insert a chat
    chatDao.insertChat(chat)

    // Retrieve the chat by ID
    val retrievedChat = chatDao.getChatById("chat1")

    // Assert that the retrieved chat matches the inserted chat
    assertEquals(chat, retrievedChat)
  }

  @Test
  fun getAllChats() = runBlocking {
    val chat1 =
        ChatEntity(chatId = "chat1", workeruid = "worker1", useruid = "user1", messages = "[]")
    val chat2 =
        ChatEntity(chatId = "chat2", workeruid = "worker2", useruid = "user2", messages = "[]")

    // Insert chats
    chatDao.insertChat(chat1)
    chatDao.insertChat(chat2)

    // Retrieve all chats
    val allChats = chatDao.getAllChats().first()

    // Assert the size and contents
    assertEquals(2, allChats.size)
    assertEquals(listOf(chat1, chat2), allChats)
  }

  @Test
  fun deleteChat() = runBlocking {
    val chat =
        ChatEntity(chatId = "chat1", workeruid = "worker1", useruid = "user1", messages = "[]")

    // Insert a chat
    chatDao.insertChat(chat)

    // Delete the chat
    chatDao.deleteChat("chat1")

    // Attempt to retrieve the deleted chat
    val retrievedChat = chatDao.getChatById("chat1")

    // Assert that the chat no longer exists
    assertEquals(null, retrievedChat)
  }
}
