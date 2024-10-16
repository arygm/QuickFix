package com.arygm.quickfix.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.List
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.arygm.quickfix.R
import com.arygm.quickfix.ui.navigation.NavigationActions

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navigationActions: NavigationActions) {

  Scaffold(
      containerColor = colorScheme.background,
      topBar = {
        TopAppBar(
            title = {
              Box(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Mohamed Abbes",
                    style = MaterialTheme.typography.headlineMedium,
                    color = colorScheme.onBackground,
                    modifier = Modifier.align(Alignment.Center))
              }
            },
            navigationIcon = {
              IconButton(onClick = { /*Nothing to do here*/}, Modifier.testTag("ProfileButton")) {
                Icon(
                    painter = painterResource(R.drawable.profilevector),
                    contentDescription = "Profile Icon",
                    tint = colorScheme.primary,
                    modifier = Modifier.size(24.dp) // Adjust size of the icon if needed
                    )
              }
            },
            // modifier = Modifier.height(56.dp),
            colors = TopAppBarDefaults.topAppBarColors(containerColor = colorScheme.background),
        )
      },
      content = { padding ->
        Column(
            modifier =
                Modifier.fillMaxWidth()
                    .padding(top = padding.calculateTopPadding())
                    .padding(horizontal = 16.dp)) {
              // Remove the profile card that was originally here

              // Upcoming Activities Placeholder
              Card(
                  modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                  shape = RoundedCornerShape(5.dp),
                  colors =
                      CardDefaults.cardColors(
                          containerColor = colorScheme.surface,
                          contentColor = colorScheme.onBackground),
                  elevation = CardDefaults.cardElevation(4.dp)) {
                    Text(
                        text =
                            "This isn’t developed yet; but it can display upcoming activities for both a user and worker",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyMedium)
                  }

              // Wallet and Help Row
              Row(
                  modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                  horizontalArrangement = Arrangement.SpaceBetween) {
                    Button(
                        onClick = { /* Wallet click action */},
                        modifier = Modifier.weight(1f).height(70.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = colorScheme.surface),
                        shape = RoundedCornerShape(5.dp),
                        elevation = ButtonDefaults.buttonElevation(4.dp)) {
                          Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                painter = painterResource(R.drawable.wallletvector),
                                contentDescription = "Wallet Icon",
                                modifier = Modifier.size(24.dp),
                                tint = colorScheme.primary)
                            Text(text = "Wallet", color = colorScheme.onBackground)
                            Text(text = "___ CHF", color = colorScheme.onBackground)
                          }
                        }
                    Spacer(modifier = Modifier.width(16.dp))
                    Button(
                        onClick = { /* Help click action */},
                        modifier = Modifier.weight(1f).height(70.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = colorScheme.surface),
                        shape = RoundedCornerShape(5.dp),
                        elevation = ButtonDefaults.buttonElevation(4.dp)) {
                          Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                painter = painterResource(R.drawable.helpvector),
                                contentDescription = "Help Icon",
                                modifier = Modifier.size(24.dp),
                                tint = colorScheme.primary)
                            Text(text = "Help", color = colorScheme.onBackground)
                          }
                        }
                  }

              // Options Section
              Column(modifier = Modifier.fillMaxWidth().padding(top = 10.dp)) {
                options.forEach { option ->
                  Card(
                      modifier = Modifier.fillMaxWidth().height(65.dp).padding(vertical = 4.dp),
                      shape = RoundedCornerShape(7.dp),
                      colors =
                          CardDefaults.cardColors(
                              containerColor = colorScheme.surface,
                              contentColor = colorScheme.onBackground),
                      elevation = CardDefaults.cardElevation(4.dp),
                      onClick = { /* Handle click */}) {
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
                              Text(text = option.label, style = MaterialTheme.typography.titleSmall)
                            }
                      }
                }
              }

              // Logout Button
              Button(
                  onClick = { /* Handle logout */},
                  modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                  colors = ButtonDefaults.buttonColors(containerColor = colorScheme.primary)) {
                    Text(
                        text = "Log out",
                        color = colorScheme.onBackground,
                        style = MaterialTheme.typography.titleMedium)
                  }
            }
      })
}

sealed class IconType {
  data class Resource(val resId: Int) : IconType()

  data class Vector(val imageVector: ImageVector) : IconType()
}

private data class OptionItem(val label: String, val icon: IconType)

private val options =
    listOf(
        OptionItem("Settings", IconType.Vector(Icons.Outlined.Settings)),
        OptionItem(
            "Activity",
            IconType.Vector(Icons.Outlined.List)), // Assuming you're using Material Icons
        OptionItem("Set up your business account", IconType.Resource(R.drawable.workvector)),
        OptionItem("Account configuration", IconType.Resource(R.drawable.accountsettingsvector)),
        OptionItem("Workers network", IconType.Vector(Icons.Outlined.Phone)),
        OptionItem("Legal", IconType.Vector(Icons.Outlined.Info)) // Example vector icon
        )
