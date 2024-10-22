package com.arygm.quickfix.ui.profile

import android.annotation.SuppressLint
import android.widget.Toast
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Lock
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.arygm.quickfix.R
import com.arygm.quickfix.model.profile.LoggedInProfileViewModel
import com.arygm.quickfix.model.profile.ProfileViewModel
import com.arygm.quickfix.model.profile.UserProfile
import com.arygm.quickfix.ui.authentication.CustomTextField
import com.arygm.quickfix.ui.elements.QuickFixTextFieldCustom
import com.arygm.quickfix.ui.navigation.NavigationActions
import com.arygm.quickfix.ui.theme.poppinsTypography
import com.arygm.quickfix.utils.isValidDate
import com.arygm.quickfix.utils.isValidEmail
import com.google.firebase.Timestamp
import java.util.Calendar
import java.util.GregorianCalendar

@SuppressLint("StateFlowValueCalledInComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileConfigurationScreen(
    navigationActions: NavigationActions,
    isUser: Boolean = true,
    userViewModel: ProfileViewModel,
    workerViewModel: ProfileViewModel,
    loggedInProfileViewModel: LoggedInProfileViewModel
) {
  val loggedInProfile =
      loggedInProfileViewModel.loggedInProfile.collectAsState().value
          ?: return Text(
              text = "No profile currently selected. Should not happen",
              color = colorScheme.primary)

  var firstName by remember { mutableStateOf(loggedInProfile.firstName) }
  var lastName by remember { mutableStateOf(loggedInProfile.lastName) }
  var email by remember { mutableStateOf(loggedInProfile.email) }

  var emailError by remember { mutableStateOf(false) }
  var birthDateError by remember { mutableStateOf(false) }

  var birthDate by remember {
    mutableStateOf(
        loggedInProfile.birthDate.let {
          val calendar = GregorianCalendar()
          calendar.time = loggedInProfile.birthDate.toDate()
          return@let "${calendar.get(Calendar.DAY_OF_MONTH)}/${calendar.get(Calendar.MONTH) + 1}/${calendar.get(Calendar.YEAR)}"
        })
  }
  val context = LocalContext.current

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
            colors = TopAppBarDefaults.topAppBarColors(containerColor = colorScheme.background),
            modifier = Modifier.testTag("AccountConfigurationTopAppBar"))
      },
      content = { padding ->
        Column(
            modifier = Modifier.fillMaxSize().fillMaxWidth().padding(padding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally) {
              // Profile Image Placeholder
              Icon(
                  imageVector = Icons.Default.AccountCircle,
                  contentDescription = "Account Circle Icon",
                  tint = colorScheme.surface,
                  modifier =
                      Modifier.size(100.dp)
                          .clip(CircleShape)
                          .border(2.dp, colorScheme.background, CircleShape)
                          .testTag("ProfileImage"))

              Spacer(modifier = Modifier.height(16.dp))

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

                          val displayName =
                              capitalizeName(loggedInProfile.firstName, loggedInProfile.lastName)

                          Text(
                              text = displayName,
                              style = MaterialTheme.typography.bodyLarge,
                              color = colorScheme.onBackground,
                              modifier = Modifier.testTag("ProfileName"))
                        }
                  }

              Spacer(modifier = Modifier.height(60.dp))

              Column(
                  modifier =
                      Modifier.align(Alignment.CenterHorizontally)
                          .padding(16.dp)
                          .zIndex(100f), // Ensure it's on top
                  horizontalAlignment = Alignment.CenterHorizontally,
                  verticalArrangement = Arrangement.Center) {
                    Row(
                        modifier = Modifier.fillMaxWidth().height(55.dp).padding(start = 8.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically) {
                          CustomTextField(
                              value = firstName,
                              onValueChange = { firstName = it },
                              placeHolderText = "First Name",
                              placeHolderColor = colorScheme.onSecondaryContainer,
                              label = "First Name",
                              columnModifier = Modifier.weight(1f),
                              modifier = Modifier.testTag("firstNameInput"))

                          CustomTextField(
                              value = lastName,
                              onValueChange = { lastName = it },
                              placeHolderText = "Last Name",
                              placeHolderColor = colorScheme.onSecondaryContainer,
                              label = "Last Name",
                              columnModifier = Modifier.weight(1f),
                              modifier = Modifier.testTag("lastNameInput"))
                        }

                    Spacer(modifier = Modifier.padding(6.dp))

                    Column(modifier = Modifier.fillMaxWidth().padding(start = 8.dp)) {
                      QuickFixTextFieldCustom(
                          value = email,
                          onValueChange = {
                            email = it
                            emailError = !isValidEmail(it)
                            userViewModel.profileExists(email) { exists, profile ->
                              emailError =
                                  exists && profile != null && email != loggedInProfile.email
                            }
                          },
                          placeHolderText = "Enter your email address",
                          placeHolderColor = colorScheme.onSecondaryContainer,
                          isError = emailError,
                          showError = emailError,
                          errorText = "INVALID EMAIL",
                          modifier = Modifier.testTag("emailInput"),
                          showLabel = true,
                          label = {
                            Text(
                                "Email",
                                style = MaterialTheme.typography.headlineSmall,
                                color = colorScheme.onBackground,
                                modifier = Modifier.padding(start = 3.dp).testTag("emailLabel"))
                          })
                    }

                    Spacer(modifier = Modifier.padding(6.dp))

                    Column(modifier = Modifier.fillMaxWidth().padding(start = 8.dp)) {
                      QuickFixTextFieldCustom(
                          value = birthDate,
                          onValueChange = {
                            birthDate = it
                            birthDateError = !isValidDate(it)
                          },
                          placeHolderText = "Enter your birthdate (DD/MM/YYYY)",
                          placeHolderColor = colorScheme.onSecondaryContainer,
                          isError = birthDateError,
                          showError = birthDateError,
                          errorText = "INVALID DATE",
                          modifier = Modifier.testTag("birthDateInput"),
                          showLabel = true,
                          label = {
                            Text(
                                "Birthdate",
                                style = MaterialTheme.typography.headlineSmall,
                                color = colorScheme.onBackground,
                                modifier = Modifier.padding(start = 3.dp).testTag("birthDateLabel"))
                          })
                    }
                  }

              Spacer(modifier = Modifier.height(16.dp))

              // Change password button
              Button(
                  onClick = { /* Handle change password */},
                  modifier =
                      Modifier.fillMaxWidth(0.8f)
                          .padding(horizontal = 16.dp)
                          .testTag("ChangePasswordButton"),
                  colors = ButtonDefaults.buttonColors(containerColor = colorScheme.primary)) {
                    Icon(Icons.Outlined.Lock, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Change password",
                        color = colorScheme.onPrimary,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.testTag("ChangePasswordText"))
                  }

              Spacer(modifier = Modifier.height(8.dp))

              // Save button
              Button(
                  onClick = {
                    val calendar = GregorianCalendar()
                    val parts = birthDate.split("/")
                    if (parts.size == 3) {
                      try {
                        calendar.set(
                            parts[2].toInt(),
                            parts[1].toInt() - 1, // Months are 0-based indexed
                            parts[0].toInt(),
                            0,
                            0,
                            0)

                        if (loggedInProfile is UserProfile) {
                          userViewModel.updateProfile(
                              UserProfile(
                                  uid = loggedInProfile.uid,
                                  firstName = firstName,
                                  lastName = lastName,
                                  email = email,
                                  birthDate = Timestamp(calendar.time),
                                  isWorker = loggedInProfile.isWorker),
                              onSuccess = {
                                userViewModel.fetchUserProfile(loggedInProfile.uid) { profile ->
                                  loggedInProfileViewModel.setLoggedInProfile(profile!!)
                                }
                              },
                              onFailure = {})
                          navigationActions.goBack()
                          return@Button
                        } else {
                          // TODO
                        }
                      } catch (_: NumberFormatException) {}
                    }

                    Toast.makeText(
                            context, "Invalid format, date must be DD/MM/YYYY.", Toast.LENGTH_SHORT)
                        .show()
                  },
                  modifier =
                      Modifier.fillMaxWidth(0.8f).padding(horizontal = 16.dp).testTag("SaveButton"),
                  colors = ButtonDefaults.buttonColors(containerColor = colorScheme.primary)) {
                    Text(
                        text = "Save",
                        color = colorScheme.onPrimary,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.testTag("SaveButtonText"))
                  }
            }
      })
}

private fun capitalizeName(firstName: String?, lastName: String?): String {
  val capitalizedFirstName = firstName?.lowercase()?.replaceFirstChar { it.uppercase() } ?: ""
  val capitalizedLastName = lastName?.lowercase()?.replaceFirstChar { it.uppercase() } ?: ""
  return "$capitalizedFirstName $capitalizedLastName".trim()
}
