package com.arygm.quickfix.ui.elements

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

class QuickFixBackButtonTopBarTest {

  @get:Rule val composeTestRule = createComposeRule()

  private lateinit var onBackClick: () -> Unit
  private lateinit var title: String

  @Before
  fun setUp() {
    onBackClick = mock() // Mocking the onBackClick lambda function
    title = "Test Title"
  }

  @Test
  fun quickFixBackButtonTopBarIsDisplayed() {
    composeTestRule.setContent {
      QuickFixBackButtonTopBar(onBackClick = onBackClick, title = title)
    }

    // Check that the top bar and back button are displayed
    composeTestRule.onNodeWithTag("goBackTopBar").assertIsDisplayed()

    composeTestRule.onNodeWithTag("goBackButton").assertIsDisplayed()
  }

  @Test
  fun quickFixBackButtonTopBarBackButtonPerformsClick() {
    composeTestRule.setContent {
      QuickFixBackButtonTopBar(onBackClick = onBackClick, title = title)
    }

    // Perform a click on the back button and verify the onClick callback
    composeTestRule.onNodeWithTag("goBackButton").performClick()

    verify(onBackClick).invoke()
  }
}
