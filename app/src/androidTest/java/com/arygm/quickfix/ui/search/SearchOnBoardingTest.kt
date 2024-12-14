package com.arygm.quickfix.ui.search

import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.filter
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasParent
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToIndex
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTextReplacement
import androidx.compose.ui.test.printToLog
import androidx.compose.ui.text.AnnotatedString
import com.arygm.quickfix.model.account.AccountViewModel
import com.arygm.quickfix.model.category.CategoryRepositoryFirestore
import com.arygm.quickfix.model.category.CategoryViewModel
import com.arygm.quickfix.model.category.Subcategory
import com.arygm.quickfix.model.locations.Location
import com.arygm.quickfix.model.profile.WorkerProfile
import com.arygm.quickfix.model.profile.WorkerProfileRepositoryFirestore
import com.arygm.quickfix.model.search.SearchViewModel
import com.arygm.quickfix.ressources.C
import com.arygm.quickfix.ui.navigation.NavigationActions
import com.arygm.quickfix.ui.uiMode.appContentUI.userModeUI.search.SearchOnBoarding
import com.arygm.quickfix.ui.userModeUI.navigation.UserTopLevelDestinations
import io.mockk.mockk
import java.time.LocalDate
import java.time.LocalTime
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

class SearchOnBoardingTest {

