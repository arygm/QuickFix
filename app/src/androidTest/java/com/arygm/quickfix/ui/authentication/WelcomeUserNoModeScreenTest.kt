package com.arygm.quickfix.ui.authentication

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.datastore.preferences.core.Preferences
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.arygm.quickfix.model.account.AccountRepository
import com.arygm.quickfix.model.account.AccountViewModel
import com.arygm.quickfix.model.offline.small.PreferencesRepository
import com.arygm.quickfix.model.offline.small.PreferencesViewModel
import com.arygm.quickfix.model.profile.ProfileViewModel
import com.arygm.quickfix.model.profile.UserProfileRepositoryFirestore
import com.arygm.quickfix.model.profile.WorkerProfileRepositoryFirestore
import com.arygm.quickfix.ui.navigation.NavigationActions
import com.arygm.quickfix.ui.noModeUI.navigation.NoModeRoute
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.flow.flowOf
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@RunWith(AndroidJUnit4::class)
class WelcomeUserNoModeScreenTest {

  @get:Rule val composeTestRule = createAndroidComposeRule<ComponentActivity>()

  private lateinit var rootNavigationActions: NavigationActions
  private lateinit var navigationActions: NavigationActions
  private lateinit var mockFirestore: FirebaseFirestore
  private lateinit var accountRepository: AccountRepository
  private lateinit var accountViewModel: AccountViewModel
  private lateinit var userProfileRepositoryFirestore: UserProfileRepositoryFirestore
  private lateinit var workerProfileRepositoryFirestore: WorkerProfileRepositoryFirestore
  private lateinit var userViewModel: ProfileViewModel
  private lateinit var preferencesRepository: PreferencesRepository
  private lateinit var preferencesViewModel: PreferencesViewModel
  private lateinit var mockStorage: FirebaseStorage
  private lateinit var userPreferencesViewModel: PreferencesViewModel
  private lateinit var userPreferencesRepository: PreferencesRepository
  @Mock private lateinit var storageRef: StorageReference

  private var intentsInitialized = false // Keep track of Intents initialization

  @Before
  fun setup() {
    userPreferencesRepository = mock()
    userPreferencesViewModel = PreferencesViewModel(userPreferencesRepository)
    rootNavigationActions = mock()
    mockFirestore = mock()
    navigationActions = mock()
    mockStorage = Mockito.mock(FirebaseStorage::class.java)
    storageRef = Mockito.mock(StorageReference::class.java)
    whenever(mockStorage.reference).thenReturn(storageRef)
    userProfileRepositoryFirestore = UserProfileRepositoryFirestore(mockFirestore, mockStorage)
    workerProfileRepositoryFirestore = WorkerProfileRepositoryFirestore(mockFirestore, mockStorage)
    accountRepository = mock()
    accountViewModel = AccountViewModel(accountRepository)
    userViewModel = ProfileViewModel(userProfileRepositoryFirestore)
    preferencesRepository = mock()
    preferencesViewModel = PreferencesViewModel(preferencesRepository)

    // Explicitly specify the type for getPreferenceByKey
    whenever(preferencesRepository.getPreferenceByKey(any<Preferences.Key<Boolean>>()))
        .thenReturn(flowOf(false))

    whenever(navigationActions.currentRoute()).thenReturn(NoModeRoute.WELCOME)
  }

  @After
  fun tearDown() {
    // Only release Intents if they were initialized
    if (intentsInitialized) {
      Intents.release()
      intentsInitialized = false
    }
  }

  @Test
  fun testInitialState() {
    composeTestRule.setContent {
      WelcomeScreen(
          navigationActions,
          accountViewModel,
          userViewModel,
          preferencesViewModel,
          rootNavigationActions,
          userPreferencesViewModel)
    }

    // Check if the background image is displayed
    composeTestRule.onNodeWithTag("welcomeBox").assertIsDisplayed()
    composeTestRule.onNodeWithTag("boxDecoration1").assertIsDisplayed()
    composeTestRule.onNodeWithTag("workerBackground").assertIsDisplayed()

    // Check that the QuickFix logo is displayed
    composeTestRule.onNodeWithTag("quickFixLogo").assertIsDisplayed()

    // Check that the QuickFix text is displayed
    composeTestRule.onNodeWithTag("quickFixText").assertIsDisplayed()
    composeTestRule.onNodeWithTag("quickFixText").assertTextEquals("QuickFix")

    // Check that the buttons are displayed
    composeTestRule.onNodeWithTag("logInButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("logInButton").assertHasClickAction()
    composeTestRule.onNodeWithTag("logInButton").assertTextEquals("LOG IN TO QUICKFIX")
    composeTestRule.onNodeWithTag("RegistrationButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("RegistrationButton").assertHasClickAction()
    composeTestRule.onNodeWithTag("RegistrationButton").assertTextEquals("REGISTER TO QUICKFIX")

    // Check if Google button and logo are displayed
    composeTestRule.onNodeWithTag("googleButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("googleButton").assertTextEquals("CONTINUE WITH GOOGLE")
  }

  @Test
  fun testLogInButtonClickNavigatesToLogin() {
    composeTestRule.setContent {
      WelcomeScreen(
          navigationActions,
          accountViewModel,
          userViewModel,
          preferencesViewModel,
          rootNavigationActions,
          userPreferencesViewModel)
    }

    // Click the "LOG IN TO QUICKFIX" button
    composeTestRule.onNodeWithTag("logInButton").performClick()

    composeTestRule.waitUntil(timeoutMillis = 10000) {
      Mockito.mockingDetails(navigationActions).invocations.isNotEmpty()
    }

    // Verify that the navigation action is triggered for the login screen
    Mockito.verify(navigationActions).navigateTo(NoModeRoute.LOGIN)
  }

  @Test
  fun testRegistrationButtonClickNavigatesToRegister() {
    composeTestRule.setContent {
      WelcomeScreen(
          navigationActions,
          accountViewModel,
          userViewModel,
          preferencesViewModel,
          rootNavigationActions,
          userPreferencesViewModel)
    }

    // Click the "REGISTER TO QUICKFIX" button
    composeTestRule.onNodeWithTag("RegistrationButton").performClick()

    composeTestRule.waitUntil(timeoutMillis = 10000) {
      Mockito.mockingDetails(navigationActions).invocations.isNotEmpty()
    }

    // Verify that the navigation action is triggered for the registration/info screen
    Mockito.verify(navigationActions).navigateTo(NoModeRoute.REGISTER)
  }

  @Test
  fun testGoogleButtonClickSendsIntent() {
    // Initialize Intents for this test
    Intents.init()
    intentsInitialized = true // Mark Intents as initialized

    composeTestRule.setContent {
      WelcomeScreen(
          navigationActions,
          accountViewModel,
          userViewModel,
          preferencesViewModel,
          rootNavigationActions,
          userPreferencesViewModel)
    }

    // Perform click on the Google Sign-In button
    composeTestRule.onNodeWithTag("googleButton").performClick()

    // Assert that an Intent resolving to Google Mobile Services has been sent
    Intents.intended(IntentMatchers.toPackage("com.google.android.gms"))
  }
}
