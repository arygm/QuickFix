package com.arygm.quickfix.ui.authentication

import QuickFixTextField
import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.arygm.quickfix.model.account.Account
import com.arygm.quickfix.model.account.AccountViewModel
import com.arygm.quickfix.model.account.LoggedInAccountViewModel
import com.arygm.quickfix.model.profile.ProfileViewModel
import com.arygm.quickfix.ui.elements.QuickFixAnimatedBox
import com.arygm.quickfix.ui.elements.QuickFixBackButton
import com.arygm.quickfix.ui.navigation.NavigationActions
import com.arygm.quickfix.ui.navigation.TopLevelDestinations
import com.arygm.quickfix.utils.isValidDate
import com.arygm.quickfix.utils.stringToTimestamp
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun GoogleInfoScreen(
    navigationActions: NavigationActions,
    loggedInAccountViewModel: LoggedInAccountViewModel,
    accountViewModel: AccountViewModel,
    userViewModel: ProfileViewModel
) {

  val loggedInAccount by loggedInAccountViewModel.loggedInAccount.collectAsState()

  var firstName by remember { mutableStateOf("") }
  var lastName by remember { mutableStateOf("") }
  var birthDate by remember { mutableStateOf("") }

  var birthDateError by remember { mutableStateOf(false) }

  var shrinkBox by remember { mutableStateOf(false) }
  val boxOffsetX by
      animateDpAsState(
          targetValue = if (shrinkBox) 1285.dp else 0.dp,
          animationSpec = tween(durationMillis = 300),
          label = "shrinkingBox")

  LaunchedEffect(Unit) { shrinkBox = true }

  val filledForm = firstName.isNotEmpty() && lastName.isNotEmpty() && birthDate.isNotEmpty()

  Box(modifier = Modifier.fillMaxSize().testTag("InfoBox")) {
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
                      accountViewModel.deleteAccountById(loggedInAccount!!.uid)
                      userViewModel.deleteProfileById(loggedInAccount!!.uid)
                      Firebase.auth.currentUser?.delete()?.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                          navigationActions.goBack()
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
                        Modifier.align(Alignment.BottomStart)
                            .requiredSize(180.dp)
                            .offset(x = (-150).dp, y = 64.dp)
                            .graphicsLayer(rotationZ = -28f)
                            .background(colorScheme.primary)
                            .zIndex(0f)
                            .testTag("decorationBox"))

                Column(
                    modifier =
                        Modifier.fillMaxSize()
                            .padding(start = 24.dp)
                            .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.Top) {
                      Spacer(modifier = Modifier.padding(60.dp))

                      Text(
                          "WELCOME",
                          color = colorScheme.primary,
                          style = MaterialTheme.typography.headlineLarge,
                          modifier = Modifier.testTag("welcomeText"))

                      Row(
                          modifier = Modifier.fillMaxWidth().padding(end = 18.dp),
                          horizontalArrangement =
                              Arrangement.SpaceBetween // This arranges them with space in between
                          ) {
                            QuickFixTextField(
                                value = firstName,
                                onValueChange = { firstName = it },
                                label = "FIRST NAME",
                                modifier =
                                    Modifier.weight(1f)
                                        .padding(end = 8.dp)
                                        .testTag("firstNameInput"),
                            )

                            QuickFixTextField(
                                value = lastName,
                                onValueChange = { lastName = it },
                                label = "LAST NAME",
                                modifier =
                                    Modifier.weight(1f)
                                        .padding(end = 8.dp)
                                        .testTag("lastNameInput"),
                            )
                          }

                      Spacer(modifier = Modifier.padding(6.dp))

                      // Birth Date Field
                      QuickFixTextField(
                          value = birthDate,
                          onValueChange = {
                            birthDate = it
                            birthDateError = !isValidDate(it)
                          },
                          label = "BIRTH DATE (DD/MM/YYYY)",
                          isError = birthDateError,
                          modifier = Modifier.width(360.dp).testTag("birthDateInput"),
                          singleLine = false,
                          errorText = "INVALID DATE",
                          showError = birthDateError)

                      Spacer(modifier = Modifier.padding(10.dp))

                      // Button
                      Button(
                          onClick = {
                            shrinkBox = false
                            accountViewModel.updateAccount(
                                Account(
                                    loggedInAccount!!.uid,
                                    firstName,
                                    lastName,
                                    loggedInAccount!!.email,
                                    stringToTimestamp(birthDate)!!,
                                    loggedInAccount!!.isWorker),
                                onSuccess = {
                                  loggedInAccountViewModel.setLoggedInAccount(
                                      Account(
                                          loggedInAccount!!.uid,
                                          firstName,
                                          lastName,
                                          loggedInAccount!!.email,
                                          stringToTimestamp(birthDate)!!,
                                          loggedInAccount!!.isWorker))
                                  navigationActions.navigateTo(TopLevelDestinations.HOME)
                                },
                                onFailure = {
                                  Log.d("GoogleInfoScreen", "Failed to update account.")
                                })
                          },
                          modifier = Modifier.width(360.dp).height(48.dp).testTag("nextButton"),
                          shape = RoundedCornerShape(10.dp),
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
