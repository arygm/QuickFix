package com.arygm.quickfix.ui.profile

import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.datastore.preferences.core.Preferences
import com.arygm.quickfix.R
import com.arygm.quickfix.model.offline.small.PreferencesRepository
import com.arygm.quickfix.model.offline.small.PreferencesViewModel
import com.arygm.quickfix.model.profile.UserProfileRepositoryFirestore
import com.arygm.quickfix.model.profile.WorkerProfileRepositoryFirestore
import com.arygm.quickfix.ui.navigation.NavigationActions
import com.arygm.quickfix.ui.navigation.UserScreen
import com.arygm.quickfix.ui.userModeUI.navigation.UserScreen
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.flow.flowOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class ProfileUserNoModeScreenTest {

  @get:Rule val composeTestRule = createComposeRule()

  private lateinit var navigationActions: NavigationActions
  private lateinit var mockFirestore: FirebaseFirestore
  private lateinit var userProfileRepositoryFirestore: UserProfileRepositoryFirestore
  private lateinit var workerProfileRepositoryFirestore: WorkerProfileRepositoryFirestore
  private lateinit var navigationActionsRoot: NavigationActions
  private lateinit var preferencesRepository: PreferencesRepository
  private lateinit var preferencesViewModel: PreferencesViewModel
  private lateinit var mockStorage: FirebaseStorage
  @Mock private lateinit var storageRef: StorageReference

  private val options =
      listOf(
          OptionItem("Settings", IconType.Vector(Icons.Outlined.Settings)) {},
          OptionItem("Activity", IconType.Resource(R.drawable.dashboardvector)) {},
          OptionItem("Set up your business account", IconType.Resource(R.drawable.workvector)) {},
          OptionItem("Account configuration", IconType.Resource(R.drawable.accountsettingsvector)) {
            navigationActions.navigateTo(UserScreen.ACCOUNT_CONFIGURATION)
            Log.d("userResult", navigationActions.currentRoute())
          },
          OptionItem("Workers network", IconType.Vector(Icons.Outlined.Phone)) {},
          OptionItem("Legal", IconType.Vector(Icons.Outlined.Info)) {})

  @Before
  fun setup() {
    mockStorage = mock(FirebaseStorage::class.java)
    storageRef = mock(StorageReference::class.java)
    whenever(mockStorage.reference).thenReturn(storageRef)
    mockFirestore = mock(FirebaseFirestore::class.java)
    navigationActions = mock(NavigationActions::class.java)
    navigationActionsRoot = mock(NavigationActions::class.java)
    workerProfileRepositoryFirestore = WorkerProfileRepositoryFirestore(mockFirestore, mockStorage)
    userProfileRepositoryFirestore = UserProfileRepositoryFirestore(mockFirestore, mockStorage)
    preferencesRepository = mock()
    preferencesViewModel = PreferencesViewModel(preferencesRepository)

    // Mock PreferencesRepository to return valid Flows
    whenever(preferencesRepository.getPreferenceByKey(any<Preferences.Key<String>>()))
        .thenReturn(flowOf("testValue"))

    // Explicitly mock specific keys if needed
    whenever(preferencesRepository.getPreferenceByKey(com.arygm.quickfix.utils.FIRST_NAME_KEY))
        .thenReturn(flowOf("John"))
    whenever(preferencesRepository.getPreferenceByKey(com.arygm.quickfix.utils.LAST_NAME_KEY))
        .thenReturn(flowOf("Doe"))
  }

  @Test
  fun profileScreenDisplaysCorrectly() {
    composeTestRule.setContent {
      ProfileScreen(navigationActions, navigationActionsRoot, preferencesViewModel)
    }

    // Test for profile title and name
    composeTestRule.onNodeWithTag("ProfileTopAppBar").assertIsDisplayed()
    composeTestRule.onNodeWithTag("ProfileTitle").assertTextEquals("Profile")
    composeTestRule.onNodeWithTag("ProfileCard").assertIsDisplayed()

    // Test for Upcoming Activities section
    composeTestRule.onNodeWithTag("UpcomingActivitiesCard").assertIsDisplayed()
    composeTestRule
        .onNodeWithTag("UpcomingActivitiesText")
        .assertTextEquals(
            "This isn’t developed yet; but it can display upcoming activities for both a user and worker")

    // Test for Wallet and Help buttons
    composeTestRule.onNodeWithTag("WalletButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("WalletText", useUnmergedTree = true).assertTextEquals("Wallet")
    composeTestRule.onNodeWithTag("HelpButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("HelpText", useUnmergedTree = true).assertTextEquals("Help")

    // Scroll to the Logout Button
    composeTestRule.onNodeWithTag("LogoutButton").performScrollTo()
    composeTestRule.onNodeWithTag("LogoutButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("LogoutText", useUnmergedTree = true).assertTextEquals("Log out")
  }

  @Test
  fun walletButtonClickTest() {
    composeTestRule.setContent {
      ProfileScreen(navigationActions, navigationActionsRoot, preferencesViewModel)
    }

    composeTestRule.onNodeWithTag("WalletButton").performClick()
  }

  @Test
  fun helpButtonClickTest() {
    composeTestRule.setContent {
      ProfileScreen(navigationActions, navigationActionsRoot, preferencesViewModel)
    }

    composeTestRule.onNodeWithTag("HelpButton").performClick()
  }

  @Test
  fun optionsAreDisplayedCorrectly() {
    composeTestRule.setContent {
      ProfileScreen(navigationActions, navigationActionsRoot, preferencesViewModel)
    }

    options.forEach { option ->
      val optionTag = option.label.replace(" ", "") + "Option"
      composeTestRule.onNodeWithTag(optionTag, useUnmergedTree = true).assertIsDisplayed()
      val optionTextTag = option.label.replace(" ", "") + "Text"
      composeTestRule
          .onNodeWithTag(optionTextTag, useUnmergedTree = true)
          .assertTextEquals(option.label)
    }
  }

  @Test
  fun logoutButtonClickTest() {
    composeTestRule.setContent {
      ProfileScreen(navigationActions, navigationActionsRoot, preferencesViewModel)
    }

    composeTestRule.onNodeWithTag("LogoutButton").performClick()
  }

  @Test
  fun navigateToAccountConfigurationTest() {
    composeTestRule.setContent {
      ProfileScreen(navigationActions, navigationActionsRoot, preferencesViewModel)
    }

    // Perform click on "Account configuration"
    composeTestRule.onNodeWithTag("AccountconfigurationOption").performClick()

    // Verify that the navigation to Screen.ACCOUNT_CONFIGURATION happened
    verify(navigationActions).navigateTo(UserScreen.ACCOUNT_CONFIGURATION)
  }

  @Test
  fun navigateToWorkerSetupTest() {
    composeTestRule.setContent {
      ProfileScreen(navigationActions, navigationActionsRoot, preferencesViewModel)
    }

    // Perform click on "Set up your business account"
    composeTestRule.onNodeWithTag("SetupyourbusinessaccountOption").performClick()

    // Verify that the navigation to Screen.TO_WORKER happened
    verify(navigationActions).navigateTo(UserScreen.TO_WORKER)
  }
}
