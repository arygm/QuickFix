package com.arygm.quickfix.ui.dashboard

import android.annotation.SuppressLint
import android.graphics.Bitmap
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arygm.quickfix.model.account.AccountViewModel
import com.arygm.quickfix.model.category.CategoryViewModel
import com.arygm.quickfix.model.category.getCategoryIcon
import com.arygm.quickfix.model.messaging.Chat
import com.arygm.quickfix.model.messaging.ChatStatus
import com.arygm.quickfix.model.offline.small.PreferencesViewModel
import com.arygm.quickfix.model.profile.ProfileViewModel
import com.arygm.quickfix.model.profile.WorkerProfile
import com.arygm.quickfix.model.switchModes.AppMode
import com.arygm.quickfix.ui.theme.poppinsTypography
import com.arygm.quickfix.utils.loadAppMode
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun ChatWidget(
    chatList: List<Chat>,
    onShowAllClick: () -> Unit,
    onItemClick: (Chat) -> Unit,
    modifier: Modifier = Modifier,
    itemsToShowDefault: Int = 3,
    uid: String,
    accountViewModel: AccountViewModel,
    categoryViewModel: CategoryViewModel,
    workerViewModel: ProfileViewModel,
    preferencesViewModel: PreferencesViewModel
) {
  var showAll by remember { mutableStateOf(false) } // Toggle for showing all items
  var mode by remember { mutableStateOf("") }
  LaunchedEffect(Unit) { mode = loadAppMode(preferencesViewModel) }
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

          val itemsToShow = if (showAll) chatList else chatList.take(itemsToShowDefault)
          chatList.take(itemsToShow.size).forEachIndexed { index, chat ->
            ChatItem(
                chat = chat,
                onClick = { onItemClick(chat) },
                uid = uid,
                accountViewModel = accountViewModel,
                categoryViewModel = categoryViewModel,
                workerViewModel = workerViewModel,
                mode = AppMode.valueOf(mode))

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

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun ChatItem(
    chat: Chat,
    onClick: () -> Unit,
    uid: String,
    accountViewModel: AccountViewModel,
    categoryViewModel: CategoryViewModel,
    workerViewModel: ProfileViewModel,
    mode: AppMode
) {
  var workerProfile by remember { mutableStateOf(WorkerProfile()) }
  workerViewModel.fetchUserProfile(
      chat.workeruid, onResult = { workerProfile = it as WorkerProfile })
  var image by remember { mutableStateOf<ImageVector?>(null) }
  CoroutineScope(Dispatchers.IO).launch {
    categoryViewModel.getCategoryBySubcategoryId(workerProfile.fieldOfWork) {
      image = it?.let { it1 -> getCategoryIcon(it1) }
    }
  }
  var otherUserId = chat.useruid
  if (mode == AppMode.USER) {
    otherUserId = chat.workeruid
  }

  var otherProfileBitmap by remember { mutableStateOf<Bitmap?>(null) }

  LaunchedEffect(otherUserId) {
    accountViewModel.fetchAccountProfileImageAsBitmap(
        accountId = otherUserId,
        onSuccess = { bitmap -> otherProfileBitmap = bitmap },
        onFailure = {})
  }
  Row(
      modifier =
          Modifier.fillMaxWidth()
              .padding(horizontal = 12.dp, vertical = 8.dp)
              .clickable { onClick() }
              .testTag("MessageItem_${chat.chatId}"), // Added testTag
      verticalAlignment = Alignment.CenterVertically) {
        Column(modifier = Modifier.weight(0.15f)) {
          val imageModifier =
              Modifier.size(40.dp)
                  .clip(CircleShape)
                  .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
          if (otherProfileBitmap != null) {
            Image(
                bitmap = otherProfileBitmap!!.asImageBitmap(),
                contentDescription = "Profile Picture",
                contentScale = ContentScale.Crop,
                modifier = imageModifier)
          } else {
            Box(modifier = imageModifier)
          }
          // Profile image placeholder
        }

        // Text information
        Column(modifier = Modifier.weight(0.7f)) {
          // Row for name and task description on the same line
          Row(verticalAlignment = Alignment.Bottom) {
            var name by remember { mutableStateOf("") }
            Text(
                text =
                    if (chat.messages.isEmpty() && mode == AppMode.USER) {
                      workerProfile.displayName
                    } else if (chat.messages.isEmpty() && mode == AppMode.WORKER) {
                      accountViewModel.fetchUserAccount(
                          chat.useruid,
                          onResult = {
                            if (it != null) {
                              name = it.firstName.plus(" ").plus(it.lastName)
                            }
                          })
                      name
                    } else {
                      if (uid == chat.messages.last().senderId) {
                        "You"
                      } else {
                        accountViewModel.fetchUserAccount(
                            chat.messages.last().senderId,
                            onResult = {
                              if (it != null) {
                                name = it.firstName.plus(" ").plus(it.lastName)
                              }
                            })
                        name
                      }
                    },
                modifier = Modifier.testTag(name), // Added testTag
                style = poppinsTypography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis)
            Spacer(modifier = Modifier.width(4.dp))
            image?.let {
              Icon(
                  imageVector = it,
                  contentDescription = "Service Icon",
                  tint = MaterialTheme.colorScheme.primary)
            }
          }
          Text(
              text =
                  if (chat.messages.isEmpty() &&
                      (chat.chatStatus == ChatStatus.WAITING_FOR_RESPONSE ||
                          chat.chatStatus == ChatStatus.GETTING_SUGGESTIONS)) {
                    "Await confirmation from ${workerProfile.displayName}"
                  } else chat.messages.last().content, // Removed leading comma for clarity
              modifier =
                  Modifier.testTag(
                      if (chat.messages.isEmpty()) "No messages"
                      else chat.messages.last().content), // Added testTag
              style = poppinsTypography.bodyMedium.copy(fontSize = 12.sp),
              fontWeight = FontWeight.Normal,
              color =
                  if (chat.messages.isEmpty() || chat.messages.last().isRead)
                      MaterialTheme.colorScheme.onSurface
                  else MaterialTheme.colorScheme.onBackground,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis)
        }

        Spacer(modifier = Modifier.width(8.dp))
        Column(
            modifier = Modifier.weight(0.15f).align(Alignment.Top),
            verticalArrangement = Arrangement.SpaceBetween) {
              Text(
                  text =
                      if (chat.messages.isEmpty()) {
                        "--:--"
                      } else formatCustomDate(chat.messages.last().timestamp),
                  modifier =
                      Modifier.testTag(
                              if (chat.messages.isEmpty()) "--:--"
                              else formatCustomDate(chat.messages.last().timestamp))
                          .align(Alignment.End), // Added testTag
                  style = MaterialTheme.typography.labelSmall,
                  color = MaterialTheme.colorScheme.onSurface)
              if (chat.messages.any { it.isRead }) {
                Spacer(modifier = Modifier.height(3.dp))
                Box(
                    modifier =
                        Modifier.size(20.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary)
                            .align(Alignment.End)) {
                      Text(
                          text = chat.messages.filter { it.isRead }.size.toString(),
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
