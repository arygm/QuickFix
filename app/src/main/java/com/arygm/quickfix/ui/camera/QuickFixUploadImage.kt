package com.arygm.quickfix.ui.camera

import android.Manifest
import android.net.Uri
import android.os.Environment
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import com.arygm.quickfix.R
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import java.io.File
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun QuickFixUploadImageSheet(
    sheetState: SheetState,
    showModalBottomSheet: Boolean,
    onDismissRequest: () -> Unit,
    onShowBottomSheetChange: (Boolean) -> Unit,
    cameraLauncher: ActivityResultLauncher<Uri>? = null,
    galleryLauncher: ActivityResultLauncher<String>? = null,
    createFile: (() -> Uri?)? = null,
    onActionRequest: (String) -> Unit
) {
  val scope = rememberCoroutineScope()
  val imageUri = rememberSaveable { mutableStateOf<Uri?>(null) }
  val takePictureRequested = remember { mutableStateOf(false) }
  val choosePictureRequested = remember { mutableStateOf(false) }
  val context = LocalContext.current
  val permissionState = rememberPermissionState(permission = Manifest.permission.CAMERA)

  // Use the provided or default createFile function
  val createFileFunction =
      createFile
          ?: {
            val storage = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            val file = File.createTempFile("image_${System.currentTimeMillis()}", ".jpg", storage)
            try {
              FileProvider.getUriForFile(context, "com.arygm.quickfix.fileprovider", file)
            } catch (e: Exception) {
              Log.e("CameraBottomSheet", "Error creating file: ${e.localizedMessage}")
              null
            }
          }

  // Use the provided or default cameraLauncher
  val cameraLauncherInstance =
      cameraLauncher
          ?: rememberLauncherForActivityResult(contract = ActivityResultContracts.TakePicture()) {
              success ->
            Log.d("CameraBottomSheet", "CameraLauncher: $success")
            Log.d("CameraBottomSheet", "CameraLauncher URI: ${imageUri.value}")
            if (success && imageUri.value != null) {
              onActionRequest(imageUri.value.toString())
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
              onActionRequest(uri.toString())
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
      imageUri.value = createFileFunction()
      imageUri.value?.let { uri ->
        Log.d("CameraBottomSheet", "Launching camera with URI: $uri")
        cameraLauncherInstance.launch(uri)
      } ?: run { Log.e("CameraBottomSheet", "Failed to create image file URI") }
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
                                imageUri.value = createFileFunction()
                                imageUri.value?.let { uri ->
                                  Log.d("CameraBottomSheet", "Launching camera with URI: $uri")
                                  cameraLauncherInstance.launch(uri)
                                }
                                    ?: run {
                                      Log.e("CameraBottomSheet", "Failed to create image file URI")
                                    }
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
