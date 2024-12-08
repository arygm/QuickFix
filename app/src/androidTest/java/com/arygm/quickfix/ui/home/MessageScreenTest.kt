package com.arygm.quickfix.ui.home

import androidx.compose.ui.test.assertAny
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasParent
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.arygm.quickfix.model.account.Account
import com.arygm.quickfix.model.account.AccountRepository
import com.arygm.quickfix.model.account.AccountViewModel
import com.arygm.quickfix.model.account.LoggedInAccountViewModel
import com.arygm.quickfix.model.messaging.Chat
import com.arygm.quickfix.model.messaging.ChatRepository
import com.arygm.quickfix.model.messaging.ChatViewModel
import com.arygm.quickfix.model.messaging.Message
import com.arygm.quickfix.model.profile.ProfileViewModel
import com.arygm.quickfix.model.profile.UserProfileRepositoryFirestore
import com.arygm.quickfix.model.profile.WorkerProfileRepositoryFirestore
import com.arygm.quickfix.ui.navigation.NavigationActions
import com.arygm.quickfix.ui.navigation.Screen
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.text.SimpleDateFormat
import java.util.Locale
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.doAnswer
import org.mockito.Mockito.doNothing
import org.mockito.Mockito.mock
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class MessageScreenTest {

  @get:Rule val composeTestRule = createComposeRule()

  private lateinit var mockFirestore: FirebaseFirestore
  private lateinit var navigationActions: NavigationActions
  private lateinit var userProfileRepositoryFirestore: UserProfileRepositoryFirestore
  private lateinit var workerProfileRepositoryFirestore: WorkerProfileRepositoryFirestore
  private lateinit var accountRepository: AccountRepository
  private lateinit var accountViewModel: AccountViewModel
  private lateinit var loggedInAccountViewModel: LoggedInAccountViewModel
  private lateinit var userViewModel: ProfileViewModel
  private lateinit var chatRepository: ChatRepository
  private lateinit var chatViewModel: ChatViewModel
  private lateinit var mockStorage: FirebaseStorage
  @Mock private lateinit var storageRef: StorageReference

  // Simulated state flows
  private val chatListFlow = MutableStateFlow<List<Chat>>(emptyList())

  // Test data
  private val testAccount =
      Account(
          uid = "testUserId",
          firstName = "Test",
          lastName = "User",
          email = "test@example.com",
          birthDate = Timestamp.now(),
          activeChats = listOf("testChatId"))

  private val testChat =
      Chat(
          chatId = "testChatId",
          workeruid = "workerId",
          useruid = "testUserId",
          messages = emptyList())

  @Before
  fun setup() {
    // Mock configuration
    mockStorage = mock(FirebaseStorage::class.java)
    storageRef = mock(StorageReference::class.java)
    whenever(mockStorage.reference).thenReturn(storageRef)
    mockFirestore = mock(FirebaseFirestore::class.java)
    navigationActions = mock(NavigationActions::class.java)
    accountRepository = mock(AccountRepository::class.java)
    chatRepository = mock(ChatRepository::class.java)

    // Mocked FirebaseFirestore configuration
    doNothing().whenever(chatRepository).init(any())

    // Initialization of real implementations with mocks
    userProfileRepositoryFirestore = UserProfileRepositoryFirestore(mockFirestore, mockStorage)
    workerProfileRepositoryFirestore = WorkerProfileRepositoryFirestore(mockFirestore, mockStorage)

    // ViewModel configuration
    accountViewModel = AccountViewModel(accountRepository)
    userViewModel = ProfileViewModel(userProfileRepositoryFirestore)
    loggedInAccountViewModel =
        LoggedInAccountViewModel(
            userProfileRepo = userProfileRepositoryFirestore,
            workerProfileRepo = workerProfileRepositoryFirestore)
    chatViewModel = ChatViewModel(repository = chatRepository)

    // Mock behavior configuration
    whenever(chatRepository.getChats(any(), any())).thenAnswer {
      val onSuccess = it.arguments[0] as (List<Chat>) -> Unit
      onSuccess(listOf(testChat))
      null
    }

    doAnswer {
          val onSuccess = it.getArgument<() -> Unit>(2)
          onSuccess()
          null
        }
        .whenever(accountRepository)
        .updateAccount(any(), any(), any())

    // Setting up the logged-in user
    loggedInAccountViewModel.loggedInAccount_.value = testAccount

    // Updating the chat state flow
    chatListFlow.value = listOf(testChat)

    // Assume that the current route is the registration screen (similar to the provided example)
    whenever(navigationActions.currentRoute()).thenReturn(Screen.MESSAGES)
  }

  @Test
  fun testInitialUI() {
    // Action
    composeTestRule.setContent {
      MessageScreen(
          loggedInAccountViewModel = loggedInAccountViewModel,
          chatViewModel = chatViewModel,
          navigationActions = navigationActions)
    }

    // Ensure the UI has time to load
    composeTestRule.waitForIdle()

    // Initial display verification
    composeTestRule.onNodeWithTag("messageScreen").assertIsDisplayed()
    composeTestRule.onNodeWithTag("messageInputArea").assertIsDisplayed()
    composeTestRule.onNodeWithTag("messageTextField").assertIsDisplayed()
    composeTestRule.onNodeWithTag("sendButton").assertIsDisplayed()
  }

  @Test
  fun testSendMessage() {
    // Preparation
    val testMessageContent = "Test message"

    // Simulate sending a message
    doAnswer {
          val chat: Chat = it.getArgument(0)
          val message: Message = it.getArgument(1)
          val onSuccess = it.getArgument<() -> Unit>(2)
          val updatedMessages = chat.messages.toMutableList().apply { add(message) }
          val updatedChat = chat.copy(messages = updatedMessages)

          whenever(chatRepository.getChats(any(), any())).thenAnswer { invocation ->
            val onSuccessChats = invocation.arguments[0] as (List<Chat>) -> Unit
            onSuccessChats(listOf(updatedChat))
            null
          }

          onSuccess()
          null
        }
        .whenever(chatRepository)
        .sendMessage(any(), any(), any(), any())

    // Load the UI
    composeTestRule.setContent {
      MessageScreen(
          loggedInAccountViewModel = loggedInAccountViewModel,
          chatViewModel = chatViewModel,
          navigationActions = navigationActions)
    }

    // Wait for the UI to be ready
    composeTestRule.waitForIdle()

    // Action: send a message
    composeTestRule.onNodeWithTag("messageTextField").performTextInput(testMessageContent)
    composeTestRule.onNodeWithTag("sendButton").performClick()

    // Wait for UI updates after sending the message
    composeTestRule.waitForIdle()

    // Verification
    val messageCaptor = argumentCaptor<Message>()
    verify(chatRepository).sendMessage(any(), messageCaptor.capture(), any(), any())
    val sentMessage = messageCaptor.firstValue
    assertEquals(testMessageContent, sentMessage.content)
  }

  @Test
  fun testDateDividerDisplayed() {
    // Preparation
    val yesterday = Timestamp(Timestamp.now().seconds - 86400, 0) // 1 day ago
    val today = Timestamp.now()
    val messages =
        listOf(
            Message(
                messageId = "message1",
                senderId = "otherUserId",
                content = "Yesterday's message",
                timestamp = yesterday),
            Message(
                messageId = "message2",
                senderId = "testUserId",
                content = "Today's message",
                timestamp = today))
    val updatedChat = testChat.copy(messages = messages)

    doAnswer {
          val onSuccess = it.arguments[0] as (List<Chat>) -> Unit
          onSuccess(listOf(updatedChat))
          null
        }
        .whenever(chatRepository)
        .getChats(any(), any())

    // Load the UI
    composeTestRule.setContent {
      MessageScreen(
          loggedInAccountViewModel = loggedInAccountViewModel,
          chatViewModel = chatViewModel,
          navigationActions = navigationActions)
    }

    // Execute the effect to load the chats
    chatViewModel.getChats()
    composeTestRule.waitForIdle()

    // Verification
    val dateFormat = SimpleDateFormat("d MMM", Locale.getDefault())
    val yesterdayDate = dateFormat.format(yesterday.toDate())
    val todayDate = dateFormat.format(today.toDate())

    composeTestRule
        .onAllNodesWithTag("dateDivider")
        .assertAny(hasText(yesterdayDate))
        .assertAny(hasText(todayDate))
  }

  @Test
  fun testMessageInputDisabledWhenNoChat() {
    // Preparation
    loggedInAccountViewModel.loggedInAccount_.value = testAccount.copy(activeChats = emptyList())

    // Simulate no chat
    doAnswer {
          val onSuccess = it.arguments[0] as (List<Chat>) -> Unit
          onSuccess(emptyList())
          null
        }
        .whenever(chatRepository)
        .getChats(any(), any())

    // Load the UI
    composeTestRule.setContent {
      MessageScreen(
          loggedInAccountViewModel = loggedInAccountViewModel,
          chatViewModel = chatViewModel,
          navigationActions = navigationActions)
    }

    // Ensure the UI has time to load
    composeTestRule.waitForIdle()

    // Verification
    composeTestRule.onNodeWithTag("messageInputArea").assertDoesNotExist()
  }

  @Test
  fun testReceivedMessageDisplayed() {
    // Preparation
    val testMessage =
        Message(
            messageId = "message1",
            senderId = "otherUserId",
            content = "Received message",
            timestamp = Timestamp.now())
    val updatedChat = testChat.copy(messages = listOf(testMessage))

    doAnswer {
          val onSuccess = it.arguments[0] as (List<Chat>) -> Unit
          onSuccess(listOf(updatedChat))
          null
        }
        .whenever(chatRepository)
        .getChats(any(), any())

    // Load the UI
    composeTestRule.setContent {
      MessageScreen(
          loggedInAccountViewModel = loggedInAccountViewModel,
          chatViewModel = chatViewModel,
          navigationActions = navigationActions)
    }

    // Execute the effect to load the chats
    chatViewModel.getChats()
    composeTestRule.waitForIdle()

    // Verification
    composeTestRule
        .onNode(
            hasText("Received message") and hasParent(hasTestTag("receivedMessage")),
            useUnmergedTree = true)
        .assertIsDisplayed()
  }

  @Test
  fun testBackButtonNavigatesBack() {
    composeTestRule.setContent {
      MessageScreen(
          loggedInAccountViewModel = loggedInAccountViewModel,
          chatViewModel = chatViewModel,
          navigationActions = navigationActions)
    }

    // Ensure the interface is ready
    composeTestRule.waitForIdle()

    // Click the back button
    composeTestRule.onNodeWithTag("backButton").performClick()

    // Verify that navigationActions.goBack() was called
    verify(navigationActions).goBack()
  }

  @Test
  fun testFakeMessageScreenInitialUI() {
    // Set up the composition with FakeMessageScreen
    composeTestRule.setContent { FakeMessageScreen(navigationActions) }

    // Ensure the UI has time to load
    composeTestRule.waitForIdle()

    // Verify that predefined messages are displayed
    composeTestRule.onNodeWithText("Salut bro").assertIsDisplayed()
    composeTestRule.onNodeWithText("Moi ?").assertIsDisplayed()
    composeTestRule.onNodeWithText("C'est quoi ton cours préféré à l'école?").assertIsDisplayed()
    composeTestRule.onNodeWithText("Moi c’est la cantine 😊").assertIsDisplayed()

    // Verify that the message input area is displayed
    composeTestRule.onNodeWithText("Message").assertIsDisplayed()

    // Verify that the send button is displayed
    composeTestRule.onNodeWithContentDescription("Send").assertIsDisplayed()
  }
}
