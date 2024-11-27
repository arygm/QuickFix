package com.arygm.quickfix.ui.profile.becomeWorker.views.personal

import android.graphics.Camera
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import io.mockk.InternalPlatformDsl.toStr
import kotlinx.coroutines.launch
import net.bytebuddy.asm.Advice
import java.io.File
import kotlin.coroutines.coroutineContext


@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun CameraBottomSheet(
    sheetState: SheetState,
    onDismissRequest: () -> Unit,
    onShowBottomSheetChange: (Boolean) -> Unit,
    onActionRequest: (String) -> Unit,
) {
    val scope = rememberCoroutineScope()
    val imageUri = rememberSaveable {
        mutableStateOf<Uri?>(null)
    }
    val context = LocalContext.current

    val takePictureRequested = remember { mutableStateOf(false) }

    val cameraPermission = rememberPermissionState(permission = android.Manifest.permission.CAMERA)

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        Log.d("CameraBottomSheet", "CameraLauncher: $success")
        Log.d("CameraBottomSheet", "CameraLauncher URI: ${imageUri.value}")
        if (success && imageUri.value != null) {
            onActionRequest(imageUri.value.toString())
        }
        // Hide the bottom sheet here
        scope.launch { sheetState.hide() }.invokeOnCompletion {
            if (!sheetState.isVisible) {
                onShowBottomSheetChange(false)
            }
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            onActionRequest(uri.toString())
            // Hide the bottom sheet here
            scope.launch { sheetState.hide() }.invokeOnCompletion {
                if (!sheetState.isVisible) {
                    onShowBottomSheetChange(false)
                }
            }
        }
    }

    fun createFile(): Uri? {
        val storage = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val file = File.createTempFile("image_${System.currentTimeMillis()}", ".jpg", storage)
        return try {
            FileProvider.getUriForFile(context, "com.arygm.quickfix.fileprovider", file)
        } catch (e: Exception) {
            Log.e("CameraBottomSheet", "Error creating file: ${e.localizedMessage}")
            null
        }
    }

    LaunchedEffect(cameraPermission.status) {
        if (takePictureRequested.value && cameraPermission.status.isGranted) {
            takePictureRequested.value = false
            imageUri.value = createFile()
            imageUri.value?.let { uri ->
                Log.d("CameraBottomSheet", "Launching camera with URI: $uri")
                cameraLauncher.launch(uri)
            } ?: run {
                Log.e("CameraBottomSheet", "Failed to create image file URI")
            }
        }
    }


    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState
    ) {

        Row {
            IconButton(
                modifier = Modifier.weight(0.5f),
                onClick = {
                    if (cameraPermission.status.isGranted) {
                        imageUri.value = createFile()
                        imageUri.value?.let { uri ->
                            Log.d("CameraBottomSheet", "Launching camera with URI: $uri")
                            cameraLauncher.launch(uri)
                        } ?: run {
                            Log.e("CameraBottomSheet", "Failed to create image file URI")
                        }
                    } else {
                        takePictureRequested.value = true
                        cameraPermission.launchPermissionRequest()
                    }
                }
            ) {
                Row {
                    Icon(
                        Icons.Outlined.CameraAlt,
                        contentDescription = "Camera"
                    )
                    Text("Camera")
                }
            }
            IconButton(
                modifier = Modifier.weight(0.5f),
                onClick = {
                    galleryLauncher.launch("image/*")
                }
            ) {
                Row {
                    Icon(
                        Icons.Outlined.Image,
                        contentDescription = "Gallery"
                    )
                    Text("Gallery")
                }
            }
        }
    }
}