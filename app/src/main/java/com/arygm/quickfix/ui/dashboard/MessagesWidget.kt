package com.arygm.quickfix.ui.dashboard

import android.util.Log
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
import com.arygm.quickfix.model.account.AccountViewModel
import com.arygm.quickfix.model.categories.WorkerCategory
import com.arygm.quickfix.model.messaging.Chat
import com.arygm.quickfix.model.profile.ProfileViewModel
import com.arygm.quickfix.model.profile.WorkerProfile
import com.arygm.quickfix.ui.theme.poppinsTypography
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun MessagesWidget(
    messageList: List<Chat>,
    onShowAllClick: () -> Unit,
    onItemClick: (Chat) -> Unit,
    modifier: Modifier = Modifier,
    itemsToShowDefault: Int = 3,
    isUser: Boolean,
    accountViewModel: AccountViewModel,
    profileViewModel: ProfileViewModel
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
            MessageItem(
                chat = message,
                onClick = { onItemClick(message) },
                isUser = isUser,
                accountViewModel = accountViewModel,
                profileViewModel = profileViewModel)

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
fun MessageItem(
    chat: Chat,
    onClick: () -> Unit,
    isUser: Boolean,
    accountViewModel: AccountViewModel,
    profileViewModel: ProfileViewModel
) {

  val formatter = SimpleDateFormat("HH:mm", Locale.getDefault())
  var messageName by remember { mutableStateOf("") }
  var messageDescription by remember { mutableStateOf("") }
  var serviceIcon by remember { mutableStateOf<ImageVector?>(null) }
  accountViewModel.fetchUserAccount(if (isUser) chat.workeruid else chat.useruid) { account ->
    if (account != null) {
      messageName = "${account.firstName} ${account.lastName}"
      messageDescription =
          chat.messages.last().content.apply {
            if (chat.messages.last().senderId == if (isUser) chat.useruid else chat.workeruid) {
              messageDescription = "You: $messageDescription"
            }
          }
      profileViewModel.fetchUserProfile(account.uid) { profile ->
        if (profile != null && profile is WorkerProfile) {
          serviceIcon = WorkerCategory.entries.find { it.description == profile.fieldOfWork }?.icon
        } else {
          Log.d("MessageItem", "Profile not found for ${account.uid}")
        }
      }
    }
  }
  Row(
      modifier =
          Modifier.fillMaxWidth()
              .padding(horizontal = 12.dp, vertical = 8.dp)
              .clickable { onClick() }
              .testTag("MessageItem_${chat.chatId}"), // Added testTag
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
                text = messageName,
                modifier = Modifier.testTag(messageName), // Added testTag
                style = poppinsTypography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis)
            Spacer(modifier = Modifier.width(4.dp))
            serviceIcon?.let {
              Icon(
                  imageVector = it,
                  contentDescription = "Service Icon",
                  tint = MaterialTheme.colorScheme.onSurface,
                  modifier = Modifier.size(20.dp))
            }
          }
          Text(
              text = messageDescription, // Removed leading comma for clarity
              modifier = Modifier.testTag(messageDescription), // Added testTag
              style = poppinsTypography.bodyMedium.copy(fontSize = 12.sp),
              fontWeight = FontWeight.Normal,
              color =
                  if (chat.messages.last().isRead) MaterialTheme.colorScheme.onSurface
                  else MaterialTheme.colorScheme.onBackground,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis)
        }

        Spacer(modifier = Modifier.width(8.dp))
        Column(
            modifier = Modifier.weight(0.2f).align(Alignment.Top),
            verticalArrangement = Arrangement.SpaceBetween) {
              val lastMessageDate = chat.messages.last().timestamp
              Text(
                  text = formatCustomDate(lastMessageDate),
                  modifier =
                      Modifier.testTag(formatCustomDate(lastMessageDate))
                          .align(Alignment.End), // Added testTag
                  style = MaterialTheme.typography.labelSmall,
                  color = MaterialTheme.colorScheme.onSurface)
              if (chat.messages.any { !it.isRead }) {
                Spacer(modifier = Modifier.height(3.dp))
                Box(
                    modifier =
                        Modifier.size(20.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary)
                            .align(Alignment.End)) {
                      Text(
                          text = chat.messages.count { !it.isRead }.toString(),
                          color = MaterialTheme.colorScheme.onPrimary,
                          style = MaterialTheme.typography.bodySmall,
                          modifier = Modifier.align(Alignment.Center))
                    }
              }
            }
      }
}

fun formatCustomDate(timestamp: Timestamp): String {
  val now = Calendar.getInstance()
  val lastMessageDate = Calendar.getInstance().apply { time = timestamp.toDate() }

  return when {
    // Check if the date is today
    now.get(Calendar.YEAR) == lastMessageDate.get(Calendar.YEAR) &&
        now.get(Calendar.DAY_OF_YEAR) == lastMessageDate.get(Calendar.DAY_OF_YEAR) -> {
      SimpleDateFormat("HH:mm", Locale.getDefault()).format(lastMessageDate.time)
    }
    // Check if the date is yesterday
    now.get(Calendar.YEAR) == lastMessageDate.get(Calendar.YEAR) &&
        now.get(Calendar.DAY_OF_YEAR) - lastMessageDate.get(Calendar.DAY_OF_YEAR) == 1 -> {
      "Yesterday"
    }
    // Check if the date is within the same week
    now.get(Calendar.WEEK_OF_YEAR) == lastMessageDate.get(Calendar.WEEK_OF_YEAR) &&
        now.get(Calendar.YEAR) == lastMessageDate.get(Calendar.YEAR) -> {
      SimpleDateFormat("EEEE", Locale.getDefault()).format(lastMessageDate.time)
    }
    // Otherwise, return the date in the format "dd.MM.yy"
    else -> {
      SimpleDateFormat("dd.MM.yy", Locale.getDefault()).format(lastMessageDate.time)
    }
  }
}
