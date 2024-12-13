package com.arygm.quickfix.ui.profile.becomeWorker.views.welcome

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.arygm.quickfix.model.offline.small.PreferencesViewModel
import com.arygm.quickfix.model.offline.small.PreferencesViewModelUserProfile
import com.arygm.quickfix.ressources.C
import com.arygm.quickfix.ui.navigation.NavigationActions
import com.arygm.quickfix.ui.profile.FakePreferencesRepository
import com.arygm.quickfix.ui.theme.QuickFixTheme
import com.arygm.quickfix.ui.userModeUI.navigation.UserScreen
import com.arygm.quickfix.utils.IS_WORKER_KEY
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.*

class WelcomeOnBoardUserNoModeScreenTest {

  @get:Rule val composeTestRule = createComposeRule()

  // Declare Mockito mocks for NavigationActions
  private lateinit var navigationActions: NavigationActions
  private lateinit var rootMainNavigationActions: NavigationActions
  private lateinit var appContentNavigationActions: NavigationActions

  // Initialize FakePreferencesRepository
  private lateinit var preferencesRepository: FakePreferencesRepository
  private lateinit var userPreferencesRepository: FakePreferencesRepository

  // Initialize ViewModels
  private lateinit var preferencesViewModel: PreferencesViewModel
  private lateinit var userPreferencesViewModel: PreferencesViewModelUserProfile

  @Before
  fun setup() {
    // Initialize Mockito mocks for NavigationActions
    navigationActions = mock(NavigationActions::class.java)
    rootMainNavigationActions = mock(NavigationActions::class.java)
    appContentNavigationActions = mock(NavigationActions::class.java)

    // Initialize FakePreferencesRepository
    preferencesRepository = FakePreferencesRepository()
    userPreferencesRepository = FakePreferencesRepository()

    // Set initial preferences using Preferences.Key<T> keys
    runBlocking { preferencesRepository.setPreference(IS_WORKER_KEY, true) }

    // Instantiate ViewModels with fake repositories
    preferencesViewModel = PreferencesViewModel(preferencesRepository)
    userPreferencesViewModel = PreferencesViewModelUserProfile(userPreferencesRepository)
  }

  @Test
  fun testInitialUI() {
    composeTestRule.setContent {
      QuickFixTheme {
        WelcomeOnBoardScreen(
            navigationActions = navigationActions, preferencesViewModel = preferencesViewModel)
      }
    }

    // Check UI elements are displayed
    composeTestRule.onNodeWithTag(C.Tag.welcomeOnBoardScreenStayUserButton).assertIsDisplayed()
    composeTestRule.onNodeWithTag(C.Tag.welcomeOnBoardScreenSwitchWorkerButton).assertIsDisplayed()
    composeTestRule.onNodeWithTag(C.Tag.welcomeOnBoardScreenImage).assertIsDisplayed()
    composeTestRule.onNodeWithText("Welcome on board !!").assertIsDisplayed()
  }

  @Test
  fun testInitialNavigationStayUser() = runBlocking {
    composeTestRule.setContent {
      QuickFixTheme {
        WelcomeOnBoardScreen(
            navigationActions = navigationActions,
            preferencesViewModel = preferencesViewModel,
            testingFlag = true)
      }
    }
    runBlocking { preferencesRepository.setPreference(IS_WORKER_KEY, true) }
    // Perform click on "Stay User Mode" button
    composeTestRule.onNodeWithTag(C.Tag.welcomeOnBoardScreenStayUserButton).performClick()

    // Verify navigation to UserScreen.PROFILE
    verify(navigationActions).navigateTo(UserScreen.PROFILE)
  }

  @Test
  fun testInitialNavigationSwitchWorker() = runBlocking {
    composeTestRule.setContent {
      QuickFixTheme {
        WelcomeOnBoardScreen(
            navigationActions = navigationActions,
            preferencesViewModel = preferencesViewModel,
            testingFlag = true)
      }
    }
    runBlocking { preferencesRepository.setPreference(IS_WORKER_KEY, true) }
    // Perform click on "Stay User Mode" button
    composeTestRule.onNodeWithTag(C.Tag.welcomeOnBoardScreenSwitchWorkerButton).performClick()

    // Verify navigation to UserScreen.PROFILE
    verify(navigationActions).navigateTo(UserScreen.PROFILE)
  }
}
