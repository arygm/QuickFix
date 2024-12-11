package com.arygm.quickfix.ui.profile.becomeWorker.views.welcome

import androidx.compose.foundation.Image
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arygm.quickfix.R
import com.arygm.quickfix.ressources.C
import com.arygm.quickfix.ui.elements.QuickFixButton
import com.arygm.quickfix.ui.navigation.NavigationActions
import com.arygm.quickfix.ui.theme.poppinsTypography
import com.arygm.quickfix.ui.userModeUI.navigation.UserScreen

@Composable
fun WelcomeOnBoardScreen(navigationActions: NavigationActions) {
  BoxWithConstraints {
    val widthRatio = maxWidth / 411
    val heightRatio = maxHeight / 860
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
            painter =
                painterResource(
                    id =
                        R.drawable
                            .onboarding_worker_1), // Replace 'my_image' with your PNG file name
            contentDescription = "Description of the image", // Accessibility description
            contentScale = ContentScale.Crop,
            modifier =
                Modifier.fillMaxSize().semantics {
                  testTag = C.Tag.welcomeOnBoardScreenImage
                } // Optional: Set how the image should scale
            )
      }
      Spacer(modifier = Modifier.weight(0.2f))
      Row(modifier = Modifier.fillMaxWidth().weight(0.8f)) {
        QuickFixButton(
            buttonText = "Stay User Mode",
            onClickAction = { navigationActions.navigateTo(UserScreen.PROFILE) },
            buttonColor = colorScheme.primary,
            textColor = colorScheme.onPrimary,
            textStyle =
                poppinsTypography.headlineMedium.copy(
                    fontSize = 16.sp, fontWeight = FontWeight.SemiBold),
            modifier =
                Modifier.weight(0.5f).semantics {
                  testTag = C.Tag.welcomeOnBoardScreenStayUserButton
                })
        QuickFixButton(
            buttonText = "Switch Worker Mode",
            onClickAction = { navigationActions.navigateTo(UserScreen.PROFILE) },
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
