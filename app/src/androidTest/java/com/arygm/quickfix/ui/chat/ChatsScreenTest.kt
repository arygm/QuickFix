package com.arygm.quickfix.ui.chat

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.arygm.quickfix.model.account.Account
import com.arygm.quickfix.model.account.AccountRepository
import com.arygm.quickfix.model.account.AccountViewModel
import com.arygm.quickfix.model.messaging.Chat
import com.arygm.quickfix.model.messaging.ChatRepository
import com.arygm.quickfix.model.messaging.ChatViewModel
import com.arygm.quickfix.model.messaging.Message
import com.arygm.quickfix.model.offline.small.PreferencesRepository
import com.arygm.quickfix.model.offline.small.PreferencesViewModel
import com.arygm.quickfix.ui.navigation.NavigationActions
import com.arygm.quickfix.ui.uiMode.appContentUI.workerMode.messages.ChatsScreen
import com.arygm.quickfix.utils.*
import com.google.firebase.Timestamp
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.*
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@RunWith(AndroidJUnit4::class)
@OptIn(ExperimentalCoroutinesApi::class)
class ChatsScreenTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Mock private lateinit var accountRepository: AccountRepository
  private lateinit var accountViewModel: AccountViewModel

  @Mock private lateinit var chatRepository: ChatRepository
  private lateinit var chatViewModel: ChatViewModel

  @Mock private lateinit var preferencesRepository: PreferencesRepository
  private lateinit var preferencesViewModel: PreferencesViewModel

  @Mock private lateinit var navigationActions: NavigationActions

  private val testUserId = "testUserId"

  private val testChats =
      listOf(
          Chat(
              chatId = "1",
              workeruid = "testUserId",
              useruid = "user1",
              quickFixUid = "quickfix1",
              messages =
                  listOf(
                      Message("msg1", "user1", "Hello there!", Timestamp.now()),
                      Message("msg2", "worker1", "Hi!", Timestamp.now()))),
          Chat(
              chatId = "2",
              workeruid = "testUserId",
              useruid = "user2",
              quickFixUid = "quickfix2",
              messages =
                  listOf(
                      Message("msg1", "user2", "Another message", Timestamp.now()),
                      Message("msg2", "worker2", "Reply to message", Timestamp.now()))))

  @Before
  fun setup() {
    MockitoAnnotations.openMocks(this)

    accountViewModel = AccountViewModel(accountRepository)
    chatViewModel = ChatViewModel(chatRepository)
    preferencesViewModel = PreferencesViewModel(preferencesRepository)

    // Utiliser flowOf(...) pour chaque préférence afin d'émettre une seule valeur et terminer
    whenever(preferencesRepository.getPreferenceByKey(eq(UID_KEY))).thenReturn(flowOf(testUserId))
    whenever(preferencesRepository.getPreferenceByKey(eq(APP_MODE_KEY))).thenReturn(flowOf("USER"))
    whenever(preferencesRepository.getPreferenceByKey(eq(FIRST_NAME_KEY)))
        .thenReturn(flowOf("Tester"))
    whenever(preferencesRepository.getPreferenceByKey(eq(LAST_NAME_KEY))).thenReturn(flowOf("User"))
    whenever(preferencesRepository.getPreferenceByKey(eq(EMAIL_KEY)))
        .thenReturn(flowOf("test@example.com"))
    whenever(preferencesRepository.getPreferenceByKey(eq(BIRTH_DATE_KEY)))
        .thenReturn(flowOf("01/01/2000"))
    whenever(preferencesRepository.getPreferenceByKey(eq(IS_WORKER_KEY))).thenReturn(flowOf(false))

    val mainAccount =
        Account(
            uid = testUserId,
            firstName = "Tester",
            lastName = "User",
            email = "test@example.com",
            birthDate = Timestamp.now(),
            isWorker = false,
            activeChats = listOf("1", "2"))

    whenever(accountRepository.getAccountById(eq(testUserId), any(), any())).thenAnswer {
      val onSuccess = it.arguments[1] as (Account?) -> Unit
      onSuccess(mainAccount)
    }

    // user1
    whenever(accountRepository.getAccountById(eq("user1"), any(), any())).thenAnswer {
      val onSuccess = it.arguments[1] as (Account?) -> Unit
      onSuccess(
          Account(
              uid = "user1",
              firstName = "John",
              lastName = "Doe",
              email = "john@example.com",
              birthDate = Timestamp.now(),
              isWorker = false,
              activeChats = emptyList()))
    }

    // user2
    whenever(accountRepository.getAccountById(eq("user2"), any(), any())).thenAnswer {
      val onSuccess = it.arguments[1] as (Account?) -> Unit
      onSuccess(
          Account(
              uid = "user2",
              firstName = "Jane",
              lastName = "Smith",
              email = "jane@example.com",
              birthDate = Timestamp.now(),
              isWorker = false,
              activeChats = emptyList()))
    }

    // Mock des chats, appelés en interne par le ChatViewModel

  }

  @Test
  fun chatsScreen_displaysChatsAndNavigatesOnClick() = runTest {
    whenever(chatRepository.getChatByChatUid(eq("1"), any(), any())).thenAnswer {
      val onSuccess = it.arguments[1] as (Chat?) -> Unit
      onSuccess(testChats[0])
    }
    whenever(chatRepository.getChatByChatUid(eq("2"), any(), any())).thenAnswer {
      val onSuccess = it.arguments[1] as (Chat?) -> Unit
      onSuccess(testChats[1])
    }
    // runTest pour pouvoir appeler du suspend si nécessaire
    composeTestRule.setContent {
      ChatsScreen(
          navigationActions = navigationActions,
          accountViewModel = accountViewModel,
          chatViewModel = chatViewModel,
          preferencesViewModel = preferencesViewModel)
    }

    // Vérifie que "John" et "Jane" s'affichent
    composeTestRule.onNodeWithText("John").assertExists()
    composeTestRule.onNodeWithText("Jane").assertExists()

    // Clique sur "John"
    composeTestRule.onNodeWithText("John").performClick()

    // Vérifie la navigation
    verify(navigationActions).navigateTo(any<String>())
  }

  @Test
  fun chatsScreen_filtersChatsBasedOnSearchQuery() = runTest {
    whenever(chatRepository.getChatByChatUid(eq("1"), any(), any())).thenAnswer {
      val onSuccess = it.arguments[1] as (Chat?) -> Unit
      onSuccess(testChats[0])
    }
    whenever(chatRepository.getChatByChatUid(eq("2"), any(), any())).thenAnswer {
      val onSuccess = it.arguments[1] as (Chat?) -> Unit
      onSuccess(testChats[1])
    }
    composeTestRule.setContent {
      ChatsScreen(
          navigationActions = navigationActions,
          accountViewModel = accountViewModel,
          chatViewModel = chatViewModel,
          preferencesViewModel = preferencesViewModel)
    }

    // Entrer "Jane"
    composeTestRule.onNodeWithTag("customSearchField").performTextInput("Jane")

    // Vérifie que seul "Jane" est visible
    composeTestRule.onAllNodesWithText("Jane").filter(hasTestTag("ChatItem")).assertCountEquals(1)
    composeTestRule.onNodeWithText("John").assertDoesNotExist()
  }
}
