package com.arygm.quickfix.ui.authentication

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.arygm.quickfix.ui.navigation.NavigationActions
import com.arygm.quickfix.ui.navigation.Screen
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

class RegisterScreenTest {

  @get:Rule val composeTestRule = createComposeRule()

  private lateinit var navigationActions: NavigationActions

  @Before
  fun setup() {
    navigationActions = mock(NavigationActions::class.java)
    `when`(navigationActions.currentRoute()).thenReturn(Screen.REGISTER)
  }

  @Test
  fun testInitialUI() {
    composeTestRule.setContent { RegisterScreen(navigationActions) }

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
    composeTestRule.setContent { RegisterScreen(navigationActions) }

    // Input an invalid email
    composeTestRule.onNodeWithTag("emailInput").performTextInput("invalidemail")

    // Assert that the email error is shown
    composeTestRule.onNodeWithText("INVALID EMAIL").assertIsDisplayed()
    composeTestRule.onNodeWithTag("registerButton").assertIsNotEnabled()
  }

  @Test
  fun testInvalidDateShowsError() {
    composeTestRule.setContent { RegisterScreen(navigationActions) }

    // Input an invalid birth date
    composeTestRule.onNodeWithTag("birthDateInput").performTextInput("99/99/9999")

    // Assert that the birth date error is shown
    composeTestRule.onNodeWithText("INVALID DATE").assertIsDisplayed()
    composeTestRule.onNodeWithTag("registerButton").assertIsNotEnabled()
  }

  @Test
  fun testPasswordMismatch() {
    composeTestRule.setContent { RegisterScreen(navigationActions) }

    // Enter different passwords
    composeTestRule.onNodeWithTag("passwordInput").performTextInput("Password123")
    composeTestRule.onNodeWithTag("repeatPasswordInput").performTextInput("Password321")

    // Assert that no match error message is displayed
    composeTestRule.onNodeWithTag("noMatchText").assertIsDisplayed()
  }

  @Test
  fun testRegisterButtonEnabledWhenFormIsValid() {
    composeTestRule.setContent { RegisterScreen(navigationActions) }

    // Fill out valid inputs
    composeTestRule.onNodeWithTag("firstNameInput").performTextInput("John")
    composeTestRule.onNodeWithTag("lastNameInput").performTextInput("Doe")
    composeTestRule.onNodeWithTag("emailInput").performTextInput("john.doe@example.com")
    composeTestRule.onNodeWithTag("birthDateInput").performTextInput("01/01/1990")
    composeTestRule.onNodeWithTag("passwordInput").performTextInput("Password123")
    composeTestRule.onNodeWithTag("repeatPasswordInput").performTextInput("Password123")

    // Check the terms and privacy policy checkboxes
    composeTestRule.onNodeWithTag("checkbox").performClick()

    // Assert that the "Register" button is now enabled
    composeTestRule.onNodeWithTag("registerButton").assertIsEnabled()

    // Click the button and verify navigation
    composeTestRule.onNodeWithTag("registerButton").performClick()
    Mockito.verify(navigationActions).navigateTo(Screen.HOME)
  }

  @Test
  fun testRegisterButtonDisabledWhenFormIncomplete() {
    composeTestRule.setContent { RegisterScreen(navigationActions) }

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
    composeTestRule.setContent { RegisterScreen(navigationActions) }

    // Click the back button
    composeTestRule.onNodeWithTag("goBackButton").performClick()

    // Verify that the navigation action was triggered
    Mockito.verify(navigationActions).goBack()
  }

  @Test
  fun testLoginButtonNavigatesToLogin() {
    composeTestRule.setContent { RegisterScreen(navigationActions) }

    // Click the "Login !" button
    composeTestRule.onNodeWithTag("clickableLoginButtonText").performClick()

    // Verify that the navigation action is triggered for the login screen
    Mockito.verify(navigationActions).navigateTo(Screen.LOGIN)
  }
}
