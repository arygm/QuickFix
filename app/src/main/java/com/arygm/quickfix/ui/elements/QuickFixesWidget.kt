package com.arygm.quickfix.ui.elements

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arygm.quickfix.R
import com.arygm.quickfix.ui.theme.poppinsTypography

// Data class for QuickFix item
data class QuickFix(val name: String, val taskDescription: String, val date: String)

@Composable
fun QuickFixesWidget(
    status: String = "All",
    quickFixList: List<QuickFix>,
    onShowAllClick: () -> Unit,
    onItemClick: (QuickFix) -> Unit,
    modifier: Modifier = Modifier,
    itemsToShowDefault: Int = 3
) {
  var showAll by remember { mutableStateOf(false) } // Toggle for showing all items
  BoxWithConstraints {
    val cardWidth = maxWidth * 0.4f // 40% of the available width for each card
    val horizontalSpacing = maxWidth * 0.025f // 2.5% of the available width for spacing

    // Card-like styling with shadow for the entire Column
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(horizontalSpacing)
                .shadow(5.dp, RoundedCornerShape(20.dp))
                .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp))
                .testTag("UpcomingQuickFixesColumn"), // Added testTag
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start) {
          // Header with divider
          Row(
              modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "$status QuickFixes",
                    color = MaterialTheme.colorScheme.onBackground,
                    style = poppinsTypography.headlineMedium,
                    fontSize = 19.sp,
                    modifier =
                        Modifier.testTag("UpcomingQuickFixesTitle")
                            .padding(horizontal = 8.dp) // Added testTag
                    )
                TextButton(
                    onClick = { showAll = !showAll },
                    modifier = Modifier.testTag("ShowAllButton") // Added testTag
                    ) {
                      Text(
                          text = if (showAll) "Show Less" else "Show All",
                          color = MaterialTheme.colorScheme.onSurface,
                          style = MaterialTheme.typography.bodyMedium,
                          fontWeight = FontWeight.SemiBold)
                    }
              }
          HorizontalDivider(
              modifier = Modifier.padding(horizontal = 0.dp),
              thickness = 1.dp,
              color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f))

          val itemsToShow = if (showAll) quickFixList else quickFixList.take(itemsToShowDefault)
          quickFixList.take(itemsToShow.size).forEachIndexed { index, quickFix ->
            QuickFixItem(quickFix = quickFix, onClick = { onItemClick(quickFix) })
            // Divider between items
            if (index < itemsToShow.size - 1) {
              HorizontalDivider(
                  modifier = Modifier.fillMaxWidth(),
                  thickness = 1.dp,
                  color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f))
            }
          }
        }
  }
}

@Composable
fun QuickFixItem(quickFix: QuickFix, onClick: () -> Unit) {
  Row(
      modifier =
          Modifier.fillMaxWidth()
              .padding(horizontal = 12.dp, vertical = 8.dp)
              .clickable { onClick() }
              .testTag("QuickFixItem_${quickFix.name}"), // Added testTag
      verticalAlignment = Alignment.CenterVertically) {
        Column(modifier = Modifier.weight(0.15f)) {
          // Profile image placeholder
          Image(
              painter = painterResource(id = R.drawable.profile), // Replace with an actual drawable
              contentDescription = "Profile Picture",
              modifier =
                  Modifier.size(40.dp)
                      .clip(CircleShape)
                      .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)))
        }

        // Text information
        Column(modifier = Modifier.weight(0.7f)) {
          // Row for name and task description on the same line
          Row(verticalAlignment = Alignment.Bottom) {
            Text(
                text = quickFix.name,
                modifier = Modifier.testTag(quickFix.name), // Added testTag
                style = poppinsTypography.bodyMedium,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis)
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = quickFix.taskDescription, // Removed leading comma for clarity
                modifier = Modifier.testTag(quickFix.taskDescription), // Added testTag
                style = poppinsTypography.bodyMedium,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                fontWeight = FontWeight.Normal,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis)
          }
          // Date text on a separate line
          Text(
              text = quickFix.date,
              modifier = Modifier.testTag(quickFix.date), // Added testTag
              style = poppinsTypography.bodyMedium,
              fontSize = 15.sp,
              fontWeight = FontWeight.Normal,
              color = MaterialTheme.colorScheme.onSurface)
        }

        Column(
            modifier = Modifier.weight(0.15f),
            horizontalAlignment = Alignment.End,
        ) {
          // Arrow icon with a circular background
          Box(
              modifier =
                  Modifier.size(32.dp)
                      .clip(CircleShape)
                      .background(MaterialTheme.colorScheme.secondary),
              contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = "Go to details",
                    tint = MaterialTheme.colorScheme.onBackground)
              }
        }
      }
}
