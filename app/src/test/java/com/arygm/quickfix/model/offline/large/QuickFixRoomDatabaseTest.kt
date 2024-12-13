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
}
