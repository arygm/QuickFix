package com.arygm.quickfix.ui.elements

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

class QuickFixBackButtonTopBarLoginTest {

  @get:Rule val composeTestRule = createComposeRule()

  private lateinit var onBackClick: () -> Unit

  @Before
  fun setUp() {
    onBackClick = mock() // Mocking the onBackClick lambda function
  }

  @Test
  fun quickFixBackButtonTopBarLoginIsDisplayedWithDefaults() {
    composeTestRule.setContent { QuickFixBackButtonTopBarLogin(onBackClick = onBackClick) }

    // Check that the top bar, back button, and image are displayed
    composeTestRule.onNodeWithTag("goBackTopBar").assertIsDisplayed()
    composeTestRule.onNodeWithTag("goBackButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("topBarLoginBackground").assertIsDisplayed()
  }

  @Test
  fun quickFixBackButtonTopBarLoginIsDisplayedWithCustomValues() {
    val customTitle = "Custom Title"
    val customColor = Color.Red

    composeTestRule.setContent {
      QuickFixBackButtonTopBarLogin(
          onBackClick = onBackClick, title = customTitle, color = customColor, modifier = Modifier)
    }

    // Check that the top bar, back button, and image are displayed
    composeTestRule.onNodeWithTag("goBackTopBar").assertIsDisplayed()
    composeTestRule.onNodeWithTag("goBackButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("topBarLoginBackground").assertIsDisplayed()
  }

  @Test
  fun quickFixBackButtonTopBarLoginBackButtonPerformsClick() {
    composeTestRule.setContent { QuickFixBackButtonTopBarLogin(onBackClick = onBackClick) }

    // Perform a click on the back button and verify the onClick callback
    composeTestRule.onNodeWithTag("goBackButton").performClick()

    verify(onBackClick).invoke()
  }
}
