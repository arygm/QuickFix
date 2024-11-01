package com.arygm.quickfix.ui.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arygm.quickfix.ui.theme.poppinsFontFamily

data class SearchCategory(
    val icon: ImageVector,
    val title: String,
    val description: String,
    val onClick: () -> Unit
)

@Composable
fun SearchCategoryButton(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    title: String,
    description: String,
    backgroundColor: Color = colorScheme.surface,
    onClick: () -> Unit = {},
    height: Dp,
    size: Dp,
) {
  Surface(
      shape = RoundedCornerShape(8.dp),
      modifier =
          modifier
              .shadow(elevation = 5.dp, shape = RoundedCornerShape(8.dp), clip = false)
              .fillMaxWidth()
              .clickable { onClick() }
              .height(height)
              .testTag("backgroundColorTag-${backgroundColor.toArgb()}")) {
        Row(
            modifier =
                Modifier.padding(horizontal = 16.dp, vertical = 8.dp).background(backgroundColor),
            verticalAlignment = Alignment.CenterVertically) {
              // Icon
              Icon(
                  imageVector = icon,
                  contentDescription = null,
                  tint = Color.Red,
                  modifier = Modifier.size(size).testTag("categoryIcon"))

              Spacer(modifier = Modifier.width(16.dp))

              // Text Column
              Column {
                Text(
                    text = title,
                    color = colorScheme.onBackground,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = poppinsFontFamily,
                    fontSize = 16.sp)
                Text(
                    text = description,
                    color = colorScheme.onSecondary,
                    fontWeight = FontWeight.Medium,
                    fontFamily = poppinsFontFamily,
                    fontSize = 11.sp,
                    lineHeight = 16.sp)
              }
            }
      }
}
