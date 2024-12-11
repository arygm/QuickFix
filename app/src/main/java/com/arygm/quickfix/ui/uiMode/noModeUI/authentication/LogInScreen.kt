package com.arygm.quickfix.ui.authentication

import android.util.Log
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
import com.arygm.quickfix.model.account.AccountViewModel
import com.arygm.quickfix.model.offline.small.PreferencesViewModel
import com.arygm.quickfix.ui.elements.QuickFixAnimatedBox
import com.arygm.quickfix.ui.elements.QuickFixBackButtonTopBar
import com.arygm.quickfix.ui.elements.QuickFixButton
import com.arygm.quickfix.ui.elements.QuickFixTextFieldCustom
import com.arygm.quickfix.ui.navigation.NavigationActions
import com.arygm.quickfix.ui.navigation.RootRoute
import com.arygm.quickfix.ui.noModeUI.navigation.NoModeRoute
import com.arygm.quickfix.utils.ANIMATED_BOX_ROTATION
import com.arygm.quickfix.utils.BOX_COLLAPSE_SPEED
import com.arygm.quickfix.utils.BOX_OFFSET_X_EXPANDED
import com.arygm.quickfix.utils.BOX_OFFSET_X_SHRUNK
import com.arygm.quickfix.utils.isValidEmail
import com.arygm.quickfix.utils.signInWithEmailAndFetchAccount
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// make relative
@Composable
fun LogInScreen(
    navigationActions: NavigationActions,
    accountViewModel: AccountViewModel,
    preferencesViewModel: PreferencesViewModel,
    rootNavigationActions: NavigationActions
) {
  var errorHasOccurred by remember { mutableStateOf(false) }
  var emailError = false

  var email by remember { mutableStateOf("") }
  var password by remember { mutableStateOf("") }
  var passwordVisible by remember { mutableStateOf(false) }

  var shrinkBox by remember { mutableStateOf(false) }
  val boxOffsetX by
      animateDpAsState(
          targetValue = if (shrinkBox) BOX_OFFSET_X_SHRUNK else BOX_OFFSET_X_EXPANDED,
          animationSpec = tween(durationMillis = BOX_COLLAPSE_SPEED),
          label = "shrinkingBox")

  val filledForm = email.isNotEmpty() && password.isNotEmpty()

  val coroutineScope = rememberCoroutineScope()

  LaunchedEffect(Unit) { shrinkBox = true }
  BoxWithConstraints(modifier = Modifier.fillMaxSize().testTag("LoginBox")) {
    val screenWidth = maxWidth
    val screenHeight = maxHeight

    QuickFixAnimatedBox(boxOffsetX)
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
                                painterResource(id = com.arygm.quickfix.R.drawable.worker_image),
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
                      Box(
                          modifier =
                              Modifier.size(
                                      screenWidth *
                                          0.5f) // Scale box size to be relative to screen size
                                  .align(Alignment.BottomStart)
                                  .offset(
                                      x =
                                          -screenWidth *
                                              0.4f, // Offset slightly left relative to screen width
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
                                  .padding(
                                      screenWidth * 0.05f) // Relative padding based on screen width
                                  .zIndex(100f),
                          horizontalAlignment = Alignment.CenterHorizontally,
                          verticalArrangement = Arrangement.Center) {
                            Text(
                                "Login",
                                style = MaterialTheme.typography.headlineLarge,
                                color = colorScheme.primary,
                                modifier = Modifier.testTag("WelcomeText"))

                            Spacer(
                                modifier =
                                    Modifier.height(screenHeight * 0.01f)) // Small vertical spacing

                            Text(
                                "Your perfect fix is just a click away!",
                                style = MaterialTheme.typography.labelSmall,
                                color = colorScheme.onSecondaryContainer,
                                modifier = Modifier.testTag("WelcomeTextBis"))

                            Spacer(
                                modifier =
                                    Modifier.height(
                                        screenHeight * 0.02f)) // Slightly larger vertical spacing

                            QuickFixTextFieldCustom(
                                value = email,
                                onValueChange = {
                                  email = it
                                  accountViewModel.accountExists(email) { exists, profile ->
                                    emailError =
                                        if (exists && profile != null) {
                                          !isValidEmail(it)
                                        } else {
                                          true
                                        }
                                  }
                                },
                                shape = RoundedCornerShape(12.dp),
                                widthField =
                                    screenWidth * 0.9f, // Relative width for the text field
                                moveContentHorizontal =
                                    screenWidth * 0.02f, // Relative padding for content
                                placeHolderText = "Username or Email",
                                isError =
                                    email.isNotEmpty() && (!isValidEmail(email) || emailError),
                                errorText = "INVALID EMAIL",
                                showError =
                                    email.isNotEmpty() && (!isValidEmail(email) || emailError),
                                modifier = Modifier.testTag("inputEmail"))

                            Spacer(modifier = Modifier.height(screenHeight * 0.02f))

                            QuickFixTextFieldCustom(
                                value = password,
                                modifier = Modifier.testTag("inputPassword"),
                                onValueChange = { password = it },
                                placeHolderText = "Password",
                                shape = RoundedCornerShape(12.dp),
                                widthField = screenWidth * 0.9f,
                                moveContentHorizontal = screenWidth * 0.02f,
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

                            Spacer(modifier = Modifier.height(screenHeight * 0.01f))

                            QuickFixButton(
                                buttonText = "Forgot your password?",
                                onClickAction = {
                                  navigationActions.navigateTo(NoModeRoute.RESET_PASSWORD)
                                },
                                buttonColor = Color.Transparent,
                                textColor = colorScheme.primary,
                                textStyle = MaterialTheme.typography.headlineSmall,
                                horizontalArrangement = Arrangement.End,
                                contentPadding = PaddingValues(0.dp),
                                modifier =
                                    Modifier.align(Alignment.End)
                                        .testTag("forgetPasswordButtonText"))

                            Spacer(modifier = Modifier.height(screenHeight * 0.02f))

                            QuickFixButton(
                                buttonText = "LOGIN",
                                onClickAction = {
                                  signInWithEmailAndFetchAccount(
                                      email = email,
                                      password = password,
                                      accountViewModel = accountViewModel,
                                      preferencesViewModel = preferencesViewModel,
                                      onResult = {
                                        if (it) {
                                          coroutineScope.launch {
                                            shrinkBox = false
                                            delay(BOX_COLLAPSE_SPEED.toLong())
                                            Log.d("LoginFlow", "Starting login with email: $email")
                                            rootNavigationActions.navigateTo(RootRoute.APP_CONTENT)
                                            navigationActions.navigateTo(NoModeRoute.WELCOME)
                                          }
                                        } else {
                                          Log.e("LogInScreen", "Error occurred while signing in")
                                          errorHasOccurred = true
                                        }
                                      })
                                },
                                buttonColor = colorScheme.primary,
                                textColor = colorScheme.onPrimary,
                                textStyle = MaterialTheme.typography.labelLarge,
                                modifier =
                                    Modifier.width(screenWidth * 0.9f)
                                        .height(screenHeight * 0.06f)
                                        .testTag("logInButton"),
                                enabled = filledForm && isValidEmail(email))

                            Spacer(modifier = Modifier.height(screenHeight * 0.01f))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                              Text(
                                  "Don't have an account?",
                                  style = MaterialTheme.typography.headlineSmall,
                                  color = colorScheme.onSecondaryContainer,
                                  modifier =
                                      Modifier.padding(bottom = screenHeight * 0.01f)
                                          .testTag("noAccountText"),
                                  textAlign = TextAlign.End)

                              QuickFixButton(
                                  buttonText = "Create one!",
                                  onClickAction = {
                                    navigationActions.navigateTo(NoModeRoute.REGISTER)
                                  },
                                  buttonColor = Color.Transparent,
                                  textColor = colorScheme.primary,
                                  textStyle = MaterialTheme.typography.headlineSmall,
                                  contentPadding = PaddingValues(screenWidth * 0.01f),
                                  horizontalArrangement = Arrangement.Start,
                                  modifier = Modifier.testTag("clickableCreateAccount"))
                            }

                            if (errorHasOccurred) {
                              Text(
                                  "INVALID EMAIL OR PASSWORD, TRY AGAIN.",
                                  style = MaterialTheme.typography.labelSmall,
                                  color = colorScheme.error,
                                  modifier =
                                      Modifier.padding(start = screenWidth * 0.01f)
                                          .testTag("errorText"))
                              Spacer(modifier = Modifier.height(screenHeight * 0.04f))
                            } else {
                              Spacer(modifier = Modifier.height(screenHeight * 0.05f))
                            }
                          }
                    }
              }
        })
  }
}
