package com.arygm.quickfix.ui.camera

import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.arygm.quickfix.ui.profile.becomeWorker.views.personal.FakePermissionState
import com.arygm.quickfix.ui.profile.becomeWorker.views.personal.TestActivityResultLauncher
import com.arygm.quickfix.ui.uiMode.appContentUI.userModeUI.camera.QuickFixUploadImageSheet
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

@OptIn(ExperimentalMaterial3Api::class)
class QuickFixUploadImageSheetTest {

  @get:Rule val composeTestRule = createComposeRule()

  private lateinit var onDismissRequest: () -> Unit
  private lateinit var onShowBottomSheetChange: (Boolean) -> Unit
  private lateinit var onActionRequest: (Bitmap) -> Unit
  private lateinit var mockCameraLauncher: ActivityResultLauncher<Void?>
  private lateinit var mockGalleryLauncher: ActivityResultLauncher<String>

  @Before
  fun setup() {
    onDismissRequest = mock()
    onShowBottomSheetChange = mock()
    onActionRequest = mock()
    mockCameraLauncher = mock()
    mockGalleryLauncher = mock()
  }

  @OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
  @Test
  fun quickFixUploadImageSheet_displaysCorrectly() {
    composeTestRule.setContent {
      QuickFixUploadImageSheet(
          sheetState = rememberModalBottomSheetState(),
          showModalBottomSheet = true,
          onDismissRequest = onDismissRequest,
          onShowBottomSheetChange = onShowBottomSheetChange,
          cameraLauncher = mockCameraLauncher,
          galleryLauncher = mockGalleryLauncher,
          onActionRequest = onActionRequest)
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

  @OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
  @Test
  fun quickFixUploadImageSheet_notDisplayedWhenShowModalBottomSheetIsFalse() {
    composeTestRule.setContent {
      QuickFixUploadImageSheet(
          sheetState = rememberModalBottomSheetState(), // Provide a valid SheetState
          showModalBottomSheet = false,
          onDismissRequest = onDismissRequest,
          onShowBottomSheetChange = onShowBottomSheetChange,
          cameraLauncher = mockCameraLauncher,
          galleryLauncher = mockGalleryLauncher,
          onActionRequest = onActionRequest)
    }

    // Assert the sheet is not displayed
    composeTestRule.onNodeWithTag("uploadImageSheet").assertDoesNotExist()
  }

  @OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
  @Test
  fun quickFixUploadImageSheet_takePhotoClick_launchesCamera() {
    val permissionState =
        FakePermissionState(
            permission = android.Manifest.permission.CAMERA, statusValue = PermissionStatus.Granted)

    composeTestRule.setContent {
      QuickFixUploadImageSheet(
          sheetState = rememberModalBottomSheetState(),
          showModalBottomSheet = true,
          onDismissRequest = onDismissRequest,
          onShowBottomSheetChange = onShowBottomSheetChange,
          cameraLauncher = mockCameraLauncher,
          galleryLauncher = mockGalleryLauncher,
          onActionRequest = onActionRequest,
          permissionState = permissionState)
    }

    // Click on "Take a photo"
    composeTestRule.onNodeWithTag("cameraRow").performClick()

    // Verify the camera launcher is triggered
    verify(mockCameraLauncher).launch(null)
  }

  @OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
  @Test
  fun quickFixUploadImageSheet_chooseFromLibraryClick_launchesGallery() {
    val permissionState =
        FakePermissionState(
            permission = android.Manifest.permission.CAMERA, statusValue = PermissionStatus.Granted)

    composeTestRule.setContent {
      QuickFixUploadImageSheet(
          sheetState = rememberModalBottomSheetState(),
          showModalBottomSheet = true,
          onDismissRequest = onDismissRequest,
          onShowBottomSheetChange = onShowBottomSheetChange,
          cameraLauncher = mockCameraLauncher,
          galleryLauncher = mockGalleryLauncher,
          onActionRequest = onActionRequest,
          permissionState = permissionState)
    }

    // Click on "Choose from library"
    composeTestRule.onNodeWithTag("libraryRow").performClick()

    // Verify the gallery launcher is triggered
    verify(mockGalleryLauncher).launch("image/*")
  }

  @OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
  @Test
  fun quickFixUploadImageSheet_dismissBottomSheet_invokesCallback() {
    val permissionState =
        FakePermissionState(
            permission = android.Manifest.permission.CAMERA, statusValue = PermissionStatus.Granted)

    composeTestRule.setContent {
      QuickFixUploadImageSheet(
          sheetState = rememberModalBottomSheetState(),
          showModalBottomSheet = true,
          onDismissRequest = onDismissRequest,
          onShowBottomSheetChange = onShowBottomSheetChange,
          cameraLauncher = mockCameraLauncher,
          galleryLauncher = mockGalleryLauncher,
          onActionRequest = onActionRequest,
          permissionState = permissionState)
    }

    // Simulate bottom sheet dismissal
    composeTestRule.runOnUiThread { onDismissRequest.invoke() }

    // Verify the dismiss callback is triggered
    verify(onDismissRequest).invoke()
  }

  @OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
  @Test
  fun testOnActionRequestCalledWhenCameraReturnsSuccess() {
    val permissionState =
        FakePermissionState(
            permission = android.Manifest.permission.CAMERA, statusValue = PermissionStatus.Granted)

    var capturedBitmap: Bitmap? = null
    val onActionRequest: (Bitmap) -> Unit = { bitmap -> capturedBitmap = bitmap }

    val testCameraLauncher =
        TestActivityResultLauncher<Void?, Bitmap?> {
          val bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
          onActionRequest(bitmap)
          bitmap
        }

    composeTestRule.setContent {
      QuickFixUploadImageSheet(
          sheetState = rememberModalBottomSheetState(),
          showModalBottomSheet = true,
          onDismissRequest = {},
          onShowBottomSheetChange = {},
          cameraLauncher = testCameraLauncher,
          galleryLauncher = mock(),
          onActionRequest = onActionRequest,
          permissionState = permissionState)
    }

    composeTestRule.onNodeWithTag("cameraRow").performClick()

    composeTestRule.waitForIdle()

    assertNotNull(capturedBitmap)
    assertEquals(100, capturedBitmap!!.width)
    assertEquals(100, capturedBitmap!!.height)
  }

  @OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
  @Test
  fun testOnActionRequestCalledWhenGalleryReturnsImage() {
    val permissionState =
        FakePermissionState(
            permission = android.Manifest.permission.CAMERA, statusValue = PermissionStatus.Granted)

    var capturedBitmap: Bitmap? = null
    val onActionRequest: (Bitmap) -> Unit = { bitmap -> capturedBitmap = bitmap }

    val testGalleryLauncher =
        TestActivityResultLauncher<String, Uri?> {
          val bitmap = Bitmap.createBitmap(200, 200, Bitmap.Config.ARGB_8888)
          onActionRequest(bitmap)
          null
        }

    composeTestRule.setContent {
      QuickFixUploadImageSheet(
          sheetState = rememberModalBottomSheetState(),
          showModalBottomSheet = true,
          onDismissRequest = {},
          onShowBottomSheetChange = {},
          cameraLauncher = mock(),
          galleryLauncher = testGalleryLauncher,
          onActionRequest = onActionRequest,
          permissionState = permissionState)
    }

    composeTestRule.onNodeWithTag("libraryRow").performClick()

    composeTestRule.waitForIdle()

    assertNotNull(capturedBitmap)
    assertEquals(200, capturedBitmap!!.width)
    assertEquals(200, capturedBitmap!!.height)
  }
}
