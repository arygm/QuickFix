package com.arygm.quickfix.ui.home

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arygm.quickfix.model.messaging.ChatStatus
import com.arygm.quickfix.model.messaging.ChatViewModel
import com.arygm.quickfix.model.messaging.Message
import com.arygm.quickfix.model.quickfix.QuickFixViewModel
import com.arygm.quickfix.ui.elements.QuickFixDetailsScreen
import com.arygm.quickfix.ui.elements.QuickFixSlidingWindowContent
import com.arygm.quickfix.ui.navigation.NavigationActions
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.*
import kotlinx.coroutines.launch

@Composable
fun MessageScreen(
    chatViewModel: ChatViewModel,
    navigationActions: NavigationActions,
    quickFixViewModel: QuickFixViewModel, // Directly passed QuickFix object
    userId: String, // Directly pass the user ID
    isUser: Boolean // Boolean to differentiate between user and worker
) {
  val activeChat by chatViewModel.selectedChat.collectAsState()
  val quickFixList by quickFixViewModel.quickFixes.collectAsState()

  if (activeChat == null) {
    println("dkhlt fchat khawi")
    // Placeholder si aucun chat actif
    Text("No active chat selected.", modifier = Modifier.testTag("noActiveChatPlaceholder"))
    return
  }
  val chat = activeChat!!

  val chatQuickFix = quickFixList.firstOrNull { it.uid == activeChat?.quickFixUid }
  if (chatQuickFix == null) {
    println("dkhlt fquickfix khawi")

    // Placeholder si aucun QuickFix n'est trouvé
    Text("QuickFix not found.", modifier = Modifier.testTag("quickFixNotFoundPlaceholder"))
    return
  }
  val quickFix = chatQuickFix!!
  val chatId = chat.chatId // Obtain chatUid directly from QuickFix

  var messageText by remember { mutableStateOf("") }
  var isSlidingWindowVisible by remember { mutableStateOf(false) } // Sliding window visibility
  val coroutineScope = rememberCoroutineScope()

  val chatStatus = chat.chatStatus
  // List of suggestions based on the user/worker role
  val suggestions =
      if (isUser) {
        listOf(
            "How is it going?",
            "Is the time and day okay for you?",
            "I can’t wait to work with you!")
      } else {
        listOf("How is it going?", "This time doesn’t work for me 🤔", "Yo wassup G")
      }
  val listState = rememberLazyListState()

  LaunchedEffect(key1 = chatId, key2 = chatStatus, key3 = quickFix) {
    chatViewModel.getChats()
    quickFixViewModel.getQuickFixes()
  }
  LaunchedEffect(chat.messages) {
    // Automatically scroll to the last message when messages are updated
    chat.messages.let {
      if (it.isNotEmpty()) {
        listState.animateScrollToItem(it.size - 1)
      }
    }
  }
  Box(
      modifier =
          Modifier.fillMaxSize()
              .background(MaterialTheme.colorScheme.background)
              .testTag("messageScreen")) {
        Scaffold(
            topBar = { Header(navigationActions, modifier = Modifier.testTag("backButton")) },
            bottomBar = {
              Box(
                  modifier =
                      Modifier.fillMaxWidth()
                          .testTag("messageInputBar")
                          .background(
                              MaterialTheme.colorScheme.background) // Change background color here
                  ) {
                    MessageInput(
                        messageText = messageText,
                        onMessageChange = { messageText = it },
                        onSendMessage = {
                          if (chatStatus == ChatStatus.ACCEPTED ||
                              chatStatus == ChatStatus.GETTING_SUGGESTIONS) {
                            Log.e("hhaha", "tseft hamoud lkbir lmessage")
                            // Allow sending messages only if the worker has accepted the request
                            if (messageText.isNotBlank()) {
                              val newMessage =
                                  Message(
                                      messageId = System.currentTimeMillis().toString(),
                                      senderId = userId,
                                      content = messageText,
                                      timestamp = Timestamp.now())
                              coroutineScope.launch {
                                if (chat.chatStatus == ChatStatus.GETTING_SUGGESTIONS) {
                                  chatViewModel.updateChat(
                                      chat.copy(chatStatus = ChatStatus.ACCEPTED), {}, {})
                                }
                                chatViewModel.sendMessage(chat, newMessage)
                                Log.e("hhaha", "klit chat ${chat}")

                                messageText = ""
                              }
                            }
                          } else {
                            // Do nothing if the worker hasn't accepted
                          }
                        })
                  }
            }) { paddingValues ->
              Box(
                  modifier =
                      Modifier.fillMaxSize()
                          .padding(paddingValues)
                          .testTag("messageListBox")
                          .background(MaterialTheme.colorScheme.background)) {
                    LazyColumn(
                        state = listState, // Attach LazyListState to LazyColumn
                        modifier = Modifier.fillMaxSize().testTag("messageList")) {
                          // QuickFixDetailsScreen
                          item {
                            // Add QuickFixDetailsScreen as a part of the scrollable content
                            Box(
                                modifier =
                                    Modifier.fillMaxWidth().testTag("quickFixDetailsContainer"),
                                contentAlignment = Alignment.Center) {
                                  Column(
                                      horizontalAlignment = Alignment.CenterHorizontally,
                                      modifier =
                                          Modifier.fillMaxWidth(0.9f)
                                              .fillMaxHeight(0.55f)
                                              .padding(8.dp)
                                              .testTag("quickFixDetails")
                                              .background(
                                                  MaterialTheme.colorScheme.surface,
                                                  RoundedCornerShape(16.dp))) {
                                        QuickFixDetailsScreen(
                                            quickFix = quickFix,
                                            isExpanded = false,
                                            onShowMoreToggle = { isSlidingWindowVisible = it })
                                      }
                                }
                          }
                          item {
                            when (chatStatus) {
                              ChatStatus.WAITING_FOR_RESPONSE -> {
                                if (isUser) {
                                  Text(
                                      text = "Awaiting confirmation from ${quickFix.workerName}...",
                                      style = MaterialTheme.typography.bodyMedium,
                                      color = MaterialTheme.colorScheme.onBackground,
                                      textAlign = TextAlign.Center,
                                      modifier =
                                          Modifier.padding(16.dp)
                                              .testTag("awaitingConfirmationText"))
                                } else {
                                  Column(
                                      horizontalAlignment = Alignment.CenterHorizontally,
                                      modifier =
                                          Modifier.fillMaxWidth()
                                              .padding(16.dp)
                                              .testTag("workerResponseContainer")) {
                                        Text(
                                            text =
                                                "Would you like to accept this QuickFix request?",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onBackground,
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.padding(bottom = 16.dp))
                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(40.dp),
                                            modifier = Modifier.fillMaxWidth(0.5f)) {
                                              IconButton(
                                                  onClick = {
                                                    coroutineScope.launch {
                                                      chat.let {
                                                        val updatedChat =
                                                            it.copy(
                                                                chatStatus =
                                                                    ChatStatus.GETTING_SUGGESTIONS)
                                                        chatViewModel.updateChat(
                                                            updatedChat, {}, {})
                                                      }
                                                    }
                                                  },
                                                  modifier =
                                                      Modifier.weight(0.1f)
                                                          .aspectRatio(1f)
                                                          .background(
                                                              MaterialTheme.colorScheme.surface,
                                                              CircleShape)
                                                          .testTag("acceptButton")) {
                                                    Icon(
                                                        imageVector = Icons.Default.Check,
                                                        contentDescription = "Accept",
                                                        tint = colorScheme.primary,
                                                        modifier = Modifier.fillMaxSize(0.8f))
                                                  }
                                              IconButton(
                                                  onClick = {
                                                    coroutineScope.launch {
                                                      chat.let {
                                                        val updatedChat =
                                                            it.copy(
                                                                chatStatus =
                                                                    ChatStatus.WORKER_REFUSED)
                                                        chatViewModel.updateChat(
                                                            updatedChat, {}, {})
                                                      }
                                                    }
                                                  },
                                                  modifier =
                                                      Modifier.weight(0.1f)
                                                          .aspectRatio(1f)
                                                          .background(
                                                              MaterialTheme.colorScheme.surface,
                                                              CircleShape)
                                                          .testTag("refuseButton")) {
                                                    Icon(
                                                        imageVector = Icons.Default.Close,
                                                        contentDescription = "Reject",
                                                        tint = colorScheme.primary,
                                                        modifier = Modifier.fillMaxSize(0.8f))
                                                  }
                                            }
                                      }
                                }
                              }
                              ChatStatus.GETTING_SUGGESTIONS -> {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier =
                                        Modifier.fillMaxWidth()
                                            .testTag("gettingSuggestionsContainer")
                                            .padding(16.dp)) {
                                      Text(
                                          text =
                                              if (isUser) {
                                                "${quickFix.workerName} has accepted the QuickFix! 🎉"
                                              } else {
                                                "You have accepted this request! 🎉"
                                              },
                                          style = MaterialTheme.typography.bodyMedium,
                                          color = MaterialTheme.colorScheme.onBackground,
                                          textAlign = TextAlign.Center,
                                          modifier = Modifier.padding(bottom = 4.dp))
                                      Text(
                                          text =
                                              "Check out these suggestions to kick-off the conversation 😊",
                                          style = MaterialTheme.typography.bodySmall,
                                          color = MaterialTheme.colorScheme.onBackground,
                                          textAlign = TextAlign.Center)
                                    }
                                SuggestionsRow(
                                    suggestions = suggestions,
                                    onSuggestionClick = { suggestion ->
                                      val newMessage =
                                          Message(
                                              messageId = System.currentTimeMillis().toString(),
                                              senderId = userId,
                                              content = suggestion,
                                              timestamp = Timestamp.now())
                                      Log.e("hhaha", "chat machi null ${newMessage}")

                                      coroutineScope.launch {
                                        chatViewModel.updateChat(
                                            chat.copy(chatStatus = ChatStatus.ACCEPTED), {}, {})
                                        chatViewModel.sendMessage(chat, newMessage)
                                      }
                                    })
                              }
                              ChatStatus.ACCEPTED -> {
                                Text(
                                    text = "Conversation is active. Start messaging!",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onBackground,
                                    textAlign = TextAlign.Center,
                                    modifier =
                                        Modifier.padding(16.dp)
                                            .fillMaxWidth()
                                            .testTag("conversationActiveText"))
                              }
                              ChatStatus.WORKER_REFUSED -> {
                                Text(
                                    text =
                                        if (isUser) {
                                          "${quickFix.workerName} has rejected the QuickFix. No big deal! Contact another worker from the search screen! 😊"
                                        } else {
                                          "You have rejected this request. Find your next client on the announcement screen! 😊"
                                        },
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onBackground,
                                    textAlign = TextAlign.Center,
                                    modifier =
                                        Modifier.padding(16.dp).testTag("workerRejectedText"))
                              }
                            }
                          }

                          // Messages with date dividers
                          chat.messages.let { messages ->
                            itemsIndexed(messages) { index, message ->
                              val previousMessage = if (index > 0) messages[index - 1] else null

                              if (shouldShowDateDivider(
                                  previousMessage?.timestamp, message.timestamp)) {
                                DateDivider(
                                    timestamp = message.timestamp,
                                    modifier = Modifier.testTag("dateDivider_$index"))
                              }

                              MessageBubble(
                                  message = message,
                                  isSent = message.senderId == userId,
                                  modifier =
                                      Modifier.testTag(
                                          if (message.senderId == userId) "sentMessage_$index"
                                          else "receivedMessage_$index"))
                            }
                          }
                        }

                    // Sliding window for detailed QuickFix info
                    /*    if (isSlidingWindowVisible) {
                        QuickFixSlidingWindowContent(
                            quickFix = quickFix,
                            isVisible = isSlidingWindowVisible,
                            onDismiss = { isSlidingWindowVisible = false }
                        )
                    }*/
                  }
            } // Sliding window overlay
        if (isSlidingWindowVisible) {

          QuickFixSlidingWindowContent(
              quickFix = quickFix,
              isVisible = isSlidingWindowVisible,
              onDismiss = { isSlidingWindowVisible = false })
        }
      }
}

