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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
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
import com.arygm.quickfix.model.profile.ProfileViewModel
import com.arygm.quickfix.model.profile.WorkerProfile
import com.arygm.quickfix.model.quickfix.QuickFix
import com.arygm.quickfix.ui.theme.poppinsTypography
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun QuickFixesWidget(
    status: String = "All",
    quickFixList: List<QuickFix>,
    onShowAllClick: () -> Unit,
    onItemClick: (QuickFix) -> Unit,
    workerViewModel: ProfileViewModel,
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
                    color = colorScheme.onBackground,
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
                          color = colorScheme.onSurface,
                          style = poppinsTypography.bodyMedium,
                          fontWeight = FontWeight.SemiBold)
                    }
              }
          HorizontalDivider(
              modifier = Modifier.padding(horizontal = 0.dp),
              thickness = 1.dp,
              color = colorScheme.onSurface.copy(alpha = 0.2f))

          val itemsToShow = if (showAll) quickFixList else quickFixList.take(itemsToShowDefault)
          quickFixList.take(itemsToShow.size).forEachIndexed { index, quickFix ->
            QuickFixItem(quickFix = quickFix, onClick = { onItemClick(quickFix) }, workerViewModel)
            // Divider between items
            if (index < itemsToShow.size - 1) {
              HorizontalDivider(
                  modifier = Modifier.fillMaxWidth(),
                  thickness = 1.dp,
                  color = colorScheme.onSurface.copy(alpha = 0.2f))
            }
          }
        }
  }
}

@Composable
fun QuickFixItem(quickFix: QuickFix, onClick: () -> Unit, workerViewModel: ProfileViewModel) {
  val formatter = SimpleDateFormat("EEE, dd MMM yyyy", Locale.getDefault())
  var workerName by remember { mutableStateOf("") }
  workerViewModel.fetchUserProfile(quickFix.workerId) { profile ->
    workerName = (profile as WorkerProfile).displayName
  }
  Row(
      modifier =
          Modifier.fillMaxWidth()
              .padding(horizontal = 12.dp, vertical = 8.dp)
              .clickable { onClick() }
              .testTag("QuickFixItem_${quickFix.title}"), // Added testTag
      verticalAlignment = Alignment.CenterVertically) {
        Column(modifier = Modifier.weight(0.15f)) {
          // Profile image placeholder
          Image(
              painter = painterResource(id = R.drawable.profile), // Replace with an actual drawable
              contentDescription = "Profile Picture",
              modifier =
                  Modifier.size(40.dp)
                      .clip(CircleShape)
                      .background(colorScheme.onSurface.copy(alpha = 0.1f)))
        }

        // Text information
        Column(modifier = Modifier.weight(0.7f)) {
          // Row for name and task description on the same line
          Row(verticalAlignment = Alignment.Bottom) {
            Text(
                text = workerName,
                modifier = Modifier.testTag(quickFix.title), // Added testTag
                style = poppinsTypography.bodyMedium,
                fontSize = 15.sp,
                color = colorScheme.onBackground,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis)
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = quickFix.title, // Removed leading comma for clarity
                modifier = Modifier.testTag(quickFix.title), // Added testTag
                style = poppinsTypography.bodyMedium,
                fontSize = 15.sp,
                color = colorScheme.onBackground.copy(alpha = 0.7f),
                fontWeight = FontWeight.Normal,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis)
          }
          // Date text on a separate line
          Text(
              text =
                  quickFix.date.joinToString(" - ") { timestamp ->
                    formatter.format(timestamp.toDate())
                  },
              modifier = Modifier.testTag(quickFix.date.toString()), // Added testTag
              style = poppinsTypography.bodyMedium,
              fontSize = 15.sp,
              fontWeight = FontWeight.Normal,
              color = colorScheme.onSurface)
        }

        Column(
            modifier = Modifier.weight(0.15f),
            horizontalAlignment = Alignment.End,
        ) {
          // Arrow icon with a circular background
          Box(
              modifier = Modifier.size(32.dp).clip(CircleShape).background(colorScheme.secondary),
              contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Go to details",
                    tint = colorScheme.onBackground)
              }
        }
      }
}
