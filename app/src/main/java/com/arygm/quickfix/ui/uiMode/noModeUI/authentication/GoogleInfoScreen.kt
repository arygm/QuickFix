package com.arygm.quickfix.ui.uiMode.noModeUI.authentication

import QuickFixTextField
import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.arygm.quickfix.model.account.Account
import com.arygm.quickfix.model.account.AccountViewModel
import com.arygm.quickfix.model.offline.small.PreferencesViewModel
import com.arygm.quickfix.model.offline.small.PreferencesViewModelUserProfile
import com.arygm.quickfix.model.profile.ProfileViewModel
import com.arygm.quickfix.model.profile.UserProfile
import com.arygm.quickfix.ui.elements.QuickFixAnimatedBox
import com.arygm.quickfix.ui.elements.QuickFixBackButton
import com.arygm.quickfix.ui.navigation.NavigationActions
import com.arygm.quickfix.ui.navigation.RootRoute
import com.arygm.quickfix.ui.uiMode.noModeUI.navigation.NoModeRoute
import com.arygm.quickfix.utils.ANIMATED_BOX_ROTATION
import com.arygm.quickfix.utils.isValidDate
import com.arygm.quickfix.utils.loadEmail
import com.arygm.quickfix.utils.loadUserId
import com.arygm.quickfix.utils.setAccountPreferences
import com.arygm.quickfix.utils.setUserProfilePreferences
import com.arygm.quickfix.utils.stringToTimestamp
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun GoogleInfoScreen(
    rootNavigationActions: NavigationActions,
    accountViewModel: AccountViewModel,
    userViewModel: ProfileViewModel,
    preferencesViewModel: PreferencesViewModel,
    navigationActions: NavigationActions,
    userPreferencesViewModel: PreferencesViewModelUserProfile
) {

  var firstName by remember { mutableStateOf("") }
  var lastName by remember { mutableStateOf("") }
  var birthDate by remember { mutableStateOf("") }
  var email by remember { mutableStateOf("Loading...") }
  var uid by remember { mutableStateOf("Loading...") }

  LaunchedEffect(Unit) {
    uid = loadUserId(preferencesViewModel)
    email = loadEmail(preferencesViewModel)
  }

  var birthDateError by remember { mutableStateOf(false) }

  var shrinkBox by remember { mutableStateOf(false) }
  val boxOffsetX by
      animateDpAsState(
          targetValue = if (shrinkBox) 1285.dp else 0.dp,
          animationSpec = tween(durationMillis = 300),
          label = "shrinkingBox")

  LaunchedEffect(Unit) { shrinkBox = true }

  val filledForm = firstName.isNotEmpty() && lastName.isNotEmpty() && birthDate.isNotEmpty()

  BoxWithConstraints(modifier = Modifier.fillMaxSize().testTag("InfoBox")) {
    val screenWidth = maxWidth
    val screenHeight = maxHeight
    QuickFixAnimatedBox(boxOffsetX)

    Scaffold(
        modifier = Modifier.background(colorScheme.background).testTag("InfoScaffold"),
        topBar = {
          TopAppBar(
              title = { Text("") },
              navigationIcon = {
                QuickFixBackButton(
                    onClick = {
                      shrinkBox = false
                      accountViewModel.deleteAccountById(uid)
                      userViewModel.deleteProfileById(uid)
                      Firebase.auth.currentUser?.delete()?.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                          rootNavigationActions.goBack()
                        } else {
                          Log.e(
                              "GoogleInfoScreen",
                              "Failed to delete Firebase user: ${task.exception}")
                        }
                      }
                    },
                    color = colorScheme.primary,
                    modifier = Modifier.testTag("goBackButton"))
              },
              colors = TopAppBarDefaults.topAppBarColors(containerColor = colorScheme.background))
        },
        content = { pd ->
          Box(
              modifier =
                  Modifier.fillMaxSize()
                      .background(colorScheme.background)
                      .padding(pd)
                      .imePadding()
                      .testTag("contentBox")) {
                Box(
                    modifier =
                        Modifier.size(
                                screenWidth * 0.5f) // Scale box size to be relative to screen size
                            .align(Alignment.BottomStart)
                            .offset(
                                x =
                                    -screenWidth *
                                        0.4f, // Offset slightly left relative to screen width
                                y =
                                    screenHeight *
                                        0.1f // Offset slightly upward relative to screen height
                                )
                            .graphicsLayer(rotationZ = ANIMATED_BOX_ROTATION)
                            .background(colorScheme.primary)
                            .testTag("decorationBox"))
                Column(
                    modifier =
                        Modifier.fillMaxSize()
                            .padding(
                                start =
                                    screenWidth * 0.05f) // Relative padding based on screen width
                            .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.Top) {
                      Spacer(
                          modifier = Modifier.height(screenHeight * 0.1f)) // Relative top padding

                      Text(
                          "WELCOME",
                          color = colorScheme.primary,
                          style = MaterialTheme.typography.headlineLarge,
                          modifier = Modifier.testTag("welcomeText"))

                      Spacer(
                          modifier =
                              Modifier.height(screenHeight * 0.02f)) // Small vertical spacing

                      Row(
                          modifier =
                              Modifier.fillMaxWidth()
                                  .padding(end = screenWidth * 0.05f), // Relative end padding
                          horizontalArrangement = Arrangement.SpaceBetween) {
                            QuickFixTextField(
                                value = firstName,
                                onValueChange = { firstName = it },
                                label = "FIRST NAME",
                                modifier =
                                    Modifier.weight(1f)
                                        .padding(
                                            end =
                                                screenWidth * 0.02f) // Small padding between fields
                                        .testTag("firstNameInput"),
                            )

                            QuickFixTextField(
                                value = lastName,
                                onValueChange = { lastName = it },
                                label = "LAST NAME",
                                modifier =
                                    Modifier.weight(1f)
                                        .padding(
                                            start =
                                                screenWidth * 0.02f) // Small padding between fields
                                        .testTag("lastNameInput"),
                            )
                          }

                      Spacer(
                          modifier =
                              Modifier.height(screenHeight * 0.01f)) // Slight spacing below Row

                      QuickFixTextField(
                          value = birthDate,
                          onValueChange = {
                            birthDate = it
                            birthDateError = !isValidDate(it)
                          },
                          label = "BIRTH DATE (DD/MM/YYYY)",
                          isError = birthDateError,
                          modifier =
                              Modifier.width(screenWidth * 0.9f) // Relative width for text field
                                  .testTag("birthDateInput"),
                          singleLine = false,
                          errorText = "INVALID DATE",
                          showError = birthDateError)

                      Spacer(
                          modifier =
                              Modifier.height(
                                  screenHeight * 0.02f)) // Relative spacing before button

                      Button(
                          onClick = {
                            val newAccount =
                                Account(
                                    uid,
                                    firstName,
                                    lastName,
                                    email,
                                    stringToTimestamp(birthDate)!!,
                                    false // default to non-worker account
                                    )
                            shrinkBox = false
                            accountViewModel.updateAccount(
                                newAccount,
                                onSuccess = {
                                  setAccountPreferences(preferencesViewModel, newAccount)
                                  userViewModel.fetchUserProfile(uid) { userProfile ->
                                    val profileFetched = userProfile as UserProfile
                                    setUserProfilePreferences(
                                        userPreferencesViewModel, profileFetched)
                                  }
                                  rootNavigationActions.navigateTo(RootRoute.APP_CONTENT)
                                  navigationActions.navigateTo(NoModeRoute.WELCOME)
                                },
                                onFailure = {
                                  Log.d("GoogleInfoScreen", "Failed to update account.")
                                })
                          },
                          modifier =
                              Modifier.width(screenWidth * 0.9f) // Relative width for button
                                  .height(screenHeight * 0.06f) // Relative height for button
                                  .testTag("nextButton"),
                          shape =
                              RoundedCornerShape(
                                  screenWidth * 0.025f), // Relative corner radius for button shape
                          colors =
                              ButtonDefaults.buttonColors(containerColor = colorScheme.primary),
                          enabled = filledForm && !birthDateError) {
                            Text(
                                "NEXT",
                                style = MaterialTheme.typography.labelLarge,
                                color = colorScheme.background)
                          }
                    }
              }
        })
  }
}
