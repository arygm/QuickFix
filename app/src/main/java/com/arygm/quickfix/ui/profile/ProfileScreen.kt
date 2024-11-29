package com.arygm.quickfix.ui.profile

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.MaterialTheme
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

  Scaffold(
      containerColor = MaterialTheme.colorScheme.background,
      topBar = {
        TopAppBar(
            title = {
              Column(
                  modifier =
                      Modifier.fillMaxWidth()
                          .padding(horizontal = 16.dp)
                          .testTag("ProfileTopAppBar"),
                  horizontalAlignment = Alignment.Start) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = displayName,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        fontSize = 32.sp,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.testTag("ProfileDisplayName"))
                    Text(
                        text = email,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 10.sp,
                        modifier = Modifier.testTag("ProfileEmail"))
                  }
            },
            modifier = Modifier.height(100.dp),
            actions = {
              Box(
                  modifier =
                      Modifier.padding(end = 16.dp, top = 16.dp)
                          .size(72.dp)
                          .testTag("ProfilePicture")) {
                    Image(
                        painter = painterResource(id = R.drawable.placeholder_worker),
                        contentDescription = "Profile Picture",
                        modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(50)),
                        contentScale = ContentScale.Crop)
                  }
            },
            colors =
                TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background))
      },
      content = { padding ->
        Column(
            modifier =
                Modifier.fillMaxWidth()
                    .padding(padding)
                    .padding(horizontal = 16.dp)
                    .verticalScroll(rememberScrollState())
                    .testTag("ProfileContent")) {
              Spacer(modifier = Modifier.height(26.dp))

              Card(
                  modifier = Modifier.fillMaxWidth().testTag("BalanceCard"),
                  shape = RoundedCornerShape(16.dp),
                  colors =
                      CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)) {
                    Column(
                        modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.Start) {
                          Text(
                              text = "Current balance",
                              style = MaterialTheme.typography.bodyMedium,
                              color = MaterialTheme.colorScheme.onPrimary)
                          Spacer(modifier = Modifier.height(8.dp))
                          Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "$wallet",
                                style = MaterialTheme.typography.headlineLarge,
                                color = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.weight(1f))
                            Button(
                                onClick = { /* View transactions action */},
                                colors = ButtonDefaults.buttonColors(),
                                modifier = Modifier.testTag("ViewTransactionsButton")) {
                                  Icon(
                                      imageVector = Icons.AutoMirrored.Outlined.ArrowForwardIos,
                                      contentDescription = "Info Icon",
                                      tint = MaterialTheme.colorScheme.onPrimary,
                                      modifier = Modifier.size(32.dp))
                                }
                          }
                          Spacer(modifier = Modifier.height(12.dp))
                          Button(
                              onClick = { /* Add funds action */},
                              colors =
                                  ButtonDefaults.buttonColors(
                                      containerColor = MaterialTheme.colorScheme.onPrimary),
                              modifier = Modifier.testTag("AddFundsButton")) {
                                Text(
                                    text = "+ Add funds",
                                    color = MaterialTheme.colorScheme.onBackground,
                                    style =
                                        MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = FontWeight.Bold),
                                )
                              }
                        }
                  }

              Spacer(modifier = Modifier.height(16.dp))

              Text(
                  text = "Personal Settings",
                  style = MaterialTheme.typography.titleMedium,
                  color = MaterialTheme.colorScheme.onBackground,
                  modifier = Modifier.testTag("PersonalSettingsHeader"))
              Spacer(modifier = Modifier.height(8.dp))

              Card(
                  modifier = Modifier.fillMaxWidth().testTag("PersonalSettingsCard"),
                  shape = RoundedCornerShape(16.dp),
                  colors =
                      CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                    Column() {
                      SettingsItem(
                          icon = Icons.Outlined.Person,
                          label = "My Account",
                          testTag = "AccountconfigurationOption") {
                            navigationActions.navigateTo(Screen.ACCOUNT_CONFIGURATION)
                          }
                      HorizontalDivider(color = MaterialTheme.colorScheme.onSurface)
                      SettingsItem(
                          icon = Icons.Outlined.Settings,
                          label = "Preferences",
                          testTag = "") { /* Action */}
                      HorizontalDivider(color = MaterialTheme.colorScheme.onSurface)
                      SettingsItem(
                          icon = Icons.Outlined.FavoriteBorder,
                          label = "Saved lists",
                          testTag = "") { /* Action */}
                    }
                  }

              Spacer(modifier = Modifier.height(16.dp))

              Text(
                  text = "Resources",
                  style = MaterialTheme.typography.titleMedium,
                  color = MaterialTheme.colorScheme.onBackground,
                  modifier = Modifier.testTag("ResourcesHeader"))
              Spacer(modifier = Modifier.height(8.dp))

              Card(
                  modifier = Modifier.fillMaxWidth().testTag("ResourcesCard"),
                  shape = RoundedCornerShape(16.dp),
                  colors =
                      CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                    Column(Modifier.testTag("SetupyourbusinessaccountOption")) {
                      SettingsItem(
                          icon = Icons.AutoMirrored.Outlined.HelpOutline,
                          label = "Support",
                          testTag = "") { /* Action */}
                      HorizontalDivider(color = MaterialTheme.colorScheme.onSurface)
                      SettingsItem(
                          icon = Icons.Outlined.Info, label = "Legal", testTag = "") { /* Action */}
                      HorizontalDivider(color = MaterialTheme.colorScheme.onSurface)
                      SettingsItem(
                          icon = Icons.Outlined.WorkOutline,
                          label = "Become a Worker",
                          testTag = "SetupyourbusinessaccountOption") {
                            navigationActions.navigateTo(Screen.TO_WORKER)
                          }
                    }
                  }

              Spacer(modifier = Modifier.height(16.dp))

              Button(
                  onClick = {
                    loggedInAccountViewModel.logOut(Firebase.auth)
                    navigationActionsRoot.navigateTo(TopLevelDestinations.WELCOME)
                    Log.d("user", Firebase.auth.currentUser.toString())
                  },
                  shape = RoundedCornerShape(10.dp),
                  border = BorderStroke(1.dp, MaterialTheme.colorScheme.onBackground),
                  colors =
                      ButtonDefaults.buttonColors(
                          containerColor = MaterialTheme.colorScheme.onPrimary),
                  modifier = Modifier.testTag("LogoutButton")) {
                    Text(
                        text = "Log out",
                        color = MaterialTheme.colorScheme.onBackground,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.testTag("LogoutText"))
                  }
            }
      })
}

