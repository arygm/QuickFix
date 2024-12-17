package com.arygm.quickfix.model.offline.large

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.arygm.quickfix.model.offline.large.messaging.ChatDao
import com.arygm.quickfix.model.offline.large.messaging.ChatEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class QuickFixRoomDatabaseTest {

  private lateinit var database: QuickFixRoomDatabase
  private lateinit var chatDao: ChatDao

  @Before
  fun setUp() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    database = Room.inMemoryDatabaseBuilder(context, QuickFixRoomDatabase::class.java).build()
    chatDao = database.chatDao()
  }

  @After
  fun tearDown() {
    database.close()
  }

  @Test
  fun insertAndRetrieveChatEntity() = runBlocking {
    val chatEntity =
        ChatEntity(chatId = "testChatId", workeruid = "worker1", useruid = "user1", messages = "[]")

    chatDao.insertChat(chatEntity)

    val allChats = chatDao.getAllChats().first()
    assertEquals(1, allChats.size)
    val retrieved = allChats[0]
    assertEquals("testChatId", retrieved.chatId)
    assertEquals("worker1", retrieved.workeruid)
    assertEquals("user1", retrieved.useruid)
    assertEquals("[]", retrieved.messages)
  }

  @Test
  fun getAllChats_emptyDatabase_returnsEmptyList() = runBlocking {
    val allChats = chatDao.getAllChats().first()
    assertTrue(allChats.isEmpty())
  }

  @Test
  fun insertMultipleChats_andRetrieveAll() = runBlocking {
    val chat1 =
        ChatEntity(chatId = "chat1", workeruid = "workerA", useruid = "userA", messages = "[]")
    val chat2 =
        ChatEntity(chatId = "chat2", workeruid = "workerB", useruid = "userB", messages = "[]")
    val chat3 =
        ChatEntity(chatId = "chat3", workeruid = "workerC", useruid = "userC", messages = "[]")

    chatDao.insertChat(chat1)
    chatDao.insertChat(chat2)
    chatDao.insertChat(chat3)

    val allChats = chatDao.getAllChats().first()
    assertEquals(3, allChats.size)
    assertTrue(allChats.any { it.chatId == "chat1" })
    assertTrue(allChats.any { it.chatId == "chat2" })
    assertTrue(allChats.any { it.chatId == "chat3" })
  }

  @Test
  fun getChatById_existingChat() = runBlocking {
    val chat =
        ChatEntity(
            chatId = "uniqueChat",
            workeruid = "workerX",
            useruid = "userX",
            messages = "[{\"text\":\"Hello\"}]")
    chatDao.insertChat(chat)

    val retrievedChat = chatDao.getChatById("uniqueChat")
    assertNotNull(retrievedChat)
    assertEquals("uniqueChat", retrievedChat?.chatId)
    assertEquals("workerX", retrievedChat?.workeruid)
    assertEquals("userX", retrievedChat?.useruid)
    assertEquals("[{\"text\":\"Hello\"}]", retrievedChat?.messages)
  }

  @Test
  fun getChatById_nonExistingChat_returnsNull() = runBlocking {
    val retrievedChat = chatDao.getChatById("nonExistentChatId")
    assertNull(retrievedChat)
  }

  @Test
  fun insertChat_replaceExistingChat() = runBlocking {
    val originalChat =
        ChatEntity(
            chatId = "replaceChat",
            workeruid = "workerOriginal",
            useruid = "userOriginal",
            messages = "[{\"msg\":\"orig\"}]")
    chatDao.insertChat(originalChat)

    // Insert another chat with the same chatId but different data
    val updatedChat =
        ChatEntity(
            chatId = "replaceChat",
            workeruid = "workerUpdated",
            useruid = "userUpdated",
            messages = "[{\"msg\":\"updated\"}]")
    chatDao.insertChat(updatedChat)

    // Verify that the chat is replaced
    val retrievedChat = chatDao.getChatById("replaceChat")
    assertNotNull(retrievedChat)
    assertEquals("workerUpdated", retrievedChat?.workeruid)
    assertEquals("userUpdated", retrievedChat?.useruid)
    assertEquals("[{\"msg\":\"updated\"}]", retrievedChat?.messages)
  }

  @Test
  fun deleteChat_existingChat_removesIt() = runBlocking {
    val chatToDelete =
        ChatEntity(
            chatId = "deleteMe",
            workeruid = "delWorker",
            useruid = "delUser",
            messages = "[\"del\"]")
    chatDao.insertChat(chatToDelete)

    // Confirm it's there
    val beforeDelete = chatDao.getChatById("deleteMe")
    assertNotNull(beforeDelete)

    // Delete and verify it's gone
    chatDao.deleteChat("deleteMe")
    val afterDelete = chatDao.getChatById("deleteMe")
    assertNull(afterDelete)
  }

  @Test
  fun deleteChat_doesNotAffectOthers() = runBlocking {
    val chat1 = ChatEntity(chatId = "chatA", workeruid = "A", useruid = "A", messages = "[]")
    val chat2 = ChatEntity(chatId = "chatB", workeruid = "B", useruid = "B", messages = "[]")
    val chat3 = ChatEntity(chatId = "chatC", workeruid = "C", useruid = "C", messages = "[]")

    chatDao.insertChat(chat1)
    chatDao.insertChat(chat2)
    chatDao.insertChat(chat3)

    // Delete chat2
    chatDao.deleteChat("chatB")

    val allChats = chatDao.getAllChats().first()
    assertEquals(2, allChats.size)
    assertNull(chatDao.getChatById("chatB"))
    assertNotNull(chatDao.getChatById("chatA"))
    assertNotNull(chatDao.getChatById("chatC"))
  }
}