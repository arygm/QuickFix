package com.arygm.quickfix.ui.profile

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.WorkOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arygm.quickfix.R
import com.arygm.quickfix.model.account.LoggedInAccountViewModel
import com.arygm.quickfix.ui.navigation.NavigationActions
import com.arygm.quickfix.ui.navigation.Screen
import com.arygm.quickfix.ui.navigation.TopLevelDestinations
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navigationActions: NavigationActions,
    loggedInAccountViewModel: LoggedInAccountViewModel,
    navigationActionsRoot: NavigationActions,
) {
  val loggedInAccount by loggedInAccountViewModel.loggedInAccount.collectAsState()
  val loggedInProfile by loggedInAccountViewModel.userProfile.collectAsState()
  val displayName =
      loggedInAccount?.let { capitalizeName(it.firstName, it.lastName) } ?: "Loading..."
  val email = loggedInAccount?.email ?: "Loading..."
  val wallet = loggedInProfile?.wallet ?: "Loading..."

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
                Spacer(modifier = Modifier.height(screenHeight * 0.04f))

                val cardCornerRadius = screenWidth * 0.04f
                val buttonCornerRadius = screenWidth * 0.06f
                val borderStrokeWidth = screenWidth * 0.003f

                Card(
                    modifier = Modifier.fillMaxWidth().testTag("BalanceCard"),
                    shape = RoundedCornerShape(cardCornerRadius),
                    colors = CardDefaults.cardColors(containerColor = colorScheme.primary)) {
                      Column(
                          modifier = Modifier.padding(screenWidth * 0.04f),
                          horizontalAlignment = Alignment.Start) {
                            Text(
                                text = "Current balance",
                                style = typography.bodyMedium,
                                color = colorScheme.onPrimary)
                            Spacer(modifier = Modifier.height(screenHeight * 0.0075f))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                              Text(
                                  text = "$wallet",
                                  style = typography.headlineLarge,
                                  color = colorScheme.onPrimary,
                                  modifier = Modifier.weight(1f))
                              Button(
                                  onClick = { /* View transactions action */},
                                  colors = ButtonDefaults.buttonColors(),
                                  modifier = Modifier.testTag("ViewTransactionsButton"),
                                  shape = RoundedCornerShape(buttonCornerRadius)) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Outlined.ArrowForwardIos,
                                        contentDescription = "Info Icon",
                                        tint = colorScheme.onPrimary,
                                        modifier = Modifier.size(screenWidth * 0.08f))
                                  }
                            }
                            Spacer(modifier = Modifier.height(screenHeight * 0.0125f))
                            Button(
                                onClick = { /* Add funds action */},
                                colors =
                                    ButtonDefaults.buttonColors(
                                        containerColor = colorScheme.onPrimary),
                                modifier = Modifier.testTag("AddFundsButton"),
                                shape = RoundedCornerShape(buttonCornerRadius)) {
                                  Text(
                                      text = "+ Add funds",
                                      color = colorScheme.onBackground,
                                      style =
                                          typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                  )
                                }
                          }
                    }

                Spacer(modifier = Modifier.height(screenHeight * 0.025f))

                Text(
                    text = "Personal Settings",
                    style = typography.titleMedium,
                    color = colorScheme.onBackground,
                    modifier = Modifier.testTag("PersonalSettingsHeader"))
                Spacer(modifier = Modifier.height(screenHeight * 0.0125f))

                Card(
                    modifier = Modifier.fillMaxWidth().testTag("PersonalSettingsCard"),
                    shape = RoundedCornerShape(cardCornerRadius),
                    colors = CardDefaults.cardColors(containerColor = colorScheme.surface)) {
                      Column {
                        SettingsItem(
                            icon = Icons.Outlined.Person,
                            label = "My Account",
                            testTag = "AccountconfigurationOption",
                            screenWidth = screenWidth,
                        ) {
                          navigationActions.navigateTo(Screen.ACCOUNT_CONFIGURATION)
                        }
                        HorizontalDivider(color = colorScheme.onSurface)
                        SettingsItem(
                            icon = Icons.Outlined.Settings,
                            label = "Preferences",
                            testTag = "Preferences",
                            screenWidth = screenWidth,
                        ) { /* Action */}
                        HorizontalDivider(color = colorScheme.onSurface)
                        SettingsItem(
                            icon = Icons.Outlined.FavoriteBorder,
                            label = "Saved lists",
                            testTag = "SavedLists",
                            screenWidth = screenWidth,
                        ) { /* Action */}
                      }
                    }

                Spacer(modifier = Modifier.height(screenHeight * 0.025f))

                Text(
                    text = "Resources",
                    style = typography.titleMedium,
                    color = colorScheme.onBackground,
                    modifier = Modifier.testTag("ResourcesHeader"))
                Spacer(modifier = Modifier.height(screenHeight * 0.0125f))

                Card(
                    modifier = Modifier.fillMaxWidth().testTag("ResourcesCard"),
                    shape = RoundedCornerShape(cardCornerRadius),
                    colors = CardDefaults.cardColors(containerColor = colorScheme.surface)) {
                      Column {
                        SettingsItem(
                            icon = Icons.AutoMirrored.Outlined.HelpOutline,
                            label = "Support",
                            testTag = "Support",
                            screenWidth = screenWidth,
                        ) { /* Action */}
                        HorizontalDivider(color = colorScheme.onSurface)
                        SettingsItem(
                            icon = Icons.Outlined.Info,
                            label = "Legal",
                            testTag = "Legal",
                            screenWidth = screenWidth,
                        ) { /* Action */}
                        HorizontalDivider(color = colorScheme.onSurface)
                        SettingsItem(
                            icon = Icons.Outlined.WorkOutline,
                            label = "Become a Worker",
                            testTag = "SetupyourbusinessaccountOption",
                            screenWidth = screenWidth,
                        ) {
                          navigationActions.navigateTo(Screen.TO_WORKER)
                        }
                      }
                    }

                Spacer(modifier = Modifier.height(screenHeight * 0.025f))

                Button(
                    onClick = {
                      loggedInAccountViewModel.logOut(Firebase.auth)
                      navigationActionsRoot.navigateTo(TopLevelDestinations.WELCOME)
                      Log.d("user", Firebase.auth.currentUser.toString())
                    },
                    shape = RoundedCornerShape(buttonCornerRadius),
                    border = BorderStroke(borderStrokeWidth, colorScheme.onBackground),
                    colors = ButtonDefaults.buttonColors(containerColor = colorScheme.onPrimary),
                    modifier = Modifier.testTag("LogoutButton")) {
                      Text(
                          text = "Log out",
                          color = colorScheme.onBackground,
                          style = typography.titleMedium,
                          modifier = Modifier.testTag("LogoutText"))
                    }
              }
        })
  }
}

