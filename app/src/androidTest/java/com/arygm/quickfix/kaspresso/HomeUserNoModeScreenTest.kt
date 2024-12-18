package com.arygm.quickfix.kaspresso

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotFocused
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.printToLog
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.arygm.quickfix.kaspresso.screen.HomeScreenObject
import com.arygm.quickfix.model.offline.small.PreferencesRepositoryDataStore
import com.arygm.quickfix.model.offline.small.PreferencesViewModel
import com.arygm.quickfix.model.profile.ProfileRepository
import com.arygm.quickfix.model.profile.ProfileViewModel
import com.arygm.quickfix.model.quickfix.QuickFixRepository
import com.arygm.quickfix.model.quickfix.QuickFixViewModel
import com.arygm.quickfix.ui.navigation.NavigationActions
import com.arygm.quickfix.ui.uiMode.appContentUI.userModeUI.home.HomeScreen
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.github.kakaocup.compose.node.element.ComposeScreen
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.kotlin.whenever

@RunWith(AndroidJUnit4::class)
class HomeUserNoModeScreenTest : TestCase() {

  @get:Rule val composeTestRule = createComposeRule()

  // Class-level MutableStateFlows for userId and appMode
  private val userIdFlow = MutableStateFlow("testUserId")
  private val appModeFlow = MutableStateFlow("USER")

  private lateinit var navigationActions: NavigationActions
  private lateinit var preferencesRepositoryDataStore: PreferencesRepositoryDataStore
  private lateinit var preferencesViewModel: PreferencesViewModel
  private lateinit var profileRepository: ProfileRepository
  private lateinit var userViewModel: ProfileViewModel
  private lateinit var workerViewModel: ProfileViewModel
  private lateinit var quickFixRepository: QuickFixRepository
  private lateinit var quickFixViewModel: QuickFixViewModel

  @Before
  fun setUp() {
    navigationActions = mock(NavigationActions::class.java)
    preferencesRepositoryDataStore = mock(PreferencesRepositoryDataStore::class.java)
    preferencesViewModel = PreferencesViewModel(preferencesRepositoryDataStore)
    profileRepository = mock(ProfileRepository::class.java)
    userViewModel = ProfileViewModel(profileRepository)
    workerViewModel = ProfileViewModel(profileRepository)
    quickFixRepository = mock(QuickFixRepository::class.java)
    quickFixViewModel = QuickFixViewModel(quickFixRepository)
    // Mock getPreferenceByKey for user_id
    val userIdKey = stringPreferencesKey("user_id")
    whenever(preferencesRepositoryDataStore.getPreferenceByKey(userIdKey)).thenReturn(userIdFlow)

    // Mock getPreferenceByKey for app_mode
    val appModeKey = stringPreferencesKey("app_mode")
    whenever(preferencesRepositoryDataStore.getPreferenceByKey(appModeKey)).thenReturn(appModeFlow)
  }

  @Test
  fun testHomeScreen() = run {
    step("Set up the HomeScreen") {
      composeTestRule.setContent {
        HomeScreen(
            navigationActions,
            preferencesViewModel,
            userViewModel,
            workerViewModel,
            quickFixViewModel)
      }
    }

    // Step 1: Check UI elements on HomeScreen
    step("Check UI elements on HomeScreen") {
      // You can add assertions and interactions here
      ComposeScreen.onComposeScreen<HomeScreenObject>(composeTestRule) {
        composeTestRule.onRoot().printToLog("TAG")
        notification { assertIsDisplayed() }
        searchBar { assertIsDisplayed() }
      }
    }

    // Step 2: Click inside the search bar to gain focus
    step("Click inside the search bar to gain focus") {
      composeTestRule.onNodeWithTag("searchBar").performClick()
    }

    // Step 3: Simulate clicking outside the search bar to lose focus
    step("Click outside the search bar") {
      composeTestRule.onNodeWithTag("homeContent").performClick()
    }

    // Step 4: Click on the floating action button to open the QuickFixToolbox
    step("Click on the floating action button to open the QuickFixToolbox") {
      composeTestRule.onNodeWithTag("ToolboxFloatingButton").performClick()
    }

    // Step 5: Click on the floating action button again to close the QuickFixToolbox
    step("Click on the floating action button to open the QuickFixToolbox") {
      composeTestRule.onNodeWithTag("ToolboxFloatingButton").performClick()
    }

    // Step 6: Assert that the search bar has lost focus
    step("Assert the search bar has lost focus") {
      composeTestRule.onNodeWithTag("searchBar").assertIsNotFocused()
    }
    step("Verify Popular Services and Upcoming QuickFixes sections are displayed") {
      // Verify Popular Services title is displayed
      composeTestRule.onNodeWithTag("PopularServicesRow").assertIsDisplayed()

      // Verify Upcoming QuickFixes title is displayed
      composeTestRule.onNodeWithTag("UpcomingQuickFixes").assertIsDisplayed()
    }
  }
}
