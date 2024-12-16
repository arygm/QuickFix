package com.arygm.quickfix.ui.search

import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.printToLog
import androidx.compose.ui.text.AnnotatedString
import com.arygm.quickfix.model.account.AccountRepositoryFirestore
import com.arygm.quickfix.model.account.AccountViewModel
import com.arygm.quickfix.model.category.CategoryRepositoryFirestore
import com.arygm.quickfix.model.category.CategoryViewModel
import com.arygm.quickfix.model.profile.WorkerProfileRepositoryFirestore
import com.arygm.quickfix.model.quickfix.QuickFixViewModel
import com.arygm.quickfix.model.search.SearchViewModel
import com.arygm.quickfix.ressources.C
import com.arygm.quickfix.ui.navigation.NavigationActions
import com.arygm.quickfix.ui.uiMode.appContentUI.userModeUI.search.SearchOnBoarding
import io.mockk.mockk
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock

class SearchOnBoardingTest {

  private lateinit var navigationActions: NavigationActions
  private lateinit var workerProfileRepo: WorkerProfileRepositoryFirestore
  private lateinit var accountRepositoryFirestore: AccountRepositoryFirestore
  private lateinit var categoryRepo: CategoryRepositoryFirestore
  private lateinit var searchViewModel: SearchViewModel
  private lateinit var accountViewModel: AccountViewModel
  private lateinit var categoryViewModel: CategoryViewModel
  private lateinit var navigationActionsRoot: NavigationActions
  private lateinit var quickFixViewModel: QuickFixViewModel

  @get:Rule val composeTestRule = createComposeRule()

  @Before
  fun setup() {
    navigationActions = mock(NavigationActions::class.java)
    navigationActionsRoot = mock(NavigationActions::class.java)
    workerProfileRepo = mockk(relaxed = true)
    categoryRepo = mockk(relaxed = true)
    accountRepositoryFirestore = mock(AccountRepositoryFirestore::class.java)
    searchViewModel = SearchViewModel(workerProfileRepo)
    categoryViewModel = CategoryViewModel(categoryRepo)
    accountViewModel = mockk(relaxed = true)
    quickFixViewModel = QuickFixViewModel(mock())
  }

  @Test
  fun searchOnBoarding_displaysSearchInput() {
    composeTestRule.setContent {
      SearchOnBoarding(
          navigationActions = navigationActions,
          navigationActionsRoot,
          searchViewModel,
          accountViewModel,
          categoryViewModel,
          quickFixViewModel)
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
          searchViewModel,
          accountViewModel,
          categoryViewModel,
          quickFixViewModel)
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

  @Test
  fun searchOnBoarding_switchesFromCategoriesToProfiles() {
    composeTestRule.setContent {
      SearchOnBoarding(
          navigationActions = navigationActions,
          navigationActionsRoot = navigationActionsRoot,
          searchViewModel = searchViewModel,
          accountViewModel = accountViewModel,
          categoryViewModel = categoryViewModel,
          quickFixViewModel = quickFixViewModel)
    }

    // Verify initial state (Categories are displayed)
    composeTestRule.onNodeWithText("Categories").assertIsDisplayed()
    composeTestRule.onNodeWithTag("searchContent").performTextInput("Painter")

    // Verify state after query input (Categories disappear, Profiles appear)
    composeTestRule.onNodeWithText("Categories").assertDoesNotExist()
  }
}
