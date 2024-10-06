package com.arygm.quickfix.ui.authentication

import android.annotation.SuppressLint
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.arygm.quickfix.ui.navigation.NavigationActions

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun LogInScreen(navigationActions: NavigationActions, LoD: Boolean = true) {

  val color1 = if (LoD) Color(0xFFF16138) else Color(0xFF633040)
  val color2 = if (LoD) Color(0xFF731734) else Color(0xFFB78080)
  val backgroundColor = if (LoD) Color.White else Color(0xFF282828)
  val errorColor = if (LoD) Color(0xFFFF5353) else Color(0xFFC54646)
  var errorHasOccured by remember { mutableStateOf(false) }

  var email by remember { mutableStateOf("") }
  var password by remember { mutableStateOf("") }

  val filledForm = email.isNotEmpty() && password.isNotEmpty()

  Box(modifier = Modifier.fillMaxSize()) {
    Box(
        modifier =
            Modifier.align(Alignment.TopEnd)
                .size(180.dp, 180.dp)
                .offset(x = 115.dp, y = (-80).dp)
                .graphicsLayer(rotationZ = 57f)
                .background(color1)
                .zIndex(1f))

    Scaffold(
        modifier = Modifier.background(backgroundColor),
        topBar = {
          TopAppBar(
              title = { Text("") },
              navigationIcon = {
                IconButton(
                    // TODO: Button Logic When BackEnd Functions Are Done
                    onClick = { navigationActions.goBack() },
                    modifier =
                        Modifier.testTag("goBackButton")
                            .padding(start = 9.dp, top = 35.dp)
                            .size(48.dp)) {
                      Icon(
                          imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                          contentDescription = "Back",
                          tint = color1,
                          modifier = Modifier.size(45.dp))
                    }
              },
              colors = TopAppBarDefaults.topAppBarColors(containerColor = backgroundColor))
        },
        content = { pd ->
          Box(modifier = Modifier.fillMaxSize().background(backgroundColor).padding(pd)) {
            Box(
                modifier =
                    Modifier.align(Alignment.BottomStart)
                        .size(180.dp, 180.dp)
                        .offset(x = (-150).dp, y = 64.dp)
                        .graphicsLayer(rotationZ = 57f)
                        .background(color1)
                        .zIndex(0f))

            Column(
                modifier = Modifier.fillMaxSize().padding(start = 24.dp),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Top) {
                  Spacer(modifier = Modifier.padding(100.dp))

                  Text(
                      "WELCOME BACK",
                      fontSize = 32.sp,
                      color = color1,
                      fontWeight = FontWeight.ExtraBold,
                      fontStyle = FontStyle.Italic)

                  Spacer(modifier = Modifier.padding(6.dp))

                  OutlinedTextField(
                      value = email,
                      onValueChange = { email = it },
                      label = {
                        Text(
                            "E-MAIL ADDRESS",
                            color =
                                (if (errorHasOccured) errorColor else color2).copy(alpha = 0.6f),
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 20.sp,
                            fontStyle = FontStyle.Italic)
                      },
                      textStyle =
                          androidx.compose.ui.text.TextStyle(
                              color = color2.copy(alpha = 1f), // Full opacity
                              fontWeight = FontWeight.ExtraBold,
                              fontSize = 20.sp,
                              fontStyle = FontStyle.Italic),
                      singleLine = true,
                      shape = RoundedCornerShape(10.dp),
                      modifier = Modifier.width(360.dp),
                      colors =
                          OutlinedTextFieldDefaults.colors(
                              focusedBorderColor = color1,
                              unfocusedBorderColor = color1,
                              cursorColor = color1))

                  Spacer(modifier = Modifier.padding(3.dp))

                  OutlinedTextField(
                      value = password,
                      onValueChange = { password = it },
                      label = {
                        Text(
                            "PASSWORD",
                            color =
                                (if (errorHasOccured) errorColor else color2).copy(alpha = 0.6f),
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 20.sp,
                            fontStyle = FontStyle.Italic)
                      },
                      visualTransformation =
                          PasswordVisualTransformation(), // Shows password as dots
                      textStyle =
                          androidx.compose.ui.text.TextStyle(
                              color = color2.copy(alpha = 1f), // Full opacity
                              fontWeight = FontWeight.ExtraBold,
                              fontSize = 20.sp,
                              fontStyle = FontStyle.Italic),
                      singleLine = true,
                      shape = RoundedCornerShape(10.dp),
                      modifier = Modifier.width(360.dp),
                      colors =
                          OutlinedTextFieldDefaults.colors(
                              focusedBorderColor = color1,
                              unfocusedBorderColor = color1,
                              cursorColor = color1))

                  Spacer(modifier = Modifier.padding(3.dp))

                  Row(modifier = Modifier.padding(start = 3.dp)) {
                    Text(
                        "FORGOT YOUR PASSWORD ? TRY",
                        fontSize = 12.sp,
                        fontStyle = FontStyle.Italic,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFFC0C0C0))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        "THIS.",
                        fontSize = 12.sp,
                        color = color1,
                        fontWeight = FontWeight.ExtraBold,
                        textDecoration = TextDecoration.Underline)
                  }

                  if (errorHasOccured) {
                    Text(
                        "INVALID EMAIL OR PASSWORD, TRY AGAIN.",
                        fontSize = 12.sp,
                        fontStyle = FontStyle.Italic,
                        fontWeight = FontWeight.ExtraBold,
                        color = errorColor,
                        modifier = Modifier.padding(start = 3.dp))
                    Spacer(modifier = Modifier.padding(18.2.dp))
                  } else {
                    Spacer(modifier = Modifier.padding(30.dp))
                  }

                  Button(
                      // TODO: Button Logic When BackEnd Functions Are Done
                      onClick = { errorHasOccured = !errorHasOccured },
                      modifier = Modifier.width(360.dp).height(48.dp),
                      shape = RoundedCornerShape(10.dp),
                      colors = ButtonDefaults.buttonColors(containerColor = color1),
                      enabled = filledForm) {
                        Text(
                            "LOGIN",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            fontStyle = FontStyle.Italic,
                            color = if (LoD) Color.White else Color(0xFFB78080))
                      }
                }
          }
        })
  }
}
