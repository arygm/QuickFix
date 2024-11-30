package com.arygm.quickfix.ui.profile.becomeWorker.views.personal

import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.core.app.ActivityOptionsCompat

class TestActivityResultLauncher<I, O>(private val onLaunch: (I) -> Unit) :
    ActivityResultLauncher<I>() {

  var lastInput: I? = null
    private set

  override val contract: ActivityResultContract<I, O>
    get() = throw NotImplementedError("Not needed for testing")

  override fun launch(input: I, options: ActivityOptionsCompat?) {
    lastInput = input
    onLaunch(input)
  }

  override fun unregister() {}
}
