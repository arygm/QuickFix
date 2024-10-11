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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.arygm.quickfix.ui.elements.QuickFixAnimatedBox
import com.arygm.quickfix.ui.elements.QuickFixBackButton
import com.arygm.quickfix.ui.navigation.NavigationActions
import com.arygm.quickfix.utils.BOX_COLLAPSE_SPEED
import com.arygm.quickfix.utils.BOX_OFFSET_X_EXPANDED
import com.arygm.quickfix.utils.BOX_OFFSET_X_SHRUNK
import com.arygm.quickfix.utils.isValidEmail

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun LogInScreen(navigationActions: NavigationActions) {

  var errorHasOccured by remember { mutableStateOf(false) }

  var email by remember { mutableStateOf("") }
  var password by remember { mutableStateOf("") }

  var shrinkBox by remember { mutableStateOf(false) }
  val boxOffsetX by
      animateDpAsState(
          targetValue = if (shrinkBox) BOX_OFFSET_X_SHRUNK else BOX_OFFSET_X_EXPANDED,
          animationSpec = tween(durationMillis = BOX_COLLAPSE_SPEED),
          label = "shrinkingBox")

  LaunchedEffect(Unit) { shrinkBox = true }

  val filledForm = email.isNotEmpty() && password.isNotEmpty()

  Box(modifier = Modifier.fillMaxSize().testTag("LoginBox")) {
    QuickFixAnimatedBox(boxOffsetX)

    Scaffold(
        modifier = Modifier.background(colorScheme.background).testTag("LoginScaffold"),
        topBar = {
          TopAppBar(
              title = { Text("") },
              navigationIcon = {
                QuickFixBackButton(
                    onClick = {
                      shrinkBox = false
                      navigationActions.goBack()
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
                      .testTag("ContentBox")) {
                Box(
                    modifier =
                        Modifier.align(Alignment.BottomStart)
                            .size(180.dp, 180.dp)
                            .offset(x = (-150).dp, y = 64.dp)
                            .graphicsLayer(rotationZ = 57f)
                            .background(colorScheme.primary)
                            .zIndex(0f)
                            .testTag("BoxDecoration"))

                Column(
                    modifier = Modifier.fillMaxSize().padding(start = 24.dp),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.Top) {
                      Spacer(modifier = Modifier.padding(100.dp))

                      Text(
                          "WELCOME BACK",
                          style = MaterialTheme.typography.headlineLarge,
                          color = colorScheme.primary,
                          modifier = Modifier.testTag("WelcomeText"))

                      Spacer(modifier = Modifier.padding(6.dp))

                      QuickFixTextField(
                          value = email,
                          onValueChange = { email = it },
                          label = "E-MAIL ADDRESS",
                          isError = !isValidEmail(email),
                          errorText = "INVALID EMAIL",
                          modifier = Modifier.width(360.dp).testTag("inputEmail"),
                          showError = email.isNotEmpty() && !isValidEmail(email))

                      Spacer(modifier = Modifier.padding(3.dp))

                      QuickFixTextField(
                          value = password,
                          onValueChange = { password = it },
                          label = "PASSWORD",
                          visualTransformation = PasswordVisualTransformation(),
                          modifier = Modifier.width(360.dp).testTag("inputPassword"))

                      Spacer(modifier = Modifier.padding(3.dp))

                      Row(modifier = Modifier.padding(start = 3.dp)) {
                        Text(
                            "FORGOT YOUR PASSWORD ? TRY",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFFC0C0C0),
                            modifier = Modifier.testTag("forgotText"))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            "THIS.",
                            style = MaterialTheme.typography.labelSmall,
                            textDecoration = TextDecoration.Underline,
                            color = colorScheme.primary,
                            modifier = Modifier.testTag("clickableFG"))
                      }

                      if (errorHasOccured) {
                        Text(
                            "INVALID EMAIL OR PASSWORD, TRY AGAIN.",
                            style = MaterialTheme.typography.labelSmall,
                            color = colorScheme.error,
                            modifier = Modifier.padding(start = 3.dp).testTag("errorText"))
                        Spacer(modifier = Modifier.padding(22.9.dp))
                      } else {
                        Spacer(modifier = Modifier.padding(30.dp))
                      }

                      Button(
                          // TODO: Button Logic When BackEnd Functions Are Done
                          onClick = { errorHasOccured = !errorHasOccured },
                          modifier = Modifier.width(360.dp).height(48.dp).testTag("logInButton"),
                          shape = RoundedCornerShape(10.dp),
                          colors =
                              ButtonDefaults.buttonColors(containerColor = colorScheme.primary),
                          enabled = filledForm) {
                            Text(
                                "LOGIN",
                                style = MaterialTheme.typography.labelLarge,
                                color = colorScheme.background)
                          }
                    }
              }
        })
  }
}