@Composable
fun SettingsItem(
    icon: ImageVector,
    label: String,
    testTag: String,
    screenWidth: Dp,
    onClick: () -> Unit,
) {
  Card(
      modifier = Modifier.fillMaxWidth().testTag(testTag),
      shape = RoundedCornerShape(0.dp),
      colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
      onClick = onClick) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = screenWidth * 0.02f),
            verticalAlignment = Alignment.CenterVertically) {
              Icon(
                  imageVector = icon,
                  contentDescription = "$label Icon",
                  tint = colorScheme.onSurface,
                  modifier = Modifier.size(screenWidth * 0.06f))
              Spacer(modifier = Modifier.width(screenWidth * 0.04f))
              Text(
                  text = label,
                  style = typography.bodyLarge,
                  color = colorScheme.onBackground,
                  modifier = Modifier.weight(1f).testTag(label.replace(" ", "") + "Text"))
              Icon(
                  imageVector = Icons.AutoMirrored.Outlined.ArrowForwardIos,
                  contentDescription = "Forward Icon",
                  tint = colorScheme.onSurface,
                  modifier = Modifier.size(screenWidth * 0.04f))
            }
      }
}

private fun capitalizeName(firstName: String?, lastName: String?): String {
  val capitalizedFirstName = firstName?.lowercase()?.replaceFirstChar { it.uppercase() } ?: ""
  val capitalizedLastName = lastName?.lowercase()?.replaceFirstChar { it.uppercase() } ?: ""
  return "$capitalizedFirstName $capitalizedLastName".trim()
}
