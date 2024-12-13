package com.arygm.quickfix.model.messaging

import androidx.test.core.app.ApplicationProvider
import com.arygm.quickfix.model.offline.large.messaging.ChatDao
import com.arygm.quickfix.model.offline.large.messaging.ChatEntity
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.*
import java.util.UUID
import junit.framework.TestCase.fail
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.*
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ChatRepositoryFirestoreTest {

  @Mock private lateinit var mockFirestore: FirebaseFirestore
  @Mock private lateinit var mockChatsCollection: CollectionReference
  @Mock private lateinit var mockChatDocument: DocumentReference
  @Mock private lateinit var mockDao: ChatDao
  @Mock private lateinit var mockQuerySnapshot: QuerySnapshot

  @OptIn(ExperimentalCoroutinesApi::class) private val testDispatcher = UnconfinedTestDispatcher()

  private lateinit var chatRepositoryFirestore: ChatRepositoryFirestore

  private val chat =
      Chat(
          chatId = "user1worker1",
          useruid = "user1",
          workeruid = "worker1",
          quickFixUid = "someQuickFixUid",
          messages = emptyList(),
          chatStatus = ChatStatus.WAITING_FOR_RESPONSE)

  private val chatEntity =
      ChatEntity(chatId = "user1worker1", useruid = "user1", workeruid = "worker1", messages = "[]")

  @Before
  fun setUp() {
    MockitoAnnotations.openMocks(this)

    if (FirebaseApp.getApps(ApplicationProvider.getApplicationContext()).isEmpty()) {
      FirebaseApp.initializeApp(ApplicationProvider.getApplicationContext())
    }

    chatRepositoryFirestore = ChatRepositoryFirestore(mockDao, mockFirestore, testDispatcher)

    // Mock Firestore collection and document references
    whenever(mockFirestore.collection(any())).thenReturn(mockChatsCollection)
    whenever(mockChatsCollection.document(any())).thenReturn(mockChatDocument)

    // Mock Task<QuerySnapshot> for chats.get()
    @Suppress("UNCHECKED_CAST")
    val mockTask: Task<QuerySnapshot> = Mockito.mock(Task::class.java) as Task<QuerySnapshot>
    whenever(mockChatsCollection.get()).thenReturn(mockTask)

    // Mock Task success with a QuerySnapshot
    whenever(mockTask.addOnSuccessListener(any())).thenAnswer { invocation ->
      val listener = invocation.arguments[0] as OnSuccessListener<QuerySnapshot>
      listener.onSuccess(mockQuerySnapshot)
      mockTask
    }

    // No forced failure scenario here
    whenever(mockTask.addOnFailureListener(any())).thenReturn(mockTask)
  }

  @After
  fun tearDown() {
    // No specific teardown required
  }

  @Test
  fun test_init_callsOnSuccessImmediately() {
    var successCalled = false
    chatRepositoryFirestore.init(onSuccess = { successCalled = true })
    assertTrue(successCalled)
  }

  @Test
  fun test_getRandomUid_generatesUniqueId() {
    val randomDocRef = Mockito.mock(DocumentReference::class.java)
    val generatedId = UUID.randomUUID().toString()
    whenever(mockChatsCollection.document()).thenReturn(randomDocRef)
    whenever(randomDocRef.id).thenReturn(generatedId)

    val uid = chatRepositoryFirestore.getRandomUid()
    assertEquals(generatedId, uid)
  }

  @Test
  fun test_getChats_returnsCachedData_whenAvailable() {
    runBlocking {
      // Mock ChatDao returning cached chats
      whenever(mockDao.getAllChats()).thenReturn(flowOf(listOf(chatEntity)))

      var returnedChats: List<Chat>? = null

      chatRepositoryFirestore.getChats(
          onSuccess = { chats -> returnedChats = chats },
          onFailure = { fail("Failure callback should not be called") })

      // Verify cached chats are returned
      assertNotNull(returnedChats)
      assertEquals(1, returnedChats?.size)
      assertEquals(chat.chatId, returnedChats?.first()?.chatId)

      // Verify Firestore was NOT called
      verify(mockChatsCollection, times(0)).get()
    }
  }

  @Test
  fun test_getChats_fallsBackToFirestore_whenCacheIsEmpty() {
    runBlocking {
      // Mock ChatDao returning empty cache
      whenever(mockDao.getAllChats()).thenReturn(flowOf(emptyList()))

      // Mock the DocumentSnapshot so that documentToChat(document) will succeed
      val document1 = Mockito.mock(DocumentSnapshot::class.java)
      whenever(document1.getString("chatId")).thenReturn("user1worker1")
      whenever(document1.getString("workeruid")).thenReturn("worker1")
      whenever(document1.getString("useruid")).thenReturn("user1")
      whenever(document1.getString("quickFixUid")).thenReturn("someQuickFixUid")
      whenever(document1.getString("chatStatus")).thenReturn("WAITING_FOR_RESPONSE")
      whenever(document1.get("messages")).thenReturn(emptyList<Map<String, Any>>())

      whenever(mockQuerySnapshot.documents).thenReturn(listOf(document1))

      var returnedChats: List<Chat>? = null

      chatRepositoryFirestore.getChats(
          onSuccess = { chats -> returnedChats = chats },
          onFailure = { fail("Failure callback should not be called") })

      // Verify Firestore was called
      verify(mockChatsCollection).get()

      // Verify chats were cached in Room
      verify(mockDao).insertChat(chat.toChatEntity())

      // Verify returned chats
      assertNotNull(returnedChats)
      assertEquals(1, returnedChats?.size)
      assertEquals(chat.chatId, returnedChats?.first()?.chatId)
    }
  }

  @Test
  fun test_createChat_cachesChatInRoom_beforeCallingFirestore() {
    runBlocking {
      // Mock Firestore success
      whenever(mockChatDocument.set(any<Chat>())).thenReturn(Tasks.forResult(null))

      chatRepositoryFirestore.createChat(
          chat = chat,
          onSuccess = {},
          onFailure = { fail("Failure callback should not be called") })

      // Verify chat was cached in Room
      verify(mockDao).insertChat(chat.toChatEntity())

      // Verify Firestore was called
      verify(mockChatDocument).set(eq(chat))
    }
  }

  @Test
  fun test_deleteChat_removesFromRoomAndFirestore() {
    runBlocking {
      // Mock DAO and Firestore operations
      whenever(mockDao.deleteChat(chat.chatId)).thenReturn(Unit)
      val deleteTask: Task<Void> = Tasks.forResult(null)
      whenever(mockChatDocument.delete()).thenReturn(deleteTask)

      var successCalled = false
      chatRepositoryFirestore.deleteChat(
          chat = chat,
          onSuccess = { successCalled = true },
          onFailure = { fail("Should not fail") })

      // Verify local deletion
      verify(mockDao).deleteChat(chat.chatId)
      // Verify Firestore deletion
      verify(mockChatDocument).delete()
      assertTrue(successCalled)
    }
  }

  @Test
  fun test_chatExists_returnsTrueIfLocalChatExists() {
    runBlocking {
      // Mock DAO to return the chat locally
      whenever(mockDao.getChatById(chat.chatId)).thenReturn(chatEntity)

      var result: Pair<Boolean, Chat?>? = null
      chatRepositoryFirestore.chatExists(
          userId = "user1",
          workerId = "worker1",
          onSuccess = { result = it },
          onFailure = { fail("Should not fail") })

      assertNotNull(result)
      assertTrue(result!!.first)
      assertEquals(chat.chatId, result!!.second?.chatId)

      // Verify Firestore not called since local chat found
      verify(mockChatsCollection, times(0)).whereEqualTo("chatId", chat.chatId)
    }
  }

  @Test
  fun test_chatExists_fallsBackToFirestoreIfLocalNotFound() {
    runBlocking {
      // No local chat
      whenever(mockDao.getChatById(chat.chatId)).thenReturn(null)

      // Mock Firestore response
      val mockQuery = Mockito.mock(Query::class.java)
      whenever(mockChatsCollection.whereEqualTo("chatId", chat.chatId)).thenReturn(mockQuery)

      @Suppress("UNCHECKED_CAST")
      val mockTask: Task<QuerySnapshot> = Mockito.mock(Task::class.java) as Task<QuerySnapshot>
      whenever(mockQuery.get()).thenReturn(mockTask)

      whenever(mockTask.addOnSuccessListener(any())).thenAnswer { invocation ->
        val listener = invocation.arguments[0] as OnSuccessListener<QuerySnapshot>
        // Simulate Firestore returning the chat
        whenever(mockQuerySnapshot.toObjects(Chat::class.java)).thenReturn(listOf(chat))
        listener.onSuccess(mockQuerySnapshot)
        mockTask
      }

      whenever(mockTask.addOnFailureListener(any())).thenReturn(mockTask)

      var result: Pair<Boolean, Chat?>? = null
      chatRepositoryFirestore.chatExists(
          userId = "user1",
          workerId = "worker1",
          onSuccess = { result = it },
          onFailure = { fail("Should not fail") })

      assertNotNull(result)
      assertTrue(result!!.first)
      assertEquals(chat.chatId, result!!.second?.chatId)
    }
  }

  @Test
  fun test_sendMessage_updatesRoomAndFirestore() {
    runBlocking {
      // Mock DAO insert success
      whenever(mockDao.insertChat(any())).thenReturn(Unit)

      // Mock Firestore update success
      val updateTask: Task<Void> = Tasks.forResult(null)
      whenever(mockChatDocument.update(eq("messages"), any())).thenReturn(updateTask)

      val message =
          Message(
              messageId = "msg1",
              senderId = "user1",
              content = "Hello",
              timestamp = com.google.firebase.Timestamp.now())

      var successCalled = false
      chatRepositoryFirestore.sendMessage(
          chat = chat,
          message = message,
          onSuccess = { successCalled = true },
          onFailure = { fail("Should not fail") })

      // Verify DAO insert with the updated chat
      argumentCaptor<ChatEntity>().apply {
        verify(mockDao).insertChat(capture())
        assertTrue(firstValue.messages.contains("\"messageId\":\"msg1\""))
      }

      // Verify Firestore update
      verify(mockChatDocument).update(eq("messages"), any())
    }
  }

  @Test
  fun test_deleteMessage_updatesRoomAndFirestore() {
    runBlocking {
      // Mock a chat with a message
      val message =
          Message(
              messageId = "msgToDelete",
              senderId = "user1",
              content = "This will be deleted",
              timestamp = com.google.firebase.Timestamp.now())
      val chatWithMessage = chat.copy(messages = listOf(message))
      whenever(mockDao.insertChat(any())).thenReturn(Unit)

      // Mock Firestore deletion success
      val messageDoc = Mockito.mock(DocumentReference::class.java)
      val messagesCollection = Mockito.mock(CollectionReference::class.java)
      whenever(mockChatDocument.collection("messages")).thenReturn(messagesCollection)
      whenever(messagesCollection.document(message.messageId)).thenReturn(messageDoc)
      whenever(messageDoc.delete()).thenReturn(Tasks.forResult(null))

      var successCalled = false
      chatRepositoryFirestore.deleteMessage(
          chat = chatWithMessage,
          message = message,
          onSuccess = { successCalled = true },
          onFailure = { fail("Should not fail") })

      // Verify DAO updated chat no longer has the message
      argumentCaptor<ChatEntity>().apply {
        verify(mockDao).insertChat(capture())
        assertFalse(firstValue.messages.contains("\"msgToDelete\""))
      }

      // Verify Firestore message deletion
      verify(messageDoc).delete()
      assertTrue(successCalled)
    }
  }

  @Test
  fun test_updateChat_updatesRoomAndFirestore() {
    runBlocking {
      // Mock DAO insert success
      whenever(mockDao.insertChat(any())).thenReturn(Unit)
      // Mock Firestore set success
      whenever(mockChatDocument.set(any<Chat>())).thenReturn(Tasks.forResult(null))

      var successCalled = false
      chatRepositoryFirestore.updateChat(
          chat = chat,
          onSuccess = { successCalled = true },
          onFailure = { fail("Should not fail") })

      // Verify DAO was updated
      verify(mockDao).insertChat(chat.toChatEntity())

      // Verify Firestore set
      verify(mockChatDocument).set(eq(chat))
      assertTrue(successCalled)
    }
  }

  @Test
  fun test_createChat_failure_inRoom() {
    runBlocking {
      // Simulate DAO failure
      whenever(mockDao.insertChat(any())).thenThrow(RuntimeException("Room insert failed"))

      var successCalled = false
      var failureCalled: Exception? = null

      chatRepositoryFirestore.createChat(
          chat = chat, onSuccess = { successCalled = true }, onFailure = { failureCalled = it })

      assertFalse(successCalled)
      assertNotNull(failureCalled)
      assertEquals("Room insert failed", failureCalled?.message)

      // Firestore should NOT have been called because Room failed first
      verify(mockChatDocument, times(0)).set(any())
    }
  }

  @Test
  fun test_getChats_failure_fromFirestore() {
    runBlocking {
      // Empty local cache
      whenever(mockDao.getAllChats()).thenReturn(flowOf(emptyList()))

      // Firestore returns failure
      val failureException = RuntimeException("Firestore get failed")
      val mockGetTask: Task<QuerySnapshot> = Mockito.mock(Task::class.java) as Task<QuerySnapshot>
      whenever(mockChatsCollection.get()).thenReturn(mockGetTask)
      whenever(mockGetTask.addOnSuccessListener(any())).thenReturn(mockGetTask)
      whenever(mockGetTask.addOnFailureListener(any())).thenAnswer { invocation ->
        val listener = invocation.arguments[0] as OnFailureListener
        listener.onFailure(failureException)
        mockGetTask
      }

      var returnedChats: List<Chat>? = null
      var failureCalled: Exception? = null

      chatRepositoryFirestore.getChats(
          onSuccess = { returnedChats = it }, onFailure = { failureCalled = it })

      assertNull(returnedChats)
      assertNotNull(failureCalled)
      assertEquals("Firestore get failed", failureCalled?.message)
    }
  }

  @Test
  fun test_deleteChat_failure_inRoom() {
    runBlocking {
      // Simulate DAO failure
      whenever(mockDao.deleteChat(chat.chatId)).thenThrow(RuntimeException("Room delete failed"))

      var successCalled = false
      var failureCalled: Exception? = null

      chatRepositoryFirestore.deleteChat(
          chat = chat, onSuccess = { successCalled = true }, onFailure = { failureCalled = it })

      assertFalse(successCalled)
      assertNotNull(failureCalled)
      assertEquals("Room delete failed", failureCalled?.message)

      // Firestore should NOT have been called since Room failed first
      verify(mockChatDocument, times(0)).delete()
    }
  }

  @Test
  fun test_chatExists_failure_inFirestore() {
    runBlocking {
      // No local chat
      whenever(mockDao.getChatById(chat.chatId)).thenReturn(null)

      // Firestore query fails
      val failureException = RuntimeException("Firestore query failed")
      val mockQuery = Mockito.mock(Query::class.java)
      whenever(mockChatsCollection.whereEqualTo("chatId", chat.chatId)).thenReturn(mockQuery)

      @Suppress("UNCHECKED_CAST")
      val mockTask: Task<QuerySnapshot> = Mockito.mock(Task::class.java) as Task<QuerySnapshot>
      whenever(mockQuery.get()).thenReturn(mockTask)
      whenever(mockTask.addOnSuccessListener(any())).thenReturn(mockTask)
      whenever(mockTask.addOnFailureListener(any())).thenAnswer { invocation ->
        val listener = invocation.arguments[0] as OnFailureListener
        listener.onFailure(failureException)
        mockTask
      }

      var result: Pair<Boolean, Chat?>? = null
      var failureCalled: Exception? = null

      chatRepositoryFirestore.chatExists(
          userId = "user1",
          workerId = "worker1",
          onSuccess = { result = it },
          onFailure = { failureCalled = it })

      assertNull(result)
      assertNotNull(failureCalled)
      assertEquals("Firestore query failed", failureCalled?.message)
    }
  }

  @Test
  fun test_sendMessage_failure_inRoom() {
    runBlocking {
      // Simulate DAO insert failure
      whenever(mockDao.insertChat(any())).thenThrow(RuntimeException("Room insert failed"))

      val message = Message("msg1", "user1", "Hello", com.google.firebase.Timestamp.now())

      var successCalled = false
      var failureCalled: Exception? = null

      chatRepositoryFirestore.sendMessage(
          chat = chat,
          message = message,
          onSuccess = { successCalled = true },
          onFailure = { failureCalled = it })

      assertFalse(successCalled)
      assertNotNull(failureCalled)
      assertEquals("Room insert failed", failureCalled?.message)

      // Firestore should NOT be updated since Room failed first
      verify(mockChatDocument, times(0)).update(eq("messages"), any())
    }
  }

  @Test
  fun test_sendMessage_failure_inFirestore() {
    runBlocking {
      // DAO insert succeeds
      whenever(mockDao.insertChat(any())).thenReturn(Unit)

      val failureException = RuntimeException("Firestore update failed")
      val message = Message("msg1", "user1", "Hello", com.google.firebase.Timestamp.now())
      val mockUpdateTask: Task<Void> = Mockito.mock(Task::class.java) as Task<Void>
      whenever(mockChatDocument.update(eq("messages"), any())).thenReturn(mockUpdateTask)
      whenever(mockUpdateTask.addOnSuccessListener(any())).thenReturn(mockUpdateTask)
      whenever(mockUpdateTask.addOnFailureListener(any())).thenAnswer { invocation ->
        val listener = invocation.arguments[0] as OnFailureListener
        listener.onFailure(failureException)
        mockUpdateTask
      }

      var successCalled = false
      var failureCalled: Exception? = null

      chatRepositoryFirestore.sendMessage(
          chat = chat,
          message = message,
          onSuccess = { successCalled = true },
          onFailure = { failureCalled = it })

      assertFalse(successCalled)
      assertNotNull(failureCalled)
      assertEquals("Firestore update failed", failureCalled?.message)
    }
  }

  @Test
  fun test_deleteMessage_failure_inRoom() {
    runBlocking {
      val message =
          Message("msgToDelete", "user1", "To be deleted", com.google.firebase.Timestamp.now())
      val chatWithMessage = chat.copy(messages = listOf(message))

      // Simulate DAO failure
      whenever(mockDao.insertChat(any())).thenThrow(RuntimeException("Room insert failed"))

      var successCalled = false
      var failureCalled: Exception? = null

      chatRepositoryFirestore.deleteMessage(
          chat = chatWithMessage,
          message = message,
          onSuccess = { successCalled = true },
          onFailure = { failureCalled = it })

      assertFalse(successCalled)
      assertNotNull(failureCalled)
      assertEquals("Room insert failed", failureCalled?.message)
    }
  }

  @Test
  fun test_updateChat_failure_inRoom() {
    runBlocking {
      // Simulate DAO failure
      whenever(mockDao.insertChat(any())).thenThrow(RuntimeException("Room insert failed"))

      var successCalled = false
      var failureCalled: Exception? = null

      chatRepositoryFirestore.updateChat(
          chat = chat, onSuccess = { successCalled = true }, onFailure = { failureCalled = it })

      assertFalse(successCalled)
      assertNotNull(failureCalled)
      assertEquals("Room insert failed", failureCalled?.message)

      // Firestore should NOT be called since Room failed first
      verify(mockChatDocument, times(0)).set(any())
    }
  }
}
