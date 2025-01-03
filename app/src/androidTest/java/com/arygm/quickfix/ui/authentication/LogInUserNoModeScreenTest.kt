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
import com.arygm.quickfix.model.offline.small.PreferencesViewModelUserProfile
import com.arygm.quickfix.model.profile.ProfileViewModel
import com.arygm.quickfix.model.profile.UserProfileRepositoryFirestore
import com.arygm.quickfix.model.profile.WorkerProfileRepositoryFirestore
import com.arygm.quickfix.ui.navigation.NavigationActions
import com.arygm.quickfix.ui.uiMode.noModeUI.authentication.LogInScreen
import com.arygm.quickfix.ui.uiMode.noModeUI.navigation.NoModeRoute
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

class LogInUserNoModeScreenTest {

  @get:Rule val composeTestRule = createComposeRule()
  private lateinit var rootNavigationActions: NavigationActions
  private lateinit var navigationActions: NavigationActions
  private lateinit var accountRepository: AccountRepository
  private lateinit var userProfileRepo: UserProfileRepositoryFirestore
  private lateinit var workerProfileRepo: WorkerProfileRepositoryFirestore
  private lateinit var accountViewModel: AccountViewModel
  private lateinit var mockFirestore: FirebaseFirestore
  private lateinit var mockStorage: FirebaseStorage
  private lateinit var preferencesRepository: PreferencesRepository
  private lateinit var preferencesViewModel: PreferencesViewModel
  private lateinit var userPreferencesViewModel: PreferencesViewModelUserProfile
  private lateinit var userPreferencesRepository: PreferencesRepository
  private lateinit var userViewModel: ProfileViewModel
  private lateinit var userProfileRepository: UserProfileRepositoryFirestore
  @Mock private lateinit var storageRef: StorageReference

  @Before
  fun setup() {
    userProfileRepository = mock(UserProfileRepositoryFirestore::class.java)
    userViewModel = ProfileViewModel(userProfileRepository)
    userPreferencesRepository = mock(PreferencesRepository::class.java)
    userPreferencesViewModel = PreferencesViewModelUserProfile(userPreferencesRepository)
    rootNavigationActions = mock(NavigationActions::class.java)
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

    `when`(navigationActions.currentRoute()).thenReturn(NoModeRoute.LOGIN)
  }

  @Test
  fun testInitialUI() {
    composeTestRule.setContent {
      LogInScreen(
          navigationActions,
          accountViewModel,
          preferencesViewModel,
          rootNavigationActions,
          userPreferencesViewModel,
          userViewModel)
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
      LogInScreen(
          navigationActions,
          accountViewModel,
          preferencesViewModel,
          rootNavigationActions,
          userPreferencesViewModel,
          userViewModel)
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
      LogInScreen(
          navigationActions,
          accountViewModel,
          preferencesViewModel,
          rootNavigationActions,
          userPreferencesViewModel,
          userViewModel)
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
      LogInScreen(
          navigationActions,
          accountViewModel,
          preferencesViewModel,
          rootNavigationActions,
          userPreferencesViewModel,
          userViewModel)
    }

    // Check that the forgot password text is displayed
    composeTestRule.onNodeWithTag("forgetPasswordButtonText").assertIsDisplayed()
  }

  @Test
  fun testForgotPasswordLinkNavigatesToResetPassword() {
    composeTestRule.setContent {
      LogInScreen(
          navigationActions,
          accountViewModel,
          preferencesViewModel,
          rootNavigationActions,
          userPreferencesViewModel,
          userViewModel)
    }

    // Click the "Forgot your password?" link
    composeTestRule.onNodeWithTag("forgetPasswordButtonText").performClick()

    // Verify that the navigation to RESET_PASSWORD was triggered
    Mockito.verify(navigationActions).navigateTo(NoModeRoute.RESET_PASSWORD)
  }

  @Test
  fun testBackButtonNavigatesBack() {
    composeTestRule.setContent {
      LogInScreen(
          navigationActions,
          accountViewModel,
          preferencesViewModel,
          rootNavigationActions,
          userPreferencesViewModel,
          userViewModel)
    }

    // Click the back button
    composeTestRule.onNodeWithTag("goBackButton").performClick()

    composeTestRule.mainClock.advanceTimeBy(500L)

    // Verify that the navigation action was triggered
    Mockito.verify(navigationActions).goBack()
  }
}
