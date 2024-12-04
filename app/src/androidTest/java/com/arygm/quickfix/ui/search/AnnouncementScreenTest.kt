package com.arygm.quickfix.ui.search

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.arygm.quickfix.model.locations.Location
import com.arygm.quickfix.ui.navigation.NavigationActions
import com.arygm.quickfix.ui.navigation.Screen
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`

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
    // Simulate a selected location
    val selectedLocation =
        Location(name = "New York City", latitude = 40.7128, longitude = -74.0060)
    `when`(navigationActions.getFromBackStack("selectedLocation")).thenReturn(selectedLocation)

    composeTestRule.setContent { AnnouncementScreen(navigationActions = navigationActions) }

    // Enter valid text in all fields to enable the announcement button
    composeTestRule.onNodeWithTag("titleInput").performTextInput("QuickFix Title")
    composeTestRule.onNodeWithTag("categoryInput").performTextInput("Home Repair")
    composeTestRule.onNodeWithTag("descriptionInput").performTextInput("Detailed Description")
    // No need to performTextInput on locationInput as it's a Box, not a TextField

    // Check if the "Post your announcement" button is enabled and perform click
    composeTestRule.onNodeWithTag("announcementButton").assertIsEnabled().performClick()

    // Verify that saveToCurBackStack was called
    verify(navigationActions, times(1)).saveToCurBackStack("selectedLocation", null)
  }

  @Test
  fun uploadImageButtonOpensImageSheet() {
    composeTestRule.setContent { AnnouncementScreen(navigationActions = navigationActions) }

    // Click the upload pictures button
    composeTestRule.onNodeWithTag("picturesButton").performClick()

    composeTestRule.onNodeWithTag("uploadImageSheet").assertIsDisplayed()
  }

  @Test
  fun locationBoxDisplaysCorrectText() {
    composeTestRule.setContent { AnnouncementScreen(navigationActions = navigationActions) }

    // Par défaut, sans localisation sélectionnée, la box doit afficher "Location"
    composeTestRule.onNodeWithTag("locationInput").assertTextEquals("Location")
  }

  @Test
  fun locationBoxUpdatesWhenLocationSelected() {
    // Simulez une localisation sélectionnée
    val selectedLocation = Location(name = "Paris, France", latitude = 48.8566, longitude = 2.3522)

    // Configurez le mock pour renvoyer la localisation
    `when`(navigationActions.getFromBackStack("selectedLocation")).thenReturn(selectedLocation)

    composeTestRule.setContent { AnnouncementScreen(navigationActions = navigationActions) }

    // Vérifiez que le texte dans la box de localisation est mis à jour
    composeTestRule.onNodeWithTag("locationInput").assertTextEquals("Paris, France")
  }

  @Test
  fun locationBoxShowsEllipsisForLongText() {
    val longLocationName = "A very long location name that exceeds the box width"
    val selectedLocation = Location(name = longLocationName, latitude = 0.0, longitude = 0.0)

    `when`(navigationActions.getFromBackStack("selectedLocation")).thenReturn(selectedLocation)

    composeTestRule.setContent { AnnouncementScreen(navigationActions = navigationActions) }

    composeTestRule
        .onNodeWithTag("locationInput")
        .assertTextEquals(longLocationName.take(150)) // Maximum de 150 caractères
  }

  @Test
  fun locationBoxOpensSearchScreenOnClick() {
    composeTestRule.setContent { AnnouncementScreen(navigationActions = navigationActions) }

    composeTestRule.onNodeWithTag("locationInput").performClick()
    verify(navigationActions, times(1)).navigateTo(Screen.SEARCH_LOCATION)
  }

  @Test
  fun locationResetsAfterPostingAnnouncement() {
    // Simulate a selected location
    val selectedLocation = Location(name = "London, UK", latitude = 51.5074, longitude = -0.1278)
    `when`(navigationActions.getFromBackStack("selectedLocation")).thenReturn(selectedLocation)

    composeTestRule.setContent { AnnouncementScreen(navigationActions = navigationActions) }

    // Verify that the location is displayed correctly
    composeTestRule.onNodeWithTag("locationInput").assertTextContains("London, UK")

    // Enter valid text in other required fields
    composeTestRule.onNodeWithTag("titleInput").performTextInput("QuickFix Title")
    composeTestRule.onNodeWithTag("categoryInput").performTextInput("Home Repair")
    composeTestRule.onNodeWithTag("descriptionInput").performTextInput("Detailed Description")

    // Click the "Post your announcement" button
    composeTestRule.onNodeWithTag("announcementButton").assertIsEnabled().performClick()

    // Verify that the location is reset
    composeTestRule.onNodeWithTag("locationInput").assertTextContains("Location")

    // Verify that saveToCurBackStack was called with null
    verify(navigationActions, times(1)).saveToCurBackStack("selectedLocation", null)
  }
}
