package com.arygm.quickfix.ui.quickfix

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.datastore.preferences.core.stringPreferencesKey
import com.arygm.quickfix.model.account.Account
import com.arygm.quickfix.model.account.AccountRepository
import com.arygm.quickfix.model.account.AccountViewModel
import com.arygm.quickfix.model.locations.Location
import com.arygm.quickfix.model.locations.LocationRepository
import com.arygm.quickfix.model.locations.LocationViewModel
import com.arygm.quickfix.model.messaging.Chat
import com.arygm.quickfix.model.messaging.ChatRepository
import com.arygm.quickfix.model.messaging.ChatStatus
import com.arygm.quickfix.model.messaging.ChatViewModel
import com.arygm.quickfix.model.messaging.Message
import com.arygm.quickfix.model.offline.small.PreferencesRepository
import com.arygm.quickfix.model.offline.small.PreferencesViewModel
import com.arygm.quickfix.model.profile.ProfileRepository
import com.arygm.quickfix.model.profile.ProfileViewModel
import com.arygm.quickfix.model.profile.dataFields.AddOnService
import com.arygm.quickfix.model.profile.dataFields.IncludedService
import com.arygm.quickfix.model.quickfix.QuickFix
import com.arygm.quickfix.model.quickfix.QuickFixRepository
import com.arygm.quickfix.model.quickfix.QuickFixViewModel
import com.arygm.quickfix.model.quickfix.Status
import com.arygm.quickfix.model.switchModes.AppMode
import com.arygm.quickfix.ui.navigation.NavigationActions
import com.arygm.quickfix.ui.uiMode.appContentUI.userModeUI.quickfix.QuickFixSecondStep
import com.google.firebase.Timestamp
import java.util.Date
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.whenever

class QuickFixSecondStepTest {

  @get:Rule val composeTestRule = createComposeRule()

  // Class-level MutableStateFlows for userId and appMode
  private val userIdFlow = MutableStateFlow("testUserId")
  private val appModeFlow = MutableStateFlow("USER")

  // Mocked dependencies
  private lateinit var navigationActions: NavigationActions
  private lateinit var locationRepository: LocationRepository
  private lateinit var locationViewModel: LocationViewModel
  private lateinit var chatRepository: ChatRepository
  private lateinit var chatViewModel: ChatViewModel
  private lateinit var profileRepository: ProfileRepository
  private lateinit var profileViewModel: ProfileViewModel
  private lateinit var quickFixRepository: QuickFixRepository
  private lateinit var quickFixViewModel: QuickFixViewModel
  private lateinit var preferencesRepositoryDataStore: PreferencesRepository
  private lateinit var preferencesViewModel: PreferencesViewModel
  private lateinit var accountRepository: AccountRepository
  private lateinit var accountViewModel: AccountViewModel

