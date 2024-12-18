package com.arygm.quickfix.ui.uiMode.appContentUI.workerMode.messages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.arygm.quickfix.model.messaging.Chat
import com.arygm.quickfix.model.messaging.ChatViewModel
import com.arygm.quickfix.model.offline.small.PreferencesViewModel
import com.arygm.quickfix.ui.navigation.NavigationActions
import com.arygm.quickfix.utils.loadUserId
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatsScreen(
    chatViewModel: ChatViewModel,
    preferencesViewModel: PreferencesViewModel,
    navigationActions: NavigationActions
) {
    var userId by remember { mutableStateOf("") }
    val chats = chatViewModel.chats.collectAsState().value
    val coroutineScope = rememberCoroutineScope()

    // Charge l'utilisateur ID en utilisant PreferencesViewModel
    LaunchedEffect(Unit) {
        userId = loadUserId(preferencesViewModel)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Chats",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                colors = TopAppBarDefaults.topAppBarColors(containerColor = colorScheme.surface)
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (chats.isEmpty()) {
                // Placeholder si aucun chat n'est disponible
                Text(
                    text = "No chats available.",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.align(Alignment.Center),
                    textAlign = TextAlign.Center
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(chats) { chat ->
                        ChatItem(
                            chat = chat,
                            onClick = {
                                coroutineScope.launch {
                                    chatViewModel.selectChat(chat) // Met à jour le chat sélectionné
                                    navigationActions.navigateTo("messageScreen") // Navigue vers l'écran des messages
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ChatItem(chat: Chat, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Chat with ${chat.workeruid}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Last message: ${
                    if (chat.messages.isNotEmpty()) chat.messages.last().content else "No messages yet"
                }",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}