package com.arygm.quickfix.ui.profile

import android.util.Log
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import com.arygm.quickfix.model.account.Account
import com.arygm.quickfix.model.account.LoggedInAccountViewModel
import com.arygm.quickfix.model.offline.small.PreferencesRepository
import com.arygm.quickfix.model.offline.small.PreferencesViewModel
import com.arygm.quickfix.model.profile.UserProfileRepositoryFirestore
import com.arygm.quickfix.model.profile.WorkerProfileRepositoryFirestore
import com.arygm.quickfix.model.switchModes.AppMode
import com.arygm.quickfix.model.switchModes.ModeViewModel
import com.arygm.quickfix.ressources.C
import com.arygm.quickfix.ui.navigation.NavigationActions
import com.arygm.quickfix.ui.navigation.RootRoute
import com.arygm.quickfix.ui.uiMode.appContentUI.navigation.AppContentRoute
import com.arygm.quickfix.ui.uiMode.appContentUI.userModeUI.profile.ProfileScreen
import com.arygm.quickfix.utils.EMAIL_KEY
import com.arygm.quickfix.utils.FIRST_NAME_KEY
import com.arygm.quickfix.utils.IS_WORKER_KEY
import com.arygm.quickfix.utils.LAST_NAME_KEY
import com.arygm.quickfix.utils.WALLET_KEY
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.flow.flowOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.kotlin.whenever

class ProfileScreenTest {

  @get:Rule val composeTestRule = createComposeRule()

  // Declare lateinit variables for dependencies
  private lateinit var mockFirestore: FirebaseFirestore
  private lateinit var navigationActions: NavigationActions
  private lateinit var userProfileRepositoryFirestore: UserProfileRepositoryFirestore
  private lateinit var workerProfileRepositoryFirestore: WorkerProfileRepositoryFirestore
  private lateinit var loggedInAccountViewModel: LoggedInAccountViewModel
  private lateinit var firebaseAuth: FirebaseAuth
  private lateinit var rootMainNavigationActions: NavigationActions
  private lateinit var preferencesViewModel: PreferencesViewModel
  private lateinit var preferencesRepository: PreferencesRepository
  private lateinit var userPreferencesViewModel: PreferencesViewModel
  private lateinit var userPreferencesRepository: PreferencesRepository
  private lateinit var mockStorage: FirebaseStorage
  private lateinit var appContentNavigationActions: NavigationActions
  private lateinit var modeViewModel: ModeViewModel
  private lateinit var mockReference: StorageReference

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
    // Mock dependencies
    mockReference = mock(StorageReference::class.java)
    appContentNavigationActions = mock(NavigationActions::class.java)
    mockStorage = mock(FirebaseStorage::class.java)
    userPreferencesRepository = mock(PreferencesRepository::class.java)
    preferencesRepository = mock(PreferencesRepository::class.java)
    userPreferencesViewModel = PreferencesViewModel(userPreferencesRepository)
    preferencesViewModel = PreferencesViewModel(preferencesRepository)
    rootMainNavigationActions = mock(NavigationActions::class.java)
    mockFirestore = mock(FirebaseFirestore::class.java)
    navigationActions = mock(NavigationActions::class.java)
    firebaseAuth = mock(FirebaseAuth::class.java) // Mock FirebaseAuth
    modeViewModel = ModeViewModel()

    whenever(mockStorage.reference).thenReturn(mockReference)
    // Create real repository instances using mocked Firestore
    userProfileRepositoryFirestore = UserProfileRepositoryFirestore(mockFirestore, mockStorage)
    workerProfileRepositoryFirestore = WorkerProfileRepositoryFirestore(mockFirestore, mockStorage)

    // Create the actual LoggedInAccountViewModel with the repositories
    loggedInAccountViewModel =
        LoggedInAccountViewModel(
            userProfileRepo = userProfileRepositoryFirestore,
            workerProfileRepo = workerProfileRepositoryFirestore)

    // Explicitly mock specific keys if needed
    whenever(preferencesRepository.getPreferenceByKey(com.arygm.quickfix.utils.FIRST_NAME_KEY))
        .thenReturn(flowOf("John"))
    Log.d("ProfileTest", "setup: first name key")
    whenever(preferencesRepository.getPreferenceByKey(com.arygm.quickfix.utils.LAST_NAME_KEY))
        .thenReturn(flowOf("Doe"))
    Log.d("ProfileTest", "setup: last name key")
    whenever(preferencesRepository.getPreferenceByKey(com.arygm.quickfix.utils.EMAIL_KEY))
        .thenReturn(flowOf("mail"))
    Log.d("ProfileTest", "setup: email key")
    whenever(preferencesRepository.getPreferenceByKey(com.arygm.quickfix.utils.IS_WORKER_KEY))
        .thenReturn(flowOf(false))
    Log.d("ProfileTest", "setup: is worker key")
    whenever(userPreferencesRepository.getPreferenceByKey(com.arygm.quickfix.utils.WALLET_KEY))
        .thenReturn(flowOf(0.0))
    Log.d("ProfileTest", "setup: wallet key")
    // Mock navigation actions for testing navigation behavior
  }

  @Test
  fun profileScreenDisplaysCorrectly() {
    composeTestRule.setContent {
      ProfileScreen(
          navigationActions,
          rootMainNavigationActions,
          preferencesViewModel,
          userPreferencesViewModel,
          appContentNavigationActions,
          modeViewModel)
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
      ProfileScreen(
          navigationActions,
          rootMainNavigationActions,
          preferencesViewModel,
          userPreferencesViewModel,
          appContentNavigationActions,
          modeViewModel)
    }

    // Perform click on logout button
    composeTestRule.onNodeWithTag("LogoutButton").performClick()

    // Verify navigation to the welcome screen
    verify(rootMainNavigationActions).navigateTo(RootRoute.NO_MODE)
  }

  @Test
  fun settingsOptionsAreDisplayedCorrectly() {
    composeTestRule.setContent {
      ProfileScreen(
          navigationActions,
          rootMainNavigationActions,
          preferencesViewModel,
          userPreferencesViewModel,
          appContentNavigationActions,
          modeViewModel)
    }

    // Verify settings options
    val settingsOptions = listOf("AccountconfigurationOption", "Preferences", "SavedLists")
    settingsOptions.forEach { label -> composeTestRule.onNodeWithTag(label).assertIsDisplayed() }
  }

  @Test
  fun resourcesOptionsAreDisplayedCorrectly() {
    composeTestRule.setContent {
      ProfileScreen(
          navigationActions,
          rootMainNavigationActions,
          preferencesViewModel,
          userPreferencesViewModel,
          appContentNavigationActions,
          modeViewModel)
    }

    // Verify resources options
    val resourcesOptions = listOf("Support", "Legal", "SetupyourbusinessaccountOption")
    resourcesOptions.forEach { label -> composeTestRule.onNodeWithTag(label).assertIsDisplayed() }
  }

  @Test
  fun workerModeSwitchIsNotDisplayedWhenUserIsNotWorker() {
    // Arrange: Mock IS_WORKER_KEY to return false to simulate a user who is not a worker
    whenever(preferencesRepository.getPreferenceByKey(IS_WORKER_KEY)).thenReturn(flowOf(false))

    // Act
    composeTestRule.setContent {
      ProfileScreen(
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
    // Arrange: Mock IS_WORKER_KEY to return true so the user is considered a worker
    whenever(preferencesRepository.getPreferenceByKey(IS_WORKER_KEY)).thenReturn(flowOf(true))

    // Act
    composeTestRule.setContent {
      ProfileScreen(
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
    // Arrange: Mock IS_WORKER_KEY to true to ensure the switch is displayed
    whenever(preferencesRepository.getPreferenceByKey(IS_WORKER_KEY)).thenReturn(flowOf(true))
    // Act
    composeTestRule.setContent {
      ProfileScreen(
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
