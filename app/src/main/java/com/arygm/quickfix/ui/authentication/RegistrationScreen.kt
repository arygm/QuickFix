package com.arygm.quickfix.ui.authentication

import android.annotation.SuppressLint
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.arygm.quickfix.ui.navigation.NavigationActions
import com.arygm.quickfix.ui.navigation.Screen
import java.util.regex.Pattern

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun RegistrationScreen(navigationActions: NavigationActions, LoD: Boolean = true) {
    val color1 = if (LoD) Color(0xFFF16138) else Color(0xFF633040)
    val color2 = if (LoD) Color(0xFF731734) else Color(0xFFB78080)
    val backgroundColor = if (LoD) Color.White else Color(0xFF282828)
    val errorColor = if (LoD) Color(0xFFFF5353) else Color(0xFFC54646)

    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var birthDate by remember { mutableStateOf("") }

    var emailError by remember { mutableStateOf(false) }
    var birthDateError by remember { mutableStateOf(false) }

    var acceptTerms by remember { mutableStateOf(false) }
    var acceptPrivacyPolicy by remember { mutableStateOf(false) }

    fun isValidEmail(email: String): Boolean {
        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
        return Pattern.matches(emailPattern, email)
    }

    val filledForm =
        firstName.isNotEmpty() && lastName.isNotEmpty() && email.isNotEmpty() && birthDate.isNotEmpty()

    fun isValidDate(date: String): Boolean {
        // Simple check for DD/MM/YYYY format
        val datePattern = "^([0-2][0-9]|(3)[0-1])/((0)[1-9]|(1)[0-2])/((19|20)\\d\\d)$"
        return Pattern.matches(datePattern, date)
    }

    Box(modifier = Modifier.fillMaxSize()) {

        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(180.dp, 180.dp)
                .offset(x = 115.dp, y = (-80).dp)
                .graphicsLayer(rotationZ = 57f)
                .background(color1)
                .zIndex(1f)
        )

        Scaffold(
            modifier = Modifier.background(backgroundColor),
            topBar = {
                TopAppBar(
                    title = { Text("") },
                    navigationIcon = {
                        IconButton(
                            onClick = { navigationActions.goBack() },
                            modifier = Modifier
                                .testTag("goBackButton")
                                .padding(start = 9.dp, top = 35.dp)
                                .size(48.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                                contentDescription = "Back",
                                tint = color1,
                                modifier = Modifier.size(45.dp)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = backgroundColor
                    )
                )
            },
            content = { pd ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(backgroundColor)
                        .padding(pd)
                        .imePadding()
                ) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .size(180.dp, 180.dp)
                            .offset(x = (-150).dp, y = 64.dp)
                            .graphicsLayer(rotationZ = 57f)
                            .background(color1)
                            .zIndex(0f)
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(start = 24.dp)
                            .verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.Start,
                        verticalArrangement = Arrangement.Top
                    ) {
                        Spacer(modifier = Modifier.padding(60.dp))

                        Text(
                            "WELCOME",
                            fontSize = 32.sp,
                            color = color1,
                            fontWeight = FontWeight.ExtraBold,
                            fontStyle = FontStyle.Italic,
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(end = 18.dp),
                            horizontalArrangement = Arrangement.SpaceBetween // This arranges them with space in between
                        ) {
                            OutlinedTextField(
                                value = firstName,
                                onValueChange = { firstName = it },
                                label = {
                                    Text(
                                        "FIRST NAME",
                                        color = color2.copy(alpha = 0.6f),
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 20.sp,
                                        fontStyle = FontStyle.Italic
                                    )
                                },
                                modifier = Modifier
                                    .weight(1f) // This ensures both fields take up equal space
                                    .padding(end = 8.dp),
                                textStyle = androidx.compose.ui.text.TextStyle(
                                    color = color2.copy(alpha = 1f),  // Full opacity
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 20.sp,
                                    fontStyle = FontStyle.Italic
                                ),
                                singleLine = true,
                                shape = RoundedCornerShape(10.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = color1,
                                    unfocusedBorderColor = color1,
                                    cursorColor = color1
                                )
                            )

                            OutlinedTextField(
                                value = lastName,
                                onValueChange = { lastName = it },
                                label = {
                                    Text(
                                        "LAST NAME",
                                        color = color2.copy(alpha = 0.6f),
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 20.sp,
                                        fontStyle = FontStyle.Italic
                                    )
                                },
                                modifier = Modifier
                                    .weight(1f) // This ensures both fields take up equal space
                                    .padding(end = 8.dp),
                                textStyle = androidx.compose.ui.text.TextStyle(
                                    color = color2.copy(alpha = 1f),  // Full opacity
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 20.sp,
                                    fontStyle = FontStyle.Italic
                                ),
                                singleLine = true,
                                shape = RoundedCornerShape(10.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = color1,
                                    unfocusedBorderColor = color1,
                                    cursorColor = color1
                                )

                            )
                        }

                        Spacer(modifier = Modifier.padding(6.dp))

                        // Email Field
                        OutlinedTextField(
                            value = email,
                            onValueChange = {
                                email = it
                                emailError = !isValidEmail(it)
                            },
                            label = {
                                Text(
                                    "E-MAIL ADDRESS",
                                    color = color2.copy(alpha = 0.6f),
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 20.sp,
                                    fontStyle = FontStyle.Italic
                                )
                            },
                            isError = emailError,
                            modifier = Modifier.width(360.dp),
                            singleLine = true,
                            textStyle = androidx.compose.ui.text.TextStyle(
                                color = color2.copy(alpha = 1f),  // Full opacity
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 20.sp,
                                fontStyle = FontStyle.Italic
                            ),
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = color1,
                                unfocusedBorderColor = color1,
                                cursorColor = color1
                            )
                        )

                        if (emailError) {
                            Text(
                                "INVALID E-MAIL",
                                color = errorColor,
                                fontWeight = FontWeight.ExtraBold,
                                fontStyle = FontStyle.Italic,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(top = 4.dp, start = 3.dp)
                            )
                        }

                        Spacer(modifier = Modifier.padding(6.dp))

                        // Birth Date Field
                        OutlinedTextField(
                            value = birthDate,
                            onValueChange = {
                                birthDate = it
                                birthDateError = !isValidDate(it)
                            },
                            label = {
                                Text(
                                    "BIRTH DATE: DD/MM/YYYY",
                                    color = color2.copy(alpha = 0.6f),
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 20.sp,
                                    fontStyle = FontStyle.Italic
                                )
                            },
                            isError = birthDateError,
                            modifier = Modifier.width(360.dp),
                            singleLine = true,
                            textStyle = androidx.compose.ui.text.TextStyle(
                                color = color2.copy(alpha = 1f),  // Full opacity
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 20.sp,
                                fontStyle = FontStyle.Italic
                            ),
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = color1,
                                unfocusedBorderColor = color1,
                                cursorColor = color1
                            )
                        )

                        if (birthDateError) {
                            Text(
                                "INVALID DATE",
                                color = errorColor,
                                fontWeight = FontWeight.ExtraBold,
                                fontStyle = FontStyle.Italic,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(top = 4.dp, start = 3.dp)
                            )
                        }

                        Spacer(modifier = Modifier.padding(10.dp))

                        // Checkboxes for terms and privacy policy
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = acceptTerms,
                                onCheckedChange = { acceptTerms = it },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = color1,  // The color of the checkbox when checked
                                    uncheckedColor = Color(0xFFc0c0c0),  // Lighter color when unchecked
                                    checkmarkColor = Color.Transparent  // Removes the check icon
                                ), // More rounded shape
                                modifier = Modifier.size(24.dp),  // Adjust the size if needed
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                "I HAVE READ AND ACCEPT THE",
                                fontSize = 12.sp,
                                fontStyle = FontStyle.Italic,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFFC0C0C0)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                "TERMS AND CONDITIONS.",
                                fontSize = 12.sp,
                                color = color1,
                                fontWeight = FontWeight.ExtraBold,
                                fontStyle = FontStyle.Italic,
                                textDecoration = TextDecoration.Underline
                            )

                        }

                        Spacer(modifier = Modifier.padding(4.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = acceptPrivacyPolicy,
                                onCheckedChange = { acceptPrivacyPolicy = it },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = color1,  // The color of the checkbox when checked
                                    uncheckedColor = Color(0xFFc0c0c0),  // Lighter color when unchecked
                                    checkmarkColor = Color.Transparent  // Removes the check icon
                                ), // More rounded shape
                                modifier = Modifier.size(24.dp),  // Adjust the size if needed
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                "I ACCEPT THE",
                                fontSize = 12.sp,
                                fontStyle = FontStyle.Italic,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFFC0C0C0)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                "PRIVACY POLICY",
                                fontSize = 12.sp,
                                color = color1,
                                fontWeight = FontWeight.ExtraBold,
                                fontStyle = FontStyle.Italic,
                                textDecoration = TextDecoration.Underline
                            )

                        }

                        Spacer(modifier = Modifier.padding(10.dp))

                        // Button
                        Button(
                            onClick = { /* TODO: Add button logic */
                                navigationActions.navigateTo(Screen.PASSWORD)},
                            modifier = Modifier
                                .width(360.dp)
                                .height(48.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = color1
                            ),
                            enabled = acceptTerms && acceptPrivacyPolicy && filledForm
                        ) {
                            Text(
                                "NEXT",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.ExtraBold,
                                fontStyle = FontStyle.Italic,
                                color = if (LoD) Color.White else Color(0xFFB78080)
                            )
                        }
                    }
                }
            }
        )
    }
}