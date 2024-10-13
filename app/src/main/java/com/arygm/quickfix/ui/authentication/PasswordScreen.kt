package com.arygm.quickfix.ui.authentication

import QuickFixTextField
import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.arygm.quickfix.model.profile.Profile
import com.arygm.quickfix.model.profile.ProfileViewModel
import com.arygm.quickfix.model.profile.RegistrationViewModel
import com.arygm.quickfix.ui.elements.QuickFixAnimatedBox
import com.arygm.quickfix.ui.elements.QuickFixBackButtonTopBar
import com.arygm.quickfix.ui.elements.QuickFixButton
import com.arygm.quickfix.ui.navigation.NavigationActions
import com.arygm.quickfix.ui.navigation.Screen
import com.arygm.quickfix.utils.BOX_COLLAPSE_SPEED
import com.arygm.quickfix.utils.BOX_OFFSET_X_EXPANDED
import com.arygm.quickfix.utils.BOX_OFFSET_X_SHRUNK
import com.google.firebase.auth.FirebaseAuth
import java.security.Timestamp
import java.util.Calendar
import java.util.GregorianCalendar

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "UseOfNonLambdaOffsetOverload")
@Composable
fun PasswordScreen(
    navigationActions: NavigationActions,
    registrationViewModel: RegistrationViewModel,
    profileViewModel: ProfileViewModel
) {

  val context = LocalContext.current

  val firstName by registrationViewModel.firstName.collectAsState()
  val lastName by registrationViewModel.lastName.collectAsState()
  val email by registrationViewModel.email.collectAsState()
  val birthDate by registrationViewModel.birthDate.collectAsState()

  var password by remember { mutableStateOf("") }
  var repeatPassword by remember { mutableStateOf("") }

  val noMatch by remember {
    derivedStateOf { password != repeatPassword && repeatPassword.isNotEmpty() }
  }
  var shrinkBox by remember { mutableStateOf(false) }
  val boxOffsetX by
      animateDpAsState(
          targetValue = if (shrinkBox) BOX_OFFSET_X_SHRUNK else BOX_OFFSET_X_EXPANDED,
          animationSpec = tween(durationMillis = BOX_COLLAPSE_SPEED),
          label = "shrinkingBox")

  LaunchedEffect(Unit) { shrinkBox = true }

  val passwordConditions =
      listOf(
          "PASSWORD SHOULD BE AT LEAST 8 CHARACTERS" to (password.length >= 8),
          "PASSWORD SHOULD CONTAIN AN UPPERCASE LETTER (A-Z)" to password.any { it.isUpperCase() },
          "PASSWORD SHOULD CONTAIN A LOWERCASE LETTER (a-z)" to password.any { it.isLowerCase() },
          "PASSWORD SHOULD CONTAIN A DIGIT (0-9)" to password.any { it.isDigit() })

  val buttonActive = passwordConditions.all { it.second } && !noMatch && repeatPassword.isNotEmpty()

  Box(modifier = Modifier.fillMaxSize().testTag("passwordBox")) {
    QuickFixAnimatedBox(boxOffsetX)

    Scaffold(
        modifier = Modifier.background(colorScheme.background),
        topBar = {
          QuickFixBackButtonTopBar(
              onBackClick = {
                shrinkBox = false
                navigationActions.goBack()
              })
        },
        content = { pd ->
          Box(
              modifier =
                  Modifier.fillMaxSize()
                      .background(colorScheme.background)
                      .padding(pd)
                      .imePadding()
                      .testTag("contentBox") // Adjust layout when keyboard is shown
              ) {
                Box(
                    modifier =
                        Modifier.align(Alignment.BottomStart)
                            .size(180.dp, 180.dp)
                            .offset(x = (-150).dp, y = 64.dp)
                            .graphicsLayer(rotationZ = 57f)
                            .background(colorScheme.primary)
                            .zIndex(0f)
                            .testTag("boxDecoration"))

                Column(
                    modifier =
                        Modifier.fillMaxSize()
                            .padding(start = 24.dp)
                            .verticalScroll(rememberScrollState()), // Enable vertical scrolling
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.Top) {
                      Spacer(modifier = Modifier.padding(60.dp)) // Adjusted padding for top spacing

                      Text(
                          "ENTER YOUR\nPASSWORD",
                          fontSize = 32.sp,
                          color = colorScheme.primary,
                          fontWeight = FontWeight.ExtraBold,
                          fontStyle = FontStyle.Italic,
                          lineHeight = 40.sp,
                          modifier = Modifier.testTag("passwordText"))

                      Spacer(modifier = Modifier.padding(6.dp))

                      QuickFixTextField(
                          value = password,
                          onValueChange = { password = it },
                          label = "PASSWORD",
                          modifier = Modifier.width(360.dp).testTag("passwordInput"),
                          visualTransformation = PasswordVisualTransformation(),
                          keyboardOptions =
                              KeyboardOptions.Default.copy(imeAction = ImeAction.Next))

                      Spacer(modifier = Modifier.padding(3.dp))

                      QuickFixTextField(
                          value = repeatPassword,
                          onValueChange = { repeatPassword = it },
                          label = "REPEAT PASSWORD",
                          modifier = Modifier.width(360.dp).testTag("repeatPasswordInput"),
                          visualTransformation = PasswordVisualTransformation(),
                          keyboardOptions =
                              KeyboardOptions.Default.copy(imeAction = ImeAction.Done))

                      Spacer(modifier = Modifier.padding(3.dp))

                      // Password Requirements List
                      Column(modifier = Modifier.padding(vertical = 8.dp)) {
                        passwordConditions.forEach { (condition, met) ->
                          Text(
                              text = condition,
                              color = if (met) colorScheme.tertiary else colorScheme.error,
                              style = MaterialTheme.typography.labelSmall,
                              modifier = Modifier.padding(start = 3.dp).testTag(condition))
                        }
                      }

                      // Error message if passwords don't match
                      if (noMatch) {
                        Text(
                            "PASSWORDS DO NOT MATCH.",
                            style = MaterialTheme.typography.labelSmall,
                            color = colorScheme.error,
                            modifier = Modifier.padding(start = 3.dp).testTag("noMatchText"))
                        Spacer(modifier = Modifier.padding(22.9.dp))
                      } else {
                        Spacer(modifier = Modifier.padding(30.dp))
                      }

                      QuickFixButton(
                          buttonText = "REGISTER",
                          onClickAction = {
                            shrinkBox = false
                            createAccountWithEmailAndPassword(
                                firstName = firstName,
                                lastName = lastName,
                                email = email,
                                password = password,
                                birthDate = birthDate,
                                profileViewModel = profileViewModel,
                                onSuccess = { navigationActions.navigateTo(Screen.WELCOME) },
                                onFailure = {
                                  Toast.makeText(context, "Registration Failed.", Toast.LENGTH_LONG)
                                      .show()
                                })
                          },
                          buttonColor = colorScheme.primary,
                          modifier = Modifier.graphicsLayer(alpha = 1f).testTag("registerButton"),
                          textColor = colorScheme.background,
                          enabled = buttonActive)
                    }
              }
        })
  }
}

