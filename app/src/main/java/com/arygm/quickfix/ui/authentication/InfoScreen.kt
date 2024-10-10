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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.arygm.quickfix.ui.elements.QuickFixAnimatedBox
import com.arygm.quickfix.ui.elements.QuickFixBackButton
import com.arygm.quickfix.ui.elements.QuickFixCheckBoxRow
import com.arygm.quickfix.ui.navigation.NavigationActions
import com.arygm.quickfix.ui.navigation.Screen
import com.arygm.quickfix.utils.isValidDate
import com.arygm.quickfix.utils.isValidEmail

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun InfoScreen(navigationActions: NavigationActions) {
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
          targetValue = if (shrinkBox) 1285.dp else 0.dp,
          animationSpec = tween(durationMillis = 300),
          label = "shrinkingBox")

  LaunchedEffect(Unit) { shrinkBox = true }

  val filledForm =
      firstName.isNotEmpty() &&
          lastName.isNotEmpty() &&
          email.isNotEmpty() &&
          birthDate.isNotEmpty()

  Box(modifier = Modifier.fillMaxSize()) {
    QuickFixAnimatedBox(boxOffsetX)

    Scaffold(
        modifier = Modifier.background(colorScheme.background),
        topBar = {
          TopAppBar(
              title = { Text("") },
              navigationIcon = {
                QuickFixBackButton(
                    onClick = {
                      shrinkBox = false
                      navigationActions.goBack()
                    },
                    color = colorScheme.primary)
              },
              colors = TopAppBarDefaults.topAppBarColors(containerColor = colorScheme.background))
        },
        content = { pd ->
          Box(
              modifier =
                  Modifier.fillMaxSize()
                      .background(colorScheme.background)
                      .padding(pd)
                      .imePadding()) {
                Box(
                    modifier =
                        Modifier.align(Alignment.BottomStart)
                            .requiredSize(180.dp)
                            .offset(x = (-150).dp, y = 64.dp)
                            .graphicsLayer(rotationZ = -28f)
                            .background(colorScheme.primary)
                            .zIndex(0f))

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
                      )

                      Row(
                          modifier = Modifier.fillMaxWidth().padding(end = 18.dp),
                          horizontalArrangement =
                              Arrangement.SpaceBetween // This arranges them with space in between
                          ) {
                            QuickFixTextField(
                                value = firstName,
                                onValueChange = { firstName = it },
                                label = "FIRST NAME",
                                modifier = Modifier.weight(1f).padding(end = 8.dp),
                            )

                            QuickFixTextField(
                                value = lastName,
                                onValueChange = { lastName = it },
                                label = "LAST NAME",
                                modifier = Modifier.weight(1f).padding(end = 8.dp),
                            )
                          }

                      Spacer(modifier = Modifier.padding(6.dp))

                      // Email Field
                      QuickFixTextField(
                          value = email,
                          onValueChange = {
                            email = it
                            emailError = !isValidEmail(it)
                          },
                          label = "E-MAIL",
                          isError = emailError,
                          modifier = Modifier.width(360.dp),
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
                          modifier = Modifier.width(360.dp),
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

                      // Button
                      Button(
                          onClick = {
                            shrinkBox = false
                            navigationActions.navigateTo(Screen.PASSWORD)
                          },
                          modifier = Modifier.width(360.dp).height(48.dp),
                          shape = RoundedCornerShape(10.dp),
                          colors =
                              ButtonDefaults.buttonColors(containerColor = colorScheme.primary),
                          enabled = acceptTerms && acceptPrivacyPolicy && filledForm) {
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
