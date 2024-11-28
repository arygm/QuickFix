package com.arygm.quickfix.ui.search

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.arygm.quickfix.model.category.Category
import com.arygm.quickfix.model.category.Scale
import com.arygm.quickfix.model.category.Subcategory
import com.arygm.quickfix.model.search.SearchViewModel
import com.arygm.quickfix.ressources.C
import com.arygm.quickfix.ui.navigation.NavigationActions
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock

class SearchCategoryButtonTest {

  @get:Rule val composeTestRule = createComposeRule()
  private lateinit var navigationActions: NavigationActions
  private lateinit var searchViewModel: SearchViewModel

  private val item =
      Category(
          id = "painting",
          name = "Painting",
          description = "Find skilled painters for residential or commercial projects.",
          subcategories =
              listOf(
                  Subcategory(
                      id = "residential_painting",
                      name = "Residential Painting",
                      tags = listOf("Interior Painting", "Exterior Painting", "Cabinet Painting"),
                      scale =
                          Scale(
                              longScale =
                                  "Prices are displayed relative to the cost of painting a 20 m² room.",
                              shortScale = "20 m² room equivalent"),
                      setServices =
                          listOf(
                              "Surface Preparation",
                              "Interior Painting",
                              "Exterior Painting",
                              "Cabinet Painting",
                              "Trim and Baseboard Painting",
                              "Wallpaper Removal",
                              "Deck and Fence Painting",
                              "Popcorn Ceiling Removal",
                              "Pressure Washing",
                              "Garage Floor Painting",
                              "Sealing and Caulking",
                              "Color Consultation",
                              "Minor Repairs",
                              "Clean-Up")),
                  Subcategory(
                      id = "commercial_painting",
                      name = "Commercial Painting",
                      tags = listOf("Office Buildings", "Retail Spaces"),
                      scale =
                          Scale(
                              longScale =
                                  "Prices are displayed relative to the cost of painting a 100 m² commercial space.",
                              shortScale = "100 m² commercial space equivalent"),
                      setServices =
                          listOf(
                              "Surface Preparation",
                              "Interior Commercial Painting",
                              "Exterior Commercial Painting",
                              "Specialty Coatings",
                              "Epoxy Floor Coatings",
                              "Line Striping and Markings",
                              "Power Washing",
                              "Graffiti Removal",
                              "Metal Structure Painting",
                              "Parking Lot Painting",
                              "Safety Painting",
                              "Color Branding",
                              "Clean-Up")),
                  Subcategory(
                      id = "decorative_painting",
                      name = "Decorative Painting",
                      tags = listOf("Faux Finishes", "Murals"),
                      scale =
                          Scale(
                              longScale =
                                  "Prices are displayed relative to the cost of painting a 20 m² room.",
                              shortScale = "20 m² room equivalent"),
                      setServices =
                          listOf(
                              "Decorative Painting",
                              "Faux Finishes",
                              "Murals",
                              "Accent Walls",
                              "Textured Painting",
                              "Stenciling",
                              "Color Washing",
                              "Rag Rolling",
                              "Sponging",
                              "Venetian Plaster",
                              "Glazing",
                              "Metallic Finishes",
                              "Surface Preparation",
                              "Color Consultation",
                              "Clean-Up"))))

  @Test
  fun searchCategoryButton_displaysTitleAndDescription() {
    val expandedState = mutableStateOf(false)
    navigationActions = mock(NavigationActions::class.java)
    searchViewModel = mock(SearchViewModel::class.java)
    composeTestRule.setContent {
      ExpandableCategoryItem(
          item = item,
          isExpanded = expandedState.value,
          onExpandedChange = { expandedState.value = it },
          navigationActions = navigationActions,
          searchViewModel = searchViewModel)
    }

    // Check if title and description texts are displayed
    composeTestRule.onNodeWithText(item.name).assertIsDisplayed()
    composeTestRule.onNodeWithText(item.description).assertIsDisplayed()
  }

  @Test
  fun searchCategoryButton_clickAction() {
    // Create a variable to track if the button was clicked
    val expandedState = mutableStateOf(false)
    navigationActions = mock(NavigationActions::class.java)
    searchViewModel = mock(SearchViewModel::class.java)
    composeTestRule.setContent {
      ExpandableCategoryItem(
          item = item,
          isExpanded = expandedState.value,
          onExpandedChange = { expandedState.value = it },
          navigationActions = navigationActions,
          searchViewModel = searchViewModel)
    }

    // Perform a click on the button
    composeTestRule.onNodeWithTag(C.Tag.expandableCategoryItem).performClick()

    // Assert that the click action was triggered
    assert(expandedState.value)
  }

  @Test
  fun testExpandableCategoryItem_AnimatedVisibility() {
    // Step 1: Control the clock to handle animations
    composeTestRule.mainClock.autoAdvance = false

    // Step 2: Set up the initial state
    val isExpandedState = mutableStateOf(false)
    navigationActions = mock(NavigationActions::class.java)
    searchViewModel = SearchViewModel(mock(), mock())
    composeTestRule.setContent {
      // Provide LocalInspectionMode if you prefer to disable animations
      // CompositionLocalProvider(LocalInspectionMode provides true) {
      ExpandableCategoryItem(
          item = item,
          isExpanded = isExpandedState.value,
          onExpandedChange = { isExpandedState.value = it },
          navigationActions = navigationActions,
          searchViewModel = searchViewModel)
      // }
    }

    // Step 3: Perform click to expand the item
    composeTestRule.onNodeWithTag(C.Tag.expandableCategoryItem).performClick()

    // Step 4: Advance the clock to allow animations to complete
    composeTestRule.mainClock.advanceTimeBy(1000) // Adjust based on your animation duration
    composeTestRule.waitForIdle()
    // Optionally, check for a specific subcategory
    val subCategoryName = "Residential Painting"
    composeTestRule
        .onNodeWithTag("${C.Tag.subCategoryName}_$subCategoryName")
        .assertIsDisplayed()
        .assertHasClickAction()

    // Advance the clock again for the collapse animation
    composeTestRule.mainClock.advanceTimeBy(1000)
    composeTestRule.waitForIdle()

    // Step 7: Assert that subcategories are no longer displayed
    composeTestRule.onNodeWithTag(C.Tag.subCategories).assertDoesNotExist()

    // Step 8: Restore the clock to auto-advance
    composeTestRule.mainClock.autoAdvance = true
  }
}
