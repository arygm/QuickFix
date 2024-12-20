package com.arygm.quickfix.ui.uiMode.appContentUI.userModeUI.home

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arygm.quickfix.R
import com.arygm.quickfix.model.account.AccountViewModel
import com.arygm.quickfix.model.messaging.ChatStatus
import com.arygm.quickfix.model.messaging.ChatViewModel
import com.arygm.quickfix.model.messaging.Message
import com.arygm.quickfix.model.offline.small.PreferencesViewModel
import com.arygm.quickfix.model.profile.ProfileViewModel
import com.arygm.quickfix.model.profile.WorkerProfile
import com.arygm.quickfix.model.quickfix.QuickFix
import com.arygm.quickfix.model.quickfix.QuickFixViewModel
import com.arygm.quickfix.model.switchModes.AppMode
import com.arygm.quickfix.ui.elements.QuickFixDetailsScreen
import com.arygm.quickfix.ui.elements.QuickFixSlidingWindowContent
import com.arygm.quickfix.ui.navigation.NavigationActions
import com.arygm.quickfix.ui.theme.poppinsFontFamily
import com.arygm.quickfix.utils.loadAppMode
import com.arygm.quickfix.utils.loadUserId
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*
import kotlinx.coroutines.launch

@Composable
fun MessageScreen(
    chatViewModel: ChatViewModel,
    navigationActions: NavigationActions,
    quickFixViewModel: QuickFixViewModel,
    preferencesViewModel: PreferencesViewModel,
    workerViewModel: ProfileViewModel,
    accountViewModel: AccountViewModel
) {

  // Collecting the selected chat from the ViewModel as state
  val activeChat = chatViewModel.selectedChat.collectAsState().value

  // If no active chat is selected, show a placeholder message and return
  if (activeChat == null) {
    Text("No active chat selected.", modifier = Modifier.testTag("noActiveChatPlaceholder"))
    return
  }
  // Assigning the non-null value of activeChat
  val chat = activeChat
  var userId by remember { mutableStateOf("") }
  var mode by remember { mutableStateOf("") }
  var otherUserId by remember { mutableStateOf("") }
  var chatStatus by remember { mutableStateOf(chat.chatStatus) }
  var messages by remember { mutableStateOf(chat.messages) } // État local des messages

  LaunchedEffect(Unit) {
    userId = loadUserId(preferencesViewModel)
    mode = loadAppMode(preferencesViewModel)
    otherUserId = if (userId == chat.workeruid) chat.useruid else chat.workeruid
  }
  var quickFix by remember { mutableStateOf<QuickFix?>(null) }
  // Finding the associated QuickFix for the selected chat
  quickFixViewModel.fetchQuickFix(chat.quickFixUid, onResult = { quickFix = it })
  if (quickFix == null) {
    // If no QuickFix is found, show a placeholder message and return
    Text("QuickFix not found.", modifier = Modifier.testTag("quickFixNotFoundPlaceholder"))
    return
  }
  val chatId = chat.chatId

  var otherProfileBitmap by remember { mutableStateOf<Bitmap?>(null) }

  // Mutable states to manage input text and sliding window visibility
  var messageText by remember { mutableStateOf("") }
  var isSlidingWindowVisible by remember { mutableStateOf(false) }
  val coroutineScope = rememberCoroutineScope()
  LaunchedEffect(otherUserId) {
    accountViewModel.fetchAccountProfileImageAsBitmap(
        accountId = otherUserId,
        onSuccess = { bitmap -> otherProfileBitmap = bitmap },
        onFailure = {
          // En cas d'erreur, on laisse otherProfileBitmap = null (placeholder)
        })
  }
  // Retrieve chat status and prepare suggestions based on user role (User or Worker)
  val suggestions =
      if (mode == AppMode.USER.name) {
        listOf(
            "How is it going?",
            "Is the time and day okay for you?",
            "I can’t wait to work with you!")
      } else {
        listOf("How is it going?", "This time doesn’t work for me 🤔", "Yo wassup G")
      }
  DisposableEffect(chatId) {
    val db = FirebaseFirestore.getInstance()
    val chatRef = db.collection("chats").document(chatId)

    val listener =
        chatRef.addSnapshotListener { snapshot, e ->
          if (e != null) {
            Log.e("MessageScreen", "Listen failed: ${e.message}")
            return@addSnapshotListener
          }
          if (snapshot != null && snapshot.exists()) {
            // Mise à jour des messages
            val messagesList = snapshot.get("messages") as? List<Map<String, Any>> ?: emptyList()
            val newMessages =
                messagesList.mapNotNull { messageData ->
                  try {
                    Message(
                        messageId = messageData["messageId"] as? String ?: "",
                        senderId = messageData["senderId"] as? String ?: "",
                        content = messageData["content"] as? String ?: "",
                        timestamp =
                            messageData["timestamp"] as? com.google.firebase.Timestamp
                                ?: com.google.firebase.Timestamp.now())
                  } catch (ex: Exception) {
                    Log.e("MessageScreen", "Error parsing message: ${ex.message}")
                    null
                  }
                }

            // Fusionne les nouveaux messages avec l'existant en évitant les doublons
            messages = (messages + newMessages).distinctBy { it.messageId }

            // Mise à jour du chatStatus
            val statusString =
                snapshot.getString("chatStatus") ?: ChatStatus.WAITING_FOR_RESPONSE.name
            chatStatus = ChatStatus.valueOf(statusString)
          }
        }

    // Nettoyer le listener lorsque le composant est détruit
    onDispose { listener.remove() }
  }
  val listState = rememberLazyListState()

  // Fetch chats and QuickFixes when relevant keys change
  LaunchedEffect(key1 = chatId, key2 = chatStatus, key3 = quickFix) {
    chatViewModel.getChats()
    quickFixViewModel.getQuickFixes()
  }
  // Automatically scroll to the last message when new messages are added
  LaunchedEffect(messages) {
    messages.let {
      if (it.isNotEmpty()) {
        listState.animateScrollToItem(it.size - 1)
      }
    }
  }

  var displayNameHeader by remember { mutableStateOf("") }
  var workerProfileDisplayName by remember { mutableStateOf("") }

  BoxWithConstraints(
      modifier =
          Modifier.fillMaxSize().background(colorScheme.background).testTag("messageScreen")) {
        val maxWidth = maxWidth
        val maxHeight = maxHeight

        Scaffold(
            topBar = {
              if (mode == AppMode.USER.name) {
                workerViewModel.fetchUserProfile(
                    chat.workeruid,
                    onResult = {
                      if (it != null) {
                        displayNameHeader = (it as WorkerProfile).displayName
                        workerProfileDisplayName = it.displayName
                      }
                    })
              } else {
                accountViewModel.fetchUserAccount(
                    chat.useruid,
                    onResult = {
                      if (it != null) {
                        displayNameHeader = it.firstName.plus(" ").plus(it.lastName)
                      }
                    })
              }
              // Header section with a back button
              Header(
                  navigationActions = navigationActions,
                  modifier = Modifier.testTag("backButton"),
                  otherProfileBitmap = otherProfileBitmap,
                  displayName = displayNameHeader)
            },
            bottomBar = {
              // Bottom input bar for entering and sending messages
              Box(
                  modifier =
                      Modifier.fillMaxWidth()
                          .height(maxHeight * 0.1f)
                          .testTag("messageInputBar")
                          .background(colorScheme.background)) {
                    // Message input field and send button
                    MessageInput(
                        chatStatus = chatStatus,
                        messageText = messageText,
                        onMessageChange = { messageText = it },
                        onSendMessage = {
                          if (chatStatus == ChatStatus.ACCEPTED ||
                              chatStatus == ChatStatus.GETTING_SUGGESTIONS) {
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
                                messageText = ""
                              }
                            }
                          }
                        })
                  }
            }) { paddingValues ->
              Box(
                  modifier =
                      Modifier.fillMaxSize()
                          .padding(paddingValues)
                          .testTag("messageListBox")
                          .background(colorScheme.background)) {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize().testTag("messageList")) {
                          // Display QuickFix details at the top
                          item {
                            Box(
                                modifier =
                                    Modifier.fillMaxWidth()
                                        .padding(
                                            horizontal = maxWidth * 0.02f,
                                            vertical = maxHeight * 0.02f)
                                        .testTag("quickFixDetailsContainer"),
                                contentAlignment = Alignment.Center) {
                                  Column(
                                      horizontalAlignment = Alignment.CenterHorizontally,
                                      modifier =
                                          Modifier.fillMaxWidth(0.9f)
                                              .background(
                                                  colorScheme.surface, RoundedCornerShape(16.dp))
                                              .testTag("quickFixDetails")) {
                                        QuickFixDetailsScreen(
                                            quickFix = quickFix!!,
                                            isExpanded = false,
                                            onShowMoreToggle = { isSlidingWindowVisible = it },
                                            quickFixViewModel = quickFixViewModel)
                                      }
                                }
                          }
                          item {
                            // Display different UI based on the chat status
                            when (chatStatus) {
                              ChatStatus.WAITING_FOR_RESPONSE -> {
                                // UI for waiting for response
                                if (mode == AppMode.USER.name) {
                                  Column(
                                      horizontalAlignment = Alignment.CenterHorizontally,
                                      modifier =
                                          Modifier.fillMaxWidth()
                                              .padding(horizontal = maxWidth * 0.04f)
                                              .testTag("userResponseContainer")) {
                                        Text(
                                            text =
                                                "Awaiting confirmation from $workerProfileDisplayName...",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = colorScheme.onBackground,
                                            textAlign = TextAlign.Center,
                                            modifier =
                                                Modifier.padding(vertical = maxHeight * 0.02f)
                                                    .testTag("awaitingConfirmationText"))
                                      }
                                } else {
                                  // Worker response options
                                  Column(
                                      horizontalAlignment = Alignment.CenterHorizontally,
                                      modifier =
                                          Modifier.fillMaxWidth()
                                              .padding(horizontal = maxWidth * 0.04f)
                                              .testTag("workerResponseContainer")) {
                                        Text(
                                            text =
                                                "Would you like to accept this QuickFix request?",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = colorScheme.onBackground,
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.padding(bottom = maxHeight * 0.02f))
                                        Row(
                                            horizontalArrangement =
                                                Arrangement.spacedBy(maxWidth * 0.1f),
                                            modifier = Modifier.fillMaxWidth(0.5f)) {
                                              // Accept button
                                              IconButton(
                                                  onClick = {
                                                    coroutineScope.launch {
                                                      Log.e("hhaha", " acceptite")
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
                                                              colorScheme.surface, CircleShape)
                                                          .testTag("acceptButton")) {
                                                    Icon(
                                                        imageVector = Icons.Default.Check,
                                                        contentDescription = "Accept",
                                                        tint = colorScheme.primary,
                                                        modifier = Modifier.fillMaxSize(0.8f))
                                                  }
                                              // Reject button
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
                                                              colorScheme.surface, CircleShape)
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
                                // UI for suggestions state
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier =
                                        Modifier.fillMaxWidth()
                                            .padding(horizontal = maxWidth * 0.04f)
                                            .testTag("gettingSuggestionsContainer")) {
                                      Text(
                                          text =
                                              if (mode == AppMode.USER.name) {
                                                "$workerProfileDisplayName has accepted the QuickFix! 🎉"
                                              } else {
                                                "You have accepted this request! 🎉"
                                              },
                                          style = MaterialTheme.typography.bodyMedium,
                                          color = colorScheme.onBackground,
                                          textAlign = TextAlign.Center,
                                          modifier = Modifier.padding(bottom = maxHeight * 0.01f))
                                      SuggestionsRow(
                                          suggestions = suggestions,
                                          onSuggestionClick = { suggestion ->
                                            val newMessage =
                                                Message(
                                                    messageId =
                                                        System.currentTimeMillis().toString(),
                                                    senderId = userId,
                                                    content = suggestion,
                                                    timestamp = Timestamp.now())
                                            coroutineScope.launch {
                                              chatViewModel.updateChat(
                                                  chat.copy(chatStatus = ChatStatus.ACCEPTED),
                                                  {},
                                                  {})
                                              chatViewModel.sendMessage(chat, newMessage)
                                            }
                                          })
                                    }
                              }
                              ChatStatus.ACCEPTED -> {
                                // UI for active conversation
                                Text(
                                    text = "Conversation is active. Start messaging!",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = colorScheme.onBackground,
                                    textAlign = TextAlign.Center,
                                    modifier =
                                        Modifier.fillMaxWidth()
                                            .padding(vertical = maxHeight * 0.02f)
                                            .testTag("conversationActiveText"))
                              }
                              ChatStatus.WORKER_REFUSED -> {
                                // UI for rejection state
                                Text(
                                    text =
                                        if (mode == AppMode.USER.name) {
                                          "$workerProfileDisplayName has rejected the QuickFix. No big deal! Contact another worker from the search screen! 😊"
                                        } else {
                                          "You have rejected this request. Find your next client on the announcement screen! 😊"
                                        },
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = colorScheme.onBackground,
                                    textAlign = TextAlign.Center,
                                    modifier =
                                        Modifier.padding(
                                                vertical = maxHeight * 0.02f,
                                                horizontal = maxWidth * 0.02f)
                                            .testTag("workerRejectedText"))
                              }
                            }
                          }
                          // Display chat messages with date dividers
                          messages.let { messages ->
                            itemsIndexed(messages) { index, message ->
                              val previousMessage = if (index > 0) messages[index - 1] else null

                              // Add date divider if the message is on a new date
                              if (shouldShowDateDivider(
                                  previousMessage?.timestamp, message.timestamp)) {
                                DateDivider(
                                    timestamp = message.timestamp,
                                    modifier =
                                        Modifier.padding(vertical = maxHeight * 0.01f)
                                            .testTag("dateDivider_$index"))
                              }

                              // Display message bubble
                              MessageBubble(
                                  message = message,
                                  isSent = message.senderId == userId,
                                  modifier =
                                      Modifier.padding(horizontal = maxWidth * 0.02f)
                                          .testTag(
                                              if (message.senderId == userId) "sentMessage_$index"
                                              else "receivedMessage_$index"))
                            }
                          }
                        }
                  }
            }
        // Sliding window overlay for additional details
        if (isSlidingWindowVisible) {
          QuickFixSlidingWindowContent(
              quickFix = quickFix!!,
              isVisible = isSlidingWindowVisible,
              onDismiss = { isSlidingWindowVisible = false },
              navigationActions = navigationActions,
              accountViewModel = accountViewModel,
              quickFixViewModel = quickFixViewModel)
        }
      }
}

