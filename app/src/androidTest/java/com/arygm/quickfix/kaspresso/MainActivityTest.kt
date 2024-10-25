package com.arygm.quickfix.kaspresso

import android.util.Log
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.printToLog
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.arygm.quickfix.MainActivity
import com.arygm.quickfix.kaspresso.screen.WelcomeScreen
import com.arygm.quickfix.ui.navigation.NavigationActions
import com.kaspersky.kaspresso.flakysafety.*
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.github.kakaocup.compose.node.element.ComposeScreen
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito

@RunWith(AndroidJUnit4::class)
class MainActivityTest : TestCase() {

  private lateinit var navigationActions: NavigationActions

  @get:Rule val composeTestRule = createAndroidComposeRule<MainActivity>()

  @Before
  fun setup() {
    // Initialize the navigationActions mock
    navigationActions = Mockito.mock(NavigationActions::class.java)
  }

  @Test
  fun shouldNotBeAbleToReg() = run {
    step("Set up the WelcomeScreen and transit to the register") {
      // Retry the action until it works with a timeout of 10 seconds
      ComposeScreen.onComposeScreen<WelcomeScreen>(composeTestRule) {
        registerButton {
          assertIsDisplayed()
          performClick()
          // Log the click action
          Log.d("TestLog", "Register button clicked")
        }
      }
      composeTestRule.mainClock.advanceTimeBy(2500L)
      composeTestRule.onNodeWithTag("firstNameInput").performTextInput("Ramy")
      composeTestRule.onNodeWithTag("lastNameInput").performTextInput("Hatimy")
      composeTestRule.onNodeWithTag("emailInput").performTextInput("hatimyramy@gmail.com")
      composeTestRule.onNodeWithTag("birthDateInput").performTextInput("28/10/2004")
      composeTestRule.onNodeWithTag("passwordInput").performTextInput("246890357Asefthuk")
      composeTestRule.onNodeWithTag("repeatPasswordInput").performTextInput("246890357Asefthuk")
      composeTestRule.onNodeWithTag("checkbox").performClick()
      composeTestRule.onNodeWithTag("registerButton").assertIsNotEnabled()
    }
  }

  @Test
  fun shouldBeAbleToLogin() = run {
    step("Set up the WelcomeScreen and transit to the register") {
      // Retry the action until it works with a timeout of 10 seconds
      ComposeScreen.onComposeScreen<WelcomeScreen>(composeTestRule) {
        loginButton {
          assertIsDisplayed()
          performClick()
          // Log the click action
        }
      }
      composeTestRule.mainClock.advanceTimeBy(2500L)
      composeTestRule.onNodeWithTag("inputEmail").performTextInput("main.activity@test.com")
      composeTestRule.onNodeWithTag("inputPassword").performTextInput("246890357Asefthuk")
      composeTestRule.onNodeWithTag("logInButton").assertIsEnabled()
      composeTestRule.onNodeWithTag("logInButton").performClick()
      composeTestRule.onRoot().printToLog("TAG")
    }
  }
}