@Composable
fun SettingsItem(
    icon: ImageVector,
    label: String,
    testTag: String,
    onClick: () -> Unit,
) {
  Card(
      modifier = Modifier.fillMaxWidth().testTag(testTag),
      shape = RoundedCornerShape(0.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
      onClick = onClick) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically) {
              Icon(
                  imageVector = icon,
                  contentDescription = "$label Icon",
                  tint = MaterialTheme.colorScheme.onSurface,
                  modifier = Modifier.size(24.dp))
              Spacer(modifier = Modifier.width(16.dp))
              Text(
                  text = label,
                  style = MaterialTheme.typography.bodyLarge,
                  color = MaterialTheme.colorScheme.onBackground,
                  modifier = Modifier.weight(1f).testTag(label.replace(" ", "") + "Text"))
              Icon(
                  imageVector = Icons.AutoMirrored.Outlined.ArrowForwardIos,
                  contentDescription = "Forward Icon",
                  tint = MaterialTheme.colorScheme.onSurface,
                  modifier = Modifier.size(16.dp))
            }
      }
}

private fun capitalizeName(firstName: String?, lastName: String?): String {
  val capitalizedFirstName = firstName?.lowercase()?.replaceFirstChar { it.uppercase() } ?: ""
  val capitalizedLastName = lastName?.lowercase()?.replaceFirstChar { it.uppercase() } ?: ""
  return "$capitalizedFirstName $capitalizedLastName".trim()
}
