package com.arygm.quickfix.ui.profile

import android.annotation.SuppressLint
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.arygm.quickfix.R
import com.arygm.quickfix.model.profile.ProfileViewModel
import com.arygm.quickfix.ui.navigation.NavigationActions
import com.arygm.quickfix.ui.theme.poppinsTypography
import java.util.Calendar
import java.util.GregorianCalendar

@SuppressLint("StateFlowValueCalledInComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileConfigurationScreen(
    navigationActions: NavigationActions,
    isUser: Boolean = true,
    profileViewModel: ProfileViewModel = viewModel(factory = ProfileViewModel.Factory)
) {
  val loggedInProfile =
      profileViewModel.loggedInProfile.collectAsState().value
          ?: return Text(
              text = "No profil currently selected. Should not happen", color = colorScheme.primary)

  var firstName by remember { mutableStateOf(loggedInProfile.firstName) }
  var lastName by remember { mutableStateOf(loggedInProfile.lastName) }
  var email by remember { mutableStateOf(loggedInProfile.email) }

  var birthDate by remember {
    mutableStateOf(
        loggedInProfile.birthDate.let {
          val calendar = GregorianCalendar()
          calendar.time = loggedInProfile.birthDate.toDate()
          return@let "${calendar.get(Calendar.DAY_OF_MONTH)}/${calendar.get(Calendar.MONTH) + 1}/${
                    calendar.get(
                        Calendar.YEAR
                    )
                }"
        })
  }

  Scaffold(
      containerColor = colorScheme.background,
      topBar = {
        TopAppBar(
            title = {
              Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    "Account configuration",
                    modifier = Modifier.testTag("AccountConfigurationTitle").padding(end = 29.dp),
                    style = poppinsTypography.headlineMedium,
                    color = colorScheme.primary)
              }
            },
            navigationIcon = {
              IconButton(
                  onClick = { navigationActions.goBack() },
                  modifier = Modifier.testTag("goBackButton")) {
                    Icon(
                        Icons.Outlined.ArrowBack,
                        contentDescription = "Back",
                        tint = colorScheme.primary)
                  }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = colorScheme.background))
      },
      content = { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally) {
              // Profile Image Placeholder
              Icon(
                  imageVector = Icons.Default.AccountCircle,
                  contentDescription = "Account Circle Icon",
                  tint = colorScheme.background,
                  modifier =
                      Modifier.size(100.dp)
                          .clip(CircleShape)
                          .border(2.dp, colorScheme.background, CircleShape))

              Spacer(modifier = Modifier.height(16.dp))

              // Name and edit icon
              Row(
                  verticalAlignment = Alignment.CenterVertically,
                  modifier = Modifier.padding(7.dp)) {
                    Icon(
                        painter = painterResource(R.drawable.profilevector),
                        contentDescription = "Profile Icon",
                        tint = colorScheme.primary,
                        modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(65.dp))

                    val displayName =
                        capitalizeName(loggedInProfile?.firstName, loggedInProfile?.lastName)

                    Text(
                        text = displayName,
                        style = MaterialTheme.typography.bodyLarge,
                        color = colorScheme.onBackground,
                        modifier = Modifier.testTag("ProfileName"))
                  }

              Spacer(modifier = Modifier.height(16.dp))

              // Input fields
              InputField(label = "First name", value = firstName)
              InputField(label = "Last name", value = lastName)
              InputField(label = "Email", value = email, enabled = false)
              InputField(label = "Birthdate", value = birthDate, enabled = false)

              Spacer(modifier = Modifier.height(16.dp))

              // Change password button
              Button(
                  onClick = { /* Handle change password */},
                  modifier = Modifier.fillMaxWidth(0.8f).padding(horizontal = 16.dp),
                  colors = ButtonDefaults.buttonColors(containerColor = colorScheme.primary)) {
                    Icon(Icons.Outlined.Lock, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Change password")
                  }

              Spacer(modifier = Modifier.height(8.dp))

              // Save button
              Button(
                  onClick = { /* Handle save */},
                  modifier = Modifier.fillMaxWidth(0.8f).padding(horizontal = 16.dp),
                  colors = ButtonDefaults.buttonColors(containerColor = colorScheme.primary)) {
                    Text("Save")
                  }
            }
      })
}

@Composable
fun InputField(label: String, value: String, enabled: Boolean = true) {
  OutlinedTextField(
      value = value,
      onValueChange = { /* Handle text change */},
      label = { Text(label) },
      enabled = enabled,
      colors =
          OutlinedTextFieldDefaults.colors(
              disabledTextColor = colorScheme.onSurface.copy(alpha = 0.38f),
              disabledLabelColor = colorScheme.onSurface.copy(alpha = 0.38f),
              disabledBorderColor = colorScheme.onSurface.copy(alpha = 0.38f),
              focusedBorderColor = colorScheme.primary,
              cursorColor = colorScheme.primary),
      modifier = Modifier.fillMaxWidth(0.8f).padding(vertical = 8.dp))
}

private fun capitalizeName(firstName: String?, lastName: String?): String {
  val capitalizedFirstName = firstName?.lowercase()?.replaceFirstChar { it.uppercase() } ?: ""
  val capitalizedLastName = lastName?.lowercase()?.replaceFirstChar { it.uppercase() } ?: ""
  return "$capitalizedFirstName $capitalizedLastName".trim()
}
