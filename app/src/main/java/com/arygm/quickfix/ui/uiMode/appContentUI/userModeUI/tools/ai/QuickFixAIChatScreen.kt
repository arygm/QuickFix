package com.arygm.quickfix.ui.uiMode.appContentUI.userModeUI.tools.ai

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.arygm.quickfix.model.tools.ai.GeminiMessageModel
import com.arygm.quickfix.model.tools.ai.GeminiViewModel

@Preview
@Composable
fun QuickFixAIChatScreen(
    modifier: Modifier = Modifier,
    viewModel: GeminiViewModel = GeminiViewModel()
) {
  Column(modifier = modifier) {
    MessageList(modifier = Modifier.weight(1f), messageList = viewModel.messageList)

    MessageInput { viewModel.sendMessage(it) }
  }
}

@Composable
fun MessageList(modifier: Modifier = Modifier, messageList: List<GeminiMessageModel>) {
  if (messageList.size <= 1) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center) {
          Icon(
              imageVector = Icons.Default.AutoAwesome,
              contentDescription = "Default Background",
              tint = MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(230.dp).zIndex(2f))

          Spacer(modifier = Modifier.size(16.dp))

          Text(
              text = "How may I help?",
              modifier = Modifier.padding(16.dp),
              color = MaterialTheme.colorScheme.onBackground,
              style = MaterialTheme.typography.headlineLarge)
        }
  } else {
    LazyColumn(modifier = modifier, reverseLayout = true) {
      items(messageList.reversed()) {
        if (messageList.indexOf(it) != 0) {
          MessageRow(messageModel = it)
        }
      }
    }
  }
}

@Composable
fun MessageRow(messageModel: GeminiMessageModel) {
  val isModel = messageModel.role == "model"

  Row(verticalAlignment = Alignment.CenterVertically) {
    Box(modifier = Modifier.fillMaxWidth()) {
      Box(
          modifier =
              Modifier.align(if (isModel) Alignment.BottomStart else Alignment.BottomEnd)
                  .padding(
                      start = if (isModel) 8.dp else 70.dp,
                      end = if (isModel) 70.dp else 8.dp,
                      top = 8.dp,
                      bottom = 8.dp)
                  .clip(RoundedCornerShape(48f))
                  .background(
                      if (isModel) MaterialTheme.colorScheme.tertiaryContainer
                      else MaterialTheme.colorScheme.primary)
                  .padding(16.dp)) {
            SelectionContainer {
              Text(
                  text = messageModel.message,
                  fontWeight = FontWeight.W500,
                  color =
                      if (isModel) MaterialTheme.colorScheme.onPrimary
                      else MaterialTheme.colorScheme.surface,
                  style = MaterialTheme.typography.bodyMedium,
              )
            }
          }
    }
  }
}

@Composable
fun MessageInput(onMessageSend: (String) -> Unit) {

  var message by remember { mutableStateOf("") }

  Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
    OutlinedTextField(
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text(text = "Describe your issue") },
        value = message,
        onValueChange = { message = it },
        trailingIcon = {
          Surface(
              color = MaterialTheme.colorScheme.primary,
              shape = RoundedCornerShape(18.dp),
              modifier = Modifier.size(40.dp).offset(x = -4.dp)) {
                IconButton(
                    onClick = {
                      if (message.isNotEmpty()) {
                        onMessageSend(message)
                        message = ""
                      }
                    },
                    Modifier.size(20.dp)) {
                      Icon(
                          imageVector = Icons.AutoMirrored.Filled.Send, contentDescription = "Send")
                    }
              }
        })
  }
}
