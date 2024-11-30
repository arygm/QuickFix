package com.arygm.quickfix.ui.elements

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.arygm.quickfix.ui.camera.QuickFixUploadImageSheet
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

class QuickFixUploadImageTest {

  @get:Rule val composeTestRule = createComposeRule()

  private lateinit var onDismissRequest: () -> Unit
  private lateinit var onTakePhotoClick: () -> Unit
  private lateinit var onChooseFromLibraryClick: () -> Unit

  @Before
  fun setup() {
    onDismissRequest = mock()
    onTakePhotoClick = mock()
    onChooseFromLibraryClick = mock()
  }

  @Test
  fun quickFixUploadImageSheet_displaysCorrectly() {
    composeTestRule.setContent {
      QuickFixUploadImageSheet(
          showModalBottomSheet = true,
          onDismissRequest = onDismissRequest,
          onTakePhotoClick = onTakePhotoClick,
          onChooseFromLibraryClick = onChooseFromLibraryClick)
    }

    // Assert the sheet is displayed
    composeTestRule.onNodeWithTag("uploadImageSheet").assertIsDisplayed()

    // Assert title is displayed
    composeTestRule.onNodeWithTag("picturesText").assertIsDisplayed()
    composeTestRule.onNodeWithText("Pictures").assertIsDisplayed()

    // Assert dividers are displayed
    composeTestRule.onNodeWithTag("divider").assertIsDisplayed()

    // Assert "Take a photo" option is displayed
    composeTestRule.onNodeWithTag("cameraRow").assertIsDisplayed()
    composeTestRule.onNodeWithTag("cameraText", useUnmergedTree = true).assertIsDisplayed()
    composeTestRule.onNodeWithText("Take a photo").assertIsDisplayed()

    // Assert "Choose from library" option is displayed
    composeTestRule.onNodeWithTag("libraryRow").assertIsDisplayed()
    composeTestRule.onNodeWithTag("libraryText", useUnmergedTree = true).assertIsDisplayed()
    composeTestRule.onNodeWithText("Choose from library").assertIsDisplayed()
  }

  @Test
  fun quickFixUploadImageSheet_takePhotoClick_invokesCallbackAndDismisses() {
    composeTestRule.setContent {
      QuickFixUploadImageSheet(
          showModalBottomSheet = true,
          onDismissRequest = onDismissRequest,
          onTakePhotoClick = onTakePhotoClick,
          onChooseFromLibraryClick = onChooseFromLibraryClick)
    }

    // Click on "Take a photo"
    composeTestRule.onNodeWithTag("cameraRow").performClick()

    // Verify the callback is triggered and the sheet is dismissed
    verify(onTakePhotoClick).invoke()
    verify(onDismissRequest).invoke()
  }

  @Test
  fun quickFixUploadImageSheet_chooseFromLibraryClick_invokesCallbackAndDismisses() {
    composeTestRule.setContent {
      QuickFixUploadImageSheet(
          showModalBottomSheet = true,
          onDismissRequest = onDismissRequest,
          onTakePhotoClick = onTakePhotoClick,
          onChooseFromLibraryClick = onChooseFromLibraryClick)
    }

    // Click on "Choose from library"
    composeTestRule.onNodeWithTag("libraryRow").performClick()

    // Verify the callback is triggered and the sheet is dismissed
    verify(onChooseFromLibraryClick).invoke()
    verify(onDismissRequest).invoke()
  }

  @Test
  fun quickFixUploadImageSheet_notDisplayedWhenShowModalBottomSheetIsFalse() {
    composeTestRule.setContent {
      QuickFixUploadImageSheet(
          showModalBottomSheet = false,
          onDismissRequest = onDismissRequest,
          onTakePhotoClick = onTakePhotoClick,
          onChooseFromLibraryClick = onChooseFromLibraryClick)
    }

    // Assert the sheet is not displayed
    composeTestRule.onNodeWithTag("uploadImageSheet").assertDoesNotExist()
  }
}
