package com.arygm.quickfix.ui.profile.becomeWorker.views.professional

import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.arygm.quickfix.model.category.Category
import com.arygm.quickfix.model.category.Subcategory
import com.arygm.quickfix.model.profile.dataFields.AddOnService
import com.arygm.quickfix.model.profile.dataFields.IncludedService
import com.arygm.quickfix.ressources.C
import com.arygm.quickfix.ressources.C.Tag.professionalInfoScreenCategoryDropdownMenu
import com.arygm.quickfix.ressources.C.Tag.professionalInfoScreenCategoryField
import com.arygm.quickfix.ressources.C.Tag.professionalInfoScreenSubcategoryDropdownMenu
import com.arygm.quickfix.ressources.C.Tag.professionalInfoScreenSubcategoryField
import com.arygm.quickfix.ui.theme.QuickFixTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ProfessionalInfoScreenTest {

  @get:Rule val composeTestRule = createComposeRule()

  // Mock data for testing
  private val mockSubcategoriesPlumbing =
      listOf(Subcategory(name = "Faucet Repair"), Subcategory(name = "Pipe Installation"))

  private val mockSubcategoriesElectrical =
      listOf(Subcategory(name = "Circuit Installation"), Subcategory(name = "Light Fixture"))

  private val mockCategories =
      listOf(
          Category(id = "1", name = "Plumbing", subcategories = mockSubcategoriesPlumbing),
          Category(id = "2", name = "Electrical", subcategories = mockSubcategoriesElectrical),
          Category(id = "3", name = "Carpentry", subcategories = emptyList()))

  private fun setUpProfessionalInfoScreen(
      price: MutableState<String> = mutableStateOf(""),
      fieldOfWork: MutableState<String> = mutableStateOf(""),
      includedServices: MutableState<List<IncludedService>> = mutableStateOf(emptyList()),
      addOnServices: MutableState<List<AddOnService>> = mutableStateOf(emptyList()),
      tags: MutableState<List<String>> = mutableStateOf(emptyList()),
      categories: List<Category> = mockCategories,
      initialPage: Int = 0,
      pagerStateHolder: (PagerState) -> Unit = {}
  ) {
    composeTestRule.setContent {
      val pagerState = rememberPagerState(pageCount = { 3 }, initialPage = initialPage)
      pagerStateHolder(pagerState)
      QuickFixTheme {
        ProfessionalInfoScreen(
            pagerState = pagerState,
            price = price,
            fieldOfWork = fieldOfWork,
            includedServices = includedServices,
            addOnServices = addOnServices,
            tags = tags,
            categories = categories)
      }
    }
  }

  @Test
  fun professionalInfoScreen_displaysTitleAndDescription() {
    setUpProfessionalInfoScreen()

    // Verify that the title is displayed
    composeTestRule
        .onNodeWithTag(C.Tag.professionalInfoScreenSectionTitle)
        .assertIsDisplayed()
        .assertTextEquals("Professional Info")

    // Verify that the description is displayed
    composeTestRule
        .onNodeWithTag(C.Tag.professionalInfoScreenSectionDescription)
        .assertIsDisplayed()
        .assertTextContains(
            "This is your time to shine. Let potential buyers know what you do best and how you gained your skills, certifications and experience.")
  }

  @Test
  fun professionalInfoScreen_categoryDropdownDisplaysOptions() {
    setUpProfessionalInfoScreen()

    // Open the category dropdown
    composeTestRule.onNodeWithTag(professionalInfoScreenCategoryField).performClick()

    // Verify that the dropdown menu is displayed
    composeTestRule.onNodeWithTag(professionalInfoScreenCategoryDropdownMenu).assertIsDisplayed()

    // Verify that all categories are displayed
    mockCategories.forEach { category ->
      composeTestRule.onNode(hasText(category.name) and hasClickAction()).assertIsDisplayed()
    }
  }

  @Test
  fun professionalInfoScreen_subcategoryDropdownDisplaysOptions() {
    setUpProfessionalInfoScreen()

    // Select a category first
    composeTestRule.onNodeWithTag(professionalInfoScreenCategoryField).performClick()
    composeTestRule.onNodeWithText("Electrical").performClick()

    // Open the subcategory dropdown
    composeTestRule.onNodeWithTag(professionalInfoScreenSubcategoryField).performClick()

    // Verify that the dropdown menu is displayed
    composeTestRule.onNodeWithTag(professionalInfoScreenSubcategoryDropdownMenu).assertIsDisplayed()

    // Verify that the subcategories are displayed
    mockSubcategoriesElectrical.forEach { subcategory ->
      composeTestRule.onNode(hasText(subcategory.name) and hasClickAction()).assertIsDisplayed()
    }
  }

  @Test
  fun professionalInfoScreen_selectSubcategoryUpdatesField() {
    setUpProfessionalInfoScreen()

    // Select a category
    composeTestRule.onNodeWithTag(professionalInfoScreenCategoryField).performClick()
    composeTestRule.onNodeWithText("Plumbing").performClick()

    // Open the subcategory dropdown
    composeTestRule.onNodeWithTag(professionalInfoScreenSubcategoryField).performClick()

    // Select a subcategory
    composeTestRule.onNodeWithText("Faucet Repair").performClick()

    // Verify that the selected subcategory is displayed
    composeTestRule
        .onNodeWithTag(professionalInfoScreenSubcategoryField)
        .assertTextContains("Faucet Repair")
  }

  @Test
  fun professionalInfoScreen_subcategoryFieldDisabledUntilCategorySelected() {
    setUpProfessionalInfoScreen()

    // Verify that the subcategory field is disabled
    composeTestRule.onNodeWithTag(professionalInfoScreenSubcategoryField).assertIsNotEnabled()

    // Try to click on the subcategory field
    composeTestRule.onNodeWithTag(professionalInfoScreenSubcategoryField).performClick()

    // Verify that the dropdown menu is not displayed
    composeTestRule
        .onNodeWithTag(professionalInfoScreenSubcategoryDropdownMenu)
        .assertDoesNotExist()
  }

  @Test
  fun professionalInfoScreen_selectingNewCategoryResetsSubcategory() {
    setUpProfessionalInfoScreen()

    // Select the first category and subcategory
    composeTestRule.onNodeWithTag(professionalInfoScreenCategoryField).performClick()
    composeTestRule.onNodeWithText("Plumbing").performClick()
    composeTestRule.onNodeWithTag(professionalInfoScreenSubcategoryField).performClick()
    composeTestRule.onNodeWithText("Faucet Repair").performClick()

    // Verify that the selected subcategory is displayed
    composeTestRule
        .onNodeWithTag(professionalInfoScreenSubcategoryField)
        .assertTextContains("Faucet Repair")

    // Select a new category
    composeTestRule.onNodeWithTag(professionalInfoScreenCategoryField).performClick()
    composeTestRule.onNodeWithText("Electrical").performClick()

    // Verify that subcategory field is reset
    composeTestRule
        .onNodeWithTag(professionalInfoScreenSubcategoryField)
        .assertTextEquals("") // Assuming it resets to empty string

    // Subcategory field should now display subcategories for "Electrical"
    composeTestRule.onNodeWithTag(professionalInfoScreenSubcategoryField).performClick()
    composeTestRule.onNodeWithTag(professionalInfoScreenSubcategoryDropdownMenu).assertIsDisplayed()

    mockSubcategoriesElectrical.forEach { subcategory ->
      composeTestRule.onNode(hasText(subcategory.name) and hasClickAction()).assertIsDisplayed()
    }
  }
}
