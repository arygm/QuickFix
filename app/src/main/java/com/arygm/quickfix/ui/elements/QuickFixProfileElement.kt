package com.arygm.quickfix.ui.elements

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForwardIos
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.WorkOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
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
import com.arygm.quickfix.model.offline.small.PreferencesViewModelUserProfile
import com.arygm.quickfix.model.switchModes.AppMode
import com.arygm.quickfix.model.switchModes.ModeViewModel
import com.arygm.quickfix.ressources.C
import com.arygm.quickfix.ui.navigation.NavigationActions
import com.arygm.quickfix.ui.navigation.RootRoute
import com.arygm.quickfix.ui.theme.poppinsTypography
import com.arygm.quickfix.ui.uiMode.workerMode.navigation.WorkerRoute
import com.arygm.quickfix.ui.userModeUI.navigation.UserRoute
import com.arygm.quickfix.utils.clearPreferences
import com.arygm.quickfix.utils.clearUserProfilePreferences
import com.arygm.quickfix.utils.setAppMode
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickFixProfileScreenElement(
    modeNavigationActions: NavigationActions,
    navigationActions: NavigationActions,
    rootMainNavigationActions: NavigationActions,
    preferencesViewModel: PreferencesViewModel,
    userPreferencesViewModel: PreferencesViewModelUserProfile,
    appContentNavigationActions: NavigationActions,
    modeViewModel: ModeViewModel,
    initialState: Boolean,
    switchMode: AppMode,
    sections: List<@Composable (Modifier) -> Unit>, // Dynamic sections
) {
  val firstName by preferencesViewModel.firstName.collectAsState(initial = "")
  val lastName by preferencesViewModel.lastName.collectAsState(initial = "")
  val email by preferencesViewModel.email.collectAsState(initial = "")
  val wallet by userPreferencesViewModel.wallet.collectAsState(initial = "")
  val isWorker by preferencesViewModel.isWorkerFlow.collectAsState(initial = false)

  // Compute display name using the collected first and last names
  val displayName = capitalizeName(firstName, lastName)
  var isChecked by remember { mutableStateOf(initialState) } // State to track the switch state

  BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
    val screenWidth = maxWidth
    val screenHeight = maxHeight

    Scaffold(
        containerColor = colorScheme.background,
        topBar = {
          TopAppBar(
              title = {
                Column(
                    modifier =
                        Modifier.fillMaxWidth()
                            .padding(horizontal = screenWidth * 0.05f)
                            .testTag("ProfileTopAppBar"),
                    horizontalAlignment = Alignment.Start) {
                      Spacer(modifier = Modifier.height(screenHeight * 0.02f))
                      Text(
                          text = displayName,
                          style = typography.headlineMedium,
                          fontWeight = FontWeight.Bold,
                          fontSize = 32.sp,
                          color = colorScheme.onBackground,
                          modifier = Modifier.testTag("ProfileDisplayName"))
                      Text(
                          text = email,
                          style = typography.bodyMedium,
                          color = colorScheme.onSurface,
                          fontSize = 10.sp,
                          modifier = Modifier.testTag("ProfileEmail"))
                    }
              },
              modifier = Modifier.height(screenHeight * 0.15f),
              actions = {
                Box(
                    modifier =
                        Modifier.padding(end = screenWidth * 0.05f, top = screenHeight * 0.02f)
                            .size(screenWidth * 0.2f)
                            .testTag("ProfilePicture")) {
                      Image(
                          painter = painterResource(id = R.drawable.placeholder_worker),
                          contentDescription = "Profile Picture",
                          modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(50)),
                          contentScale = ContentScale.Crop)
                    }
              },
              colors = TopAppBarDefaults.topAppBarColors(containerColor = colorScheme.background))
        },
        content = { padding ->
          Column(
              modifier =
                  Modifier.fillMaxWidth()
                      .padding(padding)
                      .padding(horizontal = screenWidth * 0.05f)
                      .verticalScroll(rememberScrollState())
                      .testTag("ProfileContent")) {
                Spacer(modifier = Modifier.height(screenHeight * 0.01f))

                val cardCornerRadius = screenWidth * 0.04f
                val buttonCornerRadius = screenWidth * 0.06f
                val borderStrokeWidth = screenWidth * 0.003f

                Card(
                    modifier = Modifier.fillMaxWidth().testTag("BalanceCard"),
                    shape = RoundedCornerShape(cardCornerRadius),
                    colors = CardDefaults.cardColors(containerColor = colorScheme.primary)) {
                      Column(
                          modifier =
                              Modifier.padding(
                                  start = screenWidth * 0.03f,
                                  end = screenWidth * 0.01f,
                                  top = screenHeight * 0.01f,
                                  bottom = screenHeight * 0.02f),
                          horizontalAlignment = Alignment.Start) {
                            Text(
                                text = "Current balance",
                                style =
                                    poppinsTypography.headlineSmall.copy(
                                        fontWeight = FontWeight.SemiBold, fontSize = 16.sp),
                                color = colorScheme.onPrimary,
                                modifier = Modifier.padding(start = screenHeight * 0.003f))
                            Spacer(modifier = Modifier.height(screenHeight * 0.0001f))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                              Text(
                                  text = "CHF ${wallet}",
                                  style =
                                      poppinsTypography.headlineLarge.copy(
                                          fontWeight = FontWeight.Bold, fontSize = 35.sp),
                                  color = colorScheme.onPrimary,
                                  modifier = Modifier.weight(0.7f))
                              Row(
                                  modifier = Modifier.weight(0.3f),
                                  horizontalArrangement = Arrangement.End,
                              ) {
                                IconButton(
                                    onClick = { /* View transactions action */},
                                    colors =
                                        IconButtonColors(
                                            contentColor = colorScheme.onPrimary,
                                            containerColor = colorScheme.primary,
                                            disabledContentColor = colorScheme.onPrimary,
                                            disabledContainerColor = colorScheme.primary,
                                        ),
                                    modifier = Modifier.testTag("ViewTransactionsButton"),
                                ) {
                                  Icon(
                                      imageVector = Icons.AutoMirrored.Outlined.ArrowForwardIos,
                                      contentDescription = "Info Icon",
                                      tint = colorScheme.onPrimary,
                                      modifier = Modifier.size(screenWidth * 0.08f).scale(1.1f))
                                }
                              }
                            }
                            Spacer(modifier = Modifier.height(screenHeight * 0.0125f))
                            when (switchMode) {
                              AppMode.WORKER -> {
                                Row() {
                                  Box(
                                      modifier =
                                          Modifier.fillMaxWidth()
                                              .height(screenHeight * 0.04f)
                                              .clip(RoundedCornerShape(buttonCornerRadius))
                                              .testTag("AddFundsButton")
                                              .background(
                                                  color = colorScheme.onPrimary,
                                                  shape = RoundedCornerShape(buttonCornerRadius))
                                              .weight(0.35f),
                                  ) {
                                    Text(
                                        text = "+ Add funds",
                                        color = colorScheme.onBackground,
                                        style =
                                            poppinsTypography.headlineSmall.copy(
                                                fontWeight = FontWeight.SemiBold, fontSize = 16.sp),
                                        modifier = Modifier.clickable {}.align(Alignment.Center))
                                  }
                                  Spacer(modifier = Modifier.weight(0.65f))
                                }
                              }
                              AppMode.USER -> {
                                Row() {
                                  Box(
                                      modifier =
                                          Modifier.fillMaxWidth()
                                              .height(screenHeight * 0.04f)
                                              .clip(RoundedCornerShape(buttonCornerRadius))
                                              .testTag("WithdrawFundsButton")
                                              .background(
                                                  color = colorScheme.onPrimary,
                                                  shape = RoundedCornerShape(buttonCornerRadius))
                                              .weight(0.46f),
                                  ) {
                                    Text(
                                        text = "- Withdraw funds",
                                        color = colorScheme.onBackground,
                                        style =
                                            poppinsTypography.headlineSmall.copy(
                                                fontWeight = FontWeight.SemiBold, fontSize = 16.sp),
                                        modifier = Modifier.clickable {}.align(Alignment.Center))
                                  }
                                  Spacer(modifier = Modifier.weight(0.54f))
                                }
                              }
                            }
                          }
                    }

                Spacer(modifier = Modifier.height(screenHeight * 0.025f))
                if (isWorker) {
                  Box(
                      modifier =
                          Modifier.fillMaxWidth()
                              .height(screenHeight * 0.055f)
                              .semantics { testTag = C.Tag.workerModeSwitch }
                              .clip(RoundedCornerShape(cardCornerRadius))
                              .background(
                                  color = colorScheme.surface,
                              ),
                  ) {
                    Row(
                        modifier =
                            Modifier.fillMaxWidth()
                                .padding(horizontal = screenWidth * 0.02f)
                                .align(Alignment.CenterStart),
                        verticalAlignment = Alignment.CenterVertically) {
                          Icon(
                              imageVector =
                                  when (switchMode) {
                                    AppMode.USER -> Icons.Outlined.Person
                                    AppMode.WORKER -> Icons.Outlined.WorkOutline
                                  },
                              contentDescription = "worker Icon",
                              tint = colorScheme.tertiaryContainer,
                              modifier = Modifier.size(screenWidth * 0.06f))
                          Spacer(modifier = Modifier.width(screenWidth * 0.04f))
                          Text(
                              text =
                                  when (switchMode) {
                                    AppMode.USER -> "User Mode"
                                    AppMode.WORKER -> "Worker Mode"
                                  },
                              style =
                                  poppinsTypography.bodyMedium.copy(
                                      fontWeight = FontWeight.Medium, fontSize = 16.sp),
                              color = colorScheme.onBackground,
                              modifier =
                                  Modifier.weight(1f).semantics {
                                    testTag = C.Tag.workerModeSwitchText
                                  })
                          Switch(
                              checked = isChecked,
                              modifier = Modifier.testTag(C.Tag.buttonSwitch),
                              onCheckedChange = {
                                isChecked = it
                                when (switchMode) {
                                  AppMode.USER -> {
                                    modeViewModel.setonSwitchStartDestUser(
                                        UserRoute.PROFILE, modeNavigationActions)
                                  }
                                  AppMode.WORKER -> {
                                    modeViewModel.setonSwitchStartDestWorker(
                                        WorkerRoute.PROFILE, modeNavigationActions)
                                  }
                                }
                                setAppMode(preferencesViewModel, switchMode.name)
                                modeViewModel.switchMode(switchMode)

                                appContentNavigationActions.navigateTo(switchMode.route)
                              },
                              colors =
                                  SwitchDefaults.colors(
                                      checkedThumbColor = colorScheme.onPrimary,
                                      uncheckedThumbColor = colorScheme.onPrimary,
                                      checkedTrackColor = colorScheme.primary,
                                      uncheckedTrackColor = colorScheme.tertiaryContainer,
                                      uncheckedBorderColor = colorScheme.tertiaryContainer,
                                      checkedBorderColor = colorScheme.primary))
                        }
                  }
                }
                Spacer(modifier = Modifier.height(screenHeight * 0.025f))

                sections.forEach { section ->
                  section(Modifier.padding(vertical = 8.dp))
                  Spacer(modifier = Modifier.height(screenHeight * 0.025f))
                }

                Spacer(modifier = Modifier.height(screenHeight * 0.025f))

                Button(
                    onClick = {
                      modeViewModel.setonSwitchStartDestUser(UserRoute.HOME, modeNavigationActions)
                      Log.d("QuickFixProfileElement", modeViewModel.onSwitchStartDestUser.value)
                      setAppMode(preferencesViewModel, AppMode.USER.name)
                      clearPreferences(preferencesViewModel)
                      clearUserProfilePreferences(userPreferencesViewModel)
                      rootMainNavigationActions.navigateTo(RootRoute.NO_MODE)
                      Log.d("user", Firebase.auth.currentUser.toString())
                    },
                    shape = RoundedCornerShape(screenWidth * 0.04f),
                    border = BorderStroke(borderStrokeWidth, colorScheme.onBackground),
                    colors = ButtonDefaults.buttonColors(containerColor = colorScheme.onPrimary),
                    modifier = Modifier.testTag("LogoutButton")) {
                      Text(
                          text = "Log out",
                          color = colorScheme.onBackground,
                          style =
                              poppinsTypography.bodyMedium.copy(
                                  fontWeight = FontWeight.Medium, fontSize = 16.sp),
                          modifier = Modifier.testTag("LogoutText"))
                    }
              }
        })
  }
}

private fun capitalizeName(firstName: String?, lastName: String?): String {
  val capitalizedFirstName = firstName?.lowercase()?.replaceFirstChar { it.uppercase() } ?: ""
  val capitalizedLastName = lastName?.lowercase()?.replaceFirstChar { it.uppercase() } ?: ""
  return "$capitalizedFirstName $capitalizedLastName".trim()
}
