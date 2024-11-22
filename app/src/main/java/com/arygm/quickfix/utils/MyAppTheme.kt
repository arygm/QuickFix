package com.arygm.quickfix.utils
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.arygm.quickfix.ui.theme.poppinsFontFamily

@Composable
fun MyAppTheme(content: @Composable () -> Unit) {
    val dynamicColorScheme = MaterialTheme.colorScheme // Access current color scheme dynamically

    val poppinsTypography =
        Typography(
            titleLarge =
            TextStyle(
                fontFamily = poppinsFontFamily,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 20.sp,
                fontStyle = FontStyle.Normal),
            headlineLarge =
            TextStyle(
                fontFamily = poppinsFontFamily,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 32.sp,
                fontStyle = FontStyle.Normal),
            headlineMedium =
            TextStyle(
                fontFamily = poppinsFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                fontStyle = FontStyle.Normal),
            headlineSmall =
            TextStyle(
                fontFamily = poppinsFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 11.sp,
                fontStyle = FontStyle.Normal),
            labelLarge =
            TextStyle(
                fontFamily = poppinsFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                fontStyle = FontStyle.Normal),
            labelMedium =
            TextStyle(
                fontFamily = poppinsFontFamily,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 18.sp,
                fontStyle = FontStyle.Normal),
            labelSmall =
            TextStyle(
                fontFamily = poppinsFontFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 12.sp,
                fontStyle = FontStyle.Normal),
            bodySmall =
            TextStyle(
                fontFamily = poppinsFontFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 13.sp,
                fontStyle = FontStyle.Normal),
            bodyMedium =
            TextStyle(
                fontFamily = poppinsFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                fontStyle = FontStyle.Normal)
        )
    val customColorScheme = lightColorScheme(
        primary = dynamicColorScheme.primary,
        onPrimary = dynamicColorScheme.onPrimary,
        secondary = dynamicColorScheme.secondary,
        secondaryContainer = dynamicColorScheme.primary.copy(alpha = 0.2f),
        onSecondary = dynamicColorScheme.onSecondary,
        background = dynamicColorScheme.background,
        surface = dynamicColorScheme.surface,
        onSurface = dynamicColorScheme.onBackground
    )

    MaterialTheme(
        colorScheme = customColorScheme,
        typography = poppinsTypography
    ) {
        content()
    }
}
