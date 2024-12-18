package com.arygm.quickfix.ui.uiMode.appContentUI.userModeUI.quickfix

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.PendingActions
import androidx.compose.material.icons.outlined.Sms
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arygm.quickfix.model.account.AccountViewModel
import com.arygm.quickfix.model.category.CategoryViewModel
import com.arygm.quickfix.model.locations.LocationViewModel
import com.arygm.quickfix.model.messaging.ChatViewModel
import com.arygm.quickfix.model.offline.small.PreferencesViewModel
import com.arygm.quickfix.model.profile.ProfileViewModel
import com.arygm.quickfix.model.quickfix.QuickFixViewModel
import com.arygm.quickfix.model.switchModes.ModeViewModel
import com.arygm.quickfix.ui.elements.QuickFixStepper
import com.arygm.quickfix.ui.navigation.NavigationActions
import kotlinx.coroutines.launch

@Composable
fun QuickFixOnBoarding(
    navigationActionsRoot: NavigationActions,
    navigationActions: NavigationActions,
    modeViewModel: ModeViewModel,
    quickFixViewModel: QuickFixViewModel,
    preferencesViewModel: PreferencesViewModel,
    userViewModel: ProfileViewModel,
    workerViewModel: ProfileViewModel,
    locationViewModel: LocationViewModel,
    accountViewModel: AccountViewModel,
    chatViewModel: ChatViewModel,
    categoryViewModel: CategoryViewModel
) {
  BackHandler { navigationActions.goBack() }
  val workerProfile by quickFixViewModel.selectedWorkerProfile.collectAsState()
  val quickFix by quickFixViewModel.currentQuickFix.collectAsState()
  val mode by modeViewModel.currentMode.collectAsState()
  val coroutineScope = rememberCoroutineScope()
  val pagerState =
      rememberPagerState(
          initialPage = if (quickFix.title.isEmpty()) 0 else quickFix.status.ordinal + 1,
          pageCount = { 4 })

  BoxWithConstraints {
    val widthRatio = maxWidth / 411
    val heightRatio = maxHeight / 860

    Scaffold(
        topBar = {
          Spacer(Modifier.height(30.dp * heightRatio.value))
          val steps =
              listOf("Service Information", "Contact Worker", "Facturation", "QuickFix on Process")
          val icons =
              listOf(
                  Icons.Outlined.Info,
                  Icons.Outlined.Sms,
                  Icons.AutoMirrored.Outlined.ReceiptLong,
                  Icons.Outlined.PendingActions) // Example icons
          QuickFixStepper(
              steps = steps,
              icons = icons,
              currentStep = pagerState.currentPage + 1,
              heightRatio = heightRatio,
              widthRatio = widthRatio,
              coroutineScope = coroutineScope)
        },
        content = { innerPadding ->
          HorizontalPager(
              state = pagerState,
              modifier = Modifier.padding(innerPadding),
              beyondViewportPageCount = 0,
              userScrollEnabled = false,
          ) {
            when (it) {
              0 -> {
                QuickFixFirstStep(
                    workerProfile = workerProfile,
                    quickFixViewModel = quickFixViewModel,
                    chatViewModel = chatViewModel,
                    preferencesViewModel = preferencesViewModel,
                    onQuickFixChange = { updatedQuickFix ->
                      quickFixViewModel.setUpdateQuickFix(updatedQuickFix)
                      coroutineScope.launch { pagerState.animateScrollToPage(1) }
                    },
                    userViewModel = userViewModel,
                    workerViewModel = workerViewModel,
                    locationViewModel = locationViewModel,
                    navigationActions = navigationActions,
                )
              }
              1 -> {
                BackHandler(enabled = true) { navigationActionsRoot.goBack() }
                QuickFixSecondStep(
                    quickFix = quickFix,
                    mode = mode,
                    onQuickFixMakeBill = {},
                    navigationActions = navigationActions,
                    chatViewModel = chatViewModel,
                    quickFixViewModel = quickFixViewModel,
                    accountViewModel = accountViewModel,
                    navigationActionsRoot = navigationActionsRoot)
              }
              2 -> {
                QuickFixThirdStep(
                    quickFix = quickFix,
                    quickFixViewModel = quickFixViewModel,
                    workerProfile = workerProfile,
                    onQuickFixChange = { updatedQuickFix ->
                      quickFixViewModel.setUpdateQuickFix(updatedQuickFix)
                    },
                    onQuickFixPay = { updatedQuickFix ->
                      quickFixViewModel.setUpdateQuickFix(updatedQuickFix)
                      coroutineScope.launch { pagerState.animateScrollToPage(3) }
                    },
                    mode = mode)
              }
              3 -> {
                QuickFixLastStep(
                    quickFix = quickFix,
                    workerProfile = workerProfile,
                    onQuickFixChange = { updatedQuickFix ->
                      quickFixViewModel.setUpdateQuickFix(updatedQuickFix)
                      coroutineScope.launch { pagerState.animateScrollToPage(4) }
                    },
                    mode = mode,
                    quickFixViewModel = quickFixViewModel,
                    workerViewModel = workerViewModel,
                    categoryViewModel = categoryViewModel,
                    navigationActionsRoot = navigationActionsRoot)
              }
              4 -> {
                QuickFixLastStep(
                    quickFix = quickFix,
                    workerProfile = workerProfile,
                    onQuickFixChange = { updatedQuickFix ->
                      quickFixViewModel.setUpdateQuickFix(updatedQuickFix)
                    },
                    mode = mode,
                    quickFixViewModel = quickFixViewModel,
                    workerViewModel = workerViewModel,
                    categoryViewModel = categoryViewModel,
                    navigationActionsRoot = navigationActionsRoot)
              }
            }
          }
        },
        containerColor = colorScheme.surface)
  }
}
