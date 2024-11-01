package com.arygm.quickfix.ui.authentication

import android.util.Log
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.arygm.quickfix.model.profile.LoggedInProfileViewModel
import com.arygm.quickfix.model.profile.ProfileViewModel
import com.arygm.quickfix.ui.elements.QuickFixAnimatedBox
import com.arygm.quickfix.ui.elements.QuickFixBackButtonTopBar
import com.arygm.quickfix.ui.elements.QuickFixButton
import com.arygm.quickfix.ui.elements.QuickFixTextFieldCustom
import com.arygm.quickfix.ui.navigation.NavigationActions
import com.arygm.quickfix.ui.navigation.Screen
import com.arygm.quickfix.ui.navigation.TopLevelDestinations
import com.arygm.quickfix.utils.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun LogInScreen(
    navigationActions: NavigationActions,
    userViewModel: ProfileViewModel,
    loggedInProfileViewModel: LoggedInProfileViewModel
) {
  var errorHasOccurred by remember { mutableStateOf(false) }
  var emailError = false

  var email by remember { mutableStateOf("") }
  var password by remember { mutableStateOf("") }
  var passwordVisible by remember { mutableStateOf(false) }

  var shrinkBox by remember { mutableStateOf(false) }

  val filledForm = email.isNotEmpty() && password.isNotEmpty()

  val coroutineScope = rememberCoroutineScope()

  LaunchedEffect(Unit) { shrinkBox = true }
  BoxWithConstraints(modifier = Modifier.fillMaxSize().testTag("LoginBox")) {
    val screenWidth = maxWidth
    val screenHeight = maxHeight

    // Calculate ratios based on the reference dimensions
    val widthRatio = screenWidth / referenceWidth
    val heightRatio = screenHeight / referenceHeight
    val boxOffsetX by
        animateDpAsState(
            targetValue =
                if (shrinkBox) BOX_OFFSET_X_SHRUNK * widthRatio
                else BOX_OFFSET_X_EXPANDED * widthRatio,
            animationSpec = tween(durationMillis = BOX_COLLAPSE_SPEED),
            label = "shrinkingBox")

    QuickFixAnimatedBox(boxOffsetX, widthRatio = widthRatio, heightRatio = heightRatio)
    Scaffold(
        modifier =
            Modifier.background(colorScheme.background).fillMaxSize().testTag("LoginScaffold"),
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
                      Box(modifier = Modifier.fillMaxSize()) {
                        Image(
                            painter =
                                painterResource(
                                    id =
                                        com.arygm.quickfix.R.drawable
                                            .worker_image), // Replace with your image resource
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize().testTag("topBarLoginBackground"))
                        QuickFixBackButtonTopBar(
                            onBackClick = {
                              shrinkBox = false
                              coroutineScope.launch {
                                delay(BOX_COLLAPSE_SPEED.toLong())
                                navigationActions.goBack()
                              }
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
                                    RoundedCornerShape(12.dp)) // Ensure content is above TopAppBar
                    ) {
                      // Adjusted Box using ratios
                      val relativeOffsetX = (-150).dp * widthRatio
                      val relativeOffsetY = 100.dp * heightRatio

                      Box(
                          modifier =
                              Modifier.align(Alignment.BottomStart)
                                  .size(180.dp * widthRatio, 180.dp * heightRatio)
                                  .offset(x = relativeOffsetX, y = relativeOffsetY)
                                  .graphicsLayer(rotationZ = 57f)
                                  .background(colorScheme.primary)
                                  .testTag("BoxDecoration"))

                      Column(
                          modifier =
                              Modifier.align(Alignment.Center)
                                  .padding(16.dp)
                                  .zIndex(100f)
                                  .verticalScroll(rememberScrollState()), // Ensure it's on top
                          horizontalAlignment = Alignment.CenterHorizontally,
                          verticalArrangement = Arrangement.spacedBy(8.dp * heightRatio)) {
                            Text(
                                "Login",
                                style = MaterialTheme.typography.headlineLarge,
                                color = colorScheme.primary,
                                modifier = Modifier.testTag("WelcomeText"))

                            Text(
                                "Your perfect fix is just a click away!",
                                style = MaterialTheme.typography.labelSmall,
                                color = colorScheme.onSecondaryContainer,
                                modifier = Modifier.testTag("WelcomeTextBis"))

                            QuickFixTextFieldCustom(
                                value = email,
                                onValueChange = {
                                  email = it
                                  userViewModel.profileExists(email) { exists, profile ->
                                    emailError =
                                        if (exists && profile != null) {
                                          !isValidEmail(it)
                                        } else {
                                          true
                                        }
                                  }
                                },
                                placeHolderText = "Username or Email",
                                widthField = 330.dp * widthRatio,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.testTag("inputEmail"),
                                moveContentHorizontal = 10.dp,
                                isError =
                                    email.isNotEmpty() && (!isValidEmail(email) || emailError),
                                errorText = "INVALID EMAIL",
                                showError =
                                    email.isNotEmpty() && (!isValidEmail(email) || emailError),
                            )

                            QuickFixTextFieldCustom(
                                value = password,
                                widthField = 330.dp * widthRatio,
                                onValueChange = { password = it },
                                placeHolderText = "Password",
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.testTag("inputPassword"),
                                moveContentHorizontal = 10.dp,
                                trailingIcon = {
                                  val image =
                                      if (passwordVisible) Icons.Filled.VisibilityOff
                                      else Icons.Filled.Visibility
                                  IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                    Icon(
                                        imageVector = image,
                                        contentDescription = null,
                                        tint = colorScheme.primary)
                                  }
                                },
                                visualTransformation =
                                    if (passwordVisible) VisualTransformation.None
                                    else PasswordVisualTransformation(),
                            )

                            QuickFixButton(
                                buttonText = "Forgot your password?",
                                onClickAction = { /* Navigate to forgot screen */},
                                buttonColor = Color.Transparent,
                                textColor = colorScheme.primary,
                                textStyle = MaterialTheme.typography.headlineSmall,
                                horizontalArrangement = Arrangement.End,
                                modifier =
                                    Modifier.align(Alignment.End)
                                        .testTag("forgetPasswordButtonText"))

                            QuickFixButton(
                                buttonText = "LOGIN",
                                onClickAction = {
                                  shrinkBox = false
                                  signInWithEmailAndFetchProfile(
                                      email = email,
                                      password = password,
                                      userViewModel = userViewModel,
                                      loggedInProfileViewModel = loggedInProfileViewModel,
                                      onResult = {
                                        if (it) {
                                          coroutineScope.launch {
                                            delay(BOX_COLLAPSE_SPEED.toLong())
                                            Log.d("LoginFlow", "Starting login with email: $email")
                                            navigationActions.navigateTo(TopLevelDestinations.HOME)
                                          }
                                        } else {
                                          Log.e("LogInScreen", "Error occurred while signing in")
                                          Log.e(
                                              "email don't exist",
                                              "Error occurred while signing here's the email: $email")
                                          errorHasOccurred = true
                                        }
                                      })
                                },
                                buttonColor = colorScheme.primary,
                                textColor = colorScheme.onPrimary,
                                textStyle = MaterialTheme.typography.labelLarge,
                                modifier =
                                    Modifier.fillMaxWidth()
                                        .height(55.dp)
                                        .padding(horizontal = 16.dp)
                                        .testTag("logInButton"),
                                enabled = filledForm && isValidEmail(email))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                              Text(
                                  "Don't have an account ?",
                                  style = MaterialTheme.typography.headlineSmall,
                                  color = colorScheme.onSecondaryContainer,
                                  modifier =
                                      Modifier.padding(bottom = 8.dp)
                                          .requiredWidth(200.dp)
                                          .testTag("noAccountText"),
                                  textAlign = TextAlign.End)

                              QuickFixButton(
                                  buttonText = "Create one !",
                                  onClickAction = { navigationActions.navigateTo(Screen.REGISTER) },
                                  buttonColor = Color.Transparent,
                                  textColor = colorScheme.primary,
                                  textStyle = MaterialTheme.typography.headlineSmall,
                                  contentPadding = PaddingValues(4.dp),
                                  horizontalArrangement = Arrangement.Start,
                                  modifier = Modifier.testTag("clickableCreateAccount"))
                            }

                            if (errorHasOccurred) {
                              Text(
                                  "INVALID EMAIL OR PASSWORD, TRY AGAIN.",
                                  style = MaterialTheme.typography.labelSmall,
                                  color = colorScheme.error,
                                  modifier = Modifier.padding(start = 3.dp).testTag("errorText"))
                              Spacer(modifier = Modifier.padding(32.9.dp))
                            } else {
                              Spacer(modifier = Modifier.padding(40.dp))
                            }
                          }
                    }
              }
        })
  }
}
