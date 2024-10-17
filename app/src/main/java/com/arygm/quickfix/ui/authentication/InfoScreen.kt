package com.arygm.quickfix.ui.authentication

import QuickFixTextField
import android.annotation.SuppressLint
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.arygm.quickfix.model.profile.ProfileViewModel
import com.arygm.quickfix.model.profile.RegistrationViewModel
import com.arygm.quickfix.ui.elements.QuickFixAnimatedBox
import com.arygm.quickfix.ui.elements.QuickFixBackButtonTopBar
import com.arygm.quickfix.ui.elements.QuickFixButton
import com.arygm.quickfix.ui.elements.QuickFixCheckBoxRow
import com.arygm.quickfix.ui.navigation.NavigationActions
import com.arygm.quickfix.ui.navigation.Screen
import com.arygm.quickfix.utils.BOX_COLLAPSE_SPEED
import com.arygm.quickfix.utils.BOX_OFFSET_X_EXPANDED
import com.arygm.quickfix.utils.BOX_OFFSET_X_SHRUNK
import com.arygm.quickfix.utils.isValidDate
import com.arygm.quickfix.utils.isValidEmail
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Deprecated("This composable is deprecated", ReplaceWith("RegisterScreen(navigationActions)"))
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun InfoScreen(
    navigationActions: NavigationActions,
    registrationViewModel: RegistrationViewModel,
    profileViewModel: ProfileViewModel
) {
  val colorScheme = MaterialTheme.colorScheme

  var firstName by remember { mutableStateOf("") }
  var lastName by remember { mutableStateOf("") }
  var email by remember { mutableStateOf("") }
  var birthDate by remember { mutableStateOf("") }

  var emailError by remember { mutableStateOf(false) }
  var birthDateError by remember { mutableStateOf(false) }

  var acceptTerms by remember { mutableStateOf(false) }
  var acceptPrivacyPolicy by remember { mutableStateOf(false) }

  var shrinkBox by remember { mutableStateOf(false) }
  val boxOffsetX by
      animateDpAsState(
          targetValue = if (shrinkBox) BOX_OFFSET_X_SHRUNK else BOX_OFFSET_X_EXPANDED,
          animationSpec = tween(durationMillis = BOX_COLLAPSE_SPEED),
          label = "shrinkingBox")

  LaunchedEffect(Unit) { shrinkBox = true }

  val filledForm =
      firstName.isNotEmpty() &&
          lastName.isNotEmpty() &&
          email.isNotEmpty() &&
          birthDate.isNotEmpty()

  val coroutineScope = rememberCoroutineScope()

  Box(modifier = Modifier.fillMaxSize().testTag("InfoBox")) {
    QuickFixAnimatedBox(boxOffsetX)

    Scaffold(
        modifier = Modifier.background(colorScheme.background),
        topBar = {
          QuickFixBackButtonTopBar(
              onBackClick = {
                shrinkBox = false
                coroutineScope.launch {
                  delay(BOX_COLLAPSE_SPEED.toLong())
                  navigationActions.goBack()
                }
              })
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

                      // Email Field
                      QuickFixTextField(
                          value = email,
                          onValueChange = {
                            email = it
                            emailError = !isValidEmail(it)
                            profileViewModel.profileExists(email) { exists, profile ->
                              emailError =
                                  if (exists && profile != null) {
                                    true
                                  } else {
                                    !isValidEmail(it)
                                  }
                            }
                          },
                          label = "E-MAIL",
                          isError = emailError,
                          modifier = Modifier.width(360.dp).testTag("emailInput"),
                          singleLine = false,
                          errorText = "INVALID EMAIL",
                          showError = emailError)

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

                      // Checkboxes for terms and privacy policy
                      QuickFixCheckBoxRow(
                          checked = acceptTerms,
                          onCheckedChange = { acceptTerms = it },
                          label = "I ACCEPT THE",
                          underlinedText = "TERMS AND CONDITIONS",
                          onUnderlinedTextClick = { /* TODO: Add click logic */},
                          colorScheme = colorScheme)

                      Spacer(modifier = Modifier.padding(4.dp))

                      QuickFixCheckBoxRow(
                          checked = acceptPrivacyPolicy,
                          onCheckedChange = { acceptPrivacyPolicy = it },
                          label = "I ACCEPT THE",
                          underlinedText = "PRIVACY POLICY",
                          onUnderlinedTextClick = { /* TODO: Add click logic */},
                          colorScheme = colorScheme)

                      Spacer(modifier = Modifier.padding(10.dp))

                      QuickFixButton(
                          buttonText = "NEXT",
                          onClickAction = {
                            shrinkBox = false
                            registrationViewModel.updateFirstName(firstName)
                            registrationViewModel.updateLastName(lastName)
                            registrationViewModel.updateEmail(email)
                            registrationViewModel.updateBirthDate(birthDate)
                            coroutineScope.launch {
                              delay(BOX_COLLAPSE_SPEED.toLong())
                              navigationActions.navigateTo(Screen.PASSWORD)
                            }
                          },
                          buttonColor = colorScheme.primary,
                          textColor = colorScheme.background,
                          enabled =
                              acceptTerms &&
                                  acceptPrivacyPolicy &&
                                  filledForm &&
                                  !emailError &&
                                  !birthDateError,
                          modifier = Modifier.width(360.dp).height(48.dp).testTag("nextButton"),
                      )
                    }
              }
        })
  }
}
