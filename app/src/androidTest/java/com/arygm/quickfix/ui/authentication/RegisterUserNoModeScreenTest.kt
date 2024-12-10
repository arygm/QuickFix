package com.arygm.quickfix.ui.authentication

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.arygm.quickfix.model.account.Account
import com.arygm.quickfix.model.account.AccountRepository
import com.arygm.quickfix.model.account.AccountViewModel
import com.arygm.quickfix.model.offline.small.PreferencesRepository
import com.arygm.quickfix.model.offline.small.PreferencesViewModel
import com.arygm.quickfix.model.profile.ProfileViewModel
import com.arygm.quickfix.model.profile.UserProfileRepositoryFirestore
import com.arygm.quickfix.model.profile.WorkerProfileRepositoryFirestore
import com.arygm.quickfix.ui.navigation.NavigationActions
import com.arygm.quickfix.ui.navigation.UserScreen
import com.arygm.quickfix.ui.navigation.UserTopLevelDestinations
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.whenever

class RegisterUserNoModeScreenTest {

  @get:Rule val composeTestRule = createComposeRule()

  private lateinit var mockFirestore: FirebaseFirestore
  private lateinit var accountRepository: AccountRepository
  private lateinit var accountViewModel: AccountViewModel
  private lateinit var userProfileRepositoryFirestore: UserProfileRepositoryFirestore
  private lateinit var workerProfileRepositoryFirestore: WorkerProfileRepositoryFirestore
  private lateinit var userViewModel: ProfileViewModel
  private lateinit var navigationActions: NavigationActions
  private lateinit var preferencesRepository: PreferencesRepository
  private lateinit var preferencesViewModel: PreferencesViewModel
  private lateinit var mockStorage: FirebaseStorage
  @Mock private lateinit var storageRef: StorageReference

  @Before
  fun setup() {
    mockFirestore = mock(FirebaseFirestore::class.java)
    mockStorage = mock(FirebaseStorage::class.java)
    storageRef = mock(StorageReference::class.java)
    whenever(mockStorage.reference).thenReturn(storageRef)
    navigationActions = mock(NavigationActions::class.java)
    userProfileRepositoryFirestore = UserProfileRepositoryFirestore(mockFirestore, mockStorage)
    workerProfileRepositoryFirestore = WorkerProfileRepositoryFirestore(mockFirestore, mockStorage)
    accountRepository = mock(AccountRepository::class.java)
    accountViewModel = AccountViewModel(accountRepository)
    userViewModel = ProfileViewModel(userProfileRepositoryFirestore)
    preferencesRepository = mock(PreferencesRepository::class.java)
    preferencesViewModel = PreferencesViewModel(preferencesRepository)

    `when`(navigationActions.currentRoute()).thenReturn(UserScreen.REGISTER)
  }

  @Test
  fun testInitialUI() {
    composeTestRule.setContent {
      RegisterScreen(navigationActions, accountViewModel, userViewModel, preferencesViewModel)
    }

    // Check that the scaffold and content boxes are displayed
    composeTestRule.onNodeWithTag("RegisterScaffold").assertIsDisplayed()
    composeTestRule.onNodeWithTag("ContentBox").assertIsDisplayed()
    composeTestRule.onNodeWithTag("AnimationBox").assertIsDisplayed()
    composeTestRule.onNodeWithTag("BoxDecoration").assertIsDisplayed()

    // Check that the "Register Now" text is displayed
    composeTestRule.onNodeWithTag("welcomeText").assertIsDisplayed()

    // Check that the "Join QuickFix to connect with skilled workers!" text is displayed
    composeTestRule.onNodeWithTag("welcomeTextBis").assertIsDisplayed()

    // Check that the first name, last name, email, birth date, password, and repeat password fields
    // are displayed
    composeTestRule.onNodeWithTag("firstNameInput").assertIsDisplayed()
    composeTestRule.onNodeWithTag("lastNameInput").assertIsDisplayed()
    composeTestRule.onNodeWithTag("emailInput").assertIsDisplayed()
    composeTestRule.onNodeWithTag("birthDateInput").assertIsDisplayed()
    composeTestRule.onNodeWithTag("passwordInput").assertIsDisplayed()
    composeTestRule.onNodeWithTag("repeatPasswordInput").assertIsDisplayed()

    // Check that password conditions are displayed
    composeTestRule.onNodeWithTag("passwordConditions").assertIsDisplayed()

    // Check that the terms and privacy policy checkbox are displayed
    composeTestRule.onNodeWithTag("checkbox").assertIsDisplayed()

    // Check that the register button is displayed but disabled initially
    composeTestRule.onNodeWithTag("registerButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("registerButton").assertIsNotEnabled()

    // Check that the "Already have an account?" text is displayed
    composeTestRule.onNodeWithTag("alreadyAccountText").assertIsDisplayed()

    // Check that the "Login !" text is displayed
    composeTestRule.onNodeWithTag("clickableLoginButtonText").assertIsDisplayed()
  }

  @Test
  fun testInvalidEmailShowsError() {
    composeTestRule.setContent {
      RegisterScreen(navigationActions, accountViewModel, userViewModel, preferencesViewModel)
    }

    // Input an invalid email
    composeTestRule.onNodeWithTag("emailInput").performTextInput("invalidemail")

    // Assert that the email error is shown
    composeTestRule.onNodeWithText("INVALID EMAIL").assertIsDisplayed()
    composeTestRule.onNodeWithTag("registerButton").assertIsNotEnabled()
  }

  @Test
  fun testInvalidDateShowsError() {
    composeTestRule.setContent {
      RegisterScreen(navigationActions, accountViewModel, userViewModel, preferencesViewModel)
    }

    // Input an invalid birth date
    composeTestRule.onNodeWithTag("birthDateInput").performTextInput("99/99/9999")

    // Assert that the birth date error is shown
    composeTestRule.onNodeWithText("INVALID DATE").assertIsDisplayed()
    composeTestRule.onNodeWithTag("registerButton").assertIsNotEnabled()
  }

  @Test
  fun testPasswordMismatch() {
    composeTestRule.setContent {
      RegisterScreen(navigationActions, accountViewModel, userViewModel, preferencesViewModel)
    }

    // Enter different passwords
    composeTestRule.onNodeWithTag("passwordInput").performTextInput("Password123")
    composeTestRule.onNodeWithTag("repeatPasswordInput").performTextInput("Password321")

    // Assert that no match error message is displayed
    composeTestRule.onNodeWithTag("noMatchText").assertIsDisplayed()
  }

  @Test
  fun testRegisterButtonEnabledWhenFormIsValid() {
    var createAccountFuncCalled = false
    // Arrange
    val newEmail = "new.user@example.com"

    val testCreateAccountFunc =
        {
            _: FirebaseAuth,
            _: String,
            _: String,
            _: String,
            _: String,
            _: String,
            _: AccountViewModel,
            _: ProfileViewModel,
            _: PreferencesViewModel,
            onSuccess: () -> Unit,
            _: () -> Unit ->
          createAccountFuncCalled = true
          onSuccess() // Simulate success
    }

    // Mock the profileRepository.profileExists to return exists = false, profile = null
    whenever(accountRepository.accountExists(eq(newEmail), any(), any())).thenAnswer { invocation ->
      val onSuccess = invocation.getArgument<(Pair<Boolean, Account?>) -> Unit>(1)
      onSuccess(Pair(false, null))
      null
    }

    composeTestRule.setContent {
      RegisterScreen(
          navigationActions,
          accountViewModel,
          userViewModel,
          preferencesViewModel,
          createAccountFunc = testCreateAccountFunc)
    }

    // Fill out valid inputs
    composeTestRule.onNodeWithTag("firstNameInput").performTextInput("John")
    composeTestRule.onNodeWithTag("lastNameInput").performTextInput("Doe")
    composeTestRule.onNodeWithTag("emailInput").performTextInput(newEmail)
    composeTestRule.onNodeWithTag("birthDateInput").performTextInput("01/01/1990")
    composeTestRule.onNodeWithTag("passwordInput").performTextInput("Password123")
    composeTestRule.onNodeWithTag("repeatPasswordInput").performTextInput("Password123")

    // Check the terms and privacy policy checkboxes
    composeTestRule.onNodeWithTag("checkbox").performClick()

    // Assert that the "Register" button is now enabled
    composeTestRule.onNodeWithTag("registerButton").assertIsEnabled()

    // Click the button and verify navigation
    composeTestRule.onNodeWithTag("registerButton").performClick()

    composeTestRule.waitUntil(timeoutMillis = 10000) {
      Mockito.mockingDetails(navigationActions).invocations.isNotEmpty()
    }
    Mockito.verify(navigationActions).navigateTo(UserTopLevelDestinations.HOME)
  }

  @Test
  fun testRegisterButtonDisabledWhenFormIncomplete() {
    composeTestRule.setContent {
      RegisterScreen(navigationActions, accountViewModel, userViewModel, preferencesViewModel)
    }

    // Fill only partial inputs
    composeTestRule.onNodeWithTag("firstNameInput").performTextInput("John")
    composeTestRule.onNodeWithTag("lastNameInput").performTextInput("Doe")
    composeTestRule.onNodeWithTag("passwordInput").performTextInput("Password123")
    // Leave email and repeat password empty

    // Check the terms and privacy policy checkboxes
    composeTestRule.onNodeWithTag("checkbox").performClick()

    // Assert that the "Register" button is still disabled
    composeTestRule.onNodeWithTag("registerButton").assertIsNotEnabled()
  }

  @Test
  fun testBackButtonNavigatesBack() {
    composeTestRule.setContent {
      RegisterScreen(navigationActions, accountViewModel, userViewModel, preferencesViewModel)
    }

    // Click the back button
    composeTestRule.onNodeWithTag("goBackButton").performClick()

    // Verify that the navigation action was triggered
    Mockito.verify(navigationActions).goBack()
  }

  @Test
  fun testLoginButtonNavigatesToLogin() {
    composeTestRule.setContent {
      RegisterScreen(navigationActions, accountViewModel, userViewModel, preferencesViewModel)
    }

    // Click the "Login !" button
    composeTestRule.onNodeWithTag("clickableLoginButtonText").performClick()

    // Verify that the navigation action is triggered for the login screen
    Mockito.verify(navigationActions).navigateTo(UserScreen.LOGIN)
  }

  @Test
  fun testEmailAlreadyExistsShowsError() {
    // Arrange
    val existingEmail = "john.doe@example.com"
    val profile =
        Account(
            uid = "testUid",
            firstName = "John",
            lastName = "Doe",
            email = existingEmail,
            birthDate = Timestamp.now(),
            isWorker = false)

    // Mock the profileRepository.profileExists to return exists = true, profile != null
    whenever(accountRepository.accountExists(eq(existingEmail), any(), any())).thenAnswer {
        invocation ->
      val onSuccess = invocation.getArgument<(Pair<Boolean, Account?>) -> Unit>(1)
      onSuccess(Pair(true, profile))
      null
    }

    // Act
    composeTestRule.setContent {
      RegisterScreen(navigationActions, accountViewModel, userViewModel, preferencesViewModel)
    }

    composeTestRule.onNodeWithTag("emailInput").performTextInput(existingEmail)

    // Wait for possible recompositions
    composeTestRule.waitForIdle()

    // Fill out valid inputs
    composeTestRule.onNodeWithTag("firstNameInput").performTextInput("John")
    composeTestRule.onNodeWithTag("lastNameInput").performTextInput("Doe")
    composeTestRule.onNodeWithTag("birthDateInput").performTextInput("01/01/1990")
    composeTestRule.onNodeWithTag("passwordInput").performTextInput("Password123")
    composeTestRule.onNodeWithTag("repeatPasswordInput").performTextInput("Password123")
    composeTestRule.onNodeWithTag("checkbox").performClick()

    // Assert
    composeTestRule.onNodeWithTag("errorText").assertIsDisplayed()
    composeTestRule.onNodeWithTag("errorText").assertTextEquals("INVALID EMAIL")
    composeTestRule.onNodeWithTag("registerButton").assertIsNotEnabled()
  }

  @Test
  fun testValidEmailDoesNotShowErrorWhenEmailDoesNotExist() {
    // Arrange
    val newEmail = "new.user@example.com"

    // Mock the profileRepository.profileExists to return exists = false, profile = null
    whenever(accountRepository.accountExists(eq(newEmail), any(), any())).thenAnswer { invocation ->
      val onSuccess = invocation.getArgument<(Pair<Boolean, Account?>) -> Unit>(1)
      onSuccess(Pair(false, null))
      null
    }

    // Act
    composeTestRule.setContent {
      RegisterScreen(navigationActions, accountViewModel, userViewModel, preferencesViewModel)
    }

    composeTestRule.onNodeWithTag("emailInput").performTextInput(newEmail)

    // Wait for possible recompositions
    composeTestRule.waitForIdle()

    // Fill out valid inputs
    composeTestRule.onNodeWithTag("firstNameInput").performTextInput("John")
    composeTestRule.onNodeWithTag("lastNameInput").performTextInput("Doe")
    composeTestRule.onNodeWithTag("birthDateInput").performTextInput("01/01/1990")
    composeTestRule.onNodeWithTag("passwordInput").performTextInput("Password123")
    composeTestRule.onNodeWithTag("repeatPasswordInput").performTextInput("Password123")
    composeTestRule.onNodeWithTag("checkbox").performClick()

    // Assert
    composeTestRule.onNodeWithTag("errorText").assertDoesNotExist()
    // Ensure the "NEXT" button remains disabled until other fields are filled and checkboxes are
    // checked
    composeTestRule.onNodeWithTag("registerButton").assertIsEnabled()
  }

  @Test
  fun testRegisterButtonClickSuccessfulRegistration() {
    val existingEmail = "john.doe@example.com"
    var createAccountFuncCalled = false

    val testCreateAccountFunc =
        {
            _: FirebaseAuth,
            _: String,
            _: String,
            _: String,
            _: String,
            _: String,
            _: AccountViewModel,
            _: ProfileViewModel,
            _: PreferencesViewModel,
            onSuccess: () -> Unit,
            _: () -> Unit ->
          createAccountFuncCalled = true
          onSuccess() // Simulate success
    }

    composeTestRule.setContent {
      RegisterScreen(
          navigationActions,
          accountViewModel,
          userViewModel,
          preferencesViewModel,
          createAccountFunc = testCreateAccountFunc)
    }

    // Mock the profileRepository.profileExists to return exists = false, profile = null
    whenever(accountRepository.accountExists(eq(existingEmail), any(), any())).thenAnswer {
        invocation ->
      val onSuccess = invocation.getArgument<(Pair<Boolean, Account?>) -> Unit>(1)
      onSuccess(Pair(false, null))
      null
    }

    // Fill out valid inputs
    composeTestRule.onNodeWithTag("firstNameInput").performTextInput("John")
    composeTestRule.onNodeWithTag("lastNameInput").performTextInput("Doe")
    composeTestRule.onNodeWithTag("birthDateInput").performTextInput("01/01/1990")
    composeTestRule.onNodeWithTag("emailInput").performTextInput(existingEmail)
    composeTestRule.onNodeWithTag("checkbox").performClick()

    // Input matching passwords that meet all conditions
    composeTestRule.onNodeWithTag("passwordInput").performTextInput("Password1")
    composeTestRule.onNodeWithTag("repeatPasswordInput").performTextInput("Password1")

    composeTestRule.onNodeWithTag("registerButton").assertIsEnabled()
    // Click the register button
    composeTestRule.onNodeWithTag("registerButton").performClick()

    // Verify that createAccountFunc was called
    assertTrue(createAccountFuncCalled)

    // Verify that navigation to HOME was triggered
    Mockito.verify(navigationActions).navigateTo(UserTopLevelDestinations.HOME)
  }

  @Test
  fun testRegisterButtonClickFailedRegistration() {

    val newEmail = "example@gmail.com"

    // Mock the profileRepository.profileExists to return exists = false, profile = null
    whenever(accountRepository.accountExists(eq(newEmail), any(), any())).thenAnswer { invocation ->
      val onSuccess = invocation.getArgument<(Pair<Boolean, Account?>) -> Unit>(1)
      onSuccess(Pair(false, null))
      null
    }
    var createAccountFuncCalled = false

    val testCreateAccountFunc =
        {
            _: FirebaseAuth,
            _: String,
            _: String,
            _: String,
            _: String,
            _: String,
            _: AccountViewModel,
            _: ProfileViewModel,
            _: PreferencesViewModel,
            _: () -> Unit,
            onFailure: () -> Unit ->
          createAccountFuncCalled = true
          onFailure() // Simulate failure
    }

    composeTestRule.setContent {
      RegisterScreen(
          navigationActions,
          accountViewModel,
          userViewModel,
          preferencesViewModel,
          createAccountFunc = testCreateAccountFunc)
    }
    // Fill out valid inputs
    composeTestRule.onNodeWithTag("firstNameInput").performTextInput("John")
    composeTestRule.onNodeWithTag("lastNameInput").performTextInput("Doe")
    composeTestRule.onNodeWithTag("birthDateInput").performTextInput("01/01/1990")
    composeTestRule.onNodeWithTag("emailInput").performTextInput(newEmail)
    composeTestRule.onNodeWithTag("checkbox").performClick()

    // Input matching passwords that meet all conditions
    composeTestRule.onNodeWithTag("passwordInput").performTextInput("Password1")
    composeTestRule.onNodeWithTag("repeatPasswordInput").performTextInput("Password1")

    composeTestRule.onNodeWithTag("registerButton").assertIsEnabled()
    // Click the register button
    composeTestRule.onNodeWithTag("registerButton").performClick()

    // Verify that createAccountFunc was called
    assertTrue(createAccountFuncCalled)

    // Verify that navigation to HOME was not triggered
    Mockito.verify(navigationActions, Mockito.never()).navigateTo(UserTopLevelDestinations.HOME)
  }
}
