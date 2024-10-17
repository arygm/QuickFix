package com.arygm.quickfix.ui.authentication

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.arygm.quickfix.model.profile.ProfileRepository
import com.arygm.quickfix.model.profile.ProfileViewModel
import com.arygm.quickfix.model.profile.RegistrationViewModel
import com.arygm.quickfix.ui.navigation.NavigationActions
import com.arygm.quickfix.ui.navigation.Screen
import com.arygm.quickfix.ui.navigation.TopLevelDestinations
import com.google.firebase.auth.FirebaseAuth
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

@Deprecated("Theses tests are deprecated", ReplaceWith("RegisterScreenTests"))
class PasswordScreenTest {

  @get:Rule val composeTestRule = createComposeRule()

  private lateinit var profileRepository: ProfileRepository
  private lateinit var profileViewModel: ProfileViewModel
  private lateinit var navigationActions: NavigationActions
  private lateinit var registrationViewModel: RegistrationViewModel

  @Before
  fun setup() {
    profileRepository = mock(ProfileRepository::class.java)
    navigationActions = mock(NavigationActions::class.java)
    profileViewModel = mock(ProfileViewModel::class.java) // Mock the ProfileViewModel
    registrationViewModel = RegistrationViewModel()
    registrationViewModel.updateFirstName("John")
    registrationViewModel.updateFirstName("Doe")
    registrationViewModel.updateFirstName("john.doe@example.com")
    registrationViewModel.updateFirstName("01/01/1990")

    `when`(navigationActions.currentRoute()).thenReturn(Screen.PASSWORD)
  }

