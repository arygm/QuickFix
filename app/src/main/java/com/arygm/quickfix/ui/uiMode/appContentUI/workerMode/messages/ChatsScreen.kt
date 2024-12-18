package com.arygm.quickfix.ui.uiMode.appContentUI.workerMode.messages

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.arygm.quickfix.model.account.AccountViewModel
import com.arygm.quickfix.model.messaging.Chat
import com.arygm.quickfix.model.messaging.ChatViewModel
import com.arygm.quickfix.model.offline.small.PreferencesViewModel
import com.arygm.quickfix.ui.elements.QuickFixTextFieldCustom
import com.arygm.quickfix.ui.navigation.NavigationActions
import com.arygm.quickfix.ui.theme.poppinsTypography
import com.arygm.quickfix.ui.uiMode.appContentUI.userModeUI.navigation.UserScreen
import com.arygm.quickfix.ui.uiMode.workerMode.navigation.WorkerScreen
import com.arygm.quickfix.utils.loadAppMode
import com.arygm.quickfix.utils.loadUserId
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatsScreen(
    navigationActions: NavigationActions,
    accountViewModel: AccountViewModel,
    chatViewModel: ChatViewModel,
    preferencesViewModel: PreferencesViewModel
) {
  var mode by remember { mutableStateOf("") }
  var uid by remember { mutableStateOf("") }
  var chats by remember { mutableStateOf(emptyList<Chat>()) }
  var searchQuery by remember { mutableStateOf("") }
  val coroutineScope = rememberCoroutineScope()

  // Map pour stocker useruid -> firstName
  val userFirstNameMap = remember { mutableStateMapOf<String, String>() }

  LaunchedEffect(Unit) {
    mode = loadAppMode(preferencesViewModel)
    uid = loadUserId(preferencesViewModel)

    accountViewModel.fetchUserAccount(uid) { account ->
      account?.activeChats?.forEach { chatUid ->
        coroutineScope.launch {
          chatViewModel.getChatByChatUid(
              chatUid,
              onSuccess = { chat ->
                if (chat != null) {
                  chats = chats + chat
                  // Récupérer le firstName de useruid pour chaque chat
                  accountViewModel.fetchUserAccount(chat.useruid) { userAccount ->
                    userFirstNameMap[chat.useruid] = userAccount?.firstName ?: "Unknown"
                  }
                }
              },
              onFailure = { e -> Log.e("ChatScreen", "Failed: ${e.message}") })
        }
      }
    }
  }

  BoxWithConstraints {
    val widthRatio = maxWidth.value / 411f
    val heightRatio = maxHeight.value / 860f

    Scaffold(
        containerColor = colorScheme.background,
        topBar = {
          Column(
              modifier =
                  Modifier.fillMaxWidth()
                      .padding(horizontal = 8.dp * widthRatio, vertical = 8.dp * heightRatio)) {
                Text(
                    text = "Messages",
                    style = poppinsTypography.headlineLarge.copy(fontWeight = FontWeight.Bold),
                    color = colorScheme.onBackground,
                    modifier = Modifier.padding(bottom = 8.dp * heightRatio))

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally) {
                      QuickFixTextFieldCustom(
                          value = searchQuery,
                          onValueChange = { searchQuery = it },
                          showLeadingIcon = { true },
                          leadingIcon = Icons.Outlined.Search,
                          placeHolderText = "Search",
                          modifier =
                              Modifier.fillMaxWidth()
                                  .height(40.dp * heightRatio)
                                  .testTag("customSearchField"),
                          widthField = 380.dp * widthRatio)
                    }
              }
        },
        content = { padding ->
          LazyColumn(modifier = Modifier.fillMaxSize().padding(padding)) {
            // Filtrer les chats en comparant avec userFirstName
            val filteredChats =
                chats.filter { chat ->
                  val userFirstName = userFirstNameMap[chat.useruid] ?: ""
                  userFirstName.contains(searchQuery, ignoreCase = true) && chat.workeruid == uid
                }

            itemsIndexed(filteredChats) { index, chat ->
              // Chat Item

              ChatItem(
                  chat = chat,
                  accountViewModel = accountViewModel,
                  userFirstName = userFirstNameMap[chat.useruid] ?: "Loading...",
                  onClick = {
                    chatViewModel.selectChat(chat)
                    if (mode == "USER") navigationActions.navigateTo(UserScreen.MESSAGES)
                    else navigationActions.navigateTo(WorkerScreen.MESSAGES)
                  },
                  widthRatio = widthRatio,
                  heightRatio = heightRatio)

              // Ajouter un Divider sauf pour le dernier élément
              if (index < filteredChats.size - 1) {
                Column(modifier = Modifier.padding(start = 32.dp * widthRatio)) {
                  Divider(
                      color = colorScheme.onSurface.copy(alpha = 0.2f), // Couleur de la ligne
                      thickness = 1.dp, // Épaisseur de la ligne
                      modifier =
                          Modifier.padding(
                                  vertical = 4.dp * heightRatio) // Espacement autour du Divider
                              .testTag("Divider") // Ajout du testTag ici
                      )
                }
              }
            }
          }
        })
  }
}

