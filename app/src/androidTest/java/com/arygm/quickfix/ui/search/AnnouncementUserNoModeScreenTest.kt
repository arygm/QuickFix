package com.arygm.quickfix.ui.search

import android.graphics.Bitmap
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.arygm.quickfix.model.locations.Location
import com.arygm.quickfix.model.search.AnnouncementRepository
import com.arygm.quickfix.model.search.AnnouncementViewModel
import com.arygm.quickfix.ui.navigation.NavigationActions
import com.arygm.quickfix.ui.userModeUI.navigation.UserScreen
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentMatchers.anyList
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.mock
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.eq

class AnnouncementUserNoModeScreenTest {

  @get:Rule val composeTestRule = createComposeRule()

  private lateinit var navigationActions: NavigationActions
  private lateinit var announcementRepository: AnnouncementRepository
  private lateinit var announcementViewModel: AnnouncementViewModel

  @Before
  fun setup() {
    navigationActions = mock(NavigationActions::class.java)
    announcementRepository = mock(AnnouncementRepository::class.java)
    announcementViewModel = AnnouncementViewModel(announcementRepository)
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
    verify(navigationActions, times(1)).navigateTo(UserScreen.SEARCH_LOCATION)
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

  @Test
  fun imagesAreDisplayedWhenUploadedImagesIsNotEmpty() {
    // Create dummy Bitmaps
    val bitmap1 = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
    val bitmap2 = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
    val bitmap3 = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
    val bitmap4 = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)

    composeTestRule.setContent {
      AnnouncementScreen(
          announcementViewModel = announcementViewModel, navigationActions = navigationActions)
    }

    // Add images on the UI thread to ensure Compose observes the changes
    composeTestRule.runOnUiThread {
      announcementViewModel.addUploadedImage(bitmap1)
      announcementViewModel.addUploadedImage(bitmap2)
      announcementViewModel.addUploadedImage(bitmap3)
      announcementViewModel.addUploadedImage(bitmap4)
    }

    // Wait for recomposition
    composeTestRule.waitForIdle()

    // Assert that the uploadedImages list has the expected size
    assertEquals(4, announcementViewModel.uploadedImages.value.size)

    // Verify that the images are displayed
    composeTestRule.onNodeWithTag("uploadedImagesBox").assertIsDisplayed()
    composeTestRule
        .onNodeWithTag("uploadedImagesLazyRow", useUnmergedTree = true)
        .assertIsDisplayed()

    // Check that the first three images are displayed
    composeTestRule.onNodeWithTag("uploadedImageCard0", useUnmergedTree = true).assertIsDisplayed()
    composeTestRule.onNodeWithTag("uploadedImageCard1", useUnmergedTree = true).assertIsDisplayed()
    composeTestRule.onNodeWithTag("uploadedImageCard2", useUnmergedTree = true).assertIsDisplayed()

    // Verify that the overlay is displayed for remaining images
    composeTestRule
        .onNodeWithTag("remainingImagesOverlay", useUnmergedTree = true)
        .assertIsDisplayed()
  }

  @Test
  fun deleteImageButtonRemovesImage() {
    // Create dummy Bitmaps
    val bitmap1 = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
    val bitmap2 = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
    val bitmap3 = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)

    composeTestRule.setContent {
      AnnouncementScreen(
          announcementViewModel = announcementViewModel, navigationActions = navigationActions)
    }

    // Add images on the UI thread
    composeTestRule.runOnUiThread {
      announcementViewModel.addUploadedImage(bitmap1)
      announcementViewModel.addUploadedImage(bitmap2)
      announcementViewModel.addUploadedImage(bitmap3)
    }

    // Wait for recomposition
    composeTestRule.waitForIdle()

    // Verify that the images are displayed
    composeTestRule.onNodeWithTag("uploadedImageCard0", useUnmergedTree = true).assertIsDisplayed()
    composeTestRule.onNodeWithTag("uploadedImageCard1", useUnmergedTree = true).assertIsDisplayed()
    composeTestRule.onNodeWithTag("uploadedImageCard2", useUnmergedTree = true).assertIsDisplayed()