@Composable
fun Header(
    navigationActions: NavigationActions,
    modifier: Modifier = Modifier,
    otherProfileBitmap: Bitmap?,
    widthRatio: Float = 1.0f,
    displayName: String
) {
  Box(modifier = Modifier.fillMaxWidth().background(colorScheme.surface).padding(vertical = 8.dp)) {
    // Back button aligned to the start (left)
    IconButton(
        onClick = { navigationActions.goBack() },
        modifier = modifier.align(Alignment.CenterStart)) {
          Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = "Back",
              tint = colorScheme.primary)
        }

    // Centered Column containing profile picture and name
    Column(
        modifier = Modifier.align(Alignment.Center),
        horizontalAlignment = Alignment.CenterHorizontally) {
          val imageModifier =
              Modifier.size(40.dp * widthRatio).clip(CircleShape).background(Color.Gray)
          if (otherProfileBitmap != null) {
            Log.e("hhaha", "kkhaawi  ${otherProfileBitmap}")
            Image(
                bitmap = otherProfileBitmap.asImageBitmap(),
                contentDescription = "Profile Picture",
                contentScale = ContentScale.Crop,
                modifier = imageModifier)
          } else {
            Log.e("hhaha", "kkhaawi")

            Text("allo")
            Image(
                painter = painterResource(id = R.drawable.placeholder_worker),
                contentDescription = "Profile Picture",
                contentScale = ContentScale.Crop,
                modifier = imageModifier)
          }
          Text(
              text = displayName,
              fontWeight = FontWeight.Bold,
              fontSize = 20.sp,
              fontFamily = poppinsFontFamily,
              color = colorScheme.onBackground,
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
                  fontFamily = poppinsFontFamily,
                  color = if (isSent) Color.White else Color.Black,
                  fontSize = 16.sp,
                  fontWeight = FontWeight.Medium,
              )
            }
      }
}

