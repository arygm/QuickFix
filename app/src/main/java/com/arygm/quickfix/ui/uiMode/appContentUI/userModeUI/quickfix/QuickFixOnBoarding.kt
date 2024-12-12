package com.arygm.quickfix.ui.quickfix

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ReceiptLong
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.PendingActions
import androidx.compose.material.icons.outlined.Sms
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arygm.quickfix.model.profile.WorkerProfile
import com.arygm.quickfix.model.quickfix.QuickFix
import com.arygm.quickfix.ui.elements.QuickFixStepper
import com.arygm.quickfix.ui.navigation.NavigationActions

@Composable
fun QuickFixOnBoarding(
    workerId: String = "",
    workerProfile: WorkerProfile = WorkerProfile(),
    navigationActions: NavigationActions,
) {
  var quickFix by remember { mutableStateOf(QuickFix()) }
  var step by remember { mutableIntStateOf(0) }

  BoxWithConstraints {
    val widthRatio = maxWidth / 411
    val heightRatio = maxHeight / 860

    Scaffold(
        topBar = {
          Spacer(Modifier.height(30.dp))
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
              currentStep = step, // Highlight the second step as the current one
              heightRatio = heightRatio,
              widthRatio = widthRatio,
          )
        },
        content = { innerPadding ->
          HorizontalPager(
              state = rememberPagerState(initialPage = step, pageCount = { 4 }),
              modifier = Modifier.padding(innerPadding),
              beyondViewportPageCount = 1,
              userScrollEnabled = true,
          ) {
            when (it) {
              0 -> {
                step = 0
                QuickFixFirstStep(
                    workerId = workerId,
                    navigationActions = navigationActions,
                    onQuickFixChange = { updatedQuickFix -> quickFix = updatedQuickFix })
              }
              1 -> {
                step = 1
                QuickFixSecondStep(quickFix = quickFix)
              }
              2 -> {
                step = 2
                QuickFixThirdStep(
                    quickFix = quickFix,
                    workerProfile = workerProfile,
                    onQuickFixChange = { updatedQuickFix -> quickFix = updatedQuickFix })
              }
              3 -> {
                step = 3
                QuickFixLastStep(
                    quickFix = quickFix,
                    workerProfile = workerProfile,
                    onQuickFixChange = { updatedQuickFix -> quickFix = updatedQuickFix })
              }
            }
          }
        },
        containerColor = colorScheme.surface)
  }
}
