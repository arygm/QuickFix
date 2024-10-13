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

private val DarkColorScheme =
    darkColorScheme(
        background = DarkSlateGray,
        primary = DarkBordeaux,
        secondary = RosyBrown,
        tertiary = Silver,
        error = IndianRed)

private val LightColorScheme =
    lightColorScheme(
        background = White,
        primary = Orange,
        secondary = Bordeaux,
        tertiary = LightGray,
        error = Tomato,
    )

val interFontFamily =
    FontFamily(
        Font(R.font.inter_extrabolditalic, FontWeight.ExtraBold, FontStyle.Italic),
        Font(R.font.inter_extrabold, FontWeight.ExtraBold, FontStyle.Normal),
    )

val interTypography =
    Typography(
        titleLarge =
            TextStyle(
                fontFamily = interFontFamily,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 64.sp,
                fontStyle = FontStyle.Italic),
        headlineLarge =
            TextStyle(
                fontFamily = interFontFamily,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 32.sp,
                fontStyle = FontStyle.Italic),
        labelLarge =
            TextStyle(
                fontFamily = interFontFamily,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 20.sp,
                fontStyle = FontStyle.Italic),
        labelMedium =
            TextStyle(
                fontFamily = interFontFamily,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 18.sp,
                fontStyle = FontStyle.Italic),
        labelSmall =
            TextStyle(
                fontFamily = interFontFamily,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 12.sp,
                fontStyle = FontStyle.Italic),
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

  MaterialTheme(colorScheme = colorScheme, typography = interTypography, content = content)
}
