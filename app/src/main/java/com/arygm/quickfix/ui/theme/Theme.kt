package com.arygm.quickfix.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import com.arygm.quickfix.R

private val LightColorScheme =
    lightColorScheme(
        primary = ButtonPrimary,
        secondary = ButtonSecondary,
        tertiary = ButtonTertiary,
        background = BackgroundPrimary,
        surface = BackgroundSecondary,
        onPrimary = TextButtonPrimary,
        onSecondary = TextButtonSecondary,
        onTertiary = TextButtonTertiary,
        error = AccentPrimary,
        onError = AccentSecondary,
        onBackground = TextPrimary,
        onSurface = TextSecondary,
        outline = TitlePrimary,
        surfaceVariant = ButtonQuaternary, // Fourth button color
        onSurfaceVariant = TextButtonQuaternary, // Text color for the quaternary button,
        onSecondaryContainer = TextDisabled,
        tertiaryContainer = ButtonDisabled)

private val DarkColorScheme =
    darkColorScheme(
        primary = DarkButtonPrimary,
        secondary = DarkButtonSecondary,
        tertiary = DarkButtonTertiary,
        background = DarkBackgroundPrimary,
        surface = DarkBackgroundSecondary,
        onPrimary = DarkTextButtonPrimary,
        onSecondary = DarkTextButtonSecondary,
        onTertiary = DarkTextButtonTertiary,
        error = DarkAccentPrimary,
        onError = DarkAccentSecondary,
        onBackground = DarkTextPrimary,
        onSurface = DarkTextSecondary,
        outline = DarkTitlePrimary,
        surfaceVariant = DarkButtonQuaternary, // Fourth button color
        onSurfaceVariant = DarkTextButtonQuaternary, // Text color for the quaternary button
        onSecondaryContainer = DarkTextDisabled,
        tertiaryContainer = DarkButtonDisabled)
val poppinsFontFamily =
    FontFamily(
        Font(R.font.poppins_black, FontWeight.Black, FontStyle.Normal),
        Font(R.font.poppins_blackitalic, FontWeight.Black, FontStyle.Italic),
        Font(R.font.poppins_extrabold, FontWeight.ExtraBold, FontStyle.Normal),
        Font(R.font.poppins_extrabolditalic, FontWeight.ExtraBold, FontStyle.Italic),
        Font(R.font.poppins_bold, FontWeight.Bold, FontStyle.Normal),
        Font(R.font.poppins_bolditalic, FontWeight.Bold, FontStyle.Italic),
        Font(R.font.poppins_semibold, FontWeight.SemiBold, FontStyle.Normal),
        Font(R.font.poppins_semibolditalic, FontWeight.SemiBold, FontStyle.Italic),
        Font(R.font.poppins_medium, FontWeight.Medium, FontStyle.Normal),
        Font(R.font.poppins_mediumitalic, FontWeight.Medium, FontStyle.Italic),
        Font(R.font.poppins_regular, FontWeight.Normal, FontStyle.Normal),
        Font(R.font.poppins_italic, FontWeight.Normal, FontStyle.Italic),
        Font(R.font.poppins_light, FontWeight.Light, FontStyle.Normal),
        Font(R.font.poppins_lightitalic, FontWeight.Light, FontStyle.Italic),
        Font(R.font.poppins_extralight, FontWeight.ExtraLight, FontStyle.Normal),
        Font(R.font.poppins_extralightitalic, FontWeight.ExtraLight, FontStyle.Italic),
        Font(R.font.poppins_thin, FontWeight.Thin, FontStyle.Normal),
        Font(R.font.poppins_thinitalic, FontWeight.Thin, FontStyle.Italic),
    )

val poppinsTypography =
    Typography(
        titleLarge =
            TextStyle(
                fontFamily = poppinsFontFamily,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 64.sp,
                fontStyle = FontStyle.Normal),
        headlineLarge =
            TextStyle(
                fontFamily = poppinsFontFamily,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 32.sp,
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
                fontSize = 22.sp,
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
                fontWeight = FontWeight.Bold,
                fontSize = 9.sp,
                fontStyle = FontStyle.Normal),
    )

@Composable
fun QuickFixTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
  val colorScheme =
      when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
          if (darkTheme) DarkColorScheme else LightColorScheme
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
      }
  val view = LocalView.current
  if (!view.isInEditMode) {
    SideEffect {
      val window = (view.context as Activity).window
      window.statusBarColor = colorScheme.primary.toArgb()
      WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
    }
  }

  MaterialTheme(colorScheme = colorScheme, typography = poppinsTypography, content = content)
}