  // Fake data setup
  private val fakeQuickFixUid = "qf_123"
  private val fakeQuickFix =
      QuickFix(
          uid = fakeQuickFixUid,
          status = Status.PENDING,
          imageUrl = listOf("https://example.com/image1.jpg", "https://example.com/image2.jpg"),
          date =
              listOf(
                  Timestamp(Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000)), // Yesterday
                  Timestamp(Date()) // Today
                  ),
          time = Timestamp(Date()),
          includedServices = listOf(IncludedService("Service 1"), IncludedService("Service 2")),
          addOnServices =
              listOf(AddOnService("Add-on Service 1"), AddOnService("Add-on Service 2")),
          workerId = "worker_123",
          userId = "user_456",
          chatUid = "chat_789",
          title = "Fixing the Kitchen Sink",
          description = "The kitchen sink is clogged and needs fixing.",
          bill = emptyList(),
          location =
              Location(
                  latitude = 40.7128, longitude = -74.0060, name = "123 Main St, New York, NY"))

  private val fakeUserAccount =
      Account(
          uid = "user_456",
          firstName = "Jane",
          lastName = "Doe",
          birthDate = Timestamp.now(),
          email = "email@gmail.com",
          activeChats = listOf("chat_789"))

  private val fakeWorkerAccount =
      Account(
          uid = "worker_123",
          firstName = "John",
          lastName = "Smith",
          birthDate = Timestamp.now(),
          email = "email@gmail.com",
          activeChats = listOf("chat_789"))

  private val fakeChat =
      Chat(
          chatId = "chat_789",
          chatStatus = ChatStatus.ACCEPTED,
          quickFixUid = fakeQuickFixUid,
          messages =
              listOf(
                  Message("msg_1", "user_456", "Hello!", Timestamp.now()),
                  Message("msg_2", "worker_123", "Hi, how can I help you?", Timestamp.now())),
          useruid = "user_456",
          workeruid = "worker_123")

  @Before
  fun setUp() {
    // Initialize mocks
    navigationActions = mock(NavigationActions::class.java)
    locationRepository = mock(LocationRepository::class.java)
    locationViewModel = LocationViewModel(locationRepository)
    chatRepository = mock(ChatRepository::class.java)
    chatViewModel = ChatViewModel(chatRepository)
    profileRepository = mock(ProfileRepository::class.java)
    profileViewModel = ProfileViewModel(profileRepository)
    quickFixRepository = mock(QuickFixRepository::class.java)
    quickFixViewModel = QuickFixViewModel(quickFixRepository)
    preferencesRepositoryDataStore = mock(PreferencesRepository::class.java)
    preferencesViewModel = PreferencesViewModel(preferencesRepositoryDataStore)
    accountRepository = mock(AccountRepository::class.java)
    accountViewModel = AccountViewModel(accountRepository)

    // Mock getPreferenceByKey for user_id
    val userIdKey = stringPreferencesKey("user_id")
    whenever(preferencesRepositoryDataStore.getPreferenceByKey(userIdKey)).thenReturn(userIdFlow)

    // Mock getPreferenceByKey for app_mode
    val appModeKey = stringPreferencesKey("app_mode")
    whenever(preferencesRepositoryDataStore.getPreferenceByKey(appModeKey)).thenReturn(appModeFlow)

    runBlocking {
      // Mock chatRepository.getChats to return a default fakeChat
      doAnswer { invocation ->
            val onSuccess = invocation.getArgument<(List<Chat>) -> Unit>(0)
            onSuccess(listOf(fakeChat))
            null
          }
          .whenever(chatRepository)
          .getChats(any(), any())

      // Mock quickFixRepository.getQuickFixByUid to return fakeQuickFix when called with
      // fakeQuickFixUid
      doAnswer { invocation ->
            val uid = invocation.getArgument<String>(0)
            val onResult = invocation.getArgument<(QuickFix?) -> Unit>(1)
            if (uid == fakeQuickFixUid) {
              onResult(fakeQuickFix)
            } else {
              onResult(null)
            }
            null
          }
          .whenever(quickFixRepository)
          .getQuickFixById(any(), any(), any())

      // Mock accountRepository.fetchUserAccount to return fakeUserAccount and fakeWorkerAccount
      doAnswer { invocation ->
            val userId = invocation.getArgument<String>(0)
            val onResult = invocation.getArgument<(Account?) -> Unit>(1)
            when (userId) {
              "user_456" -> onResult(fakeUserAccount)
              "worker_123" -> onResult(fakeWorkerAccount)
              else -> onResult(null)
            }
            null
          }
          .whenever(accountRepository)
          .getAccountById(any(), any(), any())

      doAnswer { invocation ->
            val userId = invocation.getArgument<String>(0)
            val onResult = invocation.getArgument<(Account?) -> Unit>(1)
            when (userId) {
              "user_456" -> onResult(fakeUserAccount)
              "worker_123" -> onResult(fakeWorkerAccount)
              else -> onResult(null)
            }
            null
          }
          .whenever(accountRepository)
          .updateAccount(any(), any(), any())

      // Mock chatRepository.addChat to invoke onSuccess
      doAnswer { invocation ->
            val chat = invocation.getArgument<Chat>(0)
            val onSuccess = invocation.getArgument<() -> Unit>(1)
            val onFailure = invocation.getArgument<() -> Unit>(2)
            // Simulate successful chat addition
            onSuccess()
            null
          }
          .whenever(chatRepository)
          .createChat(any(), any(), any())
    }

    // Initialize ViewModels with mocked repositories
    chatViewModel = ChatViewModel(chatRepository)
    quickFixViewModel = QuickFixViewModel(quickFixRepository)
    accountViewModel = AccountViewModel(accountRepository)

    runBlocking {
      chatViewModel.getChats() // Load chats
      quickFixViewModel.getQuickFixes() // Load QuickFixes if necessary
    }

    chatViewModel.selectChat(fakeChat)
  }

  @Test
  fun testHeaderIsDisplayed() = runTest {
    userIdFlow.value = "user_456"
    appModeFlow.value = "USER"

    // Act: Set the composable content
    composeTestRule.setContent {
      QuickFixSecondStep(
          quickFixViewModel = quickFixViewModel,
          accountViewModel = accountViewModel,
          chatViewModel = chatViewModel,
          navigationActions = navigationActions,
          onQuickFixMakeBill = {},
          quickFix = fakeQuickFix,
          mode = AppMode.USER)
    }

    // Assert: Check if the header text is displayed
    composeTestRule.onNodeWithTag("HeaderText").assertIsDisplayed()
  }

  @Test
  fun testButtonIsDisplayed() = runTest {
    userIdFlow.value = "user_456"
    appModeFlow.value = "USER"

    // Act: Set the composable content
    composeTestRule.setContent {
      QuickFixSecondStep(
          quickFixViewModel = quickFixViewModel,
          accountViewModel = accountViewModel,
          chatViewModel = chatViewModel,
          navigationActions = navigationActions,
          onQuickFixMakeBill = {},
          quickFix = fakeQuickFix,
          mode = AppMode.USER)
    }

    // Assert: Check if the "Consult the discussion" button is displayed
    composeTestRule.onNodeWithTag("ConsultDiscussionButton").assertIsDisplayed()
  }

  @Test
  fun testDatesAndImagesAreDisplayed() = runTest {
    userIdFlow.value = "user_456"
    appModeFlow.value = "USER"

    // Act: Set the composable content
    composeTestRule.setContent {
      QuickFixSecondStep(
          quickFixViewModel = quickFixViewModel,
          accountViewModel = accountViewModel,
          chatViewModel = chatViewModel,
          navigationActions = navigationActions,
          onQuickFixMakeBill = {},
          quickFix = fakeQuickFix,
          mode = AppMode.USER)
    }

    // Assert: Check if the dates row is displayed
    composeTestRule.onNodeWithTag("DatesRow").assertIsDisplayed()

    // Assert: Check if the day and time headers are displayed
    composeTestRule.onNodeWithTag("DayHeader").assertIsDisplayed()
    composeTestRule.onNodeWithTag("TimeHeader").assertIsDisplayed()

    // Assert: Check if the dates lazy column is displayed
    composeTestRule.onNodeWithTag("DatesLazyColumn").assertIsDisplayed()

    // Assert: Check if each date and time text is displayed
    fakeQuickFix.date.forEachIndexed { index, date ->
      composeTestRule.onNodeWithTag("DateText_$index").assertIsDisplayed()
      composeTestRule.onNodeWithTag("TimeText_$index").assertIsDisplayed()
    }

    // Assert: Check if each image is displayed
    fakeQuickFix.imageUrl.forEachIndexed { index, _ ->
      composeTestRule.onNodeWithTag("Image_$index").assertIsDisplayed()
    }
  }

  @Test
  fun testMakeBillButtonDisplayedForWorkerMode() = runTest {
    userIdFlow.value = "worker_123"
    appModeFlow.value = "WORKER"

    // Create a flag to verify if onQuickFixMakeBill is called
    var isMakeBillClicked = false

    // Act: Set the composable content with mode = WORKER
    composeTestRule.setContent {
      QuickFixSecondStep(
          quickFixViewModel = quickFixViewModel,
          accountViewModel = accountViewModel,
          chatViewModel = chatViewModel,
          navigationActions = navigationActions,
          onQuickFixMakeBill = { isMakeBillClicked = true },
          quickFix = fakeQuickFix,
          mode = AppMode.WORKER)
    }

    // Assert: Check if the "Make the bill" button is displayed
    composeTestRule.onNodeWithTag("MakethebillButton").assertIsDisplayed()

    // Perform click on the "Make the bill" button
    composeTestRule.onNodeWithTag("MakethebillButton").performClick()

    // Assert: Verify that the onQuickFixMakeBill lambda was invoked
    assert(isMakeBillClicked) { "onQuickFixMakeBill was not called upon button click." }
  }
}
