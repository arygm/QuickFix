package com.arygm.quickfix.ui.uiMode.appContentUI.userModeUI.profile.becomeWorker.views.welcome

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arygm.quickfix.R
import com.arygm.quickfix.model.offline.small.PreferencesViewModel
import com.arygm.quickfix.ressources.C
import com.arygm.quickfix.ui.elements.QuickFixButton
import com.arygm.quickfix.ui.navigation.NavigationActions
import com.arygm.quickfix.ui.theme.poppinsTypography
import com.arygm.quickfix.ui.uiMode.appContentUI.userModeUI.navigation.UserScreen
import com.arygm.quickfix.ui.uiMode.workerMode.navigation.WorkerScreen

@Composable
fun WelcomeOnBoardScreen(
    navigationActions: NavigationActions,
    preferencesViewModel: PreferencesViewModel,
    testingFlag: Boolean = false
) {
  BoxWithConstraints {
    val widthRatio = maxWidth / 411
    val heightRatio = maxHeight / 860
    val isWorker by preferencesViewModel.isWorkerFlow.collectAsState(initial = false)

    // If the user chooses "Switch Worker Mode," we set this to true and show a loader until
    // isWorker == true
    var isWaitingForWorkerMode by remember { mutableStateOf(false) }

    // When isWorker changes, if we're waiting for worker mode and it's now true, navigate
    LaunchedEffect(isWorker) {
      if (isWaitingForWorkerMode && isWorker) {
        navigationActions.navigateTo(WorkerScreen.PROFILE)
      }
    }
    if (isWaitingForWorkerMode) {
      Column(
          modifier = Modifier.fillMaxSize(),
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.Center) {
            androidx.compose.material3.CircularProgressIndicator(
                color = colorScheme.primary, modifier = Modifier.testTag("Loader"))
          }
    } else {
      Column(
          modifier = Modifier.fillMaxSize().padding(16.dp),
          horizontalAlignment = Alignment.CenterHorizontally,
      ) {
        Spacer(modifier = Modifier.height(100.dp * heightRatio.value))
        Text(
            "Welcome on board !!",
            style =
                poppinsTypography.headlineLarge.copy(
                    color = MaterialTheme.colorScheme.onBackground, fontSize = 26.sp))
        Spacer(modifier = Modifier.weight(0.2f))
        Row(modifier = Modifier.weight(0.9f)) {
          Image(
              painter = painterResource(id = R.drawable.onboarding_worker_1),
              contentDescription = "Description of the image",
              contentScale = ContentScale.Crop,
              modifier =
                  Modifier.fillMaxSize().semantics { testTag = C.Tag.welcomeOnBoardScreenImage })
        }
        Spacer(modifier = Modifier.weight(0.2f))

        // Show a loader while waiting for isWorker to become true

        Row(modifier = Modifier.fillMaxWidth().weight(0.8f)) {
          // "Stay User Mode" button: If isWorker is already false, we can navigate immediately
          // or just navigate directly without waiting since we don't need isWorker to change
          QuickFixButton(
              buttonText = "Stay User Mode",
              onClickAction = {
                if (!testingFlag) {
                  isWaitingForWorkerMode = true
                } else {
                  navigationActions.navigateTo(UserScreen.PROFILE)
                }
              },
              buttonColor = colorScheme.primary,
              textColor = colorScheme.onPrimary,
              textStyle =
                  poppinsTypography.headlineMedium.copy(
                      fontSize = 16.sp, fontWeight = FontWeight.SemiBold),
              modifier =
                  Modifier.weight(0.5f).semantics {
                    testTag = C.Tag.welcomeOnBoardScreenStayUserButton
                  })

          // "Switch Worker Mode": We wait for isWorker to become true
          QuickFixButton(
              buttonText = "Switch Worker Mode",
              onClickAction = {
                if (!testingFlag) {
                  isWaitingForWorkerMode = true
                } else {
                  navigationActions.navigateTo(UserScreen.PROFILE)
                }
              },
              buttonColor = colorScheme.onPrimary,
              textColor = colorScheme.primary,
              textStyle =
                  poppinsTypography.headlineMedium.copy(
                      fontSize = 16.sp, fontWeight = FontWeight.SemiBold),
              modifier =
                  Modifier.weight(0.5f).semantics {
                    testTag = C.Tag.welcomeOnBoardScreenSwitchWorkerButton
                  })
        }
      }
    }
  }
}
