package com.arygm.quickfix.ui.uiMode.appContentUI.userModeUI.profile.becomeWorker.views.personal

import android.Manifest
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import java.io.File
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun CameraBottomSheet(
    modifier: Modifier = Modifier,
    sheetState: SheetState,
    onDismissRequest: () -> Unit,
    onShowBottomSheetChange: (Boolean) -> Unit,
    onActionRequest: (String) -> Unit,
    // Injected dependencies for testing
    permissionState: PermissionState =
        rememberPermissionState(permission = Manifest.permission.CAMERA),
    cameraLauncher: ActivityResultLauncher<Uri>? = null,
    galleryLauncher: ActivityResultLauncher<String>? = null,
    createFile: (() -> Uri?)? = null,
) {
  val scope = rememberCoroutineScope()
  val imageUri = rememberSaveable { mutableStateOf<Uri?>(null) }
  val takePictureRequested = remember { mutableStateOf(false) }
  val context = LocalContext.current

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

  ModalBottomSheet(
      onDismissRequest = onDismissRequest,
      sheetState = sheetState,
      modifier = modifier,
  ) {
    Row {
      IconButton(
          modifier = Modifier.weight(0.5f),
          onClick = {
            if (permissionState.status.isGranted) {
              imageUri.value = createFileFunction()
              imageUri.value?.let { uri ->
                Log.d("CameraBottomSheet", "Launching camera with URI: $uri")
                cameraLauncherInstance.launch(uri)
              } ?: run { Log.e("CameraBottomSheet", "Failed to create image file URI") }
            } else {
              takePictureRequested.value = true
              permissionState.launchPermissionRequest()
            }
          }) {
            Row {
              Icon(Icons.Outlined.CameraAlt, contentDescription = "Camera")
              Text("Camera")
            }
          }
      IconButton(
          modifier = Modifier.weight(0.5f),
          onClick = { galleryLauncherInstance.launch("image/*") }) {
            Row {
              Icon(Icons.Outlined.Image, contentDescription = "Gallery")
              Text("Gallery")
            }
          }
    }
  }
}
