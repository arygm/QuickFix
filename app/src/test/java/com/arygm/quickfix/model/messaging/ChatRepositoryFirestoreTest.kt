package com.arygm.quickfix.model.messaging

import android.os.Looper
import androidx.test.core.app.ApplicationProvider
import com.google.android.gms.tasks.TaskCompletionSource
import com.google.android.gms.tasks.Tasks
import com.google.firebase.FirebaseApp
import com.google.firebase.Timestamp
import com.google.firebase.firestore.*
import junit.framework.TestCase.fail
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.verify
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf

@RunWith(RobolectricTestRunner::class)
class ChatRepositoryFirestoreTest {

    @Mock
    private lateinit var mockFirestore: FirebaseFirestore

    @Mock
    private lateinit var mockChatsCollection: CollectionReference

    @Mock
    private lateinit var mockChatDocument: DocumentReference

    @Mock
    private lateinit var mockMessagesCollection: CollectionReference

    @Mock
    private lateinit var mockMessageDocument: DocumentReference

    @Mock
    private lateinit var mockQuery: Query

    @Mock
    private lateinit var mockQuerySnapshot: QuerySnapshot

    @Mock
    private lateinit var mockDocumentSnapshot: DocumentSnapshot

    private lateinit var chatRepositoryFirestore: ChatRepositoryFirestore

    private val chat = Chat(
        chatId = "user1worker1",
        useruid = "user1",
        workeruid = "worker1"
    )

    private val chat2 = Chat(
        chatId = "user2worker2",
        useruid = "user2",
        workeruid = "worker2"
    )

