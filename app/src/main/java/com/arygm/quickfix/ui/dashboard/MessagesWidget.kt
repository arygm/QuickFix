package com.arygm.quickfix.ui.dashboard

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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arygm.quickfix.R
import com.arygm.quickfix.ui.theme.poppinsTypography

// Data class for QuickFix item
data class MessageSneakPeak(
    val name: String,
    val messageOverview: String,
    val date: String,
    val profileImage: Int,
    val isRead: Boolean,
    val notificationCount: Int,
    val serviceIcon: ImageVector
)

@Composable
fun MessagesWidget(
    messageList: List<MessageSneakPeak>,
    onShowAllClick: () -> Unit,
    onItemClick: (MessageSneakPeak) -> Unit,
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
                .testTag("MessagesWidget"), // Added testTag
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start) {
          // Header with divider
          Row(
              modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Messages",
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
              thickness = 1.dp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f))

          val itemsToShow = if (showAll) messageList else messageList.take(itemsToShowDefault)
          messageList.take(itemsToShow.size).forEachIndexed { index, message ->
            MessageItem(messageSneakPeak = message, onClick = { onItemClick(message) })

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
fun MessageItem(messageSneakPeak: MessageSneakPeak, onClick: () -> Unit) {
  Row(
      modifier =
          Modifier.fillMaxWidth()
              .padding(horizontal = 12.dp, vertical = 8.dp)
              .clickable { onClick() }
              .testTag("MessageItem_${messageSneakPeak.name}"), // Added testTag
      verticalAlignment = Alignment.CenterVertically) {
        Column(modifier = Modifier.weight(0.15f)) {
          // Profile image placeholder
          Image(
              painter =
                  painterResource(
                      id = R.drawable.placeholder_worker), // Replace with an actual drawable
              contentDescription = "Profile Picture",
              contentScale = ContentScale.Crop,
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
                text = messageSneakPeak.name,
                modifier = Modifier.testTag(messageSneakPeak.name), // Added testTag
                style = poppinsTypography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis)
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                imageVector = messageSneakPeak.serviceIcon,
                contentDescription = "Service Icon",
                tint = MaterialTheme.colorScheme.primary)
          }
          Text(
              text = messageSneakPeak.messageOverview, // Removed leading comma for clarity
              modifier = Modifier.testTag(messageSneakPeak.messageOverview), // Added testTag
              style = poppinsTypography.bodyMedium.copy(fontSize = 12.sp),
              fontWeight = FontWeight.Normal,
              color =
                  if (messageSneakPeak.isRead) MaterialTheme.colorScheme.onSurface
                  else MaterialTheme.colorScheme.onBackground,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis)
        }

        Spacer(modifier = Modifier.width(8.dp))
        Column(
            modifier = Modifier.weight(0.15f).align(Alignment.Top),
            verticalArrangement = Arrangement.SpaceBetween) {
              Text(
                  text = messageSneakPeak.date,
                  modifier =
                      Modifier.testTag(messageSneakPeak.date).align(Alignment.End), // Added testTag
                  style = MaterialTheme.typography.labelSmall,
                  color = MaterialTheme.colorScheme.onSurface)
              if (messageSneakPeak.notificationCount > 0) {
                Spacer(modifier = Modifier.height(3.dp))
                Box(
                    modifier =
                        Modifier.size(20.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary)
                            .align(Alignment.End)) {
                      Text(
                          text = messageSneakPeak.notificationCount.toString(),
                          color = MaterialTheme.colorScheme.onPrimary,
                          style = MaterialTheme.typography.bodySmall,
                          modifier = Modifier.align(Alignment.Center))
                    }
              }
            }
      }
}
