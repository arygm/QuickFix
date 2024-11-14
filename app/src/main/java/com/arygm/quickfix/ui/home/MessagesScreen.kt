package com.arygm.quickfix.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arygm.quickfix.model.account.LoggedInAccountViewModel
import com.arygm.quickfix.model.messaging.ChatViewModel
import com.arygm.quickfix.model.messaging.Message
import com.arygm.quickfix.ui.navigation.NavigationActions
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.*
import kotlinx.coroutines.launch

@Composable
fun MessageScreen(
    loggedInAccountViewModel: LoggedInAccountViewModel,
    chatViewModel: ChatViewModel,
    navigationActions: NavigationActions
) {
  val loggedInAccount by loggedInAccountViewModel.loggedInAccount.collectAsState()

  val userId = loggedInAccount?.uid ?: return
  val activeChatId = loggedInAccount?.activeChats?.firstOrNull()

  // If no chat is active, we can display an empty interface. This will be changed later
  if (activeChatId == null) {
    Column(
        modifier =
            Modifier.fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .testTag("messageScreen")) {
          // Header avec bouton de retour et photo de profil
          Header(navigationActions)
          // Message list vide
          Box(
              modifier =
                  Modifier.weight(1f)
                      .padding(horizontal = 16.dp, vertical = 8.dp)
                      .testTag("messageList")) {
                // Aucune conversation n'est active
              }
        }
    return
  }

  var messageText by remember { mutableStateOf("") }
  val coroutineScope = rememberCoroutineScope()

  val chatList by chatViewModel.chats.collectAsState()
  val chat = chatList.firstOrNull { it.chatId == activeChatId }

  // Charge les chats lorsqu'un chat actif est défini
  LaunchedEffect(key1 = activeChatId) { chatViewModel.getChats() }

  Column(
      modifier =
          Modifier.fillMaxSize()
              .background(MaterialTheme.colorScheme.background)
              .testTag("messageScreen")) {
        // Header with back button and profile picture
        Header(navigationActions)

        // Message list
        LazyColumn(
            modifier =
                Modifier.weight(1f)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .testTag("messageList")) {
              chat?.messages?.let { messages ->
                itemsIndexed(messages) { index, message ->
                  val previousMessage = if (index > 0) messages[index - 1] else null
                  if (shouldShowDateDivider(previousMessage?.timestamp, message.timestamp)) {
                    DateDivider(timestamp = message.timestamp)
                  }
                  MessageBubble(message = message, isSent = message.senderId == userId)
                }
              }
            }

        // Input area for sending messages
        if (chat != null) {
          MessageInput(
              messageText = messageText,
              onMessageChange = { messageText = it },
              onSendMessage = {
                if (messageText.isNotBlank()) {
                  val newMessage =
                      Message(
                          messageId = System.currentTimeMillis().toString(),
                          senderId = userId,
                          content = messageText,
                          timestamp = Timestamp.now())
                  coroutineScope.launch {
                    chatViewModel.sendMessage(chat = chat, message = newMessage)
                    messageText = ""
                  }
                }
              })
        }
      }
}

@Composable
fun Header(navigationActions: NavigationActions) {
  Box(
      modifier =
          Modifier.fillMaxWidth()
              .background(MaterialTheme.colorScheme.surface)
              .padding(vertical = 8.dp)) {
        // Back button aligned to the start (left)
        IconButton(
            onClick = { navigationActions.goBack() },
            modifier = Modifier.align(Alignment.CenterStart).testTag("backButton")) {
              Icon(
                  imageVector = Icons.Default.ArrowBack,
                  contentDescription = "Back",
                  tint = MaterialTheme.colorScheme.primary)
            }

        // Centered Column containing profile picture and name
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally) {
              Box(
                  modifier =
                      Modifier.size(40.dp)
                          .background(Color.Black, CircleShape)
                          .testTag("profilePicture"))
              Text(
                  text = "Moha",
                  fontWeight = FontWeight.Bold,
                  fontSize = 20.sp,
                  color = MaterialTheme.colorScheme.onBackground,
                  modifier = Modifier.testTag("chatPartnerName").padding(vertical = 4.dp))
            }
      }
}

@Composable
fun MessageBubble(message: Message, isSent: Boolean) {
  Row(
      modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp, horizontal = 8.dp),
      horizontalArrangement = if (isSent) Arrangement.End else Arrangement.Start) {
        val size = 0.6 * LocalConfiguration.current.screenWidthDp
        Box(
            modifier =
                Modifier.widthIn(max = size.dp) // Set max width to 60% of screen width
                    .background(
                        color = if (isSent) colorScheme.primary else colorScheme.secondary,
                        shape = RoundedCornerShape(16.dp))
                    .padding(12.dp)
                    .testTag(if (isSent) "sentMessage" else "receivedMessage")) {
              Text(
                  text = message.content,
                  color = if (isSent) Color.White else Color.Black,
                  fontSize = 16.sp)
            }
      }
}

// Date divider styled with gray color
@Composable
fun DateDivider(timestamp: Timestamp) {
  Text(
      text = formatDate(timestamp),
      color = Color.Gray,
      fontSize = 12.sp,
      fontWeight = FontWeight.Bold,
      modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp).testTag("dateDivider"),
      textAlign = androidx.compose.ui.text.style.TextAlign.Center)
}

@Composable
fun MessageInput(
    messageText: String,
    onMessageChange: (String) -> Unit,
    onSendMessage: () -> Unit
) {
  Row(
      modifier =
          Modifier.padding(8.dp)
              .fillMaxWidth()
              .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(24.dp))
              .padding(
                  horizontal = 12.dp, vertical = 8.dp) // Inner padding for the rounded input box
              .testTag("messageInputArea"),
      verticalAlignment = Alignment.CenterVertically) {
        // Message input text field
        TextField(
            value = messageText,
            onValueChange = onMessageChange,
            placeholder = { Text("Message") },
            colors =
                OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = colorScheme.surface,
                    focusedContainerColor = colorScheme.surface,
                    errorContainerColor = MaterialTheme.colorScheme.errorContainer,
                    unfocusedPlaceholderColor = colorScheme.onSecondary,
                    focusedPlaceholderColor = colorScheme.onSecondary,
                    errorTextColor = MaterialTheme.colorScheme.error,
                    unfocusedBorderColor = Color.Transparent,
                    focusedBorderColor = Color.Transparent),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.weight(0.9f).padding(end = 0.dp).testTag("messageTextField"))

        // Send button
        IconButton(
            onClick = onSendMessage,
            modifier =
                Modifier.weight(0.1f)
                    .aspectRatio(1f)
                    .background(MaterialTheme.colorScheme.primary, CircleShape)
                    .testTag("sendButton")) {
              Icon(
                  imageVector = Icons.Default.Send, // Replace with your custom icon if available
                  contentDescription = "Send",
                  tint = Color.White)
            }
      }
}

// Helper function to format timestamps as dates
fun formatDate(timestamp: Timestamp): String {
  val sdf = SimpleDateFormat("d MMM", Locale.getDefault())
  return sdf.format(timestamp.toDate())
}

// Helper function to determine if a date divider is needed
fun shouldShowDateDivider(previousTimestamp: Timestamp?, currentTimestamp: Timestamp): Boolean {
  if (previousTimestamp == null) return true
  val previousDate = previousTimestamp.toDate()
  val currentDate = currentTimestamp.toDate()
  val dateFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
  return dateFormat.format(previousDate) != dateFormat.format(currentDate)
}
