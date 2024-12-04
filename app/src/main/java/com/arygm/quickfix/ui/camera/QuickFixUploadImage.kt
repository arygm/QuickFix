package com.arygm.quickfix.ui.camera

import android.Manifest
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.arygm.quickfix.R
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun QuickFixUploadImageSheet(
    sheetState: SheetState,
    showModalBottomSheet: Boolean,
    onDismissRequest: () -> Unit,
    onShowBottomSheetChange: (Boolean) -> Unit,
    cameraLauncher: ActivityResultLauncher<Void>? = null,
    galleryLauncher: ActivityResultLauncher<String>? = null,
    onActionRequest: (Bitmap) -> Unit
) {
  val scope = rememberCoroutineScope()
  val takePictureRequested = remember { mutableStateOf(false) }
  val context = LocalContext.current
  val permissionState = rememberPermissionState(permission = Manifest.permission.CAMERA)

  // Use the provided or default cameraLauncher
  val cameraLauncherInstance =
      rememberLauncherForActivityResult(contract = ActivityResultContracts.TakePicturePreview()) {
          bitmap ->
        if (bitmap != null) {
          onActionRequest(bitmap)
        }
        // Hide the bottom sheet
        scope
            .launch { sheetState.hide() }
            .invokeOnCompletion {
              if (!sheetState.isVisible) {
                onShowBottomSheetChange(false)
              }
            }
      }

  // Use the provided or default galleryLauncher
  val galleryLauncherInstance =
      galleryLauncher
          ?: rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) {
              uri ->
            if (uri != null) {
              try {
                val inputStream = context.contentResolver.openInputStream(uri)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                if (bitmap != null) {
                  onActionRequest(bitmap) // Notify that a new Bitmap is available
                }
              } catch (e: Exception) {
                Log.e("GalleryLauncher", "Error decoding selected image: ${e.localizedMessage}")
              }

              // Hide the bottom sheet
              scope
                  .launch { sheetState.hide() }
                  .invokeOnCompletion {
                    if (!sheetState.isVisible) {
                      onShowBottomSheetChange(false)
                    }
                  }
            }
          }

  LaunchedEffect(permissionState.status) {
    if (takePictureRequested.value && permissionState.status.isGranted) {
      takePictureRequested.value = false
      cameraLauncherInstance.launch(null)
    }
  }

  if (showModalBottomSheet) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        modifier = Modifier.testTag("uploadImageSheet")) {
          Column(
              modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
              horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    "Pictures",
                    style = MaterialTheme.typography.headlineLarge,
                    color = colorScheme.onBackground,
                    modifier = Modifier.testTag("picturesText"))
                Divider(
                    color = colorScheme.onSecondaryContainer,
                    thickness = 1.dp,
                    modifier = Modifier.testTag("divider"))

                // Option to take a photo
                Row(
                    modifier =
                        Modifier.fillMaxWidth()
                            .clickable {
                              // Check if the app has the required permissions to use the camera
                              if (permissionState.status.isGranted) {
                                cameraLauncherInstance.launch(null)
                              } else {
                                takePictureRequested.value = true
                                permissionState.launchPermissionRequest()
                              }
                            }
                            .padding(vertical = 8.dp)
                            .testTag("cameraRow"),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start) {
                      Icon(
                          painter = painterResource(id = R.drawable.camera),
                          contentDescription = "Take a photo",
                          tint = colorScheme.onBackground,
                          modifier = Modifier.padding(start = 16.dp))
                      Spacer(modifier = Modifier.width(120.dp))
                      Text(
                          "Take a photo",
                          style = MaterialTheme.typography.headlineSmall,
                          color = colorScheme.onBackground,
                          modifier =
                              Modifier.weight(1f)
                                  .align(Alignment.CenterVertically)
                                  .testTag("cameraText"))
                    }

                Divider(color = colorScheme.onSecondaryContainer, thickness = 1.dp)

                // Option to choose from library
                Row(
                    modifier =
                        Modifier.fillMaxWidth()
                            .clickable { galleryLauncherInstance.launch("image/*") }
                            .padding(vertical = 8.dp)
                            .testTag("libraryRow"),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start) {
                      Icon(
                          painter = painterResource(id = R.drawable.upload_image),
                          contentDescription = "Choose from library",
                          tint = colorScheme.onBackground,
                          modifier = Modifier.padding(start = 16.dp))
                      Spacer(modifier = Modifier.width(108.dp))
                      Text(
                          "Choose from library",
                          style = MaterialTheme.typography.headlineSmall,
                          color = colorScheme.onBackground,
                          modifier =
                              Modifier.weight(1f)
                                  .align(Alignment.CenterVertically)
                                  .testTag("libraryText"))
                    }

                Divider(color = colorScheme.onSecondaryContainer, thickness = 1.dp)
              }
        }
  }
}
