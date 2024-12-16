package com.arygm.quickfix.ui.quickfix

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.datastore.preferences.core.stringPreferencesKey
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
import com.arygm.quickfix.model.profile.UserProfileRepositoryFirestore
import com.arygm.quickfix.model.profile.WorkerProfile
import com.arygm.quickfix.model.profile.WorkerProfileRepositoryFirestore
import com.arygm.quickfix.model.profile.dataFields.AddOnService
import com.arygm.quickfix.model.profile.dataFields.IncludedService
import com.arygm.quickfix.model.quickfix.QuickFix
import com.arygm.quickfix.model.quickfix.QuickFixRepository
import com.arygm.quickfix.model.quickfix.QuickFixViewModel
import com.arygm.quickfix.model.quickfix.Status
import com.arygm.quickfix.ui.navigation.NavigationActions
import com.arygm.quickfix.ui.uiMode.appContentUI.userModeUI.quickfix.QuickFixFirstStep
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

class QuickFixFirstStepTest {

  @get:Rule val composeTestRule = createComposeRule()

  // Class-level MutableStateFlows for userId and appMode
  private val userIdFlow = MutableStateFlow("testUserId")
  private val appModeFlow = MutableStateFlow("USER")

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
  private lateinit var userProfileRepository: UserProfileRepositoryFirestore
  private lateinit var workerProfileRepository: WorkerProfileRepositoryFirestore
  private lateinit var userViewModel: ProfileViewModel
  private lateinit var workerViewModel: ProfileViewModel

