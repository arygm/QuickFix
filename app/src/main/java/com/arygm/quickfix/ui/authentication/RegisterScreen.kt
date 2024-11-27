package com.arygm.quickfix.ui.authentication

import android.widget.Toast
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.arygm.quickfix.model.account.AccountViewModel
import com.arygm.quickfix.model.account.LoggedInAccountViewModel
import com.arygm.quickfix.model.profile.ProfileViewModel
import com.arygm.quickfix.ui.elements.QuickFixAnimatedBox
import com.arygm.quickfix.ui.elements.QuickFixBackButtonTopBar
import com.arygm.quickfix.ui.elements.QuickFixButton
import com.arygm.quickfix.ui.elements.QuickFixCheckBoxRow
import com.arygm.quickfix.ui.elements.QuickFixTextFieldCustom
import com.arygm.quickfix.ui.navigation.NavigationActions
import com.arygm.quickfix.ui.navigation.Screen
import com.arygm.quickfix.ui.navigation.TopLevelDestinations
import com.arygm.quickfix.utils.ANIMATED_BOX_ROTATION
import com.arygm.quickfix.utils.createAccountWithEmailAndPassword
import com.arygm.quickfix.utils.isValidDate
import com.arygm.quickfix.utils.isValidEmail
import com.google.firebase.auth.FirebaseAuth

