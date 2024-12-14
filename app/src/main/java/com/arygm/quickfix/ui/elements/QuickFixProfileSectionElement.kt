package com.arygm.quickfix.ui.elements

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForwardIos
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arygm.quickfix.ui.theme.poppinsTypography

data class SettingItemData(
    val icon: ImageVector,
    val label: String,
    val testTag: String,
    val action: () -> Unit
)

@Composable
fun SettingsSection(
    title: String,
    items: List<SettingItemData>,
    screenWidth: Dp,
    cardCornerRadius: Dp,
    showConditionalItem: Boolean = false,
    conditionalItem: SettingItemData? = null
) {
    Column {
        Text(
            text = title,
            style = poppinsTypography.headlineSmall.copy(
                fontWeight = FontWeight.SemiBold, fontSize = 16.sp
            ),
            color = colorScheme.onBackground,
            modifier = Modifier.testTag("${title.replace(" ", "")}Header")
        )
        Spacer(modifier = Modifier.height(8.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("${title.replace(" ", "")}Card"),
            shape = RoundedCornerShape(cardCornerRadius),
            colors = CardDefaults.cardColors(containerColor = colorScheme.surface)
        ) {
            Column {
                items.forEachIndexed { index, item ->
                    SettingsItem(
                        icon = item.icon,
                        label = item.label,
                        testTag = item.testTag,
                        screenWidth = screenWidth,
                        onClick = item.action
                    )
                    if (index < items.size - 1) {
                        HorizontalDivider(color = colorScheme.tertiaryContainer)
                    }
                }

                // Add the conditional item if applicable
                if (showConditionalItem && conditionalItem != null) {
                    HorizontalDivider(color = colorScheme.tertiaryContainer)
                    SettingsItem(
                        icon = conditionalItem.icon,
                        label = conditionalItem.label,
                        testTag = conditionalItem.testTag,
                        screenWidth = screenWidth,
                        onClick = conditionalItem.action
                    )
                }
            }
        }
    }
}

@Composable
fun SettingsItem(
    icon: ImageVector,
    label: String,
    testTag: String,
    screenWidth: Dp,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth().testTag(testTag),
        shape = RoundedCornerShape(0.dp),
        colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
        onClick = onClick) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = screenWidth * 0.02f),
            verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = "$label Icon",
                tint = colorScheme.tertiaryContainer,
                modifier = Modifier.size(screenWidth * 0.06f))
            Spacer(modifier = Modifier.width(screenWidth * 0.04f))
            Text(
                text = label,
                style =
                poppinsTypography.bodyMedium.copy(
                    fontWeight = FontWeight.Medium, fontSize = 16.sp),
                color = colorScheme.onBackground,
                modifier = Modifier.weight(1f).testTag(label.replace(" ", "") + "Text"))
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.ArrowForwardIos,
                contentDescription = "Forward Icon",
                tint = colorScheme.tertiaryContainer,
                modifier = Modifier.size(screenWidth * 0.04f))
        }
    }
}