package com.arygm.quickfix.ui.profile

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.arygm.quickfix.model.profile.*
import com.arygm.quickfix.ui.navigation.NavigationActions
import com.arygm.quickfix.ui.theme.QuickFixTheme
import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint
import junit.framework.TestCase.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito
import org.mockito.kotlin.*

class BusinessScreenTest {

  @get:Rule val composeTestRule = createComposeRule()

  private lateinit var navigationActions: NavigationActions
  private lateinit var userViewModel: ProfileViewModel
  private lateinit var workerViewModel: ProfileViewModel
  private lateinit var userRepository: ProfileRepository
  private lateinit var workerRepository: ProfileRepository
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
    navigationActions = mock()
    userRepository = mock()
    workerRepository = mock()
    userViewModel = ProfileViewModel(userRepository)
    workerViewModel = ProfileViewModel(workerRepository)
    loggedInProfileViewModel = LoggedInProfileViewModel()
    loggedInProfileViewModel.setLoggedInProfile(testUserProfile)
  }

  @Test
  fun testInitialUI() {
    composeTestRule.setContent {
      QuickFixTheme {
        BusinessScreen(navigationActions, userViewModel, workerViewModel, loggedInProfileViewModel)
      }
    }

    // Check UI elements are displayed
    composeTestRule.onNodeWithTag("BusinessAccountTitle").assertIsDisplayed()
    composeTestRule.onNodeWithText("Business Account").assertIsDisplayed()
    composeTestRule.onNodeWithTag("goBackButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("ProfileCard").assertIsDisplayed()
    composeTestRule.onNodeWithTag("ProfileName").assertIsDisplayed()
    composeTestRule.onNodeWithText("John Doe").assertIsDisplayed()
    composeTestRule.onNodeWithTag("occupationInput").assertIsDisplayed()
    composeTestRule.onNodeWithTag("descriptionInput").assertIsDisplayed()
    composeTestRule.onNodeWithTag("hourlyRateInput").assertIsDisplayed()
    composeTestRule.onNodeWithTag("locationInput").assertIsDisplayed()
    composeTestRule.onNodeWithTag("validateButton").assertIsDisplayed().assertIsEnabled()
  }

  @Test
  fun testBackButtonNavigatesBack() {
    composeTestRule.setContent {
      QuickFixTheme {
        BusinessScreen(navigationActions, userViewModel, workerViewModel, loggedInProfileViewModel)
      }
    }

    composeTestRule.onNodeWithTag("goBackButton").performClick()
    Mockito.verify(navigationActions).goBack()
  }

  @Test
  fun testOccupationDropdownFunctionality() {
    composeTestRule.setContent {
      QuickFixTheme {
        BusinessScreen(navigationActions, userViewModel, workerViewModel, loggedInProfileViewModel)
      }
    }

    composeTestRule.onNodeWithTag("occupationInput").performTextInput("Car")
    composeTestRule.waitForIdle()

    //        composeTestRule.onNodeWithTag("occupationDropdownIcon").performClick()
    //        composeTestRule.waitForIdle()

    composeTestRule.onNodeWithText("Carpenter").assertIsDisplayed()
    composeTestRule.onNodeWithText("Carpenter").performClick()
    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithTag("occupationInput").assertTextContains("Carpenter")
  }

  @Test
  fun testValidateButtonWithEmptyFieldsShowsError() {
    composeTestRule.setContent {
      QuickFixTheme {
        BusinessScreen(navigationActions, userViewModel, workerViewModel, loggedInProfileViewModel)
      }
    }

    composeTestRule.onNodeWithTag("validateButton").performClick()
    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithTag("errorMessage").assertIsDisplayed()
    composeTestRule.onNodeWithText("Please fill all fields").assertIsDisplayed()
  }

  @Test
  fun testValidateButtonWithInvalidHourlyRateShowsError() {
    composeTestRule.setContent {
      QuickFixTheme {
        BusinessScreen(navigationActions, userViewModel, workerViewModel, loggedInProfileViewModel)
      }
    }

    composeTestRule.onNodeWithTag("occupationInput").performTextInput("Plumber")
    composeTestRule.onNodeWithTag("hourlyRateInput").performTextInput("invalid_rate")
    composeTestRule.onNodeWithTag("locationInput").performTextInput("New York")

    composeTestRule.onNodeWithTag("validateButton").performClick()
    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithTag("errorMessage").assertIsDisplayed()
    composeTestRule.onNodeWithText("Please fill all fields").assertIsDisplayed()
  }

  @Test
  fun testSuccessfulBusinessAccountValidation() {
    doAnswer { invocation ->
          val onSuccess = invocation.getArgument<() -> Unit>(1)
          onSuccess()
          null
        }
        .whenever(userRepository)
        .updateProfile(any(), any(), any())

    doAnswer { invocation ->
          val onSuccess = invocation.getArgument<() -> Unit>(1)
          onSuccess()
          null
        }
        .whenever(workerRepository)
        .addProfile(any(), any(), any())

    composeTestRule.setContent {
      QuickFixTheme {
        BusinessScreen(navigationActions, userViewModel, workerViewModel, loggedInProfileViewModel)
      }
    }

    composeTestRule.onNodeWithTag("occupationInput").performTextInput("Plumber")
    composeTestRule.onNodeWithTag("descriptionInput").performTextInput("Experienced plumber")
    composeTestRule.onNodeWithTag("hourlyRateInput").performTextInput("50")
    composeTestRule.onNodeWithTag("locationInput").performTextInput("New York")
    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithTag("validateButton").performClick()
    composeTestRule.waitForIdle()

    Mockito.verify(navigationActions).goBack()
  }

  @Test
  fun testFailedBusinessAccountValidation() {
    doAnswer { invocation ->
          val onFailure = invocation.getArgument<(Exception) -> Unit>(2)
          onFailure(Exception("Failed to add profile"))
          null
        }
        .whenever(workerRepository)
        .addProfile(any(), any(), any())

    composeTestRule.setContent {
      QuickFixTheme {
        BusinessScreen(navigationActions, userViewModel, workerViewModel, loggedInProfileViewModel)
      }
    }

    composeTestRule.onNodeWithTag("occupationInput").performTextInput("Electrician")
    composeTestRule.onNodeWithTag("descriptionInput").performTextInput("Expert electrician")
    composeTestRule.onNodeWithTag("hourlyRateInput").performTextInput("60")
    composeTestRule.onNodeWithTag("locationInput").performTextInput("Los Angeles")
    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithTag("validateButton").performClick()
    composeTestRule.waitForIdle()

    Mockito.verify(navigationActions, Mockito.never()).goBack()
  }

  @Test
  fun testDescriptionInputAcceptsMultiline() {
    composeTestRule.setContent {
      QuickFixTheme {
        BusinessScreen(navigationActions, userViewModel, workerViewModel, loggedInProfileViewModel)
      }
    }

    val multilineText = "Line1\nLine2\nLine3"
    composeTestRule.onNodeWithTag("descriptionInput").performTextInput(multilineText)
    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithTag("descriptionInput").assertTextContains(multilineText)
  }

  @Test
  fun testHourlyRateInputAllowsOnlyNumbers() {
    composeTestRule.setContent {
      QuickFixTheme {
        BusinessScreen(navigationActions, userViewModel, workerViewModel, loggedInProfileViewModel)
      }
    }

    composeTestRule.onNodeWithTag("hourlyRateInput").performTextInput("abc123.45def")
    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithTag("hourlyRateInput").assertTextContains("123.45")
  }

  @Test
  fun testDropdownMenuFiltersOccupations() {
    composeTestRule.setContent {
      QuickFixTheme {
        BusinessScreen(navigationActions, userViewModel, workerViewModel, loggedInProfileViewModel)
      }
    }

    composeTestRule.onNodeWithTag("occupationInput").performTextInput("P")
    composeTestRule.waitForIdle()

    //        composeTestRule.onNodeWithTag("occupationDropdownIcon").performClick()
    //        composeTestRule.waitForIdle()

    composeTestRule.onNodeWithText("Painter").assertIsDisplayed()
    composeTestRule.onNodeWithText("Plumber").assertIsDisplayed()
    composeTestRule.onNodeWithText("Carpenter").assertDoesNotExist()
  }

  @Test
  fun testErrorMessageDisappearsAfterCorrectInput() {
    composeTestRule.setContent {
      QuickFixTheme {
        BusinessScreen(navigationActions, userViewModel, workerViewModel, loggedInProfileViewModel)
      }
    }

    composeTestRule.onNodeWithTag("validateButton").performClick()
    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithTag("errorMessage").assertIsDisplayed()

    composeTestRule.onNodeWithTag("occupationInput").performTextInput("Electrician")
    composeTestRule.onNodeWithTag("hourlyRateInput").performTextInput("40")
    composeTestRule.onNodeWithTag("locationInput").performTextInput("Chicago")
    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithTag("validateButton").performClick()
    composeTestRule.waitForIdle()

    // Since error messages are shown via Toasts, and errorMessage may not be updated,
    // We can check that the error message is no longer displayed (if applicable)
    composeTestRule.onNodeWithTag("errorMessage").assertDoesNotExist()
  }

  @Test
  fun testWorkerProfileAddedWithCorrectFields() {
    // Arrange
    val profileCaptor = argumentCaptor<Profile>()
    doAnswer { invocation ->
          val onSuccess = invocation.getArgument<() -> Unit>(1)
          onSuccess()
          null
        }
        .whenever(userRepository)
        .updateProfile(any(), any(), any())

    doAnswer { invocation ->
          val onSuccess = invocation.getArgument<() -> Unit>(1)
          onSuccess()
          null
        }
        .whenever(workerRepository)
        .addProfile(profileCaptor.capture(), any(), any())

    composeTestRule.setContent {
      QuickFixTheme {
        BusinessScreen(navigationActions, userViewModel, workerViewModel, loggedInProfileViewModel)
      }
    }

    // Act
    composeTestRule.onNodeWithTag("occupationInput").performTextInput("Plumber")
    composeTestRule.onNodeWithTag("occupationDropdownIcon").performClick()
    composeTestRule.onNodeWithTag("descriptionInput").performTextInput("Experienced plumber")
    composeTestRule.onNodeWithTag("hourlyRateInput").performTextInput("50")
    composeTestRule.onNodeWithTag("locationInput").performTextInput("New York")
    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithTag("validateButton").performClick()
    composeTestRule.waitForIdle()

    // Assert
    Mockito.verify(workerRepository).addProfile(any(), any(), any())
    val addedProfile = profileCaptor.firstValue as WorkerProfile

    assertEquals("testUid", addedProfile.uid)
    assertEquals("John", addedProfile.firstName)
    assertEquals("Doe", addedProfile.lastName)
    assertEquals("john.doe@example.com", addedProfile.email)
    assertEquals("Plumber", addedProfile.fieldOfWork)
    assertEquals(50.0, addedProfile.hourlyRate)
    assertEquals("Experienced plumber", addedProfile.description)
    assertEquals(GeoPoint(0.0, 0.0), addedProfile.location)
  }

  @Test
  fun testUserProfileIsUpdatedWithIsWorkerTrue() {
    // Arrange
    val userProfileCaptor = argumentCaptor<Profile>()
    doAnswer { invocation ->
          val onSuccess = invocation.getArgument<() -> Unit>(1)
          onSuccess()
          null
        }
        .whenever(userRepository)
        .updateProfile(userProfileCaptor.capture(), any(), any())

    doAnswer { invocation ->
          val onSuccess = invocation.getArgument<() -> Unit>(1)
          onSuccess()
          null
        }
        .whenever(workerRepository)
        .addProfile(any(), any(), any())

    composeTestRule.setContent {
      QuickFixTheme {
        BusinessScreen(navigationActions, userViewModel, workerViewModel, loggedInProfileViewModel)
      }
    }

    // Act
    // Fill all required fields
    composeTestRule.onNodeWithTag("occupationInput").performTextInput("Plumber")
    composeTestRule.onNodeWithTag("occupationDropdownIcon").performClick()
    composeTestRule.onNodeWithTag("descriptionInput").performTextInput("Experienced plumber")
    composeTestRule.onNodeWithTag("hourlyRateInput").performTextInput("50")
    composeTestRule.onNodeWithTag("locationInput").performTextInput("New York")
    composeTestRule.waitForIdle()

    // Click the validate button
    composeTestRule.onNodeWithTag("validateButton").performClick()
    composeTestRule.waitForIdle()

    // Assert
    // Verify that the user profile was updated with isWorker = true
    Mockito.verify(userRepository).updateProfile(any(), any(), any())
    val updatedUserProfile = userProfileCaptor.firstValue as UserProfile

    assertEquals("testUid", updatedUserProfile.uid)
    assertEquals("John", updatedUserProfile.firstName)
    assertEquals("Doe", updatedUserProfile.lastName)
    assertEquals("john.doe@example.com", updatedUserProfile.email)
    assertEquals(true, updatedUserProfile.isWorker)
    // You can also verify other fields if necessary
  }
}
