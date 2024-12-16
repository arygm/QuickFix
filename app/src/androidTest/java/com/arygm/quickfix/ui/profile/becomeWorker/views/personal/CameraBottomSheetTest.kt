// CameraBottomSheetTest.kt

package com.arygm.quickfix.ui.profile.becomeWorker.views.personal

import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.arygm.quickfix.ui.uiMode.appContentUI.userModeUI.profile.becomeWorker.views.personal.CameraBottomSheet
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import io.mockk.*
import junit.framework.TestCase.assertEquals
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalMaterial3Api::class)
class CameraBottomSheetTest {

  @get:Rule val composeTestRule = createComposeRule()

  @OptIn(ExperimentalPermissionsApi::class)
  @Test
  fun testCameraPermissionDenied() {
    var permissionRequested = false
    val permissionState =
        FakePermissionState(
            permission = android.Manifest.permission.CAMERA,
            statusValue = PermissionStatus.Denied(shouldShowRationale = false),
            onPermissionRequest = { permissionRequested = true })

    composeTestRule.setContent {
      CameraBottomSheet(
          modifier = Modifier,
          sheetState = rememberModalBottomSheetState(),
          onDismissRequest = {},
          onShowBottomSheetChange = {},
          onActionRequest = {},
          permissionState = permissionState)
    }

    composeTestRule.onNodeWithText("Camera").performClick()

    assert(permissionRequested)
  }

  @OptIn(ExperimentalPermissionsApi::class)
  @Test
  fun testCameraPermissionGrantedAndLaunchCamera() {
    val permissionState =
        FakePermissionState(
            permission = android.Manifest.permission.CAMERA, statusValue = PermissionStatus.Granted)

    // Capture variable for onActionRequest
    var capturedActionRequestValue: String? = null
    val onActionRequest: (String) -> Unit = { value -> capturedActionRequestValue = value }

    // Define test URI and createFile lambda
    val testUri = Uri.parse("file://test_image.jpg")
    val createFile: () -> Uri? = { testUri }

    // Create a TestActivityResultLauncher that invokes onActionRequest when launch is called
    val testLauncher =
        TestActivityResultLauncher<Uri, Boolean> { uri -> onActionRequest(uri.toString()) }

    composeTestRule.setContent {
      CameraBottomSheet(
          modifier = Modifier,
          sheetState = rememberModalBottomSheetState(),
          onDismissRequest = {},
          onShowBottomSheetChange = {},
          onActionRequest = onActionRequest,
          permissionState = permissionState,
          cameraLauncher = testLauncher,
          createFile = createFile)
    }

    composeTestRule.onNodeWithText("Camera").performClick()

    composeTestRule.waitForIdle()

    // Assertions
    // Verify that createFile was called by checking if imageUri was set and launch was called
    assertEquals(testUri.toString(), capturedActionRequestValue)
  }

  @OptIn(ExperimentalPermissionsApi::class)
  @Test
  fun testCameraLaunchFailsToCreateFile() {
    val permissionState =
        FakePermissionState(
            permission = android.Manifest.permission.CAMERA, statusValue = PermissionStatus.Granted)

    // Capture variable for onActionRequest
    var capturedActionRequestValue: String? = null
    val onActionRequest: (String) -> Unit = { value -> capturedActionRequestValue = value }

    // Simulate failure by returning null
    val createFile: () -> Uri? = { null }

    // Create a TestActivityResultLauncher that should not be called
    val testLauncher =
        TestActivityResultLauncher<Uri, Boolean> { uri -> onActionRequest(uri.toString()) }

    composeTestRule.setContent {
      CameraBottomSheet(
          modifier = Modifier,
          sheetState = rememberModalBottomSheetState(),
          onDismissRequest = {},
          onShowBottomSheetChange = {},
          onActionRequest = onActionRequest,
          permissionState = permissionState,
          cameraLauncher = testLauncher,
          createFile = createFile)
    }

    composeTestRule.onNodeWithText("Camera").performClick()

    composeTestRule.waitForIdle()

    // Assertions
    assertEquals(null, capturedActionRequestValue)
  }

