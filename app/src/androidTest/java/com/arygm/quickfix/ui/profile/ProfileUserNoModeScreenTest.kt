package com.arygm.quickfix.ui.profile

import androidx.compose.ui.test.*


/*
class ProfileScreenTest {

  @get:Rule val composeTestRule = createComposeRule()

  // Declare lateinit variables for dependencies
  private lateinit var navigationActions: NavigationActions
  private lateinit var rootMainNavigationActions: NavigationActions
  private lateinit var appContentNavigationActions: NavigationActions
  private lateinit var mockStorage: FirebaseStorage
  private lateinit var mockReference: StorageReference

  // Fake repositories
  private lateinit var preferencesRepository: FakePreferencesRepository
  private lateinit var userPreferencesRepository: FakePreferencesRepository

  // ViewModels
  private lateinit var preferencesViewModel: PreferencesViewModel
  private lateinit var userPreferencesViewModel: PreferencesViewModelUserProfile
  private lateinit var modeViewModel: ModeViewModel

  // Firestore repositories (if needed)
  private lateinit var userProfileRepositoryFirestore: UserProfileRepositoryFirestore
  private lateinit var workerProfileRepositoryFirestore: WorkerProfileRepositoryFirestore

  private val account =
      Account(
          uid = "1",
          firstName = "John",
          lastName = "Doe",
          email = "john.doe@example.com",
          birthDate = Timestamp.now(),
          isWorker = false)

  private val account2 =
      Account(
          uid = "2",
          firstName = "Jane",
          lastName = "Smith",
          email = "jane.smith@example.com",
          birthDate = Timestamp.now(),
          isWorker = true)

  @Before
  fun setup() {
    // Initialize Mockito mocks for NavigationActions and FirebaseStorage
    navigationActions = mock(NavigationActions::class.java)
    rootMainNavigationActions = mock(NavigationActions::class.java)
    appContentNavigationActions = mock(NavigationActions::class.java)
    mockStorage = mock(FirebaseStorage::class.java)
    mockReference = mock(StorageReference::class.java)

    // Initialize FakePreferencesRepository
    preferencesRepository = FakePreferencesRepository()
    userPreferencesRepository = FakePreferencesRepository()

    // Set initial preferences using Preferences.Key<T> keys
    runBlocking {
      preferencesRepository.setPreference(IS_WORKER_KEY, false)
      preferencesRepository.setPreference(EMAIL_KEY, "mail@example.com")
      preferencesRepository.setPreference(FIRST_NAME_KEY, "John")
      preferencesRepository.setPreference(LAST_NAME_KEY, "Doe")
      preferencesRepository.setPreference(BIRTH_DATE_KEY, "01-01-1990")
      preferencesRepository.setPreference(IS_SIGN_IN_KEY, true)
      preferencesRepository.setPreference(UID_KEY, "1")

      userPreferencesRepository.setPreference(WALLET_KEY, 0.0)
    }

    // Instantiate ViewModels with fake repositories
    preferencesViewModel = PreferencesViewModel(preferencesRepository)
    userPreferencesViewModel = PreferencesViewModelUserProfile(userPreferencesRepository)

    // Initialize ModeViewModel
    modeViewModel = ModeViewModel()

    // Stub FirebaseStorage methods
    runBlocking {
      // Assuming FirebaseStorage methods are suspend functions; adjust if not
      whenever(mockStorage.reference).thenReturn(mockReference)
      whenever(mockStorage.getReference()).thenReturn(mockReference)
      whenever(mockStorage.getReference(any<String>())).thenReturn(mockReference)
    }

    // Initialize Firestore repositories if needed
    // If UserProfileRepositoryFirestore and WorkerProfileRepositoryFirestore are used in tests,
    // instantiate them with mocked Firestore and Storage
    val mockFirestore = mock(FirebaseFirestore::class.java)
    userProfileRepositoryFirestore = UserProfileRepositoryFirestore(mockFirestore, mockStorage)
    workerProfileRepositoryFirestore = WorkerProfileRepositoryFirestore(mockFirestore, mockStorage)
  }

  @Test
  fun profileScreenDisplaysCorrectly() {
    composeTestRule.setContent {
      UserProfileScreen(
          navigationActions = navigationActions,
          rootMainNavigationActions = rootMainNavigationActions,
          preferencesViewModel = preferencesViewModel,
          userPreferencesViewModel = userPreferencesViewModel,
          appContentNavigationActions = appContentNavigationActions,
          modeViewModel = modeViewModel)
    }

    // Assert components are displayed
    composeTestRule.onNodeWithTag("ProfileContent").assertIsDisplayed()
    composeTestRule.onNodeWithTag("ProfileDisplayName").assertIsDisplayed()
    composeTestRule.onNodeWithTag("ProfileEmail").assertIsDisplayed()

    // Scroll to the Logout Button
    composeTestRule.onNodeWithTag("LogoutButton").performScrollTo().assertIsDisplayed()

    // Check the LogoutText
    composeTestRule.onNodeWithTag("LogoutText", useUnmergedTree = true).assertTextEquals("Log out")
  }

  @Test
  fun logoutButtonClickNavigatesCorrectly() {
    composeTestRule.setContent {
      UserProfileScreen(
          navigationActions = navigationActions,
          rootMainNavigationActions = rootMainNavigationActions,
          preferencesViewModel = preferencesViewModel,
          userPreferencesViewModel = userPreferencesViewModel,
          appContentNavigationActions = appContentNavigationActions,
          modeViewModel = modeViewModel)
    }

    // Perform click on logout button
    composeTestRule.onNodeWithTag("LogoutButton").performClick()

    // Verify navigation to the welcome screen
    verify(rootMainNavigationActions).navigateTo(RootRoute.NO_MODE)
  }

  @Test
  fun settingsOptionsAreDisplayedCorrectly() {
    composeTestRule.setContent {
      UserProfileScreen(
          navigationActions = navigationActions,
          rootMainNavigationActions = rootMainNavigationActions,
          preferencesViewModel = preferencesViewModel,
          userPreferencesViewModel = userPreferencesViewModel,
          appContentNavigationActions = appContentNavigationActions,
          modeViewModel = modeViewModel)
    }

    // Verify settings options
    val settingsOptions = listOf("AccountconfigurationOption", "Preferences", "SavedLists")
    settingsOptions.forEach { label -> composeTestRule.onNodeWithTag(label).assertIsDisplayed() }
  }

  @Test
  fun resourcesOptionsAreDisplayedCorrectly() {
    composeTestRule.setContent {
      UserProfileScreen(
          navigationActions = navigationActions,
          rootMainNavigationActions = rootMainNavigationActions,
          preferencesViewModel = preferencesViewModel,
          userPreferencesViewModel = userPreferencesViewModel,
          appContentNavigationActions = appContentNavigationActions,
          modeViewModel = modeViewModel)
    }

    // Verify resources options
    val resourcesOptions = listOf("Support", "Legal", "SetupyourbusinessaccountOption")
    resourcesOptions.forEach { label -> composeTestRule.onNodeWithTag(label).assertIsDisplayed() }
  }

  @Test
  fun workerModeSwitchIsNotDisplayedWhenUserIsNotWorker() {
    // Arrange: User is not a worker
    runBlocking { preferencesRepository.setPreference(IS_WORKER_KEY, false) }

    // Act
    composeTestRule.setContent {
      UserProfileScreen(
          navigationActions = navigationActions,
          rootMainNavigationActions = rootMainNavigationActions,
          preferencesViewModel = preferencesViewModel,
          userPreferencesViewModel = userPreferencesViewModel,
          appContentNavigationActions = appContentNavigationActions,
          modeViewModel = modeViewModel)
    }

    // Assert: The worker mode switch should not be displayed since isWorker.value = false
    composeTestRule
        .onNodeWithTag(C.Tag.workerModeSwitch, useUnmergedTree = true)
        .assertDoesNotExist()

    composeTestRule.onNodeWithTag(C.Tag.buttonSwitch, useUnmergedTree = true).assertDoesNotExist()
  }

  @Test
  fun workerModeSwitchIsDisplayedWhenUserIsWorker() {
    // Arrange: User is a worker
    runBlocking { preferencesRepository.setPreference(IS_WORKER_KEY, true) }

    // Act
    composeTestRule.setContent {
      UserProfileScreen(
          navigationActions = navigationActions,
          rootMainNavigationActions = rootMainNavigationActions,
          preferencesViewModel = preferencesViewModel,
          userPreferencesViewModel = userPreferencesViewModel,
          appContentNavigationActions = appContentNavigationActions,
          modeViewModel = modeViewModel)
    }

    // Assert: The worker mode switch and related UI should be displayed
    composeTestRule
        .onNodeWithTag(C.Tag.workerModeSwitch, useUnmergedTree = true)
        .assertIsDisplayed()
    composeTestRule
        .onNodeWithTag(C.Tag.workerModeSwitchText, useUnmergedTree = true)
        .assertIsDisplayed()
    composeTestRule.onNodeWithTag(C.Tag.buttonSwitch, useUnmergedTree = true).assertIsDisplayed()
  }

  @Test
  fun togglingWorkerModeSwitchNavigatesToWorkerModeAndChangesMode() {
    // Arrange: User is a worker
    runBlocking { preferencesRepository.setPreference(IS_WORKER_KEY, true) }

    // Act
    composeTestRule.setContent {
      UserProfileScreen(
          navigationActions = navigationActions,
          rootMainNavigationActions = rootMainNavigationActions,
          preferencesViewModel = preferencesViewModel,
          userPreferencesViewModel = userPreferencesViewModel,
          appContentNavigationActions = appContentNavigationActions,
          modeViewModel = modeViewModel)
    }

    // Assert initial state
    composeTestRule.onNodeWithTag(C.Tag.buttonSwitch, useUnmergedTree = true).assertIsDisplayed()

    // Act: Perform a click on the switch
    composeTestRule.onNodeWithTag(C.Tag.buttonSwitch, useUnmergedTree = true).performClick()

    // Verify that navigation and mode switch actions are triggered
    verify(appContentNavigationActions).navigateTo(AppContentRoute.WORKER_MODE)
    assert(modeViewModel.currentMode.value == AppMode.WORKER)
  }
}

 */
