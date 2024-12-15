package com.arygm.quickfix.model.switchModes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.arygm.quickfix.ui.navigation.NavigationActions
import com.arygm.quickfix.ui.uiMode.appContentUI.navigation.AppContentRoute
import com.arygm.quickfix.ui.uiMode.workerMode.navigation.WorkerRoute
import com.arygm.quickfix.ui.userModeUI.navigation.UserRoute
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

enum class AppMode(val route: String) {
  USER(AppContentRoute.USER_MODE),
  WORKER(AppContentRoute.WORKER_MODE)
}

open class ModeViewModel : ViewModel() {
  private val _currentMode = MutableStateFlow(AppMode.USER)
  val currentMode: StateFlow<AppMode> = _currentMode

  private val _onSwitchStartDestWorker = MutableStateFlow(WorkerRoute.HOME)
  val onSwitchStartDestWorker: StateFlow<String> = _onSwitchStartDestWorker

  private val _onSwitchStartDestUser = MutableStateFlow(UserRoute.HOME)
  val onSwitchStartDestUser: StateFlow<String> = _onSwitchStartDestUser

  companion object {
    val Factory: ViewModelProvider.Factory =
        object : ViewModelProvider.Factory {
          @Suppress("UNCHECKED_CAST")
          override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ModeViewModel() as T
          }
        }
  }

  fun switchMode(mode: AppMode) {
    _currentMode.value = mode
  }

  fun setonSwitchStartDestWorker(route: String, modeNavigationActions: NavigationActions) {
    _onSwitchStartDestWorker.value = route
    modeNavigationActions.setCurrentRoute(onSwitchStartDestWorker.value)
  }

  fun setonSwitchStartDestUser(route: String, modeNavigationActions: NavigationActions) {
    _onSwitchStartDestUser.value = route
    modeNavigationActions.setCurrentRoute(onSwitchStartDestUser.value)
  }
}
