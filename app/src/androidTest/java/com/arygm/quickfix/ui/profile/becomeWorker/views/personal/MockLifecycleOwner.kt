package com.arygm.quickfix.ui.profile.becomeWorker.views.personal

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry

class MockLifecycleOwner : LifecycleOwner {
  private val lifecycleRegistry =
      LifecycleRegistry(this).apply { currentState = Lifecycle.State.RESUMED }

  override val lifecycle: Lifecycle
    get() = lifecycleRegistry
}