@Composable
fun Header(navigationActions: NavigationActions, modifier: Modifier = Modifier) {
  Box(
      modifier =
          Modifier.fillMaxWidth()
              .background(MaterialTheme.colorScheme.surface)
              .padding(vertical = 8.dp)) {
        // Back button aligned to the start (left)
        IconButton(
            onClick = { navigationActions.goBack() },
            modifier = modifier.align(Alignment.CenterStart)) {
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
fun MessageBubble(message: Message, isSent: Boolean, modifier: Modifier = Modifier) {
  Row(
      modifier = modifier.fillMaxWidth().padding(vertical = 10.dp, horizontal = 8.dp),
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
fun DateDivider(timestamp: Timestamp, modifier: Modifier = Modifier) {
  Text(
      text = formatDate(timestamp),
      color = Color.Gray,
      fontSize = 12.sp,
      fontWeight = FontWeight.Bold,
      modifier = modifier.fillMaxWidth().padding(vertical = 8.dp).testTag("dateDivider"),
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
            onClick = {
              Log.e("hhaha", "tseft lmessage")
              onSendMessage()
            },
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

@Composable
fun SuggestionsRow(suggestions: List<String>, onSuggestionClick: (String) -> Unit) {
  LazyRow(
      modifier = Modifier.fillMaxWidth().padding(vertical = 0.dp),
      horizontalArrangement = Arrangement.spacedBy(16.dp),
      contentPadding = PaddingValues(horizontal = 8.dp)) {
        items(suggestions) { suggestion ->
          Button(
              onClick = { onSuggestionClick(suggestion) },
              colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF800020)),
              shape = RoundedCornerShape(16.dp),
              modifier = Modifier.padding(vertical = 8.dp)) {
                Text(
                    text = suggestion,
                    color = Color.White,
                    style = MaterialTheme.typography.bodySmall)
              }
        }
      }
}