    // Click the delete button on the first image
    composeTestRule.onNodeWithTag("deleteImageButton0", useUnmergedTree = true).performClick()

    // Wait for recomposition
    composeTestRule.waitForIdle()

    // Verify that there are only two displayed images
    composeTestRule.onNodeWithTag("uploadedImageCard2", useUnmergedTree = true).assertDoesNotExist()

    // Verify that the images list has been updated
    assertEquals(2, announcementViewModel.uploadedImages.value.size)
  }

  @Test
  fun clickingRemainingImagesOverlayNavigatesToDisplayUploadedImages() {
    // Create dummy Bitmaps
    val bitmap1 = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
    val bitmap2 = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
    val bitmap3 = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
    val bitmap4 = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
    val bitmap5 = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)

    composeTestRule.setContent {
      AnnouncementScreen(
          announcementViewModel = announcementViewModel, navigationActions = navigationActions)
    }

    // Add images on the UI thread
    composeTestRule.runOnUiThread {
      announcementViewModel.addUploadedImage(bitmap1)
      announcementViewModel.addUploadedImage(bitmap2)
      announcementViewModel.addUploadedImage(bitmap3)
      announcementViewModel.addUploadedImage(bitmap4)
      announcementViewModel.addUploadedImage(bitmap5)
    }

    // Wait for recomposition
    composeTestRule.waitForIdle()

    // Verify that the overlay is displayed
    composeTestRule
        .onNodeWithTag("remainingImagesOverlay", useUnmergedTree = true)
        .assertIsDisplayed()

    // Click the overlay
    composeTestRule.onNodeWithTag("remainingImagesOverlay", useUnmergedTree = true).performClick()

    // Verify that navigateTo(Screen.DISPLAY_UPLOADED_IMAGES) was called
    verify(navigationActions, times(1)).navigateTo(UserScreen.DISPLAY_UPLOADED_IMAGES)
  }

  @Test
  fun clickingPostButtonWithNoImagesCallsHandleSuccessfulImageUploadDirectly() {
    // No images uploaded

    // Simulate a selected location
    val selectedLocation = Location(name = "Test Location", latitude = 0.0, longitude = 0.0)
    `when`(navigationActions.getFromBackStack("selectedLocation")).thenReturn(selectedLocation)

    // Mock the repository behavior
    `when`(announcementRepository.getNewUid()).thenReturn("testAnnouncementId")
    doAnswer {
          val onSuccess = it.getArgument<(List<String>) -> Unit>(2)
          onSuccess(emptyList())
        }
        .`when`(announcementRepository)
        .uploadAnnouncementImages(anyString(), anyList(), any(), any())

    composeTestRule.setContent {
      AnnouncementScreen(
          announcementViewModel = announcementViewModel, navigationActions = navigationActions)
    }

    // Wait for any initial recomposition
    composeTestRule.waitForIdle()

    // Enter valid text in all fields to enable the announcement button
    composeTestRule.onNodeWithTag("titleInput").performTextInput("Test Title")
    composeTestRule.onNodeWithTag("categoryInput").performTextInput("Test Category")
    composeTestRule.onNodeWithTag("descriptionInput").performTextInput("Test Description")

    // Click the "Post your announcement" button
    composeTestRule.onNodeWithTag("announcementButton").assertIsEnabled().performClick()

    // Verify that announce() was called
    verify(announcementRepository, times(1)).announce(any(), any(), any())

    // Verify that clearUploadedImages() was called
    assertTrue(announcementViewModel.uploadedImages.value.isEmpty())
  }

  @Test
  fun clickingPostButtonWithImagesUploadsImagesAndCallsHandleSuccessfulImageUpload() {
    // Create dummy Bitmaps
    val bitmap1 = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
    val bitmap2 = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)

    // Add images on the UI thread
    composeTestRule.runOnUiThread {
      announcementViewModel.addUploadedImage(bitmap1)
      announcementViewModel.addUploadedImage(bitmap2)
    }

    // Wait for recomposition
    composeTestRule.waitForIdle()

    // Simulate a selected location
    val selectedLocation = Location(name = "Test Location", latitude = 0.0, longitude = 0.0)
    `when`(navigationActions.getFromBackStack("selectedLocation")).thenReturn(selectedLocation)

    // Mock the repository behavior
    `when`(announcementRepository.getNewUid()).thenReturn("testAnnouncementId")
    doAnswer {
          val onSuccess = it.getArgument<(List<String>) -> Unit>(2)
          // Simulate uploaded image URLs
          onSuccess(listOf("uploadedImageUrl1", "uploadedImageUrl2"))
        }
        .`when`(announcementRepository)
        .uploadAnnouncementImages(anyString(), anyList(), any(), any())

    composeTestRule.setContent {
      AnnouncementScreen(
          announcementViewModel = announcementViewModel, navigationActions = navigationActions)
    }

    // Wait for any initial recomposition
    composeTestRule.waitForIdle()

    // Enter valid text in all fields to enable the announcement button
    composeTestRule.onNodeWithTag("titleInput").performTextInput("Test Title")
    composeTestRule.onNodeWithTag("categoryInput").performTextInput("Test Category")
    composeTestRule.onNodeWithTag("descriptionInput").performTextInput("Test Description")

    // Click the "Post your announcement" button
    composeTestRule.onNodeWithTag("announcementButton").assertIsEnabled().performClick()

    // Verify that uploadAnnouncementImages() was called
    verify(announcementRepository, times(1))
        .uploadAnnouncementImages(
            eq("testAnnouncementId"), eq(listOf(bitmap1, bitmap2)), any(), any())

    // Verify that announce() was called
    verify(announcementRepository, times(1)).announce(any(), any(), any())

    // Verify that clearUploadedImages() was called
    assertTrue(announcementViewModel.uploadedImages.value.isEmpty())
  }

  @Test
  fun clickingPostButtonWithImageUploadFailureHandlesFailure() {
    // Create dummy Bitmaps
    val bitmap1 = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
    val bitmap2 = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)

    // Add images on the UI thread
    composeTestRule.runOnUiThread {
      announcementViewModel.addUploadedImage(bitmap1)
      announcementViewModel.addUploadedImage(bitmap2)
    }

    // Wait for recomposition
    composeTestRule.waitForIdle()

    // Simulate a selected location
    val selectedLocation = Location(name = "Test Location", latitude = 0.0, longitude = 0.0)
    `when`(navigationActions.getFromBackStack("selectedLocation")).thenReturn(selectedLocation)

    // Mock the repository behavior to simulate failure
    `when`(announcementRepository.getNewUid()).thenReturn("testAnnouncementId")
    doAnswer {
          val onFailure = it.getArgument<(Exception) -> Unit>(3)
          onFailure(Exception("Simulated upload failure"))
        }
        .`when`(announcementRepository)
        .uploadAnnouncementImages(anyString(), anyList(), any(), any())

    composeTestRule.setContent {
      AnnouncementScreen(
          announcementViewModel = announcementViewModel, navigationActions = navigationActions)
    }

    // Wait for any initial recomposition
    composeTestRule.waitForIdle()

    // Enter valid text in all fields to enable the announcement button
    composeTestRule.onNodeWithTag("titleInput").performTextInput("Test Title")
    composeTestRule.onNodeWithTag("categoryInput").performTextInput("Test Category")
    composeTestRule.onNodeWithTag("descriptionInput").performTextInput("Test Description")

    // Click the "Post your announcement" button
    composeTestRule.onNodeWithTag("announcementButton").assertIsEnabled().performClick()

    // Verify that uploadAnnouncementImages() was called
    verify(announcementRepository, times(1))
        .uploadAnnouncementImages(
            eq("testAnnouncementId"), eq(listOf(bitmap1, bitmap2)), any(), any())

    // Verify that announce() was not called due to upload failure
    verify(announcementRepository, times(0)).announce(any(), any(), any())
  }
}