@Composable
fun RegisterScreen(
    navigationActions: NavigationActions,
    accountViewModel: AccountViewModel,
    loggedInAccountViewModel: LoggedInAccountViewModel,
    userViewModel: ProfileViewModel,
    firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance(), // Injected dependency
    createAccountFunc:
        (
            firebaseAuth: FirebaseAuth,
            firstName: String,
            lastName: String,
            email: String,
            password: String,
            birthDate: String,
            accountViewModel: AccountViewModel,
            loggedInAccountViewModel: LoggedInAccountViewModel,
            userViewModel: ProfileViewModel,
            onSuccess: () -> Unit,
            onFailure: () -> Unit) -> Unit =
        ::createAccountWithEmailAndPassword // Default implementation
) {
  val context = LocalContext.current
  var firstName by remember { mutableStateOf("") }
  var lastName by remember { mutableStateOf("") }
  var email by remember { mutableStateOf("") }
  var birthDate by remember { mutableStateOf("") }

  var emailError by remember { mutableStateOf(false) }
  var birthDateError by remember { mutableStateOf(false) }

  var acceptTerms by remember { mutableStateOf(false) }

  var password by remember { mutableStateOf("") }
  var passwordVisible by remember { mutableStateOf(false) }
  var repeatPasswordVisible by remember { mutableStateOf(false) }
  var repeatPassword by remember { mutableStateOf("") }

  val noMatch by remember {
    derivedStateOf { password != repeatPassword && repeatPassword.isNotEmpty() }
  }

  val passwordConditions1 =
      listOf(
          "• At least 8 characters" to (password.length >= 8),
          "• Contains an uppercase letter (A-Z)" to password.any { it.isUpperCase() })

  val passwordConditions2 =
      listOf(
          "• Contains a lowercase letter (a-z)" to password.any { it.isLowerCase() },
          "• Contains a digit (0-9)" to password.any { it.isDigit() })

  var shrinkBox by remember { mutableStateOf(false) }
  val boxOffsetX by
      animateDpAsState(
          targetValue = if (shrinkBox) 1285.dp else 0.dp,
          animationSpec = tween(durationMillis = 300),
          label = "shrinkingBox")

  LaunchedEffect(Unit) { shrinkBox = true }

  val focusManager = LocalFocusManager.current

  val filledForm =
      firstName.isNotEmpty() &&
          lastName.isNotEmpty() &&
          email.isNotEmpty() &&
          birthDate.isNotEmpty() &&
          passwordConditions1.all { it.second } &&
          passwordConditions2.all { it.second } &&
          !noMatch &&
          repeatPassword.isNotEmpty() &&
          acceptTerms
  BoxWithConstraints(
      modifier =
          Modifier.fillMaxSize().testTag("InfoBox").pointerInput(Unit) {
            detectTapGestures(onTap = { focusManager.clearFocus() })
          }) {
        val screenWidth = maxWidth
        val screenHeight = maxHeight
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
                              modifier = Modifier.fillMaxWidth())

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
                                  navigationActions.goBack()
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
                                  Modifier.size(
                                          screenWidth *
                                              0.5f) // Scale box size to be relative to screen size
                                      .align(Alignment.BottomStart)
                                      .offset(
                                          x =
                                              -screenWidth *
                                                  0.4f, // Offset slightly left relative to screen
                                          // width
                                          y =
                                              screenHeight *
                                                  0.1f // Offset slightly upward relative to screen
                                          // height
                                          )
                                      .graphicsLayer(rotationZ = ANIMATED_BOX_ROTATION)
                                      .background(colorScheme.primary)
                                      .testTag("BoxDecoration"))
                          Column(
                              modifier =
                                  Modifier.align(Alignment.Center)
                                      .padding(screenWidth * 0.05f)
                                      .zIndex(100f), // Ensure it's on top
                              horizontalAlignment = Alignment.CenterHorizontally,
                              verticalArrangement = Arrangement.Center) {
                                Text(
                                    "Register Now",
                                    style = MaterialTheme.typography.headlineLarge,
                                    color = colorScheme.primary,
                                    modifier = Modifier.testTag("welcomeText"))

                                Spacer(modifier = Modifier.height(screenHeight * 0.01f))

                                Text(
                                    "Join QuickFix to connect with skilled workers!",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = colorScheme.onSecondaryContainer,
                                    modifier = Modifier.testTag("welcomeTextBis"))

                                Spacer(modifier = Modifier.height(screenHeight * 0.02f))

                                Row(
                                    modifier =
                                        Modifier.fillMaxWidth()
                                            .height(screenHeight * 0.07f)
                                            .padding(start = screenWidth * 0.02f),
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

                                Spacer(modifier = Modifier.padding(screenHeight * 0.008f))

                                Column(
                                    modifier =
                                        Modifier.fillMaxWidth()
                                            .padding(start = screenWidth * 0.02f),
                                ) {
                                  QuickFixTextFieldCustom(
                                      modifier = Modifier.testTag("emailInput"),
                                      value = email,
                                      onValueChange = {
                                        email = it
                                        emailError = !isValidEmail(it)
                                        accountViewModel.accountExists(email) { exists, account ->
                                          emailError =
                                              if (exists && account != null) {
                                                true
                                              } else {
                                                !isValidEmail(it)
                                              }
                                        }
                                      },
                                      placeHolderText = "Enter your email address",
                                      shape = RoundedCornerShape(12.dp),
                                      placeHolderColor = colorScheme.onSecondaryContainer,
                                      widthField = screenWidth * 0.85f,
                                      heightField = screenHeight * 0.05f,
                                      moveContentHorizontal = screenWidth * 0.02f,
                                      isError = emailError,
                                      errorText = "INVALID EMAIL",
                                      showError = emailError,
                                      showLabel = true,
                                      label = {
                                        Text(
                                            "Email",
                                            style = MaterialTheme.typography.headlineSmall,
                                            color = colorScheme.onBackground,
                                            modifier =
                                                Modifier.padding(start = screenWidth * 0.01f)
                                                    .testTag("emailText"))
                                      })
                                }

                                Spacer(modifier = Modifier.padding(screenHeight * 0.008f))

                                Column(
                                    modifier = Modifier.fillMaxWidth().padding(start = 8.dp),
                                ) {
                                  QuickFixTextFieldCustom(
                                      modifier = Modifier.testTag("birthDateInput"),
                                      value = birthDate,
                                      singleLine = false,
                                      onValueChange = {
                                        birthDate = it
                                        birthDateError = !isValidDate(it)
                                      },
                                      placeHolderText = "Enter your birthdate (DD/MM/YYYY)",
                                      shape = RoundedCornerShape(12.dp),
                                      placeHolderColor = colorScheme.onSecondaryContainer,
                                      widthField = screenWidth * 0.85f,
                                      heightField = screenHeight * 0.05f,
                                      moveContentHorizontal = screenWidth * 0.02f,
                                      isError = birthDateError,
                                      errorText = "INVALID DATE",
                                      showError = birthDateError,
                                      showLabel = true,
                                      label = {
                                        Text(
                                            "Birthdate",
                                            style = MaterialTheme.typography.headlineSmall,
                                            color = colorScheme.onBackground,
                                            modifier =
                                                Modifier.padding(screenWidth * 0.01f)
                                                    .testTag("birthDateText"))
                                      })
                                }

                                Spacer(modifier = Modifier.padding(screenHeight * 0.008f))

                                Column(
                                    modifier =
                                        Modifier.fillMaxWidth()
                                            .padding(start = screenWidth * 0.02f),
                                ) {
                                  QuickFixTextFieldCustom(
                                      modifier = Modifier.testTag("passwordInput"),
                                      value = password,
                                      onValueChange = { password = it },
                                      showTrailingIcon = { password.isNotEmpty() },
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
                                      placeHolderText = "Enter your password",
                                      shape = RoundedCornerShape(12.dp),
                                      placeHolderColor = colorScheme.onSecondaryContainer,
                                      widthField = screenWidth * 0.85f,
                                      heightField = screenHeight * 0.05f,
                                      moveContentHorizontal = screenWidth * 0.02f,
                                      keyboardOptions =
                                          KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                                      visualTransformation =
                                          if (passwordVisible) VisualTransformation.None
                                          else PasswordVisualTransformation(),
                                      showLabel = true,
                                      label = {
                                        Text(
                                            "Password",
                                            style = MaterialTheme.typography.headlineSmall,
                                            color = colorScheme.onBackground,
                                            modifier =
                                                Modifier.padding(start = screenWidth * 0.01f)
                                                    .testTag("passwordText"))
                                      })

                                  Spacer(modifier = Modifier.padding(screenHeight * 0.008f))

                                  QuickFixTextFieldCustom(
                                      modifier = Modifier.testTag("repeatPasswordInput"),
                                      value = repeatPassword,
                                      onValueChange = { repeatPassword = it },
                                      showTrailingIcon = { repeatPassword.isNotEmpty() },
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
                                      placeHolderText = "Confirm password",
                                      shape = RoundedCornerShape(12.dp),
                                      placeHolderColor = colorScheme.onSecondaryContainer,
                                      widthField = screenWidth * 0.85f,
                                      heightField = screenHeight * 0.05f,
                                      moveContentHorizontal = screenWidth * 0.02f,
                                      keyboardOptions =
                                          KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                                      visualTransformation =
                                          if (repeatPasswordVisible) VisualTransformation.None
                                          else PasswordVisualTransformation()
                                  )
                                }

                                Spacer(modifier = Modifier.padding(screenHeight * 0.005f))

                                Row(
                                    modifier =
                                        Modifier.fillMaxWidth()
                                            .padding(horizontal = screenWidth * 0.02f)
                                            .testTag("passwordConditions"),
                                    horizontalArrangement = Arrangement.SpaceBetween) {
                                      PasswordConditions(
                                          password, passwordConditions1, screenWidth, screenHeight)
                                      PasswordConditions(
                                          password, passwordConditions2, screenWidth, screenHeight)
                                    }

                                // Error message if passwords don't match
                                if (noMatch) {
                                  Text(
                                      "The two password do not match, try again. ",
                                      style = MaterialTheme.typography.bodySmall,
                                      color = colorScheme.error,
                                      modifier =
                                          Modifier.padding(start = screenWidth * 0.02f)
                                              .testTag("noMatchText"),
                                      textAlign = TextAlign.Start)
                                  Spacer(modifier = Modifier.padding(screenHeight * 0.005f))
                                } else {
                                  Spacer(modifier = Modifier.padding(screenHeight * 0.01f))
                                }

                                QuickFixCheckBoxRow(
                                    modifier = Modifier.padding(start = screenWidth * 0.01f),
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
                                      createAccountFunc(
                                          firebaseAuth,
                                          firstName,
                                          lastName,
                                          email,
                                          password,
                                          birthDate,
                                          accountViewModel,
                                          loggedInAccountViewModel,
                                          userViewModel,
                                          {
                                            navigationActions.navigateTo(TopLevelDestinations.HOME)
                                          },
                                          {
                                            Toast.makeText(
                                                    context,
                                                    "Registration Failed.",
                                                    Toast.LENGTH_LONG)
                                                .show()
                                          })
                                    },
                                    buttonColor = colorScheme.primary,
                                    textColor = colorScheme.onPrimary,
                                    textStyle = MaterialTheme.typography.labelLarge,
                                    modifier =
                                        Modifier.width(screenWidth * 0.85f)
                                            .height(screenHeight * 0.07f)
                                            .testTag("registerButton")
                                            .graphicsLayer(alpha = 1f),
                                    enabled = filledForm && !emailError && !birthDateError)

                                Spacer(modifier = Modifier.padding(screenHeight * 0.001f))

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
                                          Modifier.padding(bottom = screenHeight * 0.01f)
                                              .requiredWidth(screenWidth * 0.55f)
                                              .testTag("alreadyAccountText"),
                                      textAlign = TextAlign.End)

                                  QuickFixButton(
                                      buttonText = "Login !",
                                      onClickAction = {
                                        navigationActions.navigateTo(Screen.LOGIN)
                                      },
                                      buttonColor = Color.Transparent,
                                      textColor = colorScheme.primary,
                                      textStyle = MaterialTheme.typography.headlineSmall,
                                      contentPadding = PaddingValues(4.dp),
                                      horizontalArrangement = Arrangement.Start,
                                      modifier = Modifier.testTag("clickableLoginButtonText"))
                                }
                                Spacer(modifier = Modifier.padding(bottom = screenHeight * 0.02f))
                              }
                        }
                  }
            })
      }
}