fun stringToTimestamp(birthDate: String): com.google.firebase.Timestamp? {
  val dateParts = birthDate.split("/")
  return if (dateParts.size == 3) {
    val day = dateParts[0].toIntOrNull()
    val month = dateParts[1].toIntOrNull()
    val year = dateParts[2].toIntOrNull()
    if (day != null && month != null && year != null) {
      val calendar =
          GregorianCalendar(year, month - 1, day).apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
          }
      val date = calendar.time
      com.google.firebase.Timestamp(date)
    } else {
      Log.e("DateConversion", "Invalid date format: $birthDate")
      null
    }
  } else {
    Log.e("DateConversion", "Date string is not in the correct format: $birthDate")
    null
  }
}

fun createAccountWithEmailAndPassword(
    firstName: String,
    lastName: String,
    email: String,
    password: String,
    birthDate: String,
    profileViewModel: ProfileViewModel,
    onSuccess: () -> Unit,
    onFailure: () -> Unit
) {
  FirebaseAuth.getInstance()
      .createUserWithEmailAndPassword(email, password)
      .addOnCompleteListener { task ->
        if (task.isSuccessful) {
          val user = FirebaseAuth.getInstance().currentUser
          user?.let {
            val profile =
                stringToTimestamp(birthDate)?.let { birthTimestamp ->
                  Profile(
                      uid = it.uid,
                      firstName = firstName,
                      lastName = lastName,
                      email = email,
                      password = "",
                      birthDate = birthTimestamp)
                }

            profile?.let { createdProfile ->
              profileViewModel.addProfile(
                  createdProfile,
                  onSuccess = {
                    profileViewModel.setLoggedInProfile(createdProfile)
                    onSuccess()
                  },
                  onFailure = {
                    Log.e("Registration", "Failed to save profile")
                    onFailure()
                  })
            }
                ?: run {
                  Log.e("Registration", "Failed to create profile")
                  onFailure()
                }
          }
        } else {
          Log.e("Registration", "Error creating account: ${task.exception?.message}")
          onFailure()
        }
      }
}