@Composable
fun ChatItem(
    chat: Chat,
    userFirstName: String,
    accountViewModel: AccountViewModel,
    onClick: () -> Unit,
    widthRatio: Float,
    heightRatio: Float
) {
  val otherUserId = chat.useruid

  var otherProfileBitmap by remember { mutableStateOf<Bitmap?>(null) }

  LaunchedEffect(otherUserId) {
    accountViewModel.fetchAccountProfileImageAsBitmap(
        accountId = otherUserId,
        onSuccess = { bitmap -> otherProfileBitmap = bitmap },
        onFailure = {
          // Laisser null si échec
        })
  }
  Row(
      modifier =
          Modifier.fillMaxWidth()
              .testTag("ChatItem")
              .clickable { onClick() }
              .padding(vertical = 8.dp * heightRatio, horizontal = 12.dp * widthRatio),
      verticalAlignment = Alignment.CenterVertically) {
        // Profile Picture Placeholder
        val imageModifier =
            Modifier.size(48.dp * widthRatio).clip(CircleShape).background(Color.Gray)
        if (otherProfileBitmap != null) {
          Image(
              bitmap = otherProfileBitmap!!.asImageBitmap(),
              contentDescription = "Profile Picture",
              contentScale = ContentScale.Crop,
              modifier = imageModifier)
        } else {
          Box(modifier = imageModifier)
        }

        Spacer(modifier = Modifier.width(8.dp * widthRatio))

        // Chat Info
        Column(modifier = Modifier.weight(1f)) {
          Text(
              text = userFirstName,
              style = poppinsTypography.bodyMedium.copy(fontWeight = FontWeight.Bold),
              color = colorScheme.onBackground)
          Text(
              text = chat.messages.lastOrNull()?.content ?: "No messages yet",
              style = poppinsTypography.bodySmall,
              color = colorScheme.onSurface,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis)
        }

        Spacer(modifier = Modifier.width(8.dp * widthRatio))

        // Timestamp
        val formattedDate = formatMessageTimestamp(chat.messages.lastOrNull()?.timestamp)
        Text(
            text = formattedDate,
            style = MaterialTheme.typography.labelSmall,
            color = colorScheme.onSurfaceVariant)
      }
}

// Helper function to format timestamp
fun formatMessageTimestamp(timestamp: com.google.firebase.Timestamp?): String {
  if (timestamp == null) return "--:--"

  val now = Calendar.getInstance()
  val messageTime = Calendar.getInstance().apply { time = timestamp.toDate() }

  return if (now.get(Calendar.YEAR) == messageTime.get(Calendar.YEAR) &&
      now.get(Calendar.DAY_OF_YEAR) == messageTime.get(Calendar.DAY_OF_YEAR)) {
    // If the message is from today, show hours and minutes
    SimpleDateFormat("HH:mm", Locale.getDefault()).format(messageTime.time)
  } else {
    // If the message is from a different day, show day and month
    SimpleDateFormat("dd MMM", Locale.getDefault()).format(messageTime.time)
  }
}