// Date divider styled with gray color
@Composable
fun DateDivider(timestamp: Timestamp, modifier: Modifier = Modifier) {
  Text(
      text = formatDate(timestamp),
      fontFamily = poppinsFontFamily,
      color = Color.Gray,
      fontSize = 12.sp,
      fontWeight = FontWeight.Bold,
      modifier = modifier.fillMaxWidth().padding(vertical = 8.dp).testTag("dateDivider"),
      textAlign = TextAlign.Center)
}

@Composable
fun MessageInput(
    messageText: String,
    onMessageChange: (String) -> Unit,
    onSendMessage: () -> Unit,
    chatStatus: ChatStatus
) {
  Row(
      modifier =
          Modifier.padding(8.dp)
              .fillMaxWidth()
              .background(colorScheme.surface, RoundedCornerShape(24.dp))
              .padding(
                  horizontal = 12.dp, vertical = 8.dp) // Inner padding for the rounded input box
              .testTag("messageInputArea"),
      verticalAlignment = Alignment.CenterVertically) {
        // Message input text field
        TextField(
            enabled =
                (chatStatus == ChatStatus.ACCEPTED || chatStatus == ChatStatus.GETTING_SUGGESTIONS),
            value = messageText,
            onValueChange = onMessageChange,
            placeholder = { Text("Message") },
            colors =
                OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = colorScheme.surface,
                    focusedContainerColor = colorScheme.surface,
                    errorContainerColor = colorScheme.errorContainer,
                    unfocusedPlaceholderColor = colorScheme.onSecondary,
                    focusedPlaceholderColor = colorScheme.onSecondary,
                    errorTextColor = colorScheme.error,
                    unfocusedBorderColor = Color.Transparent,
                    focusedBorderColor = Color.Transparent),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.weight(0.9f).padding(end = 0.dp).testTag("messageTextField"))

        // Send button
        IconButton(
            onClick = { onSendMessage() },
            enabled =
                (chatStatus == ChatStatus.ACCEPTED || chatStatus == ChatStatus.GETTING_SUGGESTIONS),
            modifier =
                Modifier.weight(0.1f)
                    .aspectRatio(1f)
                    .background(colorScheme.primary, CircleShape)
                    .testTag("sendButton")) {
              Icon(
                  imageVector =
                      Icons.AutoMirrored.Filled.Send, // Replace with your custom icon if available
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
