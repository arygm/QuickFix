package com.arygm.quickfix.ui.authentication

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.arygm.quickfix.model.account.AccountRepository
import com.arygm.quickfix.model.account.AccountViewModel
import com.arygm.quickfix.model.offline.small.PreferencesRepository
import com.arygm.quickfix.model.offline.small.PreferencesViewModel
import com.arygm.quickfix.model.offline.small.PreferencesViewModelUserProfile
import com.arygm.quickfix.model.profile.ProfileRepository
import com.arygm.quickfix.model.profile.ProfileViewModel
import com.arygm.quickfix.ui.navigation.NavigationActions
import com.arygm.quickfix.ui.navigation.RootRoute
import com.arygm.quickfix.ui.uiMode.noModeUI.authentication.GoogleInfoScreen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.*

@RunWith(AndroidJUnit4::class)
class GoogleInfoUserNoModeScreenTest {

  @get:Rule val composeTestRule = createComposeRule()
  private lateinit var navigationActions: NavigationActions
  private lateinit var accountRepository: AccountRepository
  private lateinit var accountViewModel: AccountViewModel
  private lateinit var profileRepository: ProfileRepository
  private lateinit var profileViewModel: ProfileViewModel
  private lateinit var preferencesRepository: PreferencesRepository
  private lateinit var preferencesViewModel: PreferencesViewModel
  private lateinit var rootNavigationActions: NavigationActions
  private lateinit var userPreferencesViewModel: PreferencesViewModelUserProfile
  private lateinit var userPreferencesRepository: PreferencesRepository

  private val USER_ID_KEY = stringPreferencesKey("user_id")
  private val EMAIL_KEY = stringPreferencesKey("email")

  @Before
  fun setup() {
    // Initialize the repositories and view models
    userPreferencesRepository = mock()
    navigationActions = mock()
    accountRepository = mock()
    profileRepository = mock()
    preferencesRepository = TestPreferencesRepository()
    userPreferencesViewModel = PreferencesViewModelUserProfile(userPreferencesRepository)

    accountViewModel = AccountViewModel(accountRepository)

    rootNavigationActions = mock()

    profileViewModel = ProfileViewModel(profileRepository)
    preferencesViewModel = PreferencesViewModel(preferencesRepository)
    // Set preferences
    runBlocking {
      preferencesRepository.setPreferenceByKey(USER_ID_KEY, "test_uid")
      preferencesRepository.setPreferenceByKey(EMAIL_KEY, "test@example.com")
    }

    // Mock accountRepository.updateAccount to call onSuccess
    doAnswer {
          val onSuccess = it.arguments[1] as () -> Unit
          onSuccess()
          null
        }
        .whenever(accountRepository)
        .updateAccount(any(), any(), any())

    // Mock accountRepository.deleteAccountById
    doAnswer {
          val onSuccess = it.arguments[1] as () -> Unit
          onSuccess()
          null
        }
        .whenever(accountRepository)
        .deleteAccountById(any(), any(), any())

    // Mock profileRepository.deleteProfileById
    doAnswer {
          val onSuccess = it.arguments[1] as () -> Unit
          onSuccess()
          null
        }
        .whenever(profileRepository)
        .deleteProfileById(any(), any(), any())
  }

  @Test
  fun testInitialState() {
    composeTestRule.setContent {
      GoogleInfoScreen(
          rootNavigationActions = rootNavigationActions,
          accountViewModel = accountViewModel,
          userViewModel = profileViewModel,
          preferencesViewModel = preferencesViewModel,
          navigationActions = navigationActions,
          userPreferencesViewModel = userPreferencesViewModel)
    }

    composeTestRule.waitForIdle()

    // Verify initial UI state
    composeTestRule.onNodeWithTag("InfoBox").assertIsDisplayed()
    composeTestRule.onNodeWithTag("goBackButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("contentBox").assertIsDisplayed()
    composeTestRule.onNodeWithTag("decorationBox").assertIsDisplayed()
    composeTestRule.onNodeWithTag("welcomeText").assertIsDisplayed()
    composeTestRule.onNodeWithTag("firstNameInput").assertIsDisplayed()
    composeTestRule.onNodeWithTag("lastNameInput").assertIsDisplayed()
    composeTestRule.onNodeWithTag("birthDateInput").assertIsDisplayed()

    // Verify "Next" button is disabled initially
    composeTestRule.onNodeWithTag("nextButton").assertIsDisplayed().assertIsNotEnabled()
  }

  @Test
  fun testInvalidDateShowsError() {
    composeTestRule.setContent {
      GoogleInfoScreen(
          rootNavigationActions = rootNavigationActions,
          accountViewModel = accountViewModel,
          userViewModel = profileViewModel,
          preferencesViewModel = preferencesViewModel,
          navigationActions = navigationActions,
          userPreferencesViewModel = userPreferencesViewModel)
    }
    composeTestRule.waitForIdle()

    // Enter an invalid date
    composeTestRule.onNodeWithTag("birthDateInput").performTextInput("99/99/9999")

    // Verify error message and button state
    composeTestRule.onNodeWithText("INVALID DATE").assertIsDisplayed()
    composeTestRule.onNodeWithTag("nextButton").assertIsNotEnabled()
  }

  @Test
  fun testNextButtonEnabledWhenFormIsValid() {
    composeTestRule.setContent {
      GoogleInfoScreen(
          rootNavigationActions = rootNavigationActions,
          accountViewModel = accountViewModel,
          userViewModel = profileViewModel,
          preferencesViewModel = preferencesViewModel,
          navigationActions = navigationActions,
          userPreferencesViewModel = userPreferencesViewModel)
    }
    composeTestRule.waitForIdle()

    // Fill out the form
    composeTestRule.onNodeWithTag("firstNameInput").performTextInput("John")
    composeTestRule.onNodeWithTag("lastNameInput").performTextInput("Doe")
    composeTestRule.onNodeWithTag("birthDateInput").performTextInput("01/01/1990")

    // Verify "Next" button is enabled
    composeTestRule.onNodeWithTag("nextButton").assertIsEnabled()

    // Click the "Next" button
    composeTestRule.onNodeWithTag("nextButton").performClick()

    // Verify account update and navigation
    verify(accountRepository).updateAccount(any(), any(), any())
    verify(rootNavigationActions).navigateTo(RootRoute.APP_CONTENT)
  }
}

// Test implementation of PreferencesRepository
class TestPreferencesRepository : PreferencesRepository {
  private val preferencesMap = mutableMapOf<Preferences.Key<*>, Any?>()

  override fun <T> getPreferenceByKey(key: Preferences.Key<T>) =
      MutableStateFlow(preferencesMap[key] as? T)

  override suspend fun <T> setPreferenceByKey(key: Preferences.Key<T>, value: T) {
    preferencesMap[key] = value
  }

  override suspend fun clearPreferences() {
    preferencesMap.clear()
  }
}
