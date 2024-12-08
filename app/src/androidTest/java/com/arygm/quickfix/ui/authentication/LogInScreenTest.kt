package com.arygm.quickfix.ui.authentication

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.arygm.quickfix.model.account.AccountRepository
import com.arygm.quickfix.model.account.AccountViewModel
import com.arygm.quickfix.model.offline.small.PreferencesRepository
import com.arygm.quickfix.model.offline.small.PreferencesViewModel
import com.arygm.quickfix.model.profile.UserProfileRepositoryFirestore
import com.arygm.quickfix.model.profile.WorkerProfileRepositoryFirestore
import com.arygm.quickfix.ui.navigation.NavigationActions
import com.arygm.quickfix.ui.navigation.Screen
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.kotlin.whenever

class LogInScreenTest {

  @get:Rule val composeTestRule = createComposeRule()

  private lateinit var navigationActions: NavigationActions
  private lateinit var accountRepository: AccountRepository
  private lateinit var userProfileRepo: UserProfileRepositoryFirestore
  private lateinit var workerProfileRepo: WorkerProfileRepositoryFirestore
  private lateinit var accountViewModel: AccountViewModel
  private lateinit var mockFirestore: FirebaseFirestore
  private lateinit var mockStorage: FirebaseStorage
  private lateinit var preferencesRepository: PreferencesRepository
  private lateinit var preferencesViewModel: PreferencesViewModel
  @Mock private lateinit var storageRef: StorageReference

  @Before
  fun setup() {
    mockStorage = mock(FirebaseStorage::class.java)
    storageRef = mock(StorageReference::class.java)
    whenever(mockStorage.reference).thenReturn(storageRef)
    mockFirestore = mock(FirebaseFirestore::class.java)
    accountRepository = mock(AccountRepository::class.java)
    navigationActions = mock(NavigationActions::class.java)
    userProfileRepo = UserProfileRepositoryFirestore(mockFirestore, mockStorage)
    workerProfileRepo = WorkerProfileRepositoryFirestore(mockFirestore, mockStorage)
    accountViewModel = AccountViewModel(accountRepository)
    preferencesRepository = mock(PreferencesRepository::class.java)
    preferencesViewModel = PreferencesViewModel(preferencesRepository)

    `when`(navigationActions.currentRoute()).thenReturn(Screen.LOGIN)
  }

  @Test
  fun testInitialUI() {
    composeTestRule.setContent {
      LogInScreen(navigationActions, accountViewModel, preferencesViewModel)
    }

    // Check that the scaffold and content boxes are displayed
    composeTestRule.onNodeWithTag("LoginScaffold").assertIsDisplayed()
    composeTestRule.onNodeWithTag("ContentBox").assertIsDisplayed()
    composeTestRule.onNodeWithTag("AnimationBox").assertIsDisplayed()
    composeTestRule.onNodeWithTag("BoxDecoration").assertIsDisplayed()

    // Check that the "Login" text is displayed
    composeTestRule.onNodeWithTag("WelcomeText").assertIsDisplayed()

    // Check that the "Your perfect fix is just a click away" text is displayed
    composeTestRule.onNodeWithTag("WelcomeTextBis").assertIsDisplayed()

    // Check that the email and password fields are empty initially
    composeTestRule.onNodeWithTag("inputEmail").assertIsDisplayed()
    composeTestRule.onNodeWithTag("inputPassword").assertIsDisplayed()

    // Check that the login button is displayed
    composeTestRule.onNodeWithTag("logInButton").assertIsDisplayed()

    // Check that the forgot password texts are displayed
    composeTestRule.onNodeWithTag("forgetPasswordButtonText").assertIsDisplayed()

    // Check that the "Don't have an account?" text is displayed
    composeTestRule.onNodeWithTag("noAccountText").assertIsDisplayed()

    // Check that the "Create One" text is displayed
    composeTestRule.onNodeWithTag("clickableCreateAccount").assertIsDisplayed()
  }

  @Test
  fun testLoginButtonEnabledWhenFieldsAreFilled() {
    composeTestRule.setContent {
      LogInScreen(navigationActions, accountViewModel, preferencesViewModel)
    }

    // Input valid email and password
    composeTestRule.onNodeWithTag("inputEmail").performTextInput("test@example.com")
    composeTestRule.onNodeWithTag("inputPassword").performTextInput("password123")

    // Click the login button
    composeTestRule.onNodeWithTag("logInButton").performClick()
  }

  @Test
  fun testInvalidEmailShowsError() {
    composeTestRule.setContent {
      LogInScreen(navigationActions, accountViewModel, preferencesViewModel)
    }

    // Input an invalid email
    composeTestRule.onNodeWithTag("inputEmail").performTextInput("invalidemail")

    // Click the login button
    composeTestRule.onNodeWithTag("logInButton").assertIsNotEnabled()

    // Check that the email error is displayed
    composeTestRule.onNodeWithTag("errorText").assertIsDisplayed()
  }

  @Test
  fun testForgotPasswordLinkIsDisplayed() {
    composeTestRule.setContent {
      LogInScreen(navigationActions, accountViewModel, preferencesViewModel)
    }

    // Check that the forgot password text is displayed
    composeTestRule.onNodeWithTag("forgetPasswordButtonText").assertIsDisplayed()
  }

  @Test
  fun testForgotPasswordLinkNavigatesToResetPassword() {
    composeTestRule.setContent {
      LogInScreen(navigationActions, accountViewModel, preferencesViewModel)
    }

    // Click the "Forgot your password?" link
    composeTestRule.onNodeWithTag("forgetPasswordButtonText").performClick()

    // Verify that the navigation to RESET_PASSWORD was triggered
    Mockito.verify(navigationActions).navigateTo(Screen.RESET_PASSWORD)
  }

  @Test
  fun testBackButtonNavigatesBack() {
    composeTestRule.setContent {
      LogInScreen(navigationActions, accountViewModel, preferencesViewModel)
    }

    // Click the back button
    composeTestRule.onNodeWithTag("goBackButton").performClick()

    composeTestRule.mainClock.advanceTimeBy(500L)

    // Verify that the navigation action was triggered
    Mockito.verify(navigationActions).goBack()
  }
}