@Composable
private fun PasswordConditions(
    password: String,
    listConditions: List<Pair<String, Boolean>>,
    screenWidth: Dp,
    screenHeight: Dp
) {
  Column(modifier = Modifier.padding(vertical = screenHeight * 0.005f)) {
    listConditions.forEach { (condition, met) ->
      Text(
          text = condition,
          color =
              if (met || password.isEmpty()) colorScheme.onSecondaryContainer
              else colorScheme.error,
          style = MaterialTheme.typography.bodySmall,
          modifier = Modifier.padding(start = screenWidth * 0.01f))
    }
  }
}

@Composable
fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeHolderText: String,
    placeHolderColor: Color,
    label: String,
    columnModifier: Modifier = Modifier,
    isError: Boolean = false,
    showError: Boolean = false,
    errorText: String = "",
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailingIcon: @Composable (() -> Unit)? = null,
    showTrailingIcon: () -> Boolean = { false },
    modifier: Modifier = Modifier
) {
  Column(modifier = columnModifier.padding(end = 12.dp)) {
    QuickFixTextFieldCustom(
        modifier = modifier,
        value = value,
        onValueChange = onValueChange,
        showTrailingIcon = showTrailingIcon,
        trailingIcon = trailingIcon,
        placeHolderText = placeHolderText,
        shape = RoundedCornerShape(12.dp),
        placeHolderColor = placeHolderColor,
        heightField = 42.dp,
        moveContentHorizontal = 10.dp,
        isError = isError,
        errorText = errorText,
        showError = showError,
        keyboardOptions = keyboardOptions,
        visualTransformation = visualTransformation,
        showLabel = true,
        label = {
          Text(
              label,
              style = MaterialTheme.typography.headlineSmall,
              color = colorScheme.onBackground,
              modifier = Modifier.padding(start = 3.dp))
        })
  }
}
