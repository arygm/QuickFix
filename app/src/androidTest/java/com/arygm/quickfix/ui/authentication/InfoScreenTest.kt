package com.arygm.quickfix.ui.authentication

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.arygm.quickfix.model.profile.Profile
import com.arygm.quickfix.model.profile.ProfileRepository
import com.arygm.quickfix.model.profile.ProfileViewModel
import com.arygm.quickfix.model.profile.RegistrationViewModel
import com.arygm.quickfix.ui.navigation.NavigationActions
import com.arygm.quickfix.ui.navigation.Screen
import com.google.firebase.Timestamp
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.whenever

class InfoScreenTest {

  @get:Rule val composeTestRule = createComposeRule()

  private lateinit var profileRepository: ProfileRepository
  private lateinit var profileViewModel: ProfileViewModel
  private lateinit var navigationActions: NavigationActions
  private lateinit var registrationViewModel: RegistrationViewModel

  @Before
  fun setup() {
    profileRepository = mock(ProfileRepository::class.java)
    navigationActions = mock(NavigationActions::class.java)
    profileViewModel = ProfileViewModel(profileRepository)
    registrationViewModel = RegistrationViewModel()

    `when`(navigationActions.currentRoute()).thenReturn(Screen.INFO)
  }

  @Test
  fun testInitialState() {
    composeTestRule.setContent {
      InfoScreen(navigationActions, registrationViewModel, profileViewModel)
    }

    composeTestRule.onNodeWithTag("InfoBox").assertIsDisplayed()
    composeTestRule.onNodeWithTag("goBackButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("contentBox").assertIsDisplayed()
    composeTestRule.onNodeWithTag("decorationBox").assertIsDisplayed()
    composeTestRule.onNodeWithTag("welcomeText").assertIsDisplayed()
    composeTestRule.onNodeWithTag("AnimationBox").assertIsDisplayed()

    // Check that all input fields and checkboxes are present
    composeTestRule.onNodeWithTag("firstNameInput").assertIsDisplayed()
    composeTestRule.onNodeWithTag("lastNameInput").assertIsDisplayed()
    composeTestRule.onNodeWithTag("emailInput").assertIsDisplayed()
    composeTestRule.onNodeWithTag("birthDateInput").assertIsDisplayed()

    composeTestRule
        .onAllNodesWithTag("checkbox")[0]
        .assertIsDisplayed() // First checkbox (Terms and Conditions)
    composeTestRule
        .onAllNodesWithTag("checkbox")[1]
        .assertIsDisplayed() // Second checkbox (Privacy Policy)

    // Check that both info texts are displayed
    composeTestRule
        .onAllNodesWithTag("checkBoxInfo")[0]
        .assertIsDisplayed() // First checkbox label "I ACCEPT THE"
    composeTestRule
        .onAllNodesWithTag("checkBoxInfo")[1]
        .assertIsDisplayed() // Second checkbox label "I ACCEPT THE"

    // Check that the clickable texts are displayed and clickable
    composeTestRule
        .onAllNodesWithTag("clickableLink")[0]
        .assertIsDisplayed()
        .assertHasClickAction() // "TERMS AND CONDITIONS"
    composeTestRule
        .onAllNodesWithTag("clickableLink")[1]
        .assertIsDisplayed()
        .assertHasClickAction() // "PRIVACY POLICY"

    // Check that the button exists but is initially disabled
    composeTestRule.onNodeWithTag("nextButton").assertIsDisplayed().assertIsNotEnabled()
  }

  @Test
  fun testInvalidEmailShowsError() {
    composeTestRule.setContent {
      InfoScreen(navigationActions, registrationViewModel, profileViewModel)
    }

    // Input an invalid email
    composeTestRule.onNodeWithTag("emailInput").performTextInput("invalidemail")

    // Assert that the email error is shown
    composeTestRule.onNodeWithTag("errorText").assertIsDisplayed()
    composeTestRule.onNodeWithTag("errorText").assertTextEquals("INVALID EMAIL")
    composeTestRule.onNodeWithTag("nextButton").assertIsNotEnabled()
  }

  @Test
  fun testInvalidDateShowsError() {
    composeTestRule.setContent {
      InfoScreen(navigationActions, registrationViewModel, profileViewModel)
    }

    // Input an invalid birth date
    composeTestRule.onNodeWithTag("birthDateInput").performTextInput("99/99/9999")

    // Assert that the date error is shown
    composeTestRule.onNodeWithText("INVALID DATE").assertIsDisplayed()
    composeTestRule.onNodeWithTag("nextButton").assertIsNotEnabled()
  }

  @Test
  fun testNextButtonEnabledWhenFormIsValid() {
    composeTestRule.setContent {
      InfoScreen(navigationActions, registrationViewModel, profileViewModel)
    }

    // Fill out valid inputs
    composeTestRule.onNodeWithTag("firstNameInput").performTextInput("John")
    composeTestRule.onNodeWithTag("lastNameInput").performTextInput("Doe")
    composeTestRule.onNodeWithTag("emailInput").performTextInput("john.doe@example.com")
    composeTestRule.onNodeWithTag("birthDateInput").performTextInput("01/01/1990")

    // Check the terms and privacy policy checkboxes
    composeTestRule.onAllNodesWithTag("checkbox")[0].performClick()
    composeTestRule.onAllNodesWithTag("checkbox")[1].performClick()

    // Assert that the "NEXT" button is now enabled
    composeTestRule.onNodeWithTag("nextButton").assertIsEnabled()

    // Click the button and verify navigation
    composeTestRule.onNodeWithTag("nextButton").performClick()

    composeTestRule.mainClock.advanceTimeBy(500L)

    Mockito.verify(navigationActions).navigateTo(Screen.PASSWORD)
  }

  @Test
  fun testNextButtonDisabledWhenFormIncomplete() {
    composeTestRule.setContent {
      InfoScreen(navigationActions, registrationViewModel, profileViewModel)
    }

    // Fill only partial inputs
    composeTestRule.onNodeWithTag("firstNameInput").performTextInput("John")
    composeTestRule.onNodeWithTag("lastNameInput").performTextInput("Doe")
    // Leave email and date empty

    // Check the terms and privacy policy checkboxes
    composeTestRule.onAllNodesWithTag("checkbox")[0].performClick()
    composeTestRule.onAllNodesWithTag("checkbox")[1].performClick()

    // Assert that the "NEXT" button is still disabled
    composeTestRule.onNodeWithTag("nextButton").assertIsNotEnabled()
  }

  @Test
  fun testBackButtonNavigatesBack() {
    composeTestRule.setContent {
      InfoScreen(navigationActions, registrationViewModel, profileViewModel)
    }

    // Click the back button
    composeTestRule.onNodeWithTag("goBackButton").performClick()

    // perform delay here 500ms
    composeTestRule.mainClock.advanceTimeBy(500L)
    // Verify that the navigation action was triggered
    Mockito.verify(navigationActions).goBack()
  }

  @Test
  fun testEmailAlreadyExistsShowsError() {
    // Arrange
    val existingEmail = "john.doe@example.com"
    val profile =
        Profile(
            uid = "testUid",
            firstName = "John",
            lastName = "Doe",
            email = existingEmail,
            birthDate = Timestamp.now(),
            description = "Existing user")

    // Mock the profileRepository.profileExists to return exists = true, profile != null
    whenever(profileRepository.profileExists(eq(existingEmail), any(), any())).thenAnswer {
        invocation ->
      val onSuccess = invocation.getArgument<(Pair<Boolean, Profile?>) -> Unit>(1)
      onSuccess(Pair(true, profile))
      null
    }

    // Act
    composeTestRule.setContent {
      InfoScreen(navigationActions, registrationViewModel, profileViewModel)
    }

    composeTestRule.onNodeWithTag("emailInput").performTextInput(existingEmail)

    // Wait for possible recompositions
    composeTestRule.waitForIdle()

    // Assert
    composeTestRule.onNodeWithTag("errorText").assertIsDisplayed()
    composeTestRule.onNodeWithTag("errorText").assertTextEquals("INVALID EMAIL")
    composeTestRule.onNodeWithTag("nextButton").assertIsNotEnabled()
  }

  @Test
  fun testValidEmailDoesNotShowErrorWhenEmailDoesNotExist() {
    // Arrange
    val newEmail = "new.user@example.com"

    // Mock the profileRepository.profileExists to return exists = false, profile = null
    whenever(profileRepository.profileExists(eq(newEmail), any(), any())).thenAnswer { invocation ->
      val onSuccess = invocation.getArgument<(Pair<Boolean, Profile?>) -> Unit>(1)
      onSuccess(Pair(false, null))
      null
    }

    // Act
    composeTestRule.setContent {
      InfoScreen(navigationActions, registrationViewModel, profileViewModel)
    }

    composeTestRule.onNodeWithTag("emailInput").performTextInput(newEmail)

    // Wait for possible recompositions
    composeTestRule.waitForIdle()

    // Assert
    composeTestRule.onNodeWithTag("errorText").assertDoesNotExist()
    // Ensure the "NEXT" button remains disabled until other fields are filled and checkboxes are
    // checked
    composeTestRule.onNodeWithTag("nextButton").assertIsNotEnabled()
  }
}
