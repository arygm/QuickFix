package com.arygm.quickfix.ui.search

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arygm.quickfix.ui.theme.poppinsFontFamily

@Composable
fun ExpandableCategoryItem(
    item: ExpandableCategory,
    isExpanded: Boolean,
    backgroundColor: Color = colorScheme.surface,
    onExpandedChange: (Boolean) -> Unit,
    height: Dp,
    size: Dp
) {
    val interactionSource = remember { MutableInteractionSource() }
    val rotationAngle by animateFloatAsState(targetValue = if (isExpanded) 180f else 0f, label = "")

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, shape = RoundedCornerShape(12.dp))
            .background(color = colorScheme.surface, shape = RoundedCornerShape(12.dp))
            .clickable(interactionSource = interactionSource, indication = null) {
                onExpandedChange(!isExpanded)
            }
            .padding(16.dp)
    ) {
        Row(
        modifier =
        Modifier.padding(horizontal = 16.dp, vertical = 8.dp).background(backgroundColor),
        verticalAlignment = Alignment.CenterVertically) {
        // Icon
        Icon(
            imageVector = item.category.icon,
            contentDescription = null,
            tint = colorScheme.primary,
            modifier = Modifier.size(size).testTag("categoryIcon"))

        Spacer(modifier = Modifier.width(16.dp))

        // Text Column
        Column {
            Text(
                text = item.category.categoryName,
                color = colorScheme.onBackground,
                fontWeight = FontWeight.SemiBold,
                fontFamily = poppinsFontFamily,
                fontSize = 16.sp)
            Text(
                text = item.category.description,
                color = colorScheme.onSecondary,
                fontWeight = FontWeight.Medium,
                fontFamily = poppinsFontFamily,
                fontSize = 11.sp,
                lineHeight = 16.sp)
        }
    }
        AnimatedVisibility(
            visible = isExpanded,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {

        }
    }
}