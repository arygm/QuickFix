package com.arygm.quickfix.ui.profile.becomeWorker.views.personal

import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.PermissionStatus

/**
 * A fake implementation of PermissionState for testing purposes.
 *
 * @param permission The permission string, e.g., android.Manifest.permission.CAMERA.
 * @param initialStatus The initial permission status (Granted or Denied).
 * @param onPermissionRequest A lambda to simulate permission requests.
 */
@OptIn(ExperimentalPermissionsApi::class)
class FakePermissionState
@ExperimentalPermissionsApi
constructor(
    override val permission: String,
    var statusValue: PermissionStatus,
    var onPermissionRequest: () -> Unit = {}
) : PermissionState {
  override val status: PermissionStatus
    get() = statusValue

  override fun launchPermissionRequest() {
    onPermissionRequest()
  }
}
