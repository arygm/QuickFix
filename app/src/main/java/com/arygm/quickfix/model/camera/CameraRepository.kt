package com.arygm.quickfix.model.camera

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.view.LifecycleCameraController
import androidx.core.content.ContextCompat


/** Create a new [LifecycleCameraController] to control the camera. */
fun takePhoto(
    controller: LifecycleCameraController,
    onPhotoTaken: (Bitmap) -> Unit,
    context: Context
) {
    controller.takePicture(
        ContextCompat.getMainExecutor(context),
        object : ImageCapture.OnImageCapturedCallback() {
            override fun onCaptureSuccess(image: ImageProxy) {
                super.onCaptureSuccess(image)

                // Rotate the image to match the device's orientation
                val matrix =
                    Matrix().apply { postRotate(image.imageInfo.rotationDegrees.toFloat()) }
                val rotatedBitmap =
                    Bitmap.createBitmap(
                        image.toBitmap(),
                        0,
                        0,
                        image.width,
                        image.height,
                        matrix,
                        true
                    )

                onPhotoTaken(rotatedBitmap)
            }

            // Log an error if the photo couldn't be taken
            override fun onError(exception: ImageCaptureException) {
                Log.e("Camera", "Couldn't take photo: ", exception)
            }
        })
}

fun toggleCamera(cameraSelector: CameraSelector): CameraSelector {
    if (cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA)
        return CameraSelector.DEFAULT_FRONT_CAMERA
    else return CameraSelector.DEFAULT_BACK_CAMERA
}