  @Test
  fun testInitialState() {
    composeTestRule.setContent {
      PasswordScreen(navigationActions, registrationViewModel, profileViewModel)
    }

    // Check that the input fields, password conditions, and button are displayed
    composeTestRule.onNodeWithTag("passwordBox").assertIsDisplayed()
    composeTestRule.onNodeWithTag("AnimationBox").assertIsDisplayed()
    composeTestRule.onNodeWithTag("contentBox").assertIsDisplayed()
    composeTestRule.onNodeWithTag("goBackButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("boxDecoration").assertIsDisplayed()
    composeTestRule.onNodeWithTag("passwordText").assertIsDisplayed()
    composeTestRule.onNodeWithTag("passwordInput").assertIsDisplayed()
    composeTestRule.onNodeWithTag("repeatPasswordInput").assertIsDisplayed()
    composeTestRule.onNodeWithTag("noMatchText").assertIsNotDisplayed()
    composeTestRule.onNodeWithTag("registerButton").assertIsDisplayed().assertIsNotEnabled()

    // Check that all password condition messages are displayed
    composeTestRule.onNodeWithTag("PASSWORD SHOULD BE AT LEAST 8 CHARACTERS").assertIsDisplayed()
    composeTestRule
        .onNodeWithTag("PASSWORD SHOULD CONTAIN AN UPPERCASE LETTER (A-Z)")
        .assertIsDisplayed()
    composeTestRule
        .onNodeWithTag("PASSWORD SHOULD CONTAIN A LOWERCASE LETTER (a-z)")
        .assertIsDisplayed()
    composeTestRule.onNodeWithTag("PASSWORD SHOULD CONTAIN A DIGIT (0-9)").assertIsDisplayed()
  }

  @Test
  fun testPasswordConditionsUpdateOnInput() {
    composeTestRule.setContent {
      PasswordScreen(navigationActions, registrationViewModel, profileViewModel)
    }

    // Input a password that meets all conditions
    composeTestRule.onNodeWithTag("passwordInput").performTextInput("Password1")

    // Assert that all conditions are met (colored as tertiary)
    composeTestRule.onNodeWithTag("PASSWORD SHOULD BE AT LEAST 8 CHARACTERS").assertIsDisplayed()
    composeTestRule
        .onNodeWithTag("PASSWORD SHOULD CONTAIN AN UPPERCASE LETTER (A-Z)")
        .assertIsDisplayed()
    composeTestRule
        .onNodeWithTag("PASSWORD SHOULD CONTAIN A LOWERCASE LETTER (a-z)")
        .assertIsDisplayed()
    composeTestRule.onNodeWithTag("PASSWORD SHOULD CONTAIN A DIGIT (0-9)").assertIsDisplayed()
    composeTestRule.onNodeWithTag("registerButton").assertIsDisplayed().assertIsNotEnabled()
  }

  @Test
  fun testPasswordMismatchShowsError() {
    composeTestRule.setContent {
      PasswordScreen(navigationActions, registrationViewModel, profileViewModel)
    }

    // Input a password and a mismatching repeated password
    composeTestRule.onNodeWithTag("passwordInput").performTextInput("Password1")
    composeTestRule.onNodeWithTag("repeatPasswordInput").performTextInput("Password2")

    // Assert that the mismatch error is displayed
    composeTestRule.onNodeWithTag("noMatchText").assertIsDisplayed()
    composeTestRule.onNodeWithTag("registerButton").assertIsDisplayed().assertIsNotEnabled()
  }

  @Test
  fun testRegisterButtonEnabledWhenConditionsAreMet() {
    composeTestRule.setContent {
      PasswordScreen(navigationActions, registrationViewModel, profileViewModel)
    }

    // Input matching passwords that meet all conditions
    composeTestRule.onNodeWithTag("passwordInput").performTextInput("Password1")
    composeTestRule.onNodeWithTag("repeatPasswordInput").performTextInput("Password1")

    // Assert that the register button is now enabled
    composeTestRule.onNodeWithTag("noMatchText").assertIsNotDisplayed()
    composeTestRule.onNodeWithTag("registerButton").assertIsEnabled()
  }

  @Test
  fun testRegisterButtonDisabledWhenConditionsAreNotMet() {
    composeTestRule.setContent {
      PasswordScreen(navigationActions, registrationViewModel, profileViewModel)
    }

    // Input a password that does not meet all conditions (no digit)
    composeTestRule.onNodeWithTag("passwordInput").performTextInput("Password")

    // Assert that the register button is still disabled
    composeTestRule.onNodeWithTag("registerButton").assertIsNotEnabled()
  }

  @Test
  fun testBackButtonNavigatesBack() {
    composeTestRule.setContent {
      PasswordScreen(navigationActions, registrationViewModel, profileViewModel)
    }

    // Click the back button
    composeTestRule.onNodeWithTag("goBackButton").performClick()

    // Verify that the navigation action was triggered
    Mockito.verify(navigationActions).goBack()
  }

  @Test
  fun testRegisterButtonClickSuccessfulRegistration() {
    var createAccountFuncCalled = false

    val testCreateAccountFunc =
        {
            _: FirebaseAuth,
            _: String,
            _: String,
            _: String,
            _: String,
            _: String,
            _: ProfileViewModel,
            onSuccess: () -> Unit,
            _: () -> Unit ->
          createAccountFuncCalled = true
          onSuccess() // Simulate success
    }

    composeTestRule.setContent {
      PasswordScreen(
          navigationActions = navigationActions,
          registrationViewModel = registrationViewModel,
          profileViewModel = profileViewModel,
          createAccountFunc = testCreateAccountFunc)
    }

    // Input matching passwords that meet all conditions
    composeTestRule.onNodeWithTag("passwordInput").performTextInput("Password1")
    composeTestRule.onNodeWithTag("repeatPasswordInput").performTextInput("Password1")

    // Click the register button
    composeTestRule.onNodeWithTag("registerButton").performClick()

    // Verify that createAccountFunc was called
    assertTrue(createAccountFuncCalled)

    // Verify that navigation to HOME was triggered
    Mockito.verify(navigationActions).navigateTo(TopLevelDestinations.HOME)
  }

  @Test
  fun testRegisterButtonClickFailedRegistration() {
    var createAccountFuncCalled = false

    val testCreateAccountFunc =
        {
            _: FirebaseAuth,
            _: String,
            _: String,
            _: String,
            _: String,
            _: String,
            _: ProfileViewModel,
            _: () -> Unit,
            onFailure: () -> Unit ->
          createAccountFuncCalled = true
          onFailure() // Simulate failure
    }

    composeTestRule.setContent {
      PasswordScreen(
          navigationActions = navigationActions,
          registrationViewModel = registrationViewModel,
          profileViewModel = profileViewModel,
          createAccountFunc = testCreateAccountFunc)
    }

    // Input matching passwords that meet all conditions
    composeTestRule.onNodeWithTag("passwordInput").performTextInput("Password1")
    composeTestRule.onNodeWithTag("repeatPasswordInput").performTextInput("Password1")

    // Click the register button
    composeTestRule.onNodeWithTag("registerButton").performClick()

    // Verify that createAccountFunc was called
    assertTrue(createAccountFuncCalled)

    // Verify that navigation to HOME was not triggered
    Mockito.verify(navigationActions, Mockito.never()).navigateTo(TopLevelDestinations.HOME)
  }
}