  // Fake data setup
  private val fakeQuickFixUid = "qf_123"
  private val fakeChat =
      Chat(
          chatId = "chat_123",
          chatStatus = ChatStatus.ACCEPTED,
          quickFixUid = fakeQuickFixUid,
          messages =
              listOf(
                  Message("msg_1", "testUserId", "Hello!", Timestamp(Date())),
                  Message("msg_2", "otherUserId", "Hi, how can I help you?", Timestamp(Date()))),
          useruid = "Jane the User",
          workeruid = "John the Worker")
  private val fakeQuickFix =
      QuickFix(
          uid = fakeQuickFixUid,
          status = Status.PENDING,
          imageUrl = listOf("https://example.com/image1.jpg"),
          date =
              listOf(
                  Timestamp(Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000)), // Yesterday
                  Timestamp(Date()) // Today
                  ),
          time = Timestamp(Date()),
          includedServices = listOf(IncludedService("Service 1")),
          addOnServices = listOf(AddOnService("Add-on Service 1")),
          workerId = "John the Worker",
          userId = "Jane the User",
          chatUid = "chat_123",
          title = "Fixing the Kitchen Sink",
          description = "The kitchen sink is clogged and needs fixing.",
          bill = emptyList(),
          location = Location(latitude = 40.7128, longitude = -74.0060, name = "Test Location"))

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
    userProfileRepository = mock(UserProfileRepositoryFirestore::class.java)
    workerProfileRepository = mock(WorkerProfileRepositoryFirestore::class.java)
    userViewModel = ProfileViewModel(userProfileRepository)
    workerViewModel = ProfileViewModel(workerProfileRepository)

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
    }

    doAnswer { invocation ->
          val query = invocation.getArgument<String>(0)
          val onSuccess = invocation.getArgument<(List<Location>) -> Unit>(1)
          val onError = invocation.getArgument<(Throwable) -> Unit>(2)

          // Mock response based on the query
          val mockLocations =
              listOf(
                  Location(latitude = 40.7128, longitude = -74.0060, name = "New York"),
                  Location(latitude = 34.0522, longitude = -118.2437, name = "123 Main St"))

          // Call onSuccess with the mock data
          onSuccess(mockLocations)
          null
        }
        .whenever(locationRepository)
        .search(any(), any(), any())

    // Initialize ViewModels with mocked repositories
    chatViewModel = ChatViewModel(chatRepository)
    quickFixViewModel = QuickFixViewModel(quickFixRepository)

    runBlocking {
      chatViewModel.getChats() // Load chats
      quickFixViewModel.getQuickFixes() // Load QuickFixes if necessary
    }

    chatViewModel.selectChat(fakeChat)
  }

  @Test
  fun initialStateValidation() = runTest {
    // Arrange: Set userId and AppMode if necessary
    userIdFlow.value = "testUserId"
    appModeFlow.value = "USER" // or "WORKER" based on test requirements

    // Act: Set the composable content
    composeTestRule.setContent {
      QuickFixFirstStep(
          locationViewModel = locationViewModel,
          chatViewModel = chatViewModel,
          quickFixViewModel = quickFixViewModel,
          preferencesViewModel = preferencesViewModel,
          workerProfile =
              WorkerProfile(
                  uid = "worker_123",
                  includedServices = listOf(IncludedService("Service 1")),
                  addOnServices = listOf(AddOnService("Add-on Service 1"))),
          onQuickFixChange = { _ -> },
          userViewModel = userViewModel,
          workerViewModel = workerViewModel,
          navigationActions = navigationActions)
    }

    // Assert: Verify default UI elements
    composeTestRule.onNodeWithText("Enter a title ...").assertExists()
    composeTestRule.onNodeWithText("Features services").assertExists()
    composeTestRule.onNodeWithText("Add-on services").assertExists()
    composeTestRule.onNodeWithText("Upload Pictures").assertExists().performScrollTo()

    // Verify "Continue" button is initially disabled
    composeTestRule.onNodeWithText("Continue").assertIsNotEnabled()
  }

  @Test
  fun textInputBehavior() = runTest {
    // Arrange: Set userId and AppMode if necessary
    userIdFlow.value = "testUserId"
    appModeFlow.value = "USER" // or "WORKER"

    // Act: Set the composable content
    composeTestRule.setContent {
      QuickFixFirstStep(
          locationViewModel = locationViewModel,
          chatViewModel = chatViewModel,
          quickFixViewModel = quickFixViewModel,
          preferencesViewModel = preferencesViewModel,
          workerProfile =
              WorkerProfile(
                  uid = "worker_123",
                  includedServices = listOf(IncludedService("Service 1")),
                  addOnServices = listOf(AddOnService("Add-on Service 1"))),
          onQuickFixChange = { _ -> },
          userViewModel = userViewModel,
          workerViewModel = workerViewModel,
          navigationActions = navigationActions)
    }

    // Enter text in the title field
    composeTestRule.onNodeWithText("Enter a title ...").performTextInput("Test Title")
    composeTestRule.onNodeWithText("Test Title").assertExists()

    // Enter text in the quick note field
    composeTestRule.onNodeWithText("Type a description...").performTextInput("Test Note")
    composeTestRule.onNodeWithText("Test Note").assertExists()
  }

  @Test
  fun serviceSelection() = runTest {
    // Arrange: Set userId and AppMode if necessary
    userIdFlow.value = "testUserId"
    appModeFlow.value = "USER" // or "WORKER"

    // Act: Set the composable content
    composeTestRule.setContent {
      QuickFixFirstStep(
          locationViewModel = locationViewModel,
          chatViewModel = chatViewModel,
          quickFixViewModel = quickFixViewModel,
          preferencesViewModel = preferencesViewModel,
          workerProfile =
              WorkerProfile(
                  uid = "worker_123",
                  includedServices = listOf(IncludedService("Service 1")),
                  addOnServices = listOf(AddOnService("Add-on Service 1"))),
          onQuickFixChange = { _ -> },
          userViewModel = userViewModel,
          workerViewModel = workerViewModel,
          navigationActions = navigationActions)
    }

    // Select a service
    composeTestRule.onNodeWithText("Service 1").performClick()

    // Assert that the service is selected
    composeTestRule.onNodeWithText("Service 1").assertIsOn()
  }

  @Test
  fun datePickerIntegration() = runTest {
    // Arrange: Set userId and AppMode if necessary
    userIdFlow.value = "testUserId"
    appModeFlow.value = "USER" // or "WORKER"

    // Act: Set the composable content
    composeTestRule.setContent {
      QuickFixFirstStep(
          locationViewModel = locationViewModel,
          chatViewModel = chatViewModel,
          quickFixViewModel = quickFixViewModel,
          preferencesViewModel = preferencesViewModel,
          workerProfile =
              WorkerProfile(
                  uid = "worker_123",
                  includedServices = listOf(IncludedService("Service 1")),
                  addOnServices = listOf(AddOnService("Add-on Service 1"))),
          onQuickFixChange = { _ -> },
          userViewModel = userViewModel,
          workerViewModel = workerViewModel,
          navigationActions = navigationActions)
    }

    // Open the date picker
    composeTestRule.onNodeWithText("Add Suggested Date").performClick()

    // Assert the date picker is displayed
    composeTestRule.onNodeWithText("Select Date").assertExists()

    // Simulate selecting a date
    val today = java.time.LocalDate.now()
    composeTestRule.onNodeWithText(today.dayOfMonth.toString()).performClick()

    // Click "OK" to confirm the date selection
    composeTestRule.onNodeWithText("OK").performClick()

    // Optionally, if there's a time picker, simulate selecting time
    // Assuming the first "OK" was for date and second for time
    composeTestRule.onNodeWithText("OK").performClick()

    // Assert the selected date is displayed
    composeTestRule.onNodeWithText("Suggested Date").assertExists()
  }

  @Test
  fun testLocationInput() = runTest {
    // Arrange: Set userId and AppMode if necessary
    userIdFlow.value = "testUserId"
    appModeFlow.value = "USER" // or "WORKER"

    // Act: Set the composable content
    composeTestRule.setContent {
      QuickFixFirstStep(
          locationViewModel = locationViewModel,
          chatViewModel = chatViewModel,
          quickFixViewModel = quickFixViewModel,
          preferencesViewModel = preferencesViewModel,
          workerProfile =
              WorkerProfile(
                  uid = "worker_123",
                  includedServices = listOf(IncludedService("Service 1")),
                  addOnServices = listOf(AddOnService("Add-on Service 1"))),
          onQuickFixChange = { _ -> },
          userViewModel = userViewModel,
          workerViewModel = workerViewModel,
          navigationActions = navigationActions)
    }

    // Enter text in the location field
    composeTestRule.onNodeWithText("Enter a location ...").performTextInput("123 Main St")
    composeTestRule.onNodeWithTag("locationItem_1", true).performClick()
    composeTestRule.onNodeWithText("123 Main St").assertExists()
  }
}
