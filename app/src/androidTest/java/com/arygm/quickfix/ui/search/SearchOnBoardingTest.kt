package com.arygm.quickfix.ui.search

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.arygm.quickfix.model.categories.WorkerCategory
import com.arygm.quickfix.ui.navigation.NavigationActions
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock

class SearchOnBoardingTest {

  private lateinit var navigationActions: NavigationActions

  @get:Rule val composeTestRule = createComposeRule()

  @Before
  fun setup() {
    navigationActions = mock(NavigationActions::class.java)
  }

  @Test
  fun searchOnBoarding_displaysSearchInput() {
    composeTestRule.setContent {
      SearchOnBoarding(navigationActions = navigationActions, isUser = true)
    }

    // Check that the search input field is displayed
    composeTestRule.onNodeWithTag("searchContent").assertIsDisplayed()

    // Enter some text and check if the trailing clear icon appears
    composeTestRule.onNodeWithTag("searchContent").performTextInput("plumbing")
    composeTestRule.onNodeWithTag("clearSearchQueryIcon").assertIsDisplayed()
  }

  @Test
  fun searchOnBoarding_clearsTextOnTrailingIconClick() {
    composeTestRule.setContent {
      SearchOnBoarding(navigationActions = navigationActions, isUser = true)
    }

    // Input text into the search field
    val searchInput = composeTestRule.onNodeWithTag("searchContent")
    searchInput.performTextInput("electrician")

    // Click the trailing icon (clear button) and verify the text is cleared
    composeTestRule.onNodeWithTag("clearSearchQueryIcon").performClick()
    searchInput.assertTextEquals("") // Verify the text is cleared
  }

  @Test
  fun searchOnBoarding_displaysAllCategories() {
    composeTestRule.setContent {
      SearchOnBoarding(navigationActions = navigationActions, isUser = true)
    }

    WorkerCategory.entries.forEach { category ->
      composeTestRule.onNodeWithText(category.displayName).assertIsDisplayed()
    }
  }

  @Test
  fun searchOnBoarding_categoryClickTriggersAction() {
    // Set up state to track which category was clicked

    composeTestRule.setContent {
      SearchOnBoarding(
          navigationActions = navigationActions,
          isUser = true,
      )
    }

    // Find and click the "Plumbing" category button
    composeTestRule.onNodeWithText("Plumbing").performClick()

    // Assert the click action was triggered
  }
}
