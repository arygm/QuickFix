package com.github.se.bootcamp.ui.authentication

import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import com.arygm.quickfix.ui.navigation.NavigationActions
import com.arygm.quickfix.ui.navigation.Screen


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun WelcomeScreen(navigationActions: NavigationActions, LoD: Boolean = true) {

    val color1 = if (LoD) Color(0xFFF16138) else Color(0xFF633040)
    val color2 = if (LoD) Color(0xFF731734) else Color(0xFFB78080)
    val backgroundColor = if (LoD) Color.White else Color(0xFF282828)

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = com.arygm.quickfix.R.drawable.worker_image),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            alignment = Alignment.TopStart,
            modifier = Modifier
                .fillMaxSize()
        )

        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .size(1000.dp, 1000.dp)
                .offset(x = (-164).dp, y = 43.dp)
                .graphicsLayer(rotationZ = -30f)
                .background(color1)
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .size(425.dp, 150.dp)
                .background(color1)
        )

        if(LoD){
            Image(
                painter = painterResource(id = com.arygm.quickfix.R.drawable.light_quickfix_logo),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset(x = 0.dp, y = -30.dp)
                    .size(width = 283.dp,height = 325.dp)
                    .graphicsLayer(rotationZ = 4.57f)
            )
        }else{
            Image(
                painter = painterResource(id = com.arygm.quickfix.R.drawable.dark_quickfix_logo),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset(x = 0.dp, y = -30.dp)
                    .size(width = 283.dp,height = 325.dp)
                    .graphicsLayer(rotationZ = 4.57f)
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 420.dp) // Adjust this to fine-tune the position below the logo
        ) {

            Spacer(modifier = Modifier.padding(60.dp))
            // QuickFix Text
            Text(
                text = "QuickFix",
                fontSize = 64.sp,
                fontWeight = FontWeight.ExtraBold,
                fontStyle = FontStyle.Italic,
                color = backgroundColor,
                modifier = Modifier
                    .padding(bottom = 24.dp) // Space between text and buttons
            )

            // Buttons
            Button(
                onClick = { navigationActions.navigateTo(Screen.LOGIN) },
                colors = ButtonDefaults.buttonColors(containerColor = color2),
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(50.dp)
                    .padding(bottom = 8.dp),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(text = "LOG IN TO QUICKFIX",
                    color = backgroundColor,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontStyle = FontStyle.Italic)
            }

            Button(
                onClick = { navigationActions.navigateTo(Screen.INFO) },
                colors = ButtonDefaults.buttonColors(containerColor = backgroundColor),
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(50.dp)
                    .padding(bottom = 8.dp),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(text = "REGISTER TO QUICKFIX",
                    color = color2,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontStyle = FontStyle.Italic)
            }

            Button(
                onClick = { /* TODO: Google action */ },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                border = BorderStroke(2.dp, backgroundColor),
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(50.dp),
                shape = RoundedCornerShape(10.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 0.dp)
                ) {
                    if(LoD){
                        Image(
                            painter = painterResource(id = com.arygm.quickfix.R.drawable.light_google_logo),
                            contentDescription = "Google Logo",
                            modifier = Modifier
                                .size(20.dp)
                                .offset(x = (-3).dp)
                        )
                    }else{
                        Image(
                            painter = painterResource(id = com.arygm.quickfix.R.drawable.dark_google_logo),
                            contentDescription = "Google Logo",
                            modifier = Modifier
                                .size(20.dp)
                                .offset(x = (-3).dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    // Button Text
                    Text(
                        text = "CONTINUE WITH GOOGLE",
                        color = backgroundColor,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        fontStyle = FontStyle.Italic
                    )
                }
            }
        }
    }
}