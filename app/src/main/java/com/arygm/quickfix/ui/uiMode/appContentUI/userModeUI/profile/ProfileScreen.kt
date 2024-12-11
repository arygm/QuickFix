package com.arygm.quickfix.ui.profile

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.arygm.quickfix.R
import com.arygm.quickfix.model.offline.small.PreferencesViewModel
import com.arygm.quickfix.ui.navigation.NavigationActions
import com.arygm.quickfix.ui.navigation.RootRoute
import com.arygm.quickfix.ui.navigation.TopLevelDestination
import com.arygm.quickfix.ui.userModeUI.navigation.UserScreen
import com.arygm.quickfix.ui.userModeUI.navigation.UserTopLevelDestinations
import com.arygm.quickfix.utils.clearAccountPreferences
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navigationActions: NavigationActions,
    rootMainNavigationActions: NavigationActions,
    preferencesViewModel: PreferencesViewModel,
) {

  // List of options handled by the profile screen
  val options =
      listOf(
          OptionItem("Settings", IconType.Vector(Icons.Outlined.Settings)) {},
          OptionItem("Activity", IconType.Resource(R.drawable.dashboardvector)) {},
          OptionItem("Set up your business account", IconType.Resource(R.drawable.workvector)) {
            navigationActions.navigateTo(UserScreen.TO_WORKER)
          },
          OptionItem("Account configuration", IconType.Resource(R.drawable.accountsettingsvector)) {
            navigationActions.navigateTo(UserScreen.ACCOUNT_CONFIGURATION)
          },
          OptionItem("Workers network", IconType.Vector(Icons.Outlined.Phone)) {},
          OptionItem("Legal", IconType.Vector(Icons.Outlined.Info)) {})

  Scaffold(
      containerColor = colorScheme.background,
      topBar = {
        TopAppBar(
            title = {
              Column(modifier = Modifier.fillMaxWidth().padding(8.dp).testTag("ProfileTopAppBar")) {
                // "Profile" Title
                Text(
                    text = "Profile",
                    color = colorScheme.primary,
                    style = MaterialTheme.typography.headlineLarge,
                    modifier = Modifier.padding(bottom = 8.dp).testTag("ProfileTitle"))

                // Profile Card
                Card(
                    modifier =
                        Modifier.fillMaxWidth(0.85f)
                            .align(Alignment.CenterHorizontally)
                            .testTag("ProfileCard"),
                    shape = RoundedCornerShape(16.dp),
                    colors =
                        CardDefaults.cardColors(
                            containerColor = colorScheme.surface,
                            contentColor = colorScheme.onSurface),
                    elevation = CardDefaults.cardElevation(4.dp)) {
                      Row(
                          verticalAlignment = Alignment.CenterVertically,
                          modifier = Modifier.padding(7.dp)) {
                            Icon(
                                painter = painterResource(R.drawable.profilevector),
                                contentDescription = "Profile Icon",
                                tint = colorScheme.primary,
                                modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(65.dp))

                            val displayName = remember { mutableStateOf("Loading...") }

                            LaunchedEffect(Unit) {
                              preferencesViewModel.loadPreference(
                                  key = com.arygm.quickfix.utils.FIRST_NAME_KEY) { firstName ->
                                    Log.d("user", "First name: $firstName")
                                    preferencesViewModel.loadPreference(
                                        key = com.arygm.quickfix.utils.LAST_NAME_KEY) { lastName ->
                                          Log.d("user", "Last name: $lastName")
                                          displayName.value = capitalizeName(firstName, lastName)
                                        }
                                  }
                            }
                            Text(
                                text = displayName.value,
                                style = MaterialTheme.typography.bodyLarge,
                                color = colorScheme.onBackground,
                                modifier = Modifier.testTag("ProfileName"))
                          }
                    }
              }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = colorScheme.background),
            modifier = Modifier.height(110.dp))
      },
      content = { padding ->
        Column(
            modifier =
                Modifier.fillMaxWidth()
                    .padding(
                        top = padding.calculateTopPadding(),
                        bottom = padding.calculateBottomPadding())
                    .padding(horizontal = 16.dp)
                    .verticalScroll(rememberScrollState())
                    .testTag("ProfileContent")) {
              Column(modifier = Modifier.fillMaxWidth().padding(bottom = 0.dp)) {
                // Upcoming Activities Placeholder
                Card(
                    modifier =
                        Modifier.fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .testTag("UpcomingActivitiesCard"),
                    shape = RoundedCornerShape(8.dp),
                    colors =
                        CardDefaults.cardColors(
                            containerColor = colorScheme.surface,
                            contentColor = colorScheme.onBackground),
                    elevation = CardDefaults.cardElevation(4.dp)) {
                      Text(
                          text =
                              "This isn’t developed yet; but it can display upcoming activities for both a user and worker",
                          modifier = Modifier.padding(16.dp).testTag("UpcomingActivitiesText"),
                          style = MaterialTheme.typography.bodyMedium)
                    }

                // Wallet and Help Row
                Row(
                    modifier =
                        Modifier.fillMaxWidth().padding(vertical = 8.dp).testTag("WalletHelpRow"),
                    horizontalArrangement = Arrangement.SpaceBetween) {
                      Button(
                          onClick = { /* Wallet click action */},
                          modifier = Modifier.weight(1f).height(80.dp).testTag("WalletButton"),
                          colors =
                              ButtonDefaults.buttonColors(containerColor = colorScheme.surface),
                          shape = RoundedCornerShape(8.dp),
                          elevation = ButtonDefaults.buttonElevation(4.dp)) {
                            Column(
                                horizontalAlignment = Alignment.Start,
                                verticalArrangement = Arrangement.Center,
                                modifier = Modifier.fillMaxWidth()) {
                                  Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        painter = painterResource(R.drawable.wallletvector),
                                        contentDescription = "Wallet Icon",
                                        modifier = Modifier.size(30.dp),
                                        tint = colorScheme.primary)
                                    Spacer(modifier = Modifier.width(35.dp))
                                    Column {
                                      Text(
                                          text = "Wallet",
                                          color = colorScheme.onBackground,
                                          style = MaterialTheme.typography.bodyLarge,
                                          modifier = Modifier.testTag("WalletText"))
                                      Text(
                                          text = "___ CHF",
                                          color = colorScheme.onBackground,
                                          style = MaterialTheme.typography.bodyMedium,
                                          modifier = Modifier.testTag("WalletAmountText"))
                                    }
                                  }
                                }
                          }

                      Spacer(modifier = Modifier.width(16.dp))

                      // Help Button
                      Button(
                          onClick = { /* Help click action */},
                          modifier = Modifier.weight(1f).height(80.dp).testTag("HelpButton"),
                          colors =
                              ButtonDefaults.buttonColors(containerColor = colorScheme.surface),
                          shape = RoundedCornerShape(8.dp),
                          elevation = ButtonDefaults.buttonElevation(4.dp)) {
                            Column(
                                horizontalAlignment = Alignment.Start,
                                verticalArrangement = Arrangement.Center,
                                modifier = Modifier.fillMaxWidth()) {
                                  Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        painter = painterResource(R.drawable.helpvector),
                                        contentDescription = "Help Icon",
                                        modifier = Modifier.size(30.dp),
                                        tint = colorScheme.primary)
                                    Spacer(modifier = Modifier.width(35.dp))
                                    Text(
                                        text = "Help",
                                        color = colorScheme.onBackground,
                                        style = MaterialTheme.typography.bodyLarge,
                                        modifier = Modifier.testTag("HelpText"))
                                  }
                                }
                          }
                    }
              }

              Column(modifier = Modifier.fillMaxWidth()) {
                options.forEach { option ->
                  Card(
                      modifier =
                          Modifier.fillMaxWidth()
                              .height(65.dp)
                              .padding(vertical = 4.dp)
                              .testTag(option.label.replace(" ", "") + "Option"),
                      shape = RoundedCornerShape(8.dp),
                      colors =
                          CardDefaults.cardColors(
                              containerColor = colorScheme.surface,
                              contentColor = colorScheme.onBackground),
                      elevation = CardDefaults.cardElevation(4.dp),
                      onClick = option.onClick) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically) {
                              when (val icon = option.icon) {
                                is IconType.Resource -> {
                                  Icon(
                                      painter = painterResource(icon.resId),
                                      contentDescription = "${option.label} Icon",
                                      modifier = Modifier.size(24.dp),
                                      tint = colorScheme.primary)
                                }
                                is IconType.Vector -> {
                                  Icon(
                                      imageVector = icon.imageVector,
                                      contentDescription = "${option.label} Icon",
                                      modifier = Modifier.size(24.dp),
                                      tint = colorScheme.primary)
                                }
                              }
                              Spacer(modifier = Modifier.width(16.dp))
                              Text(
                                  text = option.label,
                                  style = MaterialTheme.typography.titleSmall,
                                  modifier =
                                      Modifier.testTag(option.label.replace(" ", "") + "Text"))
                            }
                      }
                }
              }

              // Logout Button
              Button(
                  onClick = {
                    clearAccountPreferences(preferencesViewModel)
                      rootMainNavigationActions.navigateTo(RootRoute.NO_MODE)
                    Log.d("user", Firebase.auth.currentUser.toString())
                  },
                  modifier =
                      Modifier.fillMaxWidth().padding(vertical = 16.dp).testTag("LogoutButton"),
                  colors = ButtonDefaults.buttonColors(containerColor = colorScheme.primary)) {
                    Text(
                        text = "Log out",
                        color = colorScheme.onPrimary,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.testTag("LogoutText"))
                  }
            }
      },
  )
}

sealed class IconType {
  data class Resource(val resId: Int) : IconType()

  data class Vector(val imageVector: ImageVector) : IconType()
}

data class OptionItem(val label: String, val icon: IconType, val onClick: () -> Unit)

private fun capitalizeName(firstName: String?, lastName: String?): String {
  val capitalizedFirstName = firstName?.lowercase()?.replaceFirstChar { it.uppercase() } ?: ""
  val capitalizedLastName = lastName?.lowercase()?.replaceFirstChar { it.uppercase() } ?: ""
  return "$capitalizedFirstName $capitalizedLastName".trim()
}
