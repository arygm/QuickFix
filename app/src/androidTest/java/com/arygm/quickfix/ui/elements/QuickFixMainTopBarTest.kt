package com.arygm.quickfix.ui.elements

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class QuickFixMainTopBarTest {

  @get:Rule val composeTestRule = createComposeRule()

  private lateinit var title: String

  @Before
  fun setUp() {
    title = "QuickFix"
  }

  @Test
  fun quickFixMainTopBarIsDisplayed() {
    composeTestRule.setContent { QuickFixMainTopBar(title = title) }

    // Check if the TopAppBar is displayed
    composeTestRule.onNodeWithTag("topBarSurface").assertIsDisplayed()

    composeTestRule.onNodeWithTag("topBar").assertIsDisplayed()

    // Check if the title is displayed
    composeTestRule.onNodeWithTag("topBarTitle").assertIsDisplayed().assertTextEquals(title)
  }
}