    private val message = Message(
        messageId = "message1",
        senderId = "user1",
        content = "Hello",
        timestamp = Timestamp.now()
        // Add other fields as necessary
    )

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)

        if (FirebaseApp.getApps(ApplicationProvider.getApplicationContext()).isEmpty()) {
            FirebaseApp.initializeApp(ApplicationProvider.getApplicationContext())
        }

        chatRepositoryFirestore = ChatRepositoryFirestore(mockFirestore)

        Mockito.`when`(mockFirestore.collection(any())).thenReturn(mockChatsCollection)
        Mockito.`when`(mockChatsCollection.document(any())).thenReturn(mockChatDocument)
        Mockito.`when`(mockChatDocument.collection(any())).thenReturn(mockMessagesCollection)
        Mockito.`when`(mockMessagesCollection.document(any())).thenReturn(mockMessageDocument)
    }

    @After
    fun tearDown() {
        // No specific teardown required
    }

    // ----- Init Method Test -----

    @Test
    fun init_callsOnSuccess() {
        var callbackCalled = false
        chatRepositoryFirestore.init(onSuccess = { callbackCalled = true })
        assertTrue(callbackCalled)
    }

    // ----- Create Chat Tests -----

    @Test
    fun createChat_callsSetOnChatDocument() {
        Mockito.`when`(mockChatDocument.set(any<Chat>())).thenReturn(Tasks.forResult(null))

        chatRepositoryFirestore.createChat(chat, onSuccess = {}, onFailure = { fail("Failure callback should not be called") })

        shadowOf(Looper.getMainLooper()).idle()

        verify(mockChatDocument).set(eq(chat))
    }

    @Test
    fun createChat_onSuccess_callsOnSuccess() {
        val taskCompletionSource = TaskCompletionSource<Void>()
        Mockito.`when`(mockChatDocument.set(any<Chat>())).thenReturn(taskCompletionSource.task)

        var callbackCalled = false

        chatRepositoryFirestore.createChat(
            chat = chat,
            onSuccess = { callbackCalled = true },
            onFailure = { fail("Failure callback should not be called") }
        )

        taskCompletionSource.setResult(null)

        shadowOf(Looper.getMainLooper()).idle()

        assertTrue(callbackCalled)
    }

    @Test
    fun createChat_onFailure_callsOnFailure() {
        val taskCompletionSource = TaskCompletionSource<Void>()
        Mockito.`when`(mockChatDocument.set(any<Chat>())).thenReturn(taskCompletionSource.task)

        val exception = Exception("Test exception")
        var callbackCalled = false
        var returnedException: Exception? = null

        chatRepositoryFirestore.createChat(
            chat = chat,
            onSuccess = { fail("Success callback should not be called") },
            onFailure = { e ->
                callbackCalled = true
                returnedException = e
            }
        )

        taskCompletionSource.setException(exception)

        shadowOf(Looper.getMainLooper()).idle()

        assertTrue(callbackCalled)
        assertEquals(exception, returnedException)
    }

    // ----- Delete Chat Tests -----

    @Test
    fun deleteChat_callsDeleteOnChatDocument() {
        Mockito.`when`(mockChatDocument.delete()).thenReturn(Tasks.forResult(null))

        chatRepositoryFirestore.deleteChat(chat, onSuccess = {}, onFailure = { fail("Failure callback should not be called") })

        shadowOf(Looper.getMainLooper()).idle()

        verify(mockChatDocument).delete()
    }

    @Test
    fun deleteChat_onSuccess_callsOnSuccess() {
        val taskCompletionSource = TaskCompletionSource<Void>()
        Mockito.`when`(mockChatDocument.delete()).thenReturn(taskCompletionSource.task)

        var callbackCalled = false

        chatRepositoryFirestore.deleteChat(
            chat = chat,
            onSuccess = { callbackCalled = true },
            onFailure = { fail("Failure callback should not be called") }
        )

        taskCompletionSource.setResult(null)

        shadowOf(Looper.getMainLooper()).idle()

        assertTrue(callbackCalled)
    }

    @Test
    fun deleteChat_onFailure_callsOnFailure() {
        val taskCompletionSource = TaskCompletionSource<Void>()
        Mockito.`when`(mockChatDocument.delete()).thenReturn(taskCompletionSource.task)

        val exception = Exception("Test exception")
        var callbackCalled = false
        var returnedException: Exception? = null

        chatRepositoryFirestore.deleteChat(
            chat = chat,
            onSuccess = { fail("Success callback should not be called") },
            onFailure = { e ->
                callbackCalled = true
                returnedException = e
            }
        )

        taskCompletionSource.setException(exception)

        shadowOf(Looper.getMainLooper()).idle()

        assertTrue(callbackCalled)
        assertEquals(exception, returnedException)
    }

    // ----- Get Chats Tests -----

    @Test
    fun getChats_onSuccess_callsOnSuccessWithChats() {
        val taskCompletionSource = TaskCompletionSource<QuerySnapshot>()
        Mockito.`when`(mockChatsCollection.get()).thenReturn(taskCompletionSource.task)

        val document1 = Mockito.mock(DocumentSnapshot::class.java)
        val document2 = Mockito.mock(DocumentSnapshot::class.java)

        Mockito.`when`(document1.toObject(Chat::class.java)).thenReturn(chat)
        Mockito.`when`(document2.toObject(Chat::class.java)).thenReturn(chat2)
        Mockito.`when`(mockQuerySnapshot.documents).thenReturn(listOf(document1, document2))

        var callbackCalled = false
        var returnedChats: List<Chat>? = null

        chatRepositoryFirestore.getChats(
            onSuccess = { chats ->
                callbackCalled = true
                returnedChats = chats
            },
            onFailure = { fail("Failure callback should not be called") }
        )

        taskCompletionSource.setResult(mockQuerySnapshot)

        shadowOf(Looper.getMainLooper()).idle()

        assertTrue(callbackCalled)
        assertNotNull(returnedChats)
    }

    @Test
    fun getChats_onFailure_callsOnFailure() {
        val taskCompletionSource = TaskCompletionSource<QuerySnapshot>()
        Mockito.`when`(mockChatsCollection.get()).thenReturn(taskCompletionSource.task)

        val exception = Exception("Test exception")

        var callbackCalled = false
        var returnedException: Exception? = null

        chatRepositoryFirestore.getChats(
            onSuccess = { fail("Success callback should not be called") },
            onFailure = { e ->
                callbackCalled = true
                returnedException = e
            }
        )

        taskCompletionSource.setException(exception)

        shadowOf(Looper.getMainLooper()).idle()

        assertTrue(callbackCalled)
        assertEquals(exception, returnedException)
    }

    // ----- Chat Exists Tests -----

    @Test
    fun chatExists_whenChatExists_callsOnSuccessWithTrueAndChat() {
        val userId = "user1"
        val workerId = "worker1"

        Mockito.`when`(mockChatsCollection.whereEqualTo(eq("chatId"), eq(userId + workerId))).thenReturn(mockQuery)
        Mockito.`when`(mockQuery.get()).thenReturn(Tasks.forResult(mockQuerySnapshot))
        Mockito.`when`(mockQuerySnapshot.size()).thenReturn(1)
        Mockito.`when`(mockQuerySnapshot.toObjects(Chat::class.java)).thenReturn(listOf(chat))

        var callbackCalled = false

        chatRepositoryFirestore.chatExists(
            userId = userId,
            workerId = workerId,
            onSuccess = { (exists, foundChat) ->
                callbackCalled = true
                assertTrue(exists)
                assertEquals(chat, foundChat)
            },
            onFailure = { fail("Failure callback should not be called") }
        )

        shadowOf(Looper.getMainLooper()).idle()

        assertTrue(callbackCalled)
    }

    @Test
    fun chatExists_whenChatDoesNotExist_callsOnSuccessWithFalseAndNull() {
        val userId = "user1"
        val workerId = "worker1"

        Mockito.`when`(mockChatsCollection.whereEqualTo(eq("chatId"), eq(userId + workerId))).thenReturn(mockQuery)
        Mockito.`when`(mockQuery.get()).thenReturn(Tasks.forResult(mockQuerySnapshot))
        Mockito.`when`(mockQuerySnapshot.size()).thenReturn(0)
        Mockito.`when`(mockQuerySnapshot.toObjects(Chat::class.java)).thenReturn(emptyList())

        var callbackCalled = false

        chatRepositoryFirestore.chatExists(
            userId = userId,
            workerId = workerId,
            onSuccess = { (exists, foundChat) ->
                callbackCalled = true
                assertFalse(exists)
                assertNull(foundChat)
            },
            onFailure = { fail("Failure callback should not be called") }
        )

        shadowOf(Looper.getMainLooper()).idle()

        assertTrue(callbackCalled)
    }

    @Test
    fun chatExists_onFailure_callsOnFailure() {
        val userId = "user1"
        val workerId = "worker1"
        val exception = Exception("Test exception")

        Mockito.`when`(mockChatsCollection.whereEqualTo(eq("chatId"), eq(userId + workerId))).thenReturn(mockQuery)
        Mockito.`when`(mockQuery.get()).thenReturn(Tasks.forException(exception))

        var callbackCalled = false
        var returnedException: Exception? = null

        chatRepositoryFirestore.chatExists(
            userId = userId,
            workerId = workerId,
            onSuccess = { fail("Success callback should not be called") },
            onFailure = { e ->
                callbackCalled = true
                returnedException = e
            }
        )

        shadowOf(Looper.getMainLooper()).idle()

        assertTrue(callbackCalled)
        assertEquals(exception, returnedException)
    }

    // ----- Send Message Tests -----

    @Test
    fun sendMessage_callsSetOnMessageDocument() {
        Mockito.`when`(mockMessageDocument.set(any<Message>())).thenReturn(Tasks.forResult(null))

        chatRepositoryFirestore.sendMessage(
            chat = chat,
            message = message,
            onSuccess = {},
            onFailure = { fail("Failure callback should not be called") }
        )

        shadowOf(Looper.getMainLooper()).idle()

        verify(mockMessageDocument).set(eq(message))
    }

    @Test
    fun sendMessage_onSuccess_callsOnSuccess() {
        val taskCompletionSource = TaskCompletionSource<Void>()
        Mockito.`when`(mockMessageDocument.set(any<Message>())).thenReturn(taskCompletionSource.task)

        var callbackCalled = false

        chatRepositoryFirestore.sendMessage(
            chat = chat,
            message = message,
            onSuccess = { callbackCalled = true },
            onFailure = { fail("Failure callback should not be called") }
        )

        taskCompletionSource.setResult(null)

        shadowOf(Looper.getMainLooper()).idle()

        assertTrue(callbackCalled)
    }

    @Test
    fun sendMessage_onFailure_callsOnFailure() {
        val taskCompletionSource = TaskCompletionSource<Void>()
        Mockito.`when`(mockMessageDocument.set(any<Message>())).thenReturn(taskCompletionSource.task)

        val exception = Exception("Test exception")
        var callbackCalled = false
        var returnedException: Exception? = null

        chatRepositoryFirestore.sendMessage(
            chat = chat,
            message = message,
            onSuccess = { fail("Success callback should not be called") },
            onFailure = { e ->
                callbackCalled = true
                returnedException = e
            }
        )

        taskCompletionSource.setException(exception)

        shadowOf(Looper.getMainLooper()).idle()

        assertTrue(callbackCalled)
        assertEquals(exception, returnedException)
    }

    // ----- Delete Message Tests -----

    @Test
    fun deleteMessage_callsDeleteOnMessageDocument() {
        Mockito.`when`(mockMessageDocument.delete()).thenReturn(Tasks.forResult(null))

        chatRepositoryFirestore.deleteMessage(
            chat = chat,
            message = message,
            onSuccess = {},
            onFailure = { fail("Failure callback should not be called") }
        )

        shadowOf(Looper.getMainLooper()).idle()

        verify(mockMessageDocument).delete()
    }

    @Test
    fun deleteMessage_onSuccess_callsOnSuccess() {
        val taskCompletionSource = TaskCompletionSource<Void>()
        Mockito.`when`(mockMessageDocument.delete()).thenReturn(taskCompletionSource.task)

        var callbackCalled = false

        chatRepositoryFirestore.deleteMessage(
            chat = chat,
            message = message,
            onSuccess = { callbackCalled = true },
            onFailure = { fail("Failure callback should not be called") }
        )

        taskCompletionSource.setResult(null)

        shadowOf(Looper.getMainLooper()).idle()

        assertTrue(callbackCalled)
    }

    @Test
    fun deleteMessage_onFailure_callsOnFailure() {
        val taskCompletionSource = TaskCompletionSource<Void>()
        Mockito.`when`(mockMessageDocument.delete()).thenReturn(taskCompletionSource.task)

        val exception = Exception("Test exception")
        var callbackCalled = false
        var returnedException: Exception? = null

        chatRepositoryFirestore.deleteMessage(
            chat = chat,
            message = message,
            onSuccess = { fail("Success callback should not be called") },
            onFailure = { e ->
                callbackCalled = true
                returnedException = e
            }
        )

        taskCompletionSource.setException(exception)

        shadowOf(Looper.getMainLooper()).idle()

        assertTrue(callbackCalled)
        assertEquals(exception, returnedException)
    }
}
