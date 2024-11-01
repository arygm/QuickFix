package com.arygm.quickfix.ui.authentication

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arygm.quickfix.model.profile.LoggedInProfileViewModel
import com.arygm.quickfix.model.profile.ProfileViewModel
import com.arygm.quickfix.ui.elements.QuickFixButton
import com.arygm.quickfix.ui.navigation.NavigationActions
import com.arygm.quickfix.ui.navigation.Screen
import com.arygm.quickfix.ui.navigation.TopLevelDestinations
import com.arygm.quickfix.utils.referenceHeight
import com.arygm.quickfix.utils.referenceWidth
import com.arygm.quickfix.utils.rememberFirebaseAuthLauncher
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import kotlinx.coroutines.delay

@SuppressLint("UseOfNonLambdaOffsetOverload")
@Composable
fun WelcomeScreen(
    navigationActions: NavigationActions,
    userViewModel: ProfileViewModel,
    loggedInProfileViewModel: LoggedInProfileViewModel,
) {
    var fadeOut by remember { mutableStateOf(true) }
    var expandBox by remember { mutableStateOf(true) }
    var startAnimation by remember { mutableStateOf(false) }
    var targetScreen by remember { mutableStateOf("") }

    // Animation properties
    val elementsAlpha by animateFloatAsState(
        targetValue = if (fadeOut) 0f else 1f,
        label = "elementsOpacity"
    )

    val context = LocalContext.current
    val launcher =
        rememberFirebaseAuthLauncher(
            onAuthComplete = { result ->
                Log.d("SignInScreen", "User signed in: ${result.user?.displayName}")
                navigationActions.navigateTo(TopLevelDestinations.HOME)
            },
            onAuthError = { Log.e("SignInScreen", "Failed to sign in: ${it.statusCode}") },
            userViewModel,
            loggedInProfileViewModel = loggedInProfileViewModel
        )

    val token = stringResource(com.arygm.quickfix.R.string.default_web_client_id)

    // Capture screen constraints
    BoxWithConstraints(
        modifier = Modifier.fillMaxSize()
    ) {
        val screenWidth = maxWidth
        val screenHeight = maxHeight

        // Scaling factors for responsive design
        val widthRatio = screenWidth / referenceWidth
        val heightRatio = screenHeight / referenceHeight

        val boxOffsetX by animateDpAsState(
            targetValue = if (expandBox) 0.dp else (-890).dp * widthRatio,
            label = "BoxOffsetX"
        )

        LaunchedEffect(Unit) {
            expandBox = false
            delay(200)
            fadeOut = false
            delay(300)
        }

        if (startAnimation) {
            LaunchedEffect(Unit) {
                fadeOut = true
                delay(300)
                expandBox = true
                delay(500)
                navigationActions.navigateTo(targetScreen)
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .testTag("welcomeBox")
        ) {
            Image(
                painter = painterResource(id = com.arygm.quickfix.R.drawable.worker_image),
                contentDescription = null,
                contentScale = ContentScale.FillWidth,
                alignment = Alignment.TopStart,
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("workerBackground")
            )

            Box(
                modifier =
                Modifier
                    .align(Alignment.BottomStart)
                    .requiredSize(1700.dp * widthRatio, 1700.dp * widthRatio)
                    .offset(x = boxOffsetX, y = 60.dp * heightRatio)
                    .graphicsLayer(rotationZ = -28f)
                    .background(colorScheme.primary)
                    .testTag("boxDecoration1")
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp * heightRatio),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 200.dp * heightRatio)
                    .align(Alignment.Center)
            ) {
                Image(
                    painter = painterResource(id = com.arygm.quickfix.R.drawable.quickfix),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    colorFilter = ColorFilter.tint(colorScheme.background),
                    modifier = Modifier
                        .size(283.dp * heightRatio, 333.dp * heightRatio)
                        .graphicsLayer(rotationZ = 4.57f, alpha = elementsAlpha)
                        .testTag("quickFixLogo")
                )

                Text(
                    text = "QuickFix",
                    style = MaterialTheme.typography.titleLarge.copy(fontSize = 64.sp * heightRatio),
                    color = colorScheme.background,
                    modifier = Modifier
                        .graphicsLayer(alpha = elementsAlpha)
                        .testTag("quickFixText")
                )


                QuickFixButton(
                    buttonText = "LOG IN TO QUICKFIX",
                    onClickAction = {
                        targetScreen = Screen.LOGIN
                        startAnimation = true
                    },
                    buttonColor = colorScheme.tertiary,
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(50.dp * widthRatio)
                        .graphicsLayer(alpha = elementsAlpha)
                        .testTag("logInButton"),
                    textColor = colorScheme.background,
                    widthRatio = widthRatio
                )

                QuickFixButton(
                    buttonText = "REGISTER TO QUICKFIX",
                    onClickAction = {
                        targetScreen = Screen.REGISTER
                        startAnimation = true
                    },
                    buttonColor = colorScheme.background,
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(50.dp * widthRatio)
                        .graphicsLayer(alpha = elementsAlpha)
                        .testTag("registrationButton"),
                    textColor = colorScheme.primary,
                    widthRatio = widthRatio
                )

                Button(
                    onClick = {
                        val gso =
                            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                                .requestIdToken(token)
                                .requestEmail()
                                .build()
                        val googleSignInClient = GoogleSignIn.getClient(context, gso)
                        launcher.launch(googleSignInClient.signInIntent)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    border = BorderStroke(2.dp, colorScheme.background),
                    modifier =
                    Modifier
                        .fillMaxWidth(0.8f)
                        .height(50.dp * widthRatio)
                        .graphicsLayer(alpha = elementsAlpha)
                        .testTag("googleButton"),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = ButtonDefaults.ButtonWithIconContentPadding,
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 0.dp)
                    ) {
                        Image(
                            painter =
                            painterResource(
                                id = com.arygm.quickfix.R.drawable.google,
                            ),
                            contentDescription = "Google Logo",
                            colorFilter = ColorFilter.tint(colorScheme.background),
                            modifier = Modifier
                                .size(30.dp)
                                .offset(x = (-3).dp)
                                .testTag("googleLogo")
                        )

                        Spacer(modifier = Modifier.width(16.dp))

                        // Button Text
                        Text(
                            text = "CONTINUE WITH GOOGLE",
                            color = colorScheme.background,
                            style = MaterialTheme.typography.labelMedium,
                            fontSize = MaterialTheme.typography.labelMedium.fontSize * widthRatio,
                        )
                    }

                }
            }
        }
    }
}
