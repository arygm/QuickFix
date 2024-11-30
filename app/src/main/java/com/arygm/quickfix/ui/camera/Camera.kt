package com.arygm.quickfix.ui.camera

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.arygm.quickfix.R
import com.arygm.quickfix.model.camera.takePhoto
import com.arygm.quickfix.model.camera.toggleCamera
import com.arygm.quickfix.ui.navigation.NavigationActions


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraScreen(
    navigationActions: NavigationActions,
    addElem: (Bitmap) -> Unit
){
    val context = LocalContext.current
    // Request camera permission if not already granted
    if (!hasRequiredCameraPermissions(context)) {
        ActivityCompat.requestPermissions(
            context as Activity, arrayOf(Manifest.permission.CAMERA), 0)
    }

    val scaffoldState = rememberBottomSheetScaffoldState()
    val controller = remember {
        LifecycleCameraController(context).apply {
            setEnabledUseCases(CameraController.IMAGE_CAPTURE)
        }
    }

    BottomSheetScaffold(
        sheetContent = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Cancel Button
                Text(
                    text = "Cancel",
                    style = MaterialTheme.typography.titleMedium,
                    color = colorScheme.primary,
                    modifier = Modifier
                        .clickable {
                            // Handle cancel action here
                            navigationActions.goBack()
                        }
                        .padding(16.dp),
                )

                // Take Photo Button
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clickable {
                            // Implement take photo action here
                            takePhoto(
                                controller,
                                { bitmap ->
                                    addElem(bitmap)
                                },
                                context)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(80.dp) // Outer Circle
                            .background(color = colorScheme.primary, shape = CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp) // Middle Circle
                                .background(color = colorScheme.onPrimary, shape = CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp) // Inner Circle
                                    .background(color = colorScheme.primary, shape = CircleShape)
                            )
                        }
                    }

                }

                // Flip Camera Button
                IconButton(
                    onClick = {
                        // Handle flip camera action here
                        toggleCamera(controller.cameraSelector)
                    },
                    modifier = Modifier
                        .size(64.dp)
                        .background(color = colorScheme.primary, shape = CircleShape)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.flipcamera),
                        contentDescription = "Flip Camera",
                        tint = colorScheme.onPrimary,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        },
        scaffoldState = scaffoldState,
        sheetPeekHeight = 0.dp
    ) {
        CameraPreview(controller = controller, modifier = Modifier.fillMaxSize())
    }
}



@Composable
fun CameraPreview(controller: LifecycleCameraController, modifier: Modifier = Modifier) {
    val lifecycleOwner = LocalLifecycleOwner.current
    // Display the camera preview using the CameraX PreviewView
    AndroidView(
        factory = {
            PreviewView(it).apply {
                this.controller = controller
                controller.bindToLifecycle(lifecycleOwner)
            }
        },
        modifier = modifier.testTag("CameraPreview"))
}


/** Check if the app has the required permissions to use the camera and the gallery. */
private fun hasRequiredCameraPermissions(context: Context): Boolean {
    return ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED
}
