package com.arygm.quickfix.ui.profile

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.arygm.quickfix.model.location.Location
import com.arygm.quickfix.model.account.Account
import com.arygm.quickfix.model.account.AccountRepository
import com.arygm.quickfix.model.account.AccountViewModel
import com.arygm.quickfix.model.account.LoggedInAccountViewModel
import com.arygm.quickfix.model.profile.UserProfile
import com.arygm.quickfix.model.profile.UserProfileRepositoryFirestore
import com.arygm.quickfix.model.profile.WorkerProfile
import com.arygm.quickfix.model.profile.WorkerProfileRepositoryFirestore
import com.arygm.quickfix.ui.account.AccountConfigurationScreen
import com.arygm.quickfix.ui.navigation.NavigationActions
import com.arygm.quickfix.ui.theme.QuickFixTheme
import com.google.firebase.Timestamp
import java.util.Calendar
import java.util.GregorianCalendar
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.*
import org.mockito.Mockito.never
import org.mockito.kotlin.*
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class LoggedInAccountViewModelTest {

  @get:Rule val composeTestRule = createComposeRule()

  // Mocked dependencies
  @Mock private lateinit var navigationActions: NavigationActions
  @Mock private lateinit var accountRepository: AccountRepository
  @Mock private lateinit var userProfileRepositoryFirestore: UserProfileRepositoryFirestore
  @Mock private lateinit var workerProfileRepositoryFirestore: WorkerProfileRepositoryFirestore

  // The ViewModels under test
  private lateinit var accountViewModel: AccountViewModel
  private lateinit var loggedInAccountViewModel: LoggedInAccountViewModel

  // Test Accounts and Profiles
  private val testUserAccount =
      Account(
          uid = "testUid",
          firstName = "John",
          lastName = "Doe",
          birthDate = Timestamp.now(),
          email = "john.doe@example.com",
          isWorker = false)

  private val testUserProfile =
      UserProfile(
          uid = "testUid",
          locations =
              listOf(
                  Location(latitude = 0.0, longitude = 0.0, name = "Home"),
                  Location(latitude = 1.0, longitude = 1.0, name = "Work"))
          // Initialize other fields as necessary
          )

  private val testWorkerAccount =
      Account(
          uid = "workerUid",
          firstName = "Jane",
          lastName = "Smith",
          birthDate = Timestamp.now(),
          email = "jane.smith@example.com",
          isWorker = true)

  private val testWorkerProfile =
      WorkerProfile(
          uid = "workerUid",
          fieldOfWork = "Plumbing",
          hourlyRate = 30.0,
          description = "Experienced plumber",
          location = Location(latitude = 40.7128, longitude = -74.0060, name = "New York"))

  @Before
  fun setup() {
    // Initialize Mockito annotations
    MockitoAnnotations.openMocks(this)

    // Initialize ViewModels with mocked repositories
    accountViewModel = AccountViewModel(accountRepository)
    loggedInAccountViewModel =
        LoggedInAccountViewModel(
            userProfileRepo = userProfileRepositoryFirestore,
            workerProfileRepo = workerProfileRepositoryFirestore)
    loggedInAccountViewModel.loggedInAccount_.value = testUserAccount
  }

  /** Helper function to mock getProfileById for UserProfileRepositoryFirestore. */
  private fun mockUserProfileRepository(uid: String, profile: UserProfile?) {
    doAnswer { invocation ->
          val calledUid = invocation.getArgument<String>(0)
          val onSuccess = invocation.getArgument<(UserProfile?) -> Unit>(1)
          val onFailure = invocation.getArgument<(Exception) -> Unit>(2)
          if (calledUid == uid) {
            onSuccess(profile)
          } else {
            onSuccess(null)
          }
          null
        }
        .whenever(userProfileRepositoryFirestore)
        .getProfileById(eq(uid), any(), any())
  }

  /** Helper function to mock getProfileById for WorkerProfileRepositoryFirestore. */
  private fun mockWorkerProfileRepository(uid: String, profile: WorkerProfile?) {
    doAnswer { invocation ->
          val calledUid = invocation.getArgument<String>(0)
          val onSuccess = invocation.getArgument<(WorkerProfile?) -> Unit>(1)
          val onFailure = invocation.getArgument<(Exception) -> Unit>(2)
          if (calledUid == uid) {
            onSuccess(profile)
          } else {
            onSuccess(null)
          }
          null
        }
        .whenever(workerProfileRepositoryFirestore)
        .getProfileById(eq(uid), any(), any())
  }

  /**
   * Test Scenario 1: Set account to User1 (UID = "testUid", isWorker = false) Verify that only the
   * `loggedInAccount` and `userProfile` are updated. `workerProfile` should remain null.
   */
  @Test
  fun setLoggedInAccount_toUser_updatesLoggedInAccountAndUserProfileOnly() {
    // Arrange
    mockUserProfileRepository(testUserAccount.uid, testUserProfile)
    // Since isWorker = false, workerProfileRepo.getProfileById should not be called

    // Act
    loggedInAccountViewModel.setLoggedInAccount(testUserAccount)

    // Assert
    // Verify that getProfileById was called on userProfileRepo
    verify(userProfileRepositoryFirestore).getProfileById(eq(testUserAccount.uid), any(), any())

    // Verify that getProfileById was not called on workerProfileRepo
    verify(workerProfileRepositoryFirestore, never()).getProfileById(any(), any(), any())

    // Check the state flows
    val loggedInAccount = loggedInAccountViewModel.loggedInAccount.value
    val userProfile = loggedInAccountViewModel.userProfile.value
    val workerProfile = loggedInAccountViewModel.workerProfile.value

    assertEquals(testUserAccount, loggedInAccount)
    assertEquals(testUserProfile, userProfile)
    assertNull(workerProfile)
  }

  /**
   * Test Scenario 2: Set account to Worker1 (UID = "workerUid", isWorker = true) Verify that
   * `loggedInAccount`, `userProfile`, and `workerProfile` are all updated.
   */
  @Test
  fun setLoggedInAccount_toWorker_updatesAllProfiles() {
    // Arrange
    mockUserProfileRepository(testWorkerAccount.uid, testUserProfile) // Assuming userProfile exists
    mockWorkerProfileRepository(testWorkerAccount.uid, testWorkerProfile)

    // Act
    loggedInAccountViewModel.setLoggedInAccount(testWorkerAccount)

    // Assert
    // Verify that getProfileById was called on both repositories
    verify(userProfileRepositoryFirestore).getProfileById(eq(testWorkerAccount.uid), any(), any())
    verify(workerProfileRepositoryFirestore).getProfileById(eq(testWorkerAccount.uid), any(), any())

    // Check the state flows
    val loggedInAccount = loggedInAccountViewModel.loggedInAccount.value
    val userProfile = loggedInAccountViewModel.userProfile.value
    val workerProfile = loggedInAccountViewModel.workerProfile.value

    assertEquals(testWorkerAccount, loggedInAccount)
    assertEquals(testUserProfile, userProfile)
    assertEquals(testWorkerProfile, workerProfile)
  }

  /**
   * Test Scenario 3: Set account to User2 (UID = "2", isWorker = false) Verify that only the
   * `loggedInAccount` and `userProfile` are updated. `workerProfile` should remain null.
   */
  @Test
  fun setLoggedInAccount_toUser2_updatesLoggedInAccountAndUserProfileOnly() {
    // Arrange
    val user2 =
        Account(
            uid = "2",
            firstName = "Alice",
            lastName = "Johnson",
            birthDate = Timestamp.now(),
            email = "alice.johnson@example.com",
            isWorker = false)
    val userProfile2 =
        UserProfile(
            uid = "2",
            locations =
                listOf(Location(latitude = 34.0522, longitude = -118.2437, name = "Los Angeles"))
            // Initialize other fields as necessary
            )
    mockUserProfileRepository(user2.uid, userProfile2)

    // Act
    loggedInAccountViewModel.setLoggedInAccount(user2)

    // Assert
    // Verify that getProfileById was called on userProfileRepo
    verify(userProfileRepositoryFirestore).getProfileById(eq(user2.uid), any(), any())

    // Verify that getProfileById was not called on workerProfileRepo
    verify(workerProfileRepositoryFirestore, never()).getProfileById(any(), any(), any())

    // Check the state flows
    val loggedInAccount = loggedInAccountViewModel.loggedInAccount.value
    val userProfile = loggedInAccountViewModel.userProfile.value
    val workerProfile = loggedInAccountViewModel.workerProfile.value

    assertEquals(user2, loggedInAccount)
    assertEquals(userProfile2, userProfile)
    assertNull(workerProfile)
  }

  // ----- Existing Test Methods -----
  // (Include all your existing @Test methods here without changes)

  @Test
  fun testUpdateFirstNameAndLastName() {
    // Arrange
    doAnswer { invocation ->
          val profile = invocation.getArgument<Account>(0)
          val onSuccess = invocation.getArgument<() -> Unit>(1)
          val onFailure = invocation.getArgument<(Exception) -> Unit>(2)
          onSuccess()
          null
        }
        .whenever(accountRepository)
        .updateAccount(any(), any(), any())

    composeTestRule.setContent {
      QuickFixTheme {
        AccountConfigurationScreen(
            navigationActions = navigationActions,
            accountViewModel = accountViewModel,
            loggedInAccountViewModel = loggedInAccountViewModel)
      }
    }

    // Update first name and last name using performTextReplacement
    composeTestRule.onNodeWithTag("firstNameInput").performTextReplacement("Jane")
    composeTestRule.onNodeWithTag("lastNameInput").performTextReplacement("Smith")

    // Click Save button
    composeTestRule.onNodeWithTag("SaveButton").performClick()

    // Verify that updateProfile was called with updated names
    val profileCaptor = argumentCaptor<Account>()
    verify(accountRepository).updateAccount(profileCaptor.capture(), any(), any())

    val updatedProfile = profileCaptor.firstValue
    assertEquals("Jane", updatedProfile.firstName)
    assertEquals("Smith", updatedProfile.lastName)
  }

  @Test
  fun testUpdateEmailWithValidEmail() {
    // Arrange
    doAnswer { invocation ->
          val email = invocation.getArgument<String>(0)
          val onSuccess = invocation.getArgument<(Pair<Boolean, Account?>) -> Unit>(1)
          val onFailure = invocation.getArgument<(Exception) -> Unit>(2)
          // Simulate that the profile does not exist
          onSuccess(Pair(false, null))
          null
        }
        .whenever(accountRepository)
        .accountExists(any(), any(), any())

    doAnswer { invocation ->
          val profile = invocation.getArgument<Account>(0)
          val onSuccess = invocation.getArgument<() -> Unit>(1)
          val onFailure = invocation.getArgument<(Exception) -> Unit>(2)
          onSuccess()
          null
        }
        .whenever(accountRepository)
        .updateAccount(any(), any(), any())

    composeTestRule.setContent {
      QuickFixTheme {
        AccountConfigurationScreen(
            navigationActions = navigationActions,
            accountViewModel = accountViewModel,
            loggedInAccountViewModel = loggedInAccountViewModel)
      }
    }

    // Update email using performTextReplacement
    composeTestRule.onNodeWithTag("emailInput").performTextReplacement("jane.smith@example.com")

    // Click Save button
    composeTestRule.onNodeWithTag("SaveButton").performClick()

    // Verify that updateProfile was called with updated email
    val profileCaptor = argumentCaptor<Account>()
    verify(accountRepository).updateAccount(profileCaptor.capture(), any(), any())

    val updatedProfile = profileCaptor.firstValue
    assertEquals("jane.smith@example.com", updatedProfile.email)
  }

  @Test
  fun testUpdateEmailWithInvalidEmailShowsError() {
    composeTestRule.setContent {
      QuickFixTheme {
        AccountConfigurationScreen(
            navigationActions = navigationActions,
            accountViewModel = accountViewModel,
            loggedInAccountViewModel = loggedInAccountViewModel)
      }
    }

    // Update email with invalid email using performTextReplacement
    composeTestRule.onNodeWithTag("emailInput").performTextReplacement("invalidemail")

    // Attempt to click Save button
    composeTestRule.onNodeWithTag("SaveButton").performClick()

    // Verify that updateProfile was not called due to invalid email
    verify(accountRepository, never()).updateAccount(any(), any(), any())
  }

  @Test
  fun testUpdateBirthDateWithValidDate() {
    // Arrange
    doAnswer { invocation ->
          val profile = invocation.getArgument<Account>(0)
          val onSuccess = invocation.getArgument<() -> Unit>(1)
          val onFailure = invocation.getArgument<(Exception) -> Unit>(2)
          onSuccess()
          null
        }
        .whenever(accountRepository)
        .updateAccount(any(), any(), any())

    composeTestRule.setContent {
      QuickFixTheme {
        AccountConfigurationScreen(
            navigationActions = navigationActions,
            accountViewModel = accountViewModel,
            loggedInAccountViewModel = loggedInAccountViewModel)
      }
    }

    // Update birth date using performTextReplacement
    composeTestRule.onNodeWithTag("birthDateInput").performTextReplacement("01/01/1990")

    // Click Save button
    composeTestRule.onNodeWithTag("SaveButton").performClick()

    // Verify that updateProfile was called with updated birth date
    val profileCaptor = argumentCaptor<Account>()
    verify(accountRepository).updateAccount(profileCaptor.capture(), any(), any())

    val updatedProfile = profileCaptor.firstValue

    val calendar = GregorianCalendar(1990, Calendar.JANUARY, 1, 0, 0, 0)
    val expectedTimestamp = Timestamp(calendar.time)

    assertEquals(expectedTimestamp.seconds, updatedProfile.birthDate.seconds)
  }

  @Test
  fun testUpdateBirthDateWithInvalidDateShowsToast() {
    composeTestRule.setContent {
      QuickFixTheme {
        AccountConfigurationScreen(
            navigationActions = navigationActions,
            accountViewModel = accountViewModel,
            loggedInAccountViewModel = loggedInAccountViewModel)
      }
    }

    // Update birth date with invalid date using performTextReplacement
    composeTestRule.onNodeWithTag("birthDateInput").performTextReplacement("invalid-date")

    // Click Save button
    composeTestRule.onNodeWithTag("SaveButton").performClick()

    // Verify that updateProfile was not called due to invalid date
    verify(accountRepository, never()).updateAccount(any(), any(), any())
  }

  @Test
  fun testChangePasswordButtonClick() {
    composeTestRule.setContent {
      QuickFixTheme {
        AccountConfigurationScreen(
            navigationActions = navigationActions,
            accountViewModel = accountViewModel,
            loggedInAccountViewModel = loggedInAccountViewModel)
      }
    }

    // Click Change Password button
    composeTestRule.onNodeWithTag("ChangePasswordButton").performClick()

    // Since the action is not implemented, verify that nothing crashes
    // No assertions needed; if the test doesn't throw, it's successful
  }

  @Test
  fun testSaveButtonNavigatesBack() {
    // Arrange
    doAnswer { invocation ->
          val profile = invocation.getArgument<Account>(0)
          val onSuccess = invocation.getArgument<() -> Unit>(1)
          val onFailure = invocation.getArgument<(Exception) -> Unit>(2)
          onSuccess()
          null
        }
        .whenever(accountRepository)
        .updateAccount(any(), any(), any())

    composeTestRule.setContent {
      QuickFixTheme {
        AccountConfigurationScreen(
            navigationActions = navigationActions,
            accountViewModel = accountViewModel,
            loggedInAccountViewModel = loggedInAccountViewModel)
      }
    }

    // Click Save button
    composeTestRule.onNodeWithTag("SaveButton").performClick()

    // Verify that navigationActions.goBack() was called
    verify(navigationActions).goBack()
  }

  @Test
  fun testEmailAlreadyExistsShowsError() {
    // Arrange
    doAnswer { invocation ->
          val email = invocation.getArgument<String>(0)
          val onSuccess = invocation.getArgument<(Pair<Boolean, Account?>) -> Unit>(1)
          val onFailure = invocation.getArgument<(Exception) -> Unit>(2)
          val existingProfile =
              Account(
                  uid = "existingUid",
                  firstName = "Existing",
                  lastName = "User",
                  email = "existing@example.com",
                  birthDate = Timestamp.now(),
                  isWorker = false)
          onSuccess(Pair(true, existingProfile))
          null
        }
        .whenever(accountRepository)
        .accountExists(any(), any(), any())

    composeTestRule.setContent {
      QuickFixTheme {
        AccountConfigurationScreen(
            navigationActions = navigationActions,
            accountViewModel = accountViewModel,
            loggedInAccountViewModel = loggedInAccountViewModel)
      }
    }

    // Update email to an existing email using performTextReplacement
    composeTestRule.onNodeWithTag("emailInput").performTextReplacement("existing@example.com")

    // Click Save button
    composeTestRule.onNodeWithTag("SaveButton").performClick()

    // Verify that updateProfile was not called
    verify(accountRepository, never()).updateAccount(any(), any(), any())
  }

  @Test
  fun testInitialUIElementsAreDisplayed() {
    // Set up the content
    composeTestRule.setContent {
      QuickFixTheme {
        AccountConfigurationScreen(
            navigationActions = navigationActions,
            accountViewModel = accountViewModel,
            loggedInAccountViewModel = loggedInAccountViewModel)
      }
    }

    // Verify that the Top App Bar is displayed with the correct title
    composeTestRule.onNodeWithTag("AccountConfigurationTopAppBar").assertIsDisplayed()
    composeTestRule
        .onNodeWithTag("AccountConfigurationTitle")
        .assertTextEquals("Account configuration")

    // Verify that the Profile Image is displayed
    composeTestRule.onNodeWithTag("AccountImage").assertIsDisplayed()

    // Verify that the Profile Card is displayed with the correct name
    composeTestRule.onNodeWithTag("AccountCard").assertIsDisplayed()
    composeTestRule.onNodeWithTag("AccountName").assertTextEquals("John Doe")

    // Verify that the input fields are displayed
    composeTestRule.onNodeWithTag("firstNameInput").assertIsDisplayed()
    composeTestRule.onNodeWithTag("lastNameInput").assertIsDisplayed()
    composeTestRule.onNodeWithTag("emailInput").assertIsDisplayed()
    composeTestRule.onNodeWithTag("birthDateInput").assertIsDisplayed()

    // Verify that the buttons are displayed
    composeTestRule.onNodeWithTag("ChangePasswordButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("SaveButton").assertIsDisplayed()
  }
}
