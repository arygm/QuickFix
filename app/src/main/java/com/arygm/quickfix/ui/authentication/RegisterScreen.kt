package com.arygm.quickfix.ui.authentication

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.arygm.quickfix.ui.elements.QuickFixAnimatedBox
import com.arygm.quickfix.ui.elements.QuickFixBackButtonTopBar
import com.arygm.quickfix.ui.elements.QuickFixButton
import com.arygm.quickfix.ui.elements.QuickFixCheckBoxRow
import com.arygm.quickfix.ui.elements.QuickFixTextFieldCustom
import com.arygm.quickfix.ui.navigation.NavigationActions
import com.arygm.quickfix.ui.navigation.Screen
import com.arygm.quickfix.utils.isValidDate
import com.arygm.quickfix.utils.isValidEmail

@Composable
@Preview
fun RegisterScreen(navigationActions: NavigationActions? = null) {
  var firstName by remember { mutableStateOf("") }
  var lastName by remember { mutableStateOf("") }
  var email by remember { mutableStateOf("") }
  var birthDate by remember { mutableStateOf("") }

  var emailError by remember { mutableStateOf(false) }
  var birthDateError by remember { mutableStateOf(false) }

  var acceptTerms by remember { mutableStateOf(false) }
  var acceptPrivacyPolicy by remember { mutableStateOf(false) }

  var password by remember { mutableStateOf("") }
  var passwordVisible by remember { mutableStateOf(false) }
  var repeatPasswordVisible by remember { mutableStateOf(false) }
  var repeatPassword by remember { mutableStateOf("") }

  val noMatch by remember {
    derivedStateOf { password != repeatPassword && repeatPassword.isNotEmpty() }
  }

  val passwordConditions =
      listOf(
          "• At least 8 characters" to (password.length >= 8),
          "• Contains an uppercase letter (A-Z)" to password.any { it.isUpperCase() },
          "• Contains a lowercase letter (a-z)" to password.any { it.isLowerCase() },
          "• Contains a digit (0-9)" to password.any { it.isDigit() })

  var shrinkBox by remember { mutableStateOf(false) }
  val boxOffsetX by
      animateDpAsState(
          targetValue = if (shrinkBox) 1285.dp else 0.dp,
          animationSpec = tween(durationMillis = 300),
          label = "shrinkingBox")

  LaunchedEffect(Unit) { shrinkBox = true }

  val focusRequester = remember { FocusRequester() }
  val focursManager = LocalFocusManager.current

  val filledForm =
      firstName.isNotEmpty() &&
          lastName.isNotEmpty() &&
          email.isNotEmpty() &&
          birthDate.isNotEmpty() &&
          passwordConditions.all { it.second } &&
          !noMatch &&
          repeatPassword.isNotEmpty() &&
          acceptTerms
  Box(
      modifier =
          Modifier.fillMaxSize().testTag("InfoBox").pointerInput(Unit) {
            detectTapGestures(onTap = { focursManager.clearFocus() })
          }) {
        QuickFixAnimatedBox(boxOffsetX)
        Scaffold(
            modifier =
                Modifier.background(colorScheme.background)
                    .fillMaxSize()
                    .testTag("RegisterScaffold"),
            content = { dp ->

              // Background and content are wrapped in a Box to control the layering
              Box(
                  modifier =
                      Modifier.fillMaxSize()
                          .testTag("ContentBox")
                          .background(colorScheme.background)
                          .padding(dp.calculateBottomPadding())) {

                    // TopAppBar below content (layered behind content)
                    Box(
                        modifier = Modifier.zIndex(1f) // Lower zIndex so it's behind the content
                        ) {
                          Image(
                              painter =
                                  painterResource(id = com.arygm.quickfix.R.drawable.worker_image),
                              contentDescription = null,
                              contentScale = ContentScale.Crop,
                              alignment = Alignment.TopStart,
                              modifier = Modifier.fillMaxWidth().size(180.dp))

                          Box(modifier = Modifier.fillMaxSize()) {
                            Image(
                                painter =
                                    painterResource(
                                        id = com.arygm.quickfix.R.drawable.worker_image),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize().testTag("topBarBackground"))
                            QuickFixBackButtonTopBar(
                                onBackClick = {
                                  shrinkBox = false
                                  navigationActions?.goBack()
                                },
                                color = Color.Transparent)
                          }
                        }

                    // Foreground content (on top of the TopAppBar)
                    Box(
                        modifier =
                            Modifier.fillMaxWidth()
                                .align(Alignment.BottomStart)
                                .zIndex(2f)
                                .background(
                                    colorScheme.background,
                                    shape =
                                        RoundedCornerShape(
                                            12.dp)) // Ensure content is above TopAppBar
                        ) {
                          Box(
                              modifier =
                                  Modifier.align(Alignment.BottomStart)
                                      .size(180.dp, 180.dp)
                                      .offset(x = (-150).dp, y = 64.dp)
                                      .graphicsLayer(rotationZ = 57f)
                                      .background(colorScheme.primary)
                                      .testTag("BoxDecoration"))

                          Column(
                              modifier =
                                  Modifier.align(Alignment.Center)
                                      .padding(16.dp)
                                      .zIndex(100f), // Ensure it's on top
                              horizontalAlignment = Alignment.CenterHorizontally,
                              verticalArrangement = Arrangement.Center) {
                                Text(
                                    "Register Now",
                                    style = MaterialTheme.typography.headlineLarge,
                                    color = colorScheme.primary,
                                    modifier = Modifier.testTag("welcomeText"))

                                Spacer(modifier = Modifier.padding(3.dp))

                                Text(
                                    "Join QuickFix to connect with skilled workers!",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = colorScheme.onSecondaryContainer,
                                    modifier = Modifier.testTag("welcomeTextBis"))

                                Spacer(modifier = Modifier.padding(10.dp))

                                Row(
                                    modifier =
                                        Modifier.fillMaxWidth().height(55.dp).padding(start = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceEvenly,
                                    verticalAlignment = Alignment.CenterVertically) {
                                      Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
                                        Text(
                                            "First Name",
                                            style = MaterialTheme.typography.headlineSmall,
                                            color = colorScheme.onBackground,
                                            modifier =
                                                Modifier.padding(start = 3.dp)
                                                    .testTag("firstNameText"))
                                        Spacer(modifier = Modifier.padding(1.5.dp))
                                        QuickFixTextFieldCustom(
                                            value = firstName,
                                            onValueChange = { firstName = it },
                                            placeHolderText = "First Name",
                                            placeHolderColor = colorScheme.onSecondaryContainer,
                                            shape = RoundedCornerShape(12.dp),
                                            moveContentHorizontal = 10.dp,
                                            heightField = 42.dp,
                                            modifier = Modifier.testTag("firstNameInput"))
                                      }

                                      Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
                                        Text(
                                            "Last Name",
                                            style = MaterialTheme.typography.headlineSmall,
                                            color = colorScheme.onBackground,
                                            modifier =
                                                Modifier.padding(start = 3.dp)
                                                    .testTag("lastNameText"))
                                        Spacer(modifier = Modifier.padding(1.5.dp))
                                        QuickFixTextFieldCustom(
                                            value = lastName,
                                            onValueChange = { lastName = it },
                                            placeHolderText = "Last Name ",
                                            placeHolderColor = colorScheme.onSecondaryContainer,
                                            shape = RoundedCornerShape(12.dp),
                                            moveContentHorizontal = 10.dp,
                                            heightField = 42.dp,
                                            modifier = Modifier.testTag("lastNameInput"))
                                      }
                                    }

                                Spacer(modifier = Modifier.padding(6.dp))

                                Column(
                                    modifier = Modifier.fillMaxWidth().padding(start = 8.dp),
                                ) {
                                  Text(
                                      "Email",
                                      style = MaterialTheme.typography.headlineSmall,
                                      color = colorScheme.onBackground,
                                      modifier =
                                          Modifier.padding(start = 3.dp).testTag("emailText"))
                                  Spacer(modifier = Modifier.padding(1.5.dp))
                                  QuickFixTextFieldCustom(
                                      value = email,
                                      onValueChange = {
                                        email = it
                                        emailError = !isValidEmail(it)
                                      },
                                      placeHolderText = "Enter your email address",
                                      placeHolderColor = colorScheme.onSecondaryContainer,
                                      shape = RoundedCornerShape(12.dp),
                                      isError = emailError,
                                      showError = emailError,
                                      errorText = "INVALID EMAIL",
                                      moveContentHorizontal = 10.dp,
                                      heightField = 42.dp,
                                      widthField = 360.dp,
                                      modifier = Modifier.testTag("emailInput"))
                                }

                                Spacer(modifier = Modifier.padding(6.dp))

                                Column(
                                    modifier = Modifier.fillMaxWidth().padding(start = 8.dp),
                                ) {
                                  Text(
                                      "Birthdate",
                                      style = MaterialTheme.typography.headlineSmall,
                                      color = colorScheme.onBackground,
                                      modifier =
                                          Modifier.padding(start = 3.dp).testTag("birthDateText"))
                                  Spacer(modifier = Modifier.padding(1.5.dp))
                                  QuickFixTextFieldCustom(
                                      value = birthDate,
                                      onValueChange = {
                                        birthDate = it
                                        birthDateError = !isValidDate(it)
                                      },
                                      placeHolderText = "Enter your birthdate (DD/MM/YYYY)",
                                      placeHolderColor = colorScheme.onSecondaryContainer,
                                      singleLine = false,
                                      errorText = "INVALID DATE",
                                      isError = birthDateError,
                                      showError = birthDateError,
                                      moveContentHorizontal = 10.dp,
                                      heightField = 42.dp,
                                      widthField = 360.dp,
                                      shape = RoundedCornerShape(12.dp),
                                      modifier = Modifier.testTag("birthDateInput"))
                                }

                                Spacer(modifier = Modifier.padding(6.dp))

                                Column(
                                    modifier = Modifier.fillMaxWidth().padding(start = 8.dp),
                                ) {
                                  Text(
                                      "Password",
                                      style = MaterialTheme.typography.headlineSmall,
                                      color = colorScheme.onBackground,
                                      modifier =
                                          Modifier.padding(start = 3.dp).testTag("passwordText"))
                                  Spacer(modifier = Modifier.padding(1.5.dp))
                                  QuickFixTextFieldCustom(
                                      value = password,
                                      onValueChange = { password = it },
                                      placeHolderText = "Enter your password",
                                      placeHolderColor = colorScheme.onSecondaryContainer,
                                      moveContentHorizontal = 10.dp,
                                      heightField = 42.dp,
                                      widthField = 360.dp,
                                      shape = RoundedCornerShape(12.dp),
                                      trailingIcon = {
                                        val image =
                                            if (passwordVisible) Icons.Filled.VisibilityOff
                                            else Icons.Filled.Visibility
                                        IconButton(
                                            onClick = { passwordVisible = !passwordVisible }) {
                                              Icon(
                                                  imageVector = image,
                                                  contentDescription = null,
                                                  tint = colorScheme.primary)
                                            }
                                      },
                                      showTrailingIcon = { password.isNotEmpty() },
                                      visualTransformation =
                                          if (passwordVisible) VisualTransformation.None
                                          else PasswordVisualTransformation(),
                                      keyboardOptions =
                                          KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                                      modifier = Modifier.testTag("passwordInput"))

                                  Spacer(modifier = Modifier.padding(6.dp))

                                  QuickFixTextFieldCustom(
                                      value = repeatPassword,
                                      onValueChange = { repeatPassword = it },
                                      placeHolderText = "Confirm password",
                                      placeHolderColor = colorScheme.onSecondaryContainer,
                                      moveContentHorizontal = 10.dp,
                                      heightField = 42.dp,
                                      widthField = 360.dp,
                                      shape = RoundedCornerShape(12.dp),
                                      trailingIcon = {
                                        val image =
                                            if (repeatPasswordVisible) Icons.Filled.VisibilityOff
                                            else Icons.Filled.Visibility
                                        IconButton(
                                            onClick = {
                                              repeatPasswordVisible = !repeatPasswordVisible
                                            }) {
                                              Icon(
                                                  imageVector = image,
                                                  contentDescription = null,
                                                  tint = colorScheme.primary)
                                            }
                                      },
                                      showTrailingIcon = { repeatPassword.isNotEmpty() },
                                      visualTransformation =
                                          if (repeatPasswordVisible) VisualTransformation.None
                                          else PasswordVisualTransformation(),
                                      keyboardOptions =
                                          KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                                      modifier = Modifier.testTag("repeatPasswordInput"))
                                }

                                Spacer(modifier = Modifier.padding(3.dp))

                                Row(
                                    modifier =
                                        Modifier.fillMaxWidth()
                                            .padding(horizontal = 8.dp)
                                            .testTag("passwordConditions"),
                                    horizontalArrangement = Arrangement.SpaceBetween) {
                                      Column(modifier = Modifier.padding(vertical = 8.dp)) {
                                        passwordConditions.take(2).forEach { (condition, met) ->
                                          Text(
                                              text = condition,
                                              color =
                                                  if (met ||
                                                      password.isEmpty())
                                                      colorScheme.onSecondaryContainer
                                                  else colorScheme.error,
                                              style = MaterialTheme.typography.bodySmall,
                                              modifier = Modifier.padding(start = 3.dp))
                                        }
                                      }

                                      Column(modifier = Modifier.padding(vertical = 8.dp)) {
                                        passwordConditions.drop(2).forEach { (condition, met) ->
                                          Text(
                                              text = condition,
                                              color =
                                                  if (met ||
                                                      password.isEmpty())
                                                      colorScheme.onSecondaryContainer
                                                  else colorScheme.error,
                                              style = MaterialTheme.typography.bodySmall,
                                              modifier = Modifier.padding(start = 3.dp))
                                        }
                                      }
                                    }

                                // Error message if passwords don't match
                                if (noMatch) {
                                  Text(
                                      "The two password do not match, try again. ",
                                      style = MaterialTheme.typography.bodySmall,
                                      color = colorScheme.error,
                                      modifier =
                                          Modifier.padding(start = 3.dp).testTag("noMatchText"),
                                      textAlign = TextAlign.Start)
                                  Spacer(modifier = Modifier.padding(8.dp))
                                } else {
                                  Spacer(modifier = Modifier.padding(12.dp))
                                }

                                QuickFixCheckBoxRow(
                                    modifier = Modifier.padding(start = 6.dp),
                                    checked = acceptTerms,
                                    onCheckedChange = { acceptTerms = it },
                                    label = "I'm at least 18 and agree to the following",
                                    underlinedText = "Term & Conditions",
                                    onUnderlinedTextClick = { /* TODO: Add click logic */},
                                    labelBis = "and",
                                    underlinedTextBis = "Privacy Policy",
                                    onUnderlinedTextClickBis = { /* TODO: Add click logic */},
                                    colorScheme = colorScheme)

                                QuickFixButton(
                                    buttonText = "Register",
                                    onClickAction = {
                                      shrinkBox = false
                                      navigationActions?.navigateTo(Screen.HOME)
                                    },
                                    buttonColor = colorScheme.primary,
                                    textColor = colorScheme.onPrimary,
                                    textStyle = MaterialTheme.typography.labelLarge,
                                    modifier =
                                        Modifier.width(360.dp)
                                            .height(55.dp)
                                            .testTag("registerButton")
                                            .graphicsLayer(alpha = 1f),
                                    enabled = filledForm)

                                Spacer(modifier = Modifier.padding(4.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                  Text(
                                      "Already have an account ?",
                                      style = MaterialTheme.typography.headlineSmall,
                                      color = colorScheme.onSecondaryContainer,
                                      modifier =
                                          Modifier.padding(bottom = 8.dp)
                                              .requiredWidth(225.dp)
                                              .testTag("alreadyAccountText"),
                                      textAlign = TextAlign.End)

                                  QuickFixButton(
                                      buttonText = "Login !",
                                      onClickAction = {
                                        navigationActions?.navigateTo(Screen.LOGIN)
                                      },
                                      buttonColor = Color.Transparent,
                                      textColor = colorScheme.primary,
                                      textStyle = MaterialTheme.typography.headlineSmall,
                                      contentPadding = PaddingValues(4.dp),
                                      horizontalArrangement = Arrangement.Start,
                                      modifier = Modifier.testTag("clickableLoginButtonText"))
                                }
                                Spacer(modifier = Modifier.padding(bottom = 30.dp))
                              }
                        }
                  }
            })
      }
}
