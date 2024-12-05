package com.arygm.quickfix.ui.profile.becomeWorker.views.welcome

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arygm.quickfix.R
import com.arygm.quickfix.ui.theme.poppinsTypography

@Preview
@Composable
fun WelcomeOnBoardScreen() {
    BoxWithConstraints {
        val widthRatio = maxWidth / 411
        val heightRatio = maxHeight / 860
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Welcome on board !!",
                style = poppinsTypography.headlineLarge.copy(
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 26.sp
                ))
            Image(
                painter = painterResource(id = R.drawable.onboarding_worker_1), // Replace 'my_image' with your PNG file name
                contentDescription = "Description of the image",     // Accessibility description
                modifier = Modifier.size((354 * 1.1).dp * widthRatio.value, (247 * 1.1).dp * heightRatio.value),                   // Optional: Set the size
                contentScale = ContentScale.Crop                     // Optional: Set how the image should scale
            )

        }
    }

}