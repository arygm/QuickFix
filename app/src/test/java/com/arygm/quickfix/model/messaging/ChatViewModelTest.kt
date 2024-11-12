package com.arygm.quickfix.model.messaging

import com.google.firebase.Timestamp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.eq

@ExperimentalCoroutinesApi
class ChatViewModelTest {

    @Mock
    private lateinit var mockRepository: ChatRepository

    private lateinit var chatViewModel: ChatViewModel

    private val testDispatcher = UnconfinedTestDispatcher()

    private val chat = Chat(
        chatId = "chat1",
        useruid = "user1",
        workeruid = "worker1"
    )

    private val chat2 = Chat(
        chatId = "chat2",
        useruid = "user2",
        workeruid = "worker2"
    )

    private val message = Message(
        messageId = "message1",
        senderId = "user1",
        content = "Hello",
        timestamp = Timestamp.now()
    )

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
        chatViewModel = ChatViewModel(mockRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // ----- getChats Tests -----

    @Test
    fun getChats_whenSuccess_updatesChats() = runTest {
        val chatsList = listOf(chat, chat2)

        doAnswer { invocation ->
            val onSuccess = invocation.getArgument<(List<Chat>) -> Unit>(0)
            onSuccess(chatsList)
            null
        }.`when`(mockRepository).getChats(any(), any())

        chatViewModel.getChats()

        testScheduler.advanceUntilIdle()

        assertEquals(chatsList, chatViewModel.chats.value)
        verify(mockRepository).getChats(any(), any())
    }

    @Test
    fun getChats_whenFailure_logsError() = runTest {
        val exception = Exception("Test exception")

        doAnswer { invocation ->
            val onFailure = invocation.getArgument<(Exception) -> Unit>(1)
            onFailure(exception)
            null
        }.`when`(mockRepository).getChats(any(), any())

        chatViewModel.getChats()

        testScheduler.advanceUntilIdle()

        assertEquals(emptyList<Chat>(), chatViewModel.chats.value)
        verify(mockRepository).getChats(any(), any())
    }

    // ----- addChat Tests -----

    @Test
    fun addChat_whenSuccess_updatesChats() = runTest {
        var onSuccessCalled = false

        doAnswer { invocation ->
            val onSuccess = invocation.getArgument<() -> Unit>(1)
            onSuccess()
            null
        }.`when`(mockRepository).createChat(eq(chat), any(), any())

        doAnswer { invocation ->
            val onSuccess = invocation.getArgument<(List<Chat>) -> Unit>(0)
            onSuccess(listOf(chat))
            null
        }.`when`(mockRepository).getChats(any(), any())

        chatViewModel.addChat(chat, onSuccess = { onSuccessCalled = true }, onFailure = {})

        testScheduler.advanceUntilIdle()

        assertTrue(onSuccessCalled)
        assertEquals(listOf(chat), chatViewModel.chats.value)
        verify(mockRepository).createChat(eq(chat), any(), any())
        verify(mockRepository).getChats(any(), any())
    }

    @Test
    fun addChat_whenFailure_callsOnFailure() = runTest {
        val exception = Exception("Test exception")
        var onFailureCalled = false

        doAnswer { invocation ->
            val onFailure = invocation.getArgument<(Exception) -> Unit>(2)
            onFailure(exception)
            null
        }.`when`(mockRepository).createChat(eq(chat), any(), any())

        chatViewModel.addChat(chat, onSuccess = {}, onFailure = {
            onFailureCalled = true
            assertEquals(exception, it)
        })

        testScheduler.advanceUntilIdle()

        assertTrue(onFailureCalled)
        verify(mockRepository).createChat(eq(chat), any(), any())
        verify(mockRepository, never()).getChats(any(), any())
    }

    // ----- deleteChat Tests -----

    @Test
    fun deleteChat_whenSuccess_updatesChats() = runTest {
        doAnswer { invocation ->
            val onSuccess = invocation.getArgument<() -> Unit>(1)
            onSuccess()
            null
        }.`when`(mockRepository).deleteChat(eq(chat), any(), any())

        doAnswer { invocation ->
            val onSuccess = invocation.getArgument<(List<Chat>) -> Unit>(0)
            onSuccess(emptyList())
            null
        }.`when`(mockRepository).getChats(any(), any())

        chatViewModel.deleteChat(chat)

        testScheduler.advanceUntilIdle()

        assertEquals(emptyList<Chat>(), chatViewModel.chats.value)
        verify(mockRepository).deleteChat(eq(chat), any(), any())
        verify(mockRepository).getChats(any(), any())
    }

    @Test
    fun deleteChat_whenFailure_logsError() = runTest {
        val exception = Exception("Test exception")

        doAnswer { invocation ->
            val onFailure = invocation.getArgument<(Exception) -> Unit>(2)
            onFailure(exception)
            null
        }.`when`(mockRepository).deleteChat(eq(chat), any(), any())

        chatViewModel.deleteChat(chat)

        testScheduler.advanceUntilIdle()

        verify(mockRepository).deleteChat(eq(chat), any(), any())
        verify(mockRepository, never()).getChats(any(), any())
    }

    // ----- chatExists Tests -----

    @Test
    fun chatExists_whenChatExists_callsOnResultWithTrue() = runTest {
        val userId = "user1"
        val workerId = "worker1"
        var onResultCalled = false

        doAnswer { invocation ->
            val onSuccess = invocation.getArgument<(Pair<Boolean, Chat?>) -> Unit>(2)
            onSuccess(Pair(true, chat))
            null
        }.`when`(mockRepository).chatExists(eq(userId), eq(workerId), any(), any())

        chatViewModel.chatExists(userId, workerId) { exists, returnedChat ->
            onResultCalled = true
            assertTrue(exists)
            assertEquals(chat, returnedChat)
        }

        testScheduler.advanceUntilIdle()

        assertTrue(onResultCalled)
        verify(mockRepository).chatExists(eq(userId), eq(workerId), any(), any())
    }

    @Test
    fun chatExists_whenChatDoesNotExist_callsOnResultWithFalse() = runTest {
        val userId = "user1"
        val workerId = "worker1"
        var onResultCalled = false

        doAnswer { invocation ->
            val onSuccess = invocation.getArgument<(Pair<Boolean, Chat?>) -> Unit>(2)
            onSuccess(Pair(false, null))
            null
        }.`when`(mockRepository).chatExists(eq(userId), eq(workerId), any(), any())

        chatViewModel.chatExists(userId, workerId) { exists, returnedChat ->
            onResultCalled = true
            assertFalse(exists)
            assertNull(returnedChat)
        }

        testScheduler.advanceUntilIdle()

        assertTrue(onResultCalled)
        verify(mockRepository).chatExists(eq(userId), eq(workerId), any(), any())
    }

    @Test
    fun chatExists_whenFailure_callsOnResultWithFalse() = runTest {
        val userId = "user1"
        val workerId = "worker1"
        val exception = Exception("Test exception")
        var onResultCalled = false

        doAnswer { invocation ->
            val onFailure = invocation.getArgument<(Exception) -> Unit>(3)
            onFailure(exception)
            null
        }.`when`(mockRepository).chatExists(eq(userId), eq(workerId), any(), any())

        chatViewModel.chatExists(userId, workerId) { exists, returnedChat ->
            onResultCalled = true
            assertFalse(exists)
            assertNull(returnedChat)
        }

        testScheduler.advanceUntilIdle()

        assertTrue(onResultCalled)
        verify(mockRepository).chatExists(eq(userId), eq(workerId), any(), any())
    }

    // ----- sendMessage Tests -----

    @Test
    fun sendMessage_whenSuccess_updatesChats() = runTest {
        doAnswer { invocation ->
            val onSuccess = invocation.getArgument<() -> Unit>(2)
            onSuccess()
            null
        }.`when`(mockRepository).sendMessage(eq(chat), eq(message), any(), any())

        doAnswer { invocation ->
            val onSuccess = invocation.getArgument<(List<Chat>) -> Unit>(0)
            onSuccess(listOf(chat))
            null
        }.`when`(mockRepository).getChats(any(), any())

        chatViewModel.sendMessage(chat, message)

        testScheduler.advanceUntilIdle()

        assertEquals(listOf(chat), chatViewModel.chats.value)
        verify(mockRepository).sendMessage(eq(chat), eq(message), any(), any())
        verify(mockRepository).getChats(any(), any())
    }

    @Test
    fun sendMessage_whenFailure_logsError() = runTest {
        val exception = Exception("Test exception")

        doAnswer { invocation ->
            val onFailure = invocation.getArgument<(Exception) -> Unit>(3)
            onFailure(exception)
            null
        }.`when`(mockRepository).sendMessage(eq(chat), eq(message), any(), any())

        chatViewModel.sendMessage(chat, message)

        testScheduler.advanceUntilIdle()

        verify(mockRepository).sendMessage(eq(chat), eq(message), any(), any())
        verify(mockRepository, never()).getChats(any(), any())
    }

    // ----- deleteMessage Tests -----

    @Test
    fun deleteMessage_whenSuccess_updatesChats() = runTest {
        doAnswer { invocation ->
            val onSuccess = invocation.getArgument<() -> Unit>(2)
            onSuccess()
            null
        }.`when`(mockRepository).deleteMessage(eq(chat), eq(message), any(), any())

        doAnswer { invocation ->
            val onSuccess = invocation.getArgument<(List<Chat>) -> Unit>(0)
            onSuccess(listOf(chat))
            null
        }.`when`(mockRepository).getChats(any(), any())

        chatViewModel.deleteMessage(chat, message)

        testScheduler.advanceUntilIdle()

        assertEquals(listOf(chat), chatViewModel.chats.value)
        verify(mockRepository).deleteMessage(eq(chat), eq(message), any(), any())
        verify(mockRepository).getChats(any(), any())
    }

    @Test
    fun deleteMessage_whenFailure_logsError() = runTest {
        val exception = Exception("Test exception")

        doAnswer { invocation ->
            val onFailure = invocation.getArgument<(Exception) -> Unit>(3)
            onFailure(exception)
            null
        }.`when`(mockRepository).deleteMessage(eq(chat), eq(message), any(), any())

        chatViewModel.deleteMessage(chat, message)

        testScheduler.advanceUntilIdle()

        verify(mockRepository).deleteMessage(eq(chat), eq(message), any(), any())
        verify(mockRepository, never()).getChats(any(), any())
    }
}
