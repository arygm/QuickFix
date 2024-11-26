package com.arygm.quickfix.ui.search

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.arygm.quickfix.ui.navigation.NavigationActions
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.times

class AnnouncementScreenTest {

  @get:Rule val composeTestRule = createComposeRule()

  private lateinit var navigationActions: NavigationActions

  @Before
  fun setup() {
    navigationActions = mock(NavigationActions::class.java)
  }

  @Test
  fun announcementScreenDisplaysCorrectly() {
    composeTestRule.setContent { AnnouncementScreen(navigationActions = navigationActions) }

    // Check that each labeled component in the screen is displayed
    composeTestRule.onNodeWithTag("titleInput").assertIsDisplayed()
    composeTestRule.onNodeWithTag("categoryInput").assertIsDisplayed()
    composeTestRule.onNodeWithTag("descriptionInput").assertIsDisplayed()
    composeTestRule.onNodeWithTag("locationInput").assertIsDisplayed()
    composeTestRule.onNodeWithTag("availabilityButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("picturesButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("announcementButton").assertIsDisplayed()
  }

  @Test
  fun textFieldDisplaysCorrectPlaceholdersAndLabels() {
    composeTestRule.setContent { AnnouncementScreen(navigationActions = navigationActions) }

    // Check each placeholder text and labels in text fields
    composeTestRule.onNodeWithTag("titleText").assertTextEquals("Title *")
    composeTestRule.onNodeWithTag("categoryText").assertTextEquals("Category *")
    composeTestRule.onNodeWithTag("descriptionText").assertTextEquals("Description *")
    composeTestRule.onNodeWithTag("locationText").assertTextEquals("Location *")
  }

  @Test
  fun titleInputAcceptsText() {
    composeTestRule.setContent { AnnouncementScreen(navigationActions = navigationActions) }

    // Enter text in the title input and verify it
    val titleText = "My QuickFix Title"
    composeTestRule.onNodeWithTag("titleInput").performTextInput(titleText)
    composeTestRule.onNodeWithTag("titleInput").assertTextEquals(titleText)
  }

  @Test
  fun categoryInputAcceptsText() {
    composeTestRule.setContent { AnnouncementScreen(navigationActions = navigationActions) }

    // Enter text in the category input and verify it
    val categoryText = "ResidentialPainting"
    composeTestRule.onNodeWithTag("categoryInput").performTextInput(categoryText)
    composeTestRule.onNodeWithTag("categoryInput").assertTextEquals(categoryText)
  }

  @Test
  fun descriptionInputAcceptsText() {
    composeTestRule.setContent { AnnouncementScreen(navigationActions = navigationActions) }

    // Enter text in the description input and verify it
    val descriptionText = "Detailed description"
    composeTestRule.onNodeWithTag("descriptionInput").performTextInput(descriptionText)
    composeTestRule.onNodeWithTag("descriptionInput").assertTextEquals(descriptionText)
  }

  @Test
  fun locationInputAcceptsText() {
    composeTestRule.setContent { AnnouncementScreen(navigationActions = navigationActions) }

    // Enter text in the location input and verify it
    val locationText = "Lausanne Switzerland"
    composeTestRule.onNodeWithTag("locationInput").performTextInput(locationText)
    composeTestRule.onNodeWithTag("locationInput").assertTextEquals(locationText)
  }

  @Test
  fun availabilityButtonClickable() {
    composeTestRule.setContent { AnnouncementScreen(navigationActions = navigationActions) }

    // Perform click on the availability button and check if it's displayed
    composeTestRule.onNodeWithTag("availabilityButton").performClick().assertIsDisplayed()
  }

  @Test
  fun picturesButtonClickable() {
    composeTestRule.setContent { AnnouncementScreen(navigationActions = navigationActions) }

    // Perform click on the pictures button and check if it's displayed
    composeTestRule.onNodeWithTag("picturesButton").performClick().assertIsDisplayed()
  }

  @Test
  fun mandatoryFieldsMessageDisplaysCorrectly() {
    composeTestRule.setContent { AnnouncementScreen(navigationActions = navigationActions) }

    composeTestRule.onNodeWithTag("mandatoryText").assertTextEquals("* Mandatory fields")
  }

  @Test
  fun announcementButtonEnabledWhenAllFieldsAreValid() {
    composeTestRule.setContent { AnnouncementScreen(navigationActions = navigationActions) }

    // Enter valid text in all fields to enable the announcement button
    composeTestRule.onNodeWithTag("titleInput").performTextInput("QuickFix Title")
    composeTestRule.onNodeWithTag("categoryInput").performTextInput("Home Repair")
    composeTestRule.onNodeWithTag("descriptionInput").performTextInput("Detailed Description")
    composeTestRule.onNodeWithTag("locationInput").performTextInput("New York City")

    // Check if the "Post your announcement" button is enabled
    composeTestRule.onNodeWithTag("announcementButton").assertIsDisplayed().performClick()
  }

  @Test
  fun uploadImageButtonOpensImageSheet() {
    composeTestRule.setContent { AnnouncementScreen(navigationActions = navigationActions) }

    // Click the upload pictures button
    composeTestRule.onNodeWithTag("picturesButton").performClick()

    composeTestRule.onNodeWithTag("uploadImageSheet").assertIsDisplayed()
  }
}
