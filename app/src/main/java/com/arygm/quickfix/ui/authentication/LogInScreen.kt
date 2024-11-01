package com.arygm.quickfix.ui.authentication

import android.util.Log
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import com.arygm.quickfix.model.profile.LoggedInProfileViewModel
import com.arygm.quickfix.model.profile.ProfileViewModel
import com.arygm.quickfix.ui.elements.QuickFixAnimatedBox
import com.arygm.quickfix.ui.elements.QuickFixBackButtonTopBar
import com.arygm.quickfix.ui.elements.QuickFixButton
import com.arygm.quickfix.ui.elements.QuickFixTextFieldCustom
import com.arygm.quickfix.ui.navigation.NavigationActions
import com.arygm.quickfix.ui.navigation.Screen
import com.arygm.quickfix.utils.BOX_COLLAPSE_SPEED
import com.arygm.quickfix.utils.BOX_OFFSET_X_EXPANDED
import com.arygm.quickfix.utils.BOX_OFFSET_X_SHRUNK
import com.arygm.quickfix.utils.isValidEmail
import com.arygm.quickfix.utils.signInWithEmailAndFetchProfile
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
  val boxOffsetX by
      animateDpAsState(
          targetValue = if (shrinkBox) BOX_OFFSET_X_SHRUNK else BOX_OFFSET_X_EXPANDED,
          animationSpec = tween(durationMillis = BOX_COLLAPSE_SPEED),
          label = "shrinkingBox")

  val filledForm = email.isNotEmpty() && password.isNotEmpty()

  val coroutineScope = rememberCoroutineScope()

  LaunchedEffect(Unit) { shrinkBox = true }
  Box(modifier = Modifier.fillMaxSize().testTag("LoginBox")) {
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
                                "Login",
                                style = MaterialTheme.typography.headlineLarge,
                                color = colorScheme.primary,
                                modifier = Modifier.testTag("WelcomeText"))

                            Spacer(modifier = Modifier.padding(3.dp))

                            Text(
                                "Your perfect fix is just a click away!",
                                style = MaterialTheme.typography.labelSmall,
                                color = colorScheme.onSecondaryContainer,
                                modifier = Modifier.testTag("WelcomeTextBis"))

                            Spacer(modifier = Modifier.padding(10.dp))

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
                                shape = RoundedCornerShape(12.dp),
                                widthField = 360.dp,
                                moveContentHorizontal = 10.dp,
                                placeHolderText = "Username or Email",
                                isError =
                                    email.isNotEmpty() && (!isValidEmail(email) || emailError),
                                errorText = "INVALID EMAIL",
                                showError =
                                    email.isNotEmpty() && (!isValidEmail(email) || emailError),
                                modifier = Modifier.testTag("inputEmail"))

                            Spacer(modifier = Modifier.padding(10.dp))

                            QuickFixTextFieldCustom(
                                value = password,
                                onValueChange = { password = it },
                                placeHolderText = "Password",
                                shape = RoundedCornerShape(12.dp),
                                widthField = 360.dp,
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
                                modifier = Modifier.testTag("inputPassword"))

                            Spacer(modifier = Modifier.padding(4.dp))

                            QuickFixButton(
                                buttonText = "Forgot your password?",
                                onClickAction = {
                                  navigationActions.navigateTo(Screen.RESET_PASSWORD)
                                },
                                buttonColor = Color.Transparent,
                                textColor = colorScheme.primary,
                                textStyle = MaterialTheme.typography.headlineSmall,
                                horizontalArrangement = Arrangement.End,
                                contentPadding = PaddingValues(0.dp),
                                modifier =
                                    Modifier.align(Alignment.End)
                                        .testTag("forgetPasswordButtonText"))

                            Spacer(modifier = Modifier.padding(7.dp))

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
                                            navigationActions.navigateTo(Screen.HOME)
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
                                    Modifier.width(360.dp).height(55.dp).testTag("logInButton"),
                                enabled = filledForm && isValidEmail(email))

                            Spacer(modifier = Modifier.padding(4.dp))

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
