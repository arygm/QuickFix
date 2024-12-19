package com.arygm.quickfix.ui.uiMode.appContentUI.workerMode.quickfix

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.arygm.quickfix.model.quickfix.QuickFixViewModel
import com.arygm.quickfix.model.switchModes.AppMode
import com.arygm.quickfix.ui.navigation.NavigationActions
import com.arygm.quickfix.ui.uiMode.appContentUI.userModeUI.quickfix.QuickFixThirdStep
import com.arygm.quickfix.ui.uiMode.workerMode.navigation.WorkerScreen

@Composable
fun QuickFixBilling(quickFixViewModel: QuickFixViewModel, navigationActions: NavigationActions) {
  val workerProfile by quickFixViewModel.selectedWorkerProfile.collectAsState()

  QuickFixThirdStep(
      quickFixViewModel = quickFixViewModel,
      workerProfile = workerProfile,
      onQuickFixChange = {
        quickFixViewModel.setUpdateQuickFix(it)
        navigationActions.navigateTo(WorkerScreen.QUIKFIX_ONBOARDING)
      },
      onQuickFixPay = {},
      mode = AppMode.WORKER,
  )
}