  @OptIn(ExperimentalPermissionsApi::class)
  @Test
  fun testGalleryLauncherInvoked() {
    val galleryLauncher = mockk<ActivityResultLauncher<String>>(relaxed = true)

    // Capture variable for onActionRequest
    var capturedActionRequestValue: String? = null
    val onActionRequest: (String) -> Unit = { value -> capturedActionRequestValue = value }

    composeTestRule.setContent {
      CameraBottomSheet(
          modifier = Modifier,
          sheetState = rememberModalBottomSheetState(),
          onDismissRequest = {},
          onShowBottomSheetChange = {},
          onActionRequest = onActionRequest,
          galleryLauncher = galleryLauncher)
    }

    composeTestRule.onNodeWithText("Gallery").performClick()

    verify { galleryLauncher.launch("image/*") }
  }

  @OptIn(ExperimentalPermissionsApi::class)
  @Test
  fun testOnActionRequestCalledWhenCameraReturnsSuccess() {
    val permissionState =
        FakePermissionState(
            permission = android.Manifest.permission.CAMERA, statusValue = PermissionStatus.Granted)

    // Capture variable for onActionRequest
    var capturedActionRequestValue: String? = null
    val onActionRequest: (String) -> Unit = { value -> capturedActionRequestValue = value }

    // Define test URI and createFile lambda
    val testUri = Uri.parse("file://test_image.jpg")
    val createFile: () -> Uri? = { testUri }

    // Create a TestActivityResultLauncher that invokes onActionRequest when launch is called
    val testLauncher =
        TestActivityResultLauncher<Uri, Boolean> { uri -> onActionRequest(uri.toString()) }

    composeTestRule.setContent {
      CameraBottomSheet(
          modifier = Modifier,
          sheetState = rememberModalBottomSheetState(),
          onDismissRequest = {},
          onShowBottomSheetChange = {},
          onActionRequest = onActionRequest,
          permissionState = permissionState,
          cameraLauncher = testLauncher,
          createFile = createFile)
    }

    composeTestRule.onNodeWithText("Camera").performClick()

    composeTestRule.waitForIdle()

    // Assertions
    assertEquals(testUri.toString(), capturedActionRequestValue)
  }

  @OptIn(ExperimentalPermissionsApi::class)
  @Test
  fun testOnActionRequestCalledWhenGalleryReturnsUri() {
    // Capture variable for onActionRequest
    var capturedActionRequestValue: String? = null
    val onActionRequest: (String) -> Unit = { value -> capturedActionRequestValue = value }

    val testUri = Uri.parse("content://gallery_image.jpg")

    // Create a TestActivityResultLauncher that invokes onActionRequest when launch is called
    val testGalleryLauncher =
        TestActivityResultLauncher<String, Uri?> { input -> onActionRequest(testUri.toString()) }

    composeTestRule.setContent {
      CameraBottomSheet(
          modifier = Modifier,
          sheetState = rememberModalBottomSheetState(),
          onDismissRequest = {},
          onShowBottomSheetChange = {},
          onActionRequest = onActionRequest,
          galleryLauncher = testGalleryLauncher)
    }

    composeTestRule.onNodeWithText("Gallery").performClick()

    composeTestRule.waitForIdle()

    // Assertions
    assertEquals(testUri.toString(), capturedActionRequestValue)
  }

  @OptIn(ExperimentalPermissionsApi::class, ExperimentalPermissionsApi::class)
  @Test
  fun testBottomSheetDismissalOnSuccess() {
    val permissionState =
        FakePermissionState(
            permission = android.Manifest.permission.CAMERA, statusValue = PermissionStatus.Granted)

    // Capture variable for onShowBottomSheetChange
    var capturedShowBottomSheetChangeValue: Boolean? = null
    val onShowBottomSheetChange: (Boolean) -> Unit = { value ->
      capturedShowBottomSheetChangeValue = value
    }

    // Define test URI and createFile lambda
    val testUri = Uri.parse("file://test_image.jpg")
    val createFile: () -> Uri? = { testUri }

    // Create a TestActivityResultLauncher that invokes onShowBottomSheetChange(false) when launch
    // is called
    val testLauncher =
        TestActivityResultLauncher<Uri, Boolean> { uri -> onShowBottomSheetChange(false) }

    composeTestRule.setContent {
      CameraBottomSheet(
          modifier = Modifier,
          sheetState = rememberModalBottomSheetState(),
          onDismissRequest = {},
          onShowBottomSheetChange = onShowBottomSheetChange,
          onActionRequest = {},
          permissionState = permissionState,
          cameraLauncher = testLauncher,
          createFile = createFile)
    }

    composeTestRule.onNodeWithText("Camera").performClick()

    composeTestRule.waitForIdle()

    // Assertions
    assertEquals(false, capturedShowBottomSheetChangeValue)
  }
}
