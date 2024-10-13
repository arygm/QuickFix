package com.arygm.quickfix.ui.elements

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

class QuickFixBackButtonTest {

  @get:Rule val composeTestRule = createComposeRule()

  private lateinit var onClick: () -> Unit
  private var color: Color = Color.Unspecified

  @Before
  fun setUp() {
    onClick = mock() // Mocking the onClick function
    color = Color.Black
  }

  @Test
  fun quickFixBackButtonIsDisplayed() {
    composeTestRule.setContent { QuickFixBackButton(onClick = onClick, color = color) }

    composeTestRule.onNodeWithTag("goBackButton").assertIsDisplayed()
  }

  @Test
  fun quickFixBackButtonPerformsClick() {
    composeTestRule.setContent { QuickFixBackButton(onClick = onClick, color = color) }

    composeTestRule.onNodeWithTag("goBackButton").performClick()

    verify(onClick).invoke() // Verify that the onClick function is called
  }
}
