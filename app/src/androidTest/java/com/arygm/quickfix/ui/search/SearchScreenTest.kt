package com.arygm.quickfix.ui.search

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import com.arygm.quickfix.ui.navigation.NavigationActions
import com.arygm.quickfix.ui.navigation.Screen
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

class SearchScreenTest {
  @get:Rule val composeTestRule = createComposeRule()

  private lateinit var navigationActions: NavigationActions

  @Before
  fun setup() {
    navigationActions = mock(NavigationActions::class.java)

    `when`(navigationActions.currentRoute()).thenReturn(Screen.SEARCH)
  }

  @Test
  fun searchScreenUserDisplaysCorrectly() {
    composeTestRule.setContent { SearchScreen(navigationActions, true) }

    composeTestRule.onNodeWithTag("SearchContent").assertIsDisplayed()
    composeTestRule.onNodeWithTag("SearchText").assertIsDisplayed()
    composeTestRule.onNodeWithTag("SearchText").assertTextContains("Welcome to the SEARCH Screen")
  }

  // Can Remove this tbh
  @Test
  fun searchScreenWorkerDisplaysCorrectly() {
    composeTestRule.setContent { SearchScreen(navigationActions, false) }

    composeTestRule.onNodeWithTag("SearchContent").assertIsDisplayed()
    composeTestRule.onNodeWithTag("SearchText").assertIsDisplayed()
    composeTestRule.onNodeWithTag("SearchText").assertTextContains("Welcome to the SEARCH Screen")
  }
}
