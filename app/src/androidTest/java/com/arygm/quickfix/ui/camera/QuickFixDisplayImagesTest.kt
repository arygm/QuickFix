package com.arygm.quickfix.ui.camera

import android.graphics.Bitmap
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.arygm.quickfix.ui.navigation.NavigationActions
import com.arygm.quickfix.ui.uiMode.appContentUI.userModeUI.camera.QuickFixDisplayImages
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito

class QuickFixDisplayImagesTest {

  @get:Rule val composeTestRule = createComposeRule()

  private lateinit var navigationActions: NavigationActions
  private lateinit var images: List<Bitmap>

  @Before
  fun setup() {
    navigationActions = Mockito.mock(NavigationActions::class.java)

    // Create a list of 4 images
    images = List(4) { createTestBitmap() }
  }

  @Test
  fun displaysTitleWithCorrectNumberOfImages() {
    composeTestRule.setContent {
      QuickFixDisplayImages(
          canDelete = true, navigationActions = navigationActions, images = images)
    }

    // Verify the title displays the correct number of images
    composeTestRule.onNodeWithTag("DisplayedImagesTitle").assertTextEquals("4 elements")
  }

  @Test
  fun goBackButtonNavigatesCorrectly() {
    composeTestRule.setContent {
      QuickFixDisplayImages(
          canDelete = true, navigationActions = navigationActions, images = images)
    }

    // Click the "Go Back" button
    composeTestRule.onNodeWithTag("goBackButton").performClick()

    // Verify navigation action is called
    Mockito.verify(navigationActions).goBack()
  }

  @Test
  fun selectImagesButtonTogglesSelectionMode() {
    composeTestRule.setContent {
      QuickFixDisplayImages(
          canDelete = true, navigationActions = navigationActions, images = images)
    }

    // Click the "Select Images" button
    composeTestRule.onNodeWithTag("SelectImagesButton").performClick()

    // Verify "Select all" and "Done" buttons are displayed
    composeTestRule.onNodeWithTag("selectionButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("endSelectionButton").assertIsDisplayed()
  }

  @Test
  fun selectAllButtonSelectsAllImages() {
    composeTestRule.setContent {
      QuickFixDisplayImages(
          canDelete = true, navigationActions = navigationActions, images = images)
    }

    // Enter selection mode
    composeTestRule.onNodeWithTag("SelectImagesButton").performClick()

    // Click the "Select All" button
    composeTestRule.onNodeWithTag("selectionButton").performClick()

    // Verify all images are selected
    composeTestRule.onNodeWithTag("nbOfSelectedPhotos").assertTextContains("4 photos selected")
  }

  @Test
  fun endSelectionButtonExitsSelectionMode() {
    composeTestRule.setContent {
      QuickFixDisplayImages(
          canDelete = true, navigationActions = navigationActions, images = images)
    }

    // Enter selection mode
    composeTestRule.onNodeWithTag("SelectImagesButton").performClick()

    // Exit selection mode
    composeTestRule.onNodeWithTag("endSelectionButton").performClick()

    // Verify selection mode is exited
    composeTestRule.onNodeWithTag("SelectImagesButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("selectionButton").assertDoesNotExist()
  }

  @Test
  fun deleteButtonDeletesSelectedImages() {
    composeTestRule.setContent {
      QuickFixDisplayImages(
          canDelete = true, navigationActions = navigationActions, images = images)
    }

    // Enter selection mode and select all images
    composeTestRule.onNodeWithTag("SelectImagesButton").performClick()
    composeTestRule.onNodeWithTag("selectionButton").performClick()

    // Click the delete button
    composeTestRule.onNodeWithTag("nbOfSelectedPhotos").performClick()

    composeTestRule.onNodeWithTag("SelectImagesButton").assertIsDisplayed()
  }

  @Test
  fun clickingOnImageSelectsAndDeselectsIt() {
    composeTestRule.setContent {
      QuickFixDisplayImages(
          canDelete = true, navigationActions = navigationActions, images = images)
    }

    // Enter selection mode
    composeTestRule.onNodeWithTag("SelectImagesButton").performClick()

    // Click on the first image to select it
    composeTestRule.onNodeWithTag("imageCard_0").performClick()
    composeTestRule.onNodeWithTag("nbOfSelectedPhotos").assertTextContains("1 photo selected")

    // Click on the same image to deselect it
    composeTestRule.onNodeWithTag("imageCard_0").performClick()
    composeTestRule.onNodeWithTag("nbOfSelectedPhotos").assertDoesNotExist()
  }

  @Test
  fun displaysCorrectIconForSelectedImages() {
    composeTestRule.setContent {
      QuickFixDisplayImages(
          canDelete = true, navigationActions = navigationActions, images = images)
    }

    // Enter selection mode
    composeTestRule.onNodeWithTag("SelectImagesButton").performClick()

    // Select the first image
    composeTestRule.onNodeWithTag("imageCard_0").performClick()

    // Verify the icon changes to "CheckCircle"
    composeTestRule.onNodeWithTag("selectionIcon_0").assertExists()
  }

  @Test
  fun displaysRadioButtonForUnselectedImages() {
    composeTestRule.setContent {
      QuickFixDisplayImages(
          canDelete = true, navigationActions = navigationActions, images = images)
    }

    // Enter selection mode
    composeTestRule.onNodeWithTag("SelectImagesButton").performClick()

    // Verify the icon for unselected images
    composeTestRule.onNodeWithTag("selectionIcon_1").assertExists()
  }

  private fun createTestBitmap(): Bitmap {
    return Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
  }
}