  private lateinit var navigationActions: NavigationActions
  private lateinit var workerProfileRepo: WorkerProfileRepositoryFirestore
  private lateinit var categoryRepo: CategoryRepositoryFirestore
  private lateinit var searchViewModel: SearchViewModel
  private lateinit var accountViewModel: AccountViewModel
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
    accountViewModel = mockk(relaxed = true)
  }

  @Test
  fun searchOnBoarding_displaysSearchInput() {
    composeTestRule.setContent {
      SearchOnBoarding(
          onSearch = {},
          onSearchEmpty = {},
          navigationActions,
          navigationActionsRoot,
          searchViewModel,
          accountViewModel,
          categoryViewModel,
          onProfileClick = { _ -> },
      )
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
          onSearch = {},
          onSearchEmpty = {},
          navigationActions,
          navigationActionsRoot,
          searchViewModel,
          accountViewModel,
          categoryViewModel,
          onProfileClick = { _ -> },
      )
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
          onSearch = {},
          onSearchEmpty = {},
          navigationActions,
          navigationActionsRoot,
          searchViewModel,
          accountViewModel,
          categoryViewModel,
          onProfileClick = { _ -> },
      )
    }

    // Verify initial state (Categories are displayed)
    composeTestRule.onNodeWithText("Categories").assertIsDisplayed()
    composeTestRule.onNodeWithTag("searchContent").performTextInput("Painter")

    // Verify state after query input (Categories disappear, Profiles appear)
    composeTestRule.onNodeWithText("Categories").assertDoesNotExist()
  }

  @Test
  fun searchOnBoarding_showsFilterButtonsWhenQueryIsNotEmpty() {
    composeTestRule.setContent {
      SearchOnBoarding(
          onSearch = {},
          onSearchEmpty = {},
          navigationActions,
          navigationActionsRoot,
          searchViewModel,
          accountViewModel,
          categoryViewModel,
          onProfileClick = { _ -> },
      )
    }

    // Input text to simulate non-empty search
    composeTestRule.onNodeWithTag("searchContent").performTextInput("Painter")

    composeTestRule.onNodeWithTag("tuneButton").performClick()

    // Verify that the filter row becomes visible
    composeTestRule.onNodeWithTag("filter_buttons_row").assertIsDisplayed()
  }

  @Test
  fun testOpenAvailabilityFilterMenu() {
    // Set up test worker profiles
    val worker1 =
        WorkerProfile(
            uid = "worker1",
            fieldOfWork = "Painter",
            rating = 4.5,
            workingHours = Pair(LocalTime.of(9, 0), LocalTime.of(17, 0)),
            unavailability_list = listOf(LocalDate.now()),
            location = Location(40.7128, -74.0060))

    val worker2 =
        WorkerProfile(
            uid = "worker2",
            fieldOfWork = "Electrician",
            rating = 4.0,
            workingHours = Pair(LocalTime.of(8, 0), LocalTime.of(16, 0)),
            unavailability_list = emptyList(),
            location = Location(40.7128, -74.0060))

    // Update the searchViewModel with test workers
    searchViewModel._subCategoryWorkerProfiles.value = listOf(worker1, worker2)

    // Set the composable content
    composeTestRule.setContent {
      SearchOnBoarding(
          onSearch = {},
          onSearchEmpty = {},
          navigationActions = navigationActions,
          navigationActionsRoot = navigationActionsRoot,
          searchViewModel = searchViewModel,
          accountViewModel = accountViewModel,
          categoryViewModel = categoryViewModel,
          onProfileClick = { _ -> })
    }

    // Wait for the UI to settle
    composeTestRule.waitForIdle()

    // Perform a search query to display filter buttons
    composeTestRule.onNodeWithTag("searchContent").performTextInput("Painter")
    composeTestRule.onNodeWithTag("tuneButton").performClick()
    composeTestRule.onNodeWithTag("filter_buttons_row").assertIsDisplayed().performScrollToIndex(3)

    // Scroll to the "Availability" filter button

    // Click the "Availability" filter button
    composeTestRule.onNodeWithText("Availability").performClick()

    // Wait for the availability bottom sheet to appear
    composeTestRule.waitForIdle()

    // Verify the bottom sheet and its components are displayed
    composeTestRule.onNodeWithTag("availabilityBottomSheet").assertIsDisplayed()
    composeTestRule.onNodeWithTag("timePickerColumn").assertIsDisplayed()
    composeTestRule.onNodeWithTag("timeInput").assertIsDisplayed()
    composeTestRule.onNodeWithTag("bottomSheetColumn").assertIsDisplayed()
    composeTestRule.onNodeWithText("Enter time").assertIsDisplayed()

    val today = LocalDate.now()
    val day = today.dayOfMonth

    val textFields =
        composeTestRule.onAllNodes(hasSetTextAction()).filter(hasParent(hasTestTag("timeInput")))

    // Ensure that we have at least two text fields
    assert(textFields.fetchSemanticsNodes().size >= 2)

    // Set the hour to "07"
    textFields[0].performTextReplacement("08")

    // Set the minute to "00"
    textFields[1].performTextReplacement("00")

    // Find the node representing today's date and perform a click
    composeTestRule
        .onNode(hasText(day.toString()) and hasClickAction() and !hasSetTextAction())
        .performClick()

    composeTestRule.onNodeWithText("OK").performClick()

    composeTestRule.waitForIdle()

    // Verify that no workers are displayed
  }

  @Test
  fun testOpenPriceRangeFilterMenu() {
    // Set up test worker profiles
    val worker1 =
        WorkerProfile(
            uid = "worker1",
            fieldOfWork = "Painter",
            rating = 4.5,
            workingHours = Pair(LocalTime.of(9, 0), LocalTime.of(17, 0)),
            location = Location(40.7128, -74.0060))

    searchViewModel._subCategoryWorkerProfiles.value = listOf(worker1)

    // Set the composable content
    composeTestRule.setContent {
      SearchOnBoarding(
          onSearch = {},
          onSearchEmpty = {},
          navigationActions = navigationActions,
          navigationActionsRoot = navigationActionsRoot,
          searchViewModel = searchViewModel,
          accountViewModel = accountViewModel,
          categoryViewModel = categoryViewModel,
          onProfileClick = { _ -> })
    }

    // Perform a search query to display filter buttons
    composeTestRule.onNodeWithTag("searchContent").performTextInput("Painter")
    composeTestRule.onNodeWithTag("tuneButton").performClick()
    composeTestRule.onNodeWithTag("filter_buttons_row").assertIsDisplayed().performScrollToIndex(5)

    // Click on "Price Range" filter button
    composeTestRule.onNodeWithText("Price Range").performClick()

    // Verify the bottom sheet appears
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("priceRangeModalSheet").assertIsDisplayed()
  }

  @Test
  fun testOpenLocationFilterMenu() {
    // Set up test worker profiles
    val worker1 =
        WorkerProfile(
            uid = "worker1",
            fieldOfWork = "Painter",
            rating = 4.5,
            workingHours = Pair(LocalTime.of(9, 0), LocalTime.of(17, 0)),
            location = Location(40.7128, -74.0060))

    searchViewModel._subCategoryWorkerProfiles.value = listOf(worker1)

    // Set the composable content
    composeTestRule.setContent {
      SearchOnBoarding(
          onSearch = {},
          onSearchEmpty = {},
          navigationActions = navigationActions,
          navigationActionsRoot = navigationActionsRoot,
          searchViewModel = searchViewModel,
          accountViewModel = accountViewModel,
          categoryViewModel = categoryViewModel,
          onProfileClick = { _ -> })
    }

    // Perform a search query to display filter buttons
    composeTestRule.onNodeWithTag("searchContent").performTextInput("Painter")
    composeTestRule.onNodeWithTag("tuneButton").performClick()
    composeTestRule.onNodeWithTag("filter_buttons_row").assertIsDisplayed()

    // Click on "Location" filter button
    composeTestRule.onNodeWithText("Location").performClick()

    // Verify the bottom sheet appears
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("locationFilterModalSheet").assertIsDisplayed()
  }

  @Test
  fun searchOnBoarding_cancelButtonNavigatesHome() {
    composeTestRule.setContent {
      SearchOnBoarding(
          onSearch = {},
          onSearchEmpty = {},
          navigationActions,
          navigationActionsRoot,
          searchViewModel,
          accountViewModel,
          categoryViewModel,
          onProfileClick = { _ -> },
      )
    }

    // Click the Cancel button
    composeTestRule.onNodeWithText("Cancel").performClick()

    // Verify navigation to HOME was requested
    verify(navigationActionsRoot).navigateTo(UserTopLevelDestinations.HOME)
  }

  @Test
  fun searchOnBoarding_servicesFilterApplyAndClear() {
    // Set up some mock services in searchViewModel
    searchViewModel._searchSubcategory.value =
        Subcategory(name = "TestSubCategory", tags = listOf("Service1", "Service2"))

    composeTestRule.setContent {
      SearchOnBoarding(
          onSearch = {},
          onSearchEmpty = {},
          navigationActions,
          navigationActionsRoot,
          searchViewModel,
          accountViewModel,
          categoryViewModel,
          onProfileClick = { _ -> },
      )
    }

    // Perform a search to show filters
    composeTestRule.onNodeWithTag("searchContent").performTextInput("Test")
    composeTestRule.onNodeWithTag("tuneButton").performClick()

    composeTestRule.onNodeWithTag("filter_buttons_row").performScrollToIndex(1)
    // Open services bottom sheet
    composeTestRule.onNodeWithText("Service Type").performClick()

    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("chooseServiceTypeModalSheet").assertIsDisplayed()

    // Simulate selecting "Interior Painter"
    composeTestRule.onNodeWithText("Apply").performClick()
  }

  @Test
  fun searchOnBoarding_priceRangeFilterApplyAndClear() {
    composeTestRule.setContent {
      SearchOnBoarding(
          onSearch = {},
          onSearchEmpty = {},
          navigationActions,
          navigationActionsRoot,
          searchViewModel,
          accountViewModel,
          categoryViewModel,
          onProfileClick = { _ -> },
      )
    }

    // Perform a search to show filters
    composeTestRule.onNodeWithTag("searchContent").performTextInput("Tester")
    composeTestRule.onNodeWithTag("tuneButton").performClick()
    composeTestRule.onNodeWithTag("filter_buttons_row").performScrollToIndex(4)
    composeTestRule.onNodeWithText("Price Range").performClick()

    // Input a price range and apply
    composeTestRule.onNodeWithText("Apply").performClick()

    // Open price range again and clear
    composeTestRule.onNodeWithText("Price Range").performClick()
    composeTestRule.onNodeWithText("Clear").performClick()
  }
}
