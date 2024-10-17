package com.arygm.quickfix

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.arygm.quickfix.ui.theme.QuickFixTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun bottomNavigation_isVisibleOnHomeScreen() {
    composeTestRule.setContent { QuickFixTheme { QuickFixApp() } }

    val testEmail = "john.doe@epfl.ch"
    val testPassword = "verySecurePassword"

    // move to log in
    composeTestRule.onNodeWithTag("logInButton").performClick()
    composeTestRule.mainClock.advanceTimeBy(1000L)
    // enter email
    composeTestRule.onNodeWithTag("inputEmail").performTextInput(testEmail)

    // enter password
    composeTestRule.onNodeWithTag("inputPassword").performTextInput(testPassword)

    // click on log in
    composeTestRule.onNodeWithTag("logInButton").performClick()
    composeTestRule.mainClock.advanceTimeBy(500L)
    // Verify that the bottom bar is displayed on the Home screen
    composeTestRule.onNodeWithTag("BottomNavMenu").assertIsDisplayed()
  }

  @Test
  fun bottomNavigation_isNotVisibleOnLoginScreen() {
    composeTestRule.setContent { QuickFixTheme { QuickFixApp() } }

    // Verify that the bottom bar is hidden on the Login screen
    composeTestRule.onNodeWithTag("BNM").assertIsNotDisplayed()
  }
}
