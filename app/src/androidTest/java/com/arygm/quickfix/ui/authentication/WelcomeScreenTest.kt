package com.arygm.quickfix.ui.authentication

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.arygm.quickfix.model.account.AccountRepository
import com.arygm.quickfix.model.account.AccountViewModel
import com.arygm.quickfix.model.account.LoggedInAccountViewModel
import com.arygm.quickfix.model.profile.ProfileViewModel
import com.arygm.quickfix.model.profile.UserProfileRepositoryFirestore
import com.arygm.quickfix.model.profile.WorkerProfileRepositoryFirestore
import com.arygm.quickfix.ui.navigation.NavigationActions
import com.arygm.quickfix.ui.navigation.Screen
import com.google.firebase.firestore.FirebaseFirestore
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

@RunWith(AndroidJUnit4::class)
class WelcomeScreenTest {

  @get:Rule val composeTestRule = createAndroidComposeRule<ComponentActivity>()

  private lateinit var navigationActions: NavigationActions
  private lateinit var mockFirestore: FirebaseFirestore
  private lateinit var accountRepository: AccountRepository
  private lateinit var accountViewModel: AccountViewModel
  private lateinit var loggedInAccountViewModel: LoggedInAccountViewModel
  private lateinit var userProfileRepositoryFirestore: UserProfileRepositoryFirestore
  private lateinit var workerProfileRepositoryFirestore: WorkerProfileRepositoryFirestore
  private lateinit var userViewModel: ProfileViewModel

  private var intentsInitialized = false // Keep track of Intents initialization

  @Before
  fun setup() {
    mockFirestore = mock(FirebaseFirestore::class.java)
    navigationActions = mock(NavigationActions::class.java)
    userProfileRepositoryFirestore = UserProfileRepositoryFirestore(mockFirestore)
    workerProfileRepositoryFirestore = WorkerProfileRepositoryFirestore(mockFirestore)
    accountRepository = mock(AccountRepository::class.java)
    accountViewModel = AccountViewModel(accountRepository)
    userViewModel = ProfileViewModel(userProfileRepositoryFirestore)
    loggedInAccountViewModel =
        LoggedInAccountViewModel(
            userProfileRepo = userProfileRepositoryFirestore,
            workerProfileRepo = workerProfileRepositoryFirestore)

    `when`(navigationActions.currentRoute()).thenReturn(Screen.WELCOME)
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
      WelcomeScreen(navigationActions, accountViewModel, loggedInAccountViewModel, userViewModel)
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
      WelcomeScreen(navigationActions, accountViewModel, loggedInAccountViewModel, userViewModel)
    }

    // Click the "LOG IN TO QUICKFIX" button
    composeTestRule.onNodeWithTag("logInButton").performClick()

    composeTestRule.waitUntil(timeoutMillis = 10000) {
      Mockito.mockingDetails(navigationActions).invocations.isNotEmpty()
    }

    // Verify that the navigation action is triggered for the login screen
    Mockito.verify(navigationActions).navigateTo(Screen.LOGIN)
  }

  @Test
  fun testRegistrationButtonClickNavigatesToRegister() {
    composeTestRule.setContent {
      WelcomeScreen(navigationActions, accountViewModel, loggedInAccountViewModel, userViewModel)
    }

    // Click the "REGISTER TO QUICKFIX" button
    composeTestRule.onNodeWithTag("RegistrationButton").performClick()

    composeTestRule.waitUntil(timeoutMillis = 10000) {
      Mockito.mockingDetails(navigationActions).invocations.isNotEmpty()
    }

    // Verify that the navigation action is triggered for the registration/info screen
    Mockito.verify(navigationActions).navigateTo(Screen.REGISTER)
  }

  @Test
  fun testGoogleButtonClickSendsIntent() {
    // Initialize Intents for this test
    Intents.init()
    intentsInitialized = true // Mark Intents as initialized

    composeTestRule.setContent {
      WelcomeScreen(navigationActions, accountViewModel, loggedInAccountViewModel, userViewModel)
    }

    // Perform click on the Google Sign-In button
    composeTestRule.onNodeWithTag("googleButton").performClick()

    // Assert that an Intent resolving to Google Mobile Services has been sent
    Intents.intended(IntentMatchers.toPackage("com.google.android.gms"))
  }
}
