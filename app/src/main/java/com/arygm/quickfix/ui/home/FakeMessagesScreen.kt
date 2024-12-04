package com.arygm.quickfix.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.arygm.quickfix.model.messaging.Chat
import com.arygm.quickfix.model.messaging.Message
import com.arygm.quickfix.ui.navigation.NavigationActions
import com.arygm.quickfix.ui.theme.QuickFixTheme
import com.google.firebase.Timestamp

// Main MessageScreen composable function
@Composable
fun FakeMessageScreen(navigationActions: NavigationActions) {
  val userId = "user1"
  val activeChat =
      Chat(
          chatId = "chat1",
          workeruid = "worker1",
          useruid = "user1",
          messages =
              listOf(
                  Message("msg1", "user1", "Salut bro", Timestamp.now()),
                  Message("msg2", "worker1", "Moi ?", Timestamp.now()),
                  Message(
                      "msg3", "user1", "C'est quoi ton cours préféré à l'école?", Timestamp.now()),
                  Message("msg4", "worker1", "Moi c’est la cantine 😊", Timestamp.now())))

  var messageText by remember { mutableStateOf("") }

  Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
    // Header with back button and profile picture
    Header(navigationActions)

    // Message list
    LazyColumn(modifier = Modifier.weight(1f).padding(vertical = 8.dp)) {
      itemsIndexed(activeChat.messages) { index, message ->
        val previousMessage = if (index > 0) activeChat.messages[index - 1] else null
        if (shouldShowDateDivider(previousMessage?.timestamp, message.timestamp)) {
          DateDivider(timestamp = message.timestamp)
        }
        MessageBubble(message = message, isSent = message.senderId == userId)
      }
    }

    // Input area for sending messages
    MessageInput(
        messageText = messageText,
        onMessageChange = { messageText = it },
        onSendMessage = {
          if (messageText.isNotBlank()) {
            messageText = "" // Clear input after "sending"
          }
        })
  }
}

// Preview function to display the composable
@Preview(showBackground = true)
@Composable
fun PreviewMessageScreen() {
  QuickFixTheme {
    val homeNavController = rememberNavController()
    val navigationActions = remember { NavigationActions(homeNavController) }

    // A surface container using the 'background' color from the theme
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
      FakeMessageScreen(navigationActions)
    }
  }
}
