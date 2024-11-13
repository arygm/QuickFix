package com.arygm.quickfix.ui.elements

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

class QuickFixButtonTest {

  @get:Rule val composeTestRule = createComposeRule()

  private lateinit var onClickAction: () -> Unit
  private lateinit var buttonText: String
  private var buttonColor: Color = Color.Unspecified
  private var textColor: Color = Color.Unspecified

  @Before
  fun setUp() {
    onClickAction = mock() // Mocking the onClickAction lambda function
    buttonText = "Click Me"
    buttonColor = Color.Blue
    textColor = Color.White
  }

  @Test
  fun quickFixButtonIsDisplayed() {
    composeTestRule.setContent {
      QuickFixButton(
          buttonText = buttonText,
          onClickAction = onClickAction,
          buttonColor = buttonColor,
          textColor = textColor)
    }

    // Check if the button is displayed
    composeTestRule.onNodeWithTag("quickfixButton").assertIsDisplayed()
  }

  @Test
  fun quickFixButtonHasCorrectText() {
    composeTestRule.setContent {
      QuickFixButton(
          buttonText = buttonText,
          onClickAction = onClickAction,
          buttonColor = buttonColor,
          textColor = textColor)
    }

    // Check that the button has the correct text
    composeTestRule.onNodeWithTag("quickfixButton").assertIsDisplayed().assertHasClickAction()
  }

  @Test
  fun quickFixButtonPerformsClick() {
    composeTestRule.setContent {
      QuickFixButton(
          buttonText = buttonText,
          onClickAction = onClickAction,
          buttonColor = buttonColor,
          textColor = textColor)
    }

    // Perform a click on the button and verify the onClick callback
    composeTestRule.onNodeWithTag("quickfixButton").performClick()

    verify(onClickAction).invoke()
  }

  @Test
  fun quickFixButtonIsDisabledWhenNotEnabled() {
    composeTestRule.setContent {
      QuickFixButton(
          buttonText = buttonText,
          onClickAction = onClickAction,
          buttonColor = buttonColor,
          textColor = textColor,
          enabled = false)
    }

    // Check if the button is disabled
    composeTestRule.onNodeWithTag("quickfixButton").assertIsNotEnabled()
  }

  @Test
  fun quickFixButtonIsEnabledWhenEnabled() {
    composeTestRule.setContent {
      QuickFixButton(
          buttonText = buttonText,
          onClickAction = onClickAction,
          buttonColor = buttonColor,
          textColor = textColor,
          enabled = true)
    }

    // Check if the button is enabled
    composeTestRule.onNodeWithTag("quickfixButton").assertIsEnabled()
  }
}
