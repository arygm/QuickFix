package com.arygm.quickfix.ui.elements

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.testTag // Import for testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.arygm.quickfix.R

// Data class for QuickFix item
data class QuickFix(val name: String, val taskDescription: String, val date: String)

@Composable
fun UpcomingQuickFixes(
    quickFixList: List<QuickFix>,
    onShowAllClick: () -> Unit,
    onItemClick: (QuickFix) -> Unit,
    modifier: Modifier = Modifier
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
                .shadow(5.dp, RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp))
                .testTag("UpcomingQuickFixesColumn"), // Added testTag
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start) {
          // Header with divider
          Row(
              modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Upcoming QuickFixes",
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.testTag("UpcomingQuickFixesTitle") // Added testTag
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
          Divider(
              color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
              thickness = 1.dp,
              modifier = Modifier.padding(horizontal = 0.dp))

          // List of QuickFix items
          LazyColumn(
              modifier = Modifier.wrapContentHeight(), verticalArrangement = Arrangement.Top) {
                // Determine how many items to show based on showAll state
                val itemsToShow = if (showAll) quickFixList else quickFixList.take(3)

                items(itemsToShow.size) { index ->
                  val quickFix = itemsToShow[index]
                  QuickFixItem(quickFix = quickFix, onClick = { onItemClick(quickFix) })

                  // Divider between items
                  if (index < itemsToShow.size - 1) {
                    Divider(
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                        thickness = 1.dp,
                        modifier = Modifier.fillMaxWidth())
                  }
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
        // Profile image placeholder
        Image(
            painter = painterResource(id = R.drawable.profile), // Replace with an actual drawable
            contentDescription = "Profile Picture",
            modifier =
                Modifier.size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)))

        Spacer(modifier = Modifier.width(12.dp))

        // Text information
        Column(modifier = Modifier.weight(1f)) {
          // Row for name and task description on the same line
          Row(verticalAlignment = Alignment.Bottom) {
            Text(
                text = quickFix.name,
                modifier = Modifier.testTag(quickFix.name), // Added testTag
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis)
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = quickFix.taskDescription, // Removed leading comma for clarity
                modifier = Modifier.testTag(quickFix.taskDescription), // Added testTag
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis)
          }
          // Date text on a separate line
          Text(
              text = quickFix.date,
              modifier = Modifier.testTag(quickFix.date), // Added testTag
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurface)
        }

        Spacer(modifier = Modifier.width(8.dp))

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
