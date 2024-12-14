package com.arygm.quickfix.model.switchModes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.arygm.quickfix.ui.uiMode.appContentUI.navigation.AppContentRoute
import com.arygm.quickfix.ui.uiMode.workerMode.navigation.WorkerRoute
import com.arygm.quickfix.ui.uiMode.workerMode.navigation.WorkerScreen
import com.arygm.quickfix.ui.userModeUI.navigation.UserRoute
import com.arygm.quickfix.ui.userModeUI.navigation.UserScreen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

enum class AppMode(val route : String) {
  USER(AppContentRoute.USER_MODE),
  WORKER(AppContentRoute.WORKER_MODE);
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

  fun setonSwitchStartDestWorker(route: String) {
    _onSwitchStartDestWorker.value = route
  }

    fun setonSwitchStartDestUser(route: String) {
        _onSwitchStartDestUser.value = route
    }
}
