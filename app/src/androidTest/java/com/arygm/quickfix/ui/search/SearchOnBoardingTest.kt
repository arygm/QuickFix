package com.arygm.quickfix.ui.search

import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.*
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.text.AnnotatedString
import com.arygm.quickfix.model.category.CategoryRepositoryFirestore
import com.arygm.quickfix.model.category.CategoryViewModel
import com.arygm.quickfix.model.profile.WorkerProfileRepositoryFirestore
import com.arygm.quickfix.model.search.SearchViewModel
import com.arygm.quickfix.ressources.C
import com.arygm.quickfix.ui.navigation.NavigationActions
import io.mockk.mockk
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock

class SearchOnBoardingTest {

  private lateinit var navigationActions: NavigationActions
  private lateinit var workerProfileRepo: WorkerProfileRepositoryFirestore
  private lateinit var categoryRepo: CategoryRepositoryFirestore
  private lateinit var searchViewModel: SearchViewModel
  private lateinit var categoryViewModel: CategoryViewModel
  private lateinit var navigationActionsRoot: NavigationActions

  @get:Rule val composeTestRule = createComposeRule()

  @Before
  fun setup() {
    navigationActions = mock(NavigationActions::class.java)
    navigationActionsRoot = mock(NavigationActions::class.java)
    workerProfileRepo = mockk(relaxed = true)
    categoryRepo = mockk(relaxed = true)
    searchViewModel = SearchViewModel(workerProfileRepo)
    categoryViewModel = CategoryViewModel(categoryRepo)
  }

  @Test
  fun searchOnBoarding_displaysSearchInput() {
    composeTestRule.setContent {
      SearchOnBoarding(
          navigationActions = navigationActions,
          navigationActionsRoot,
          isUser = true,
          searchViewModel,
          categoryViewModel)
    }

    // Check that the search input field is displayed
    composeTestRule.onNodeWithTag("searchContent").assertIsDisplayed()

    // Enter some text and check if the trailing clear icon appears
    composeTestRule.onNodeWithTag("searchContent").performTextInput("plumbing")
    composeTestRule.onNodeWithTag(C.Tag.clear_button_text_field_custom).assertIsDisplayed()
  }

  @Test
  fun searchOnBoarding_clearsTextOnTrailingIconClick() {
    composeTestRule.setContent {
      SearchOnBoarding(
          navigationActions = navigationActions,
          navigationActionsRoot,
          isUser = true,
          searchViewModel,
          categoryViewModel)
    }

    // Input text into the search field
    val searchInput = composeTestRule.onNodeWithTag("searchContent")
    searchInput.performTextInput("electrician")
    searchInput.assertTextEquals("electrician") // Verify text input

    // Click the trailing icon (clear button)
    composeTestRule.onNodeWithTag(C.Tag.clear_button_text_field_custom).performClick()

    // Wait for UI to settle
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag(C.Tag.clear_button_text_field_custom).assertDoesNotExist()
    searchInput.printToLog("searchInput")
    // Verify the text is cleared
    searchInput.assert(
        SemanticsMatcher.expectValue(SemanticsProperties.EditableText, AnnotatedString("")))
  }
}
