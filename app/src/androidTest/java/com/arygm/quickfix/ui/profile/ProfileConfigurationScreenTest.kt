package com.arygm.quickfix.ui.profile

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.arygm.quickfix.model.profile.*
import com.arygm.quickfix.ui.navigation.NavigationActions
import com.arygm.quickfix.ui.theme.QuickFixTheme
import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint
import java.util.Calendar
import java.util.GregorianCalendar
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.*
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class ProfileConfigurationScreenTest {

  @get:Rule val composeTestRule = createComposeRule()

  private lateinit var navigationActions: NavigationActions
  private lateinit var userRepository: ProfileRepository
  private lateinit var userViewModel: ProfileViewModel
  private lateinit var workerViewModel: ProfileViewModel
  private lateinit var loggedInProfileViewModel: LoggedInProfileViewModel

  private val testUserProfile =
      UserProfile(
          uid = "testUid",
          firstName = "John",
          lastName = "Doe",
          birthDate = Timestamp.now(),
          email = "john.doe@example.com",
          location = GeoPoint(0.0, 0.0),
          isWorker = false)

  @Before
  fun setup() {
    navigationActions = mock(NavigationActions::class.java)
    userRepository = mock(ProfileRepository::class.java)
    userViewModel = ProfileViewModel(userRepository)
    workerViewModel = mock(ProfileViewModel::class.java)
    loggedInProfileViewModel = LoggedInProfileViewModel()
    loggedInProfileViewModel.setLoggedInProfile(testUserProfile)
  }

  @Test
  fun testUpdateFirstNameAndLastName() {
    // Arrange
    doAnswer { invocation ->
          val profile = invocation.getArgument<Profile>(0)
          val onSuccess = invocation.getArgument<() -> Unit>(1)
          val onFailure = invocation.getArgument<(Exception) -> Unit>(2)
          onSuccess()
          null
        }
        .whenever(userRepository)
        .updateProfile(any(), any(), any())

    composeTestRule.setContent {
      QuickFixTheme {
        ProfileConfigurationScreen(
            navigationActions = navigationActions,
            userViewModel = userViewModel,
            workerViewModel = workerViewModel,
            loggedInProfileViewModel = loggedInProfileViewModel)
      }
    }

    // Update first name and last name using performTextReplacement
    composeTestRule.onNodeWithTag("firstNameInput").performTextReplacement("Jane")
    composeTestRule.onNodeWithTag("lastNameInput").performTextReplacement("Smith")

    // Click Save button
    composeTestRule.onNodeWithTag("SaveButton").performClick()

    // Verify that updateProfile was called with updated names
    val profileCaptor = argumentCaptor<Profile>()
    verify(userRepository).updateProfile(profileCaptor.capture(), any(), any())

    val updatedProfile = profileCaptor.firstValue
    assertEquals("Jane", updatedProfile.firstName)
    assertEquals("Smith", updatedProfile.lastName)
  }

  @Test
  fun testUpdateEmailWithValidEmail() {
    // Arrange
    doAnswer { invocation ->
          val email = invocation.getArgument<String>(0)
          val onSuccess = invocation.getArgument<(Pair<Boolean, Profile?>) -> Unit>(1)
          val onFailure = invocation.getArgument<(Exception) -> Unit>(2)
          // Simulate that the profile does not exist
          onSuccess(Pair(false, null))
          null
        }
        .whenever(userRepository)
        .profileExists(any(), any(), any())

    doAnswer { invocation ->
          val profile = invocation.getArgument<Profile>(0)
          val onSuccess = invocation.getArgument<() -> Unit>(1)
          val onFailure = invocation.getArgument<(Exception) -> Unit>(2)
          onSuccess()
          null
        }
        .whenever(userRepository)
        .updateProfile(any(), any(), any())

    composeTestRule.setContent {
      QuickFixTheme {
        ProfileConfigurationScreen(
            navigationActions = navigationActions,
            userViewModel = userViewModel,
            workerViewModel = workerViewModel,
            loggedInProfileViewModel = loggedInProfileViewModel)
      }
    }

    // Update email using performTextReplacement
    composeTestRule.onNodeWithTag("emailInput").performTextReplacement("jane.smith@example.com")

    // Click Save button
    composeTestRule.onNodeWithTag("SaveButton").performClick()

    // Verify that updateProfile was called with updated email
    val profileCaptor = argumentCaptor<Profile>()
    verify(userRepository).updateProfile(profileCaptor.capture(), any(), any())

    val updatedProfile = profileCaptor.firstValue
    assertEquals("jane.smith@example.com", updatedProfile.email)
  }

  @Test
  fun testUpdateEmailWithInvalidEmailShowsError() {
    composeTestRule.setContent {
      QuickFixTheme {
        ProfileConfigurationScreen(
            navigationActions = navigationActions,
            userViewModel = userViewModel, // Use real userViewModel
            workerViewModel = workerViewModel,
            loggedInProfileViewModel = loggedInProfileViewModel)
      }
    }

    // Update email with invalid email using performTextReplacement
    composeTestRule.onNodeWithTag("emailInput").performTextReplacement("invalidemail")

    // Attempt to click Save button
    composeTestRule.onNodeWithTag("SaveButton").performClick()

    // Verify that updateProfile was not called due to invalid email
    verify(userRepository, never()).updateProfile(any(), any(), any())
  }

  @Test
  fun testUpdateBirthDateWithValidDate() {
    // Arrange
    doAnswer { invocation ->
          val profile = invocation.getArgument<Profile>(0)
          val onSuccess = invocation.getArgument<() -> Unit>(1)
          val onFailure = invocation.getArgument<(Exception) -> Unit>(2)
          onSuccess()
          null
        }
        .whenever(userRepository)
        .updateProfile(any(), any(), any())

    composeTestRule.setContent {
      QuickFixTheme {
        ProfileConfigurationScreen(
            navigationActions = navigationActions,
            userViewModel = userViewModel,
            workerViewModel = workerViewModel,
            loggedInProfileViewModel = loggedInProfileViewModel)
      }
    }

    // Update birth date using performTextReplacement
    composeTestRule.onNodeWithTag("birthDateInput").performTextReplacement("01/01/1990")

    // Click Save button
    composeTestRule.onNodeWithTag("SaveButton").performClick()

    // Verify that updateProfile was called with updated birth date
    val profileCaptor = argumentCaptor<Profile>()
    verify(userRepository).updateProfile(profileCaptor.capture(), any(), any())

    val updatedProfile = profileCaptor.firstValue

    val calendar = GregorianCalendar(1990, Calendar.JANUARY, 1, 0, 0, 0)
    val expectedTimestamp = Timestamp(calendar.time)

    assertEquals(expectedTimestamp.seconds, updatedProfile.birthDate.seconds)
  }

  @Test
  fun testUpdateBirthDateWithInvalidDateShowsToast() {
    composeTestRule.setContent {
      QuickFixTheme {
        ProfileConfigurationScreen(
            navigationActions = navigationActions,
            userViewModel = userViewModel,
            workerViewModel = workerViewModel,
            loggedInProfileViewModel = loggedInProfileViewModel)
      }
    }

    // Update birth date with invalid date using performTextReplacement
    composeTestRule.onNodeWithTag("birthDateInput").performTextReplacement("invalid-date")

    // Click Save button
    composeTestRule.onNodeWithTag("SaveButton").performClick()

    // Verify that updateProfile was not called due to invalid date
    verify(userRepository, never()).updateProfile(any(), any(), any())
  }

  @Test
  fun testChangePasswordButtonClick() {
    composeTestRule.setContent {
      QuickFixTheme {
        ProfileConfigurationScreen(
            navigationActions = navigationActions,
            userViewModel = userViewModel,
            workerViewModel = workerViewModel,
            loggedInProfileViewModel = loggedInProfileViewModel)
      }
    }

    // Click Change Password button
    composeTestRule.onNodeWithTag("ChangePasswordButton").performClick()

    // Since the action is not implemented, verify that nothing crashes
  }

  @Test
  fun testSaveButtonUpdatesLoggedInProfile() {
    // Arrange
    doAnswer { invocation ->
          val profile = invocation.getArgument<Profile>(0)
          val onSuccess = invocation.getArgument<() -> Unit>(1)
          val onFailure = invocation.getArgument<(Exception) -> Unit>(2)
          onSuccess()
          null
        }
        .whenever(userRepository)
        .updateProfile(any(), any(), any())

    doAnswer { invocation ->
          val uid = invocation.getArgument<String>(0)
          val onSuccess = invocation.getArgument<(Profile?) -> Unit>(1)
          val onFailure = invocation.getArgument<(Exception) -> Unit>(2)
          val updatedProfile =
              UserProfile(
                  uid = testUserProfile.uid,
                  firstName = "Jane",
                  lastName = testUserProfile.lastName,
                  email = testUserProfile.email,
                  birthDate = testUserProfile.birthDate,
                  location = testUserProfile.location,
                  isWorker = testUserProfile.isWorker)
          onSuccess(updatedProfile)
          null
        }
        .whenever(userRepository)
        .getProfileById(any(), any(), any())

    composeTestRule.setContent {
      QuickFixTheme {
        ProfileConfigurationScreen(
            navigationActions = navigationActions,
            userViewModel = userViewModel, // Use real userViewModel
            workerViewModel = workerViewModel,
            loggedInProfileViewModel = loggedInProfileViewModel)
      }
    }

    // Update first name using performTextReplacement
    composeTestRule.onNodeWithTag("firstNameInput").performTextReplacement("Jane")

    // Click Save button
    composeTestRule.onNodeWithTag("SaveButton").performClick()

    // Wait for UI to update
    composeTestRule.waitForIdle()

    // Check that the displayed name is updated
    composeTestRule.onNodeWithTag("ProfileName").assertTextEquals("Jane Doe")
  }

  @Test
  fun testSaveButtonNavigatesBack() {
    // Arrange
    doAnswer { invocation ->
          val profile = invocation.getArgument<Profile>(0)
          val onSuccess = invocation.getArgument<() -> Unit>(1)
          val onFailure = invocation.getArgument<(Exception) -> Unit>(2)
          onSuccess()
          null
        }
        .whenever(userRepository)
        .updateProfile(any(), any(), any())

    composeTestRule.setContent {
      QuickFixTheme {
        ProfileConfigurationScreen(
            navigationActions = navigationActions,
            userViewModel = userViewModel, // Use real userViewModel
            workerViewModel = workerViewModel,
            loggedInProfileViewModel = loggedInProfileViewModel)
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
          val onSuccess = invocation.getArgument<(Pair<Boolean, Profile?>) -> Unit>(1)
          val onFailure = invocation.getArgument<(Exception) -> Unit>(2)
          val existingProfile =
              UserProfile(
                  uid = "existingUid",
                  firstName = "Existing",
                  lastName = "User",
                  email = "existing@example.com",
                  birthDate = Timestamp.now(),
                  location = GeoPoint(0.0, 0.0),
                  isWorker = false)
          onSuccess(Pair(true, existingProfile))
          null
        }
        .whenever(userRepository)
        .profileExists(any(), any(), any())

    composeTestRule.setContent {
      QuickFixTheme {
        ProfileConfigurationScreen(
            navigationActions = navigationActions,
            userViewModel = userViewModel, // Use real userViewModel
            workerViewModel = workerViewModel,
            loggedInProfileViewModel = loggedInProfileViewModel)
      }
    }

    // Update email to an existing email using performTextReplacement
    composeTestRule.onNodeWithTag("emailInput").performTextReplacement("existing@example.com")

    // Click Save button
    composeTestRule.onNodeWithTag("SaveButton").performClick()

    // Verify that updateProfile was not called
    verify(userRepository, never()).updateProfile(any(), any(), any())
  }

  @Test
  fun testInitialUIElementsAreDisplayed() {
    // Set up the content
    composeTestRule.setContent {
      QuickFixTheme {
        ProfileConfigurationScreen(
            navigationActions = navigationActions,
            userViewModel = userViewModel, // Use real userViewModel
            workerViewModel = workerViewModel,
            loggedInProfileViewModel = loggedInProfileViewModel)
      }
    }

    // Verify that the Top App Bar is displayed with the correct title
    composeTestRule.onNodeWithTag("AccountConfigurationTopAppBar").assertIsDisplayed()
    composeTestRule
        .onNodeWithTag("AccountConfigurationTitle")
        .assertTextEquals("Account configuration")

    // Verify that the Profile Image is displayed
    composeTestRule.onNodeWithTag("ProfileImage").assertIsDisplayed()

    // Verify that the Profile Card is displayed with the correct name
    composeTestRule.onNodeWithTag("ProfileCard").assertIsDisplayed()
    composeTestRule.onNodeWithTag("ProfileName").assertTextEquals("John Doe")

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
