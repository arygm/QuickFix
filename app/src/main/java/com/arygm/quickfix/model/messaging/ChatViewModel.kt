package com.arygm.quickfix.model.messaging

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.arygm.quickfix.model.offline.large.QuickFixRoomDatabase
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ChatViewModel(
    private val repository: ChatRepository,
    dispatcher: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {

  private val _chats = MutableStateFlow<List<Chat>>(emptyList())
  val chats: StateFlow<List<Chat>> = _chats.asStateFlow()
  private val _selectedChat = MutableStateFlow<Chat?>(null)
  val selectedChat: StateFlow<Chat?> = _selectedChat.asStateFlow()
  private val viewModelScope = CoroutineScope(dispatcher)

  suspend fun getChats() {
    repository.getChats(
        onSuccess = { _chats.value = it },
        onFailure = { e -> Log.e("ChatViewModel", "Failed to fetch chats: ${e.message}") })
  }

  companion object {
    fun Factory(context: Context): ViewModelProvider.Factory =
        object : ViewModelProvider.Factory {
          @Suppress("UNCHECKED_CAST")
          override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ChatViewModel(
                ChatRepositoryFirestore(
                    QuickFixRoomDatabase.getInstance(context).chatDao(), Firebase.firestore))
                as T
          }
        }
  }

  suspend fun addChat(chat: Chat, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
    repository.createChat(
        chat = chat,
        onSuccess = {
          viewModelScope.launch { getChats() }
          onSuccess()
        },
        onFailure = { e ->
          Log.e("ChatViewModel", "Failed to add chat: ${e.message}")
          onFailure(e)
        })
  }

  suspend fun deleteChat(chat: Chat) {
    repository.deleteChat(
        chat = chat,
        onSuccess = { viewModelScope.launch { getChats() } },
        onFailure = { e -> Log.e("ChatViewModel", "Failed to delete chat: ${e.message}") })
  }

  suspend fun chatExists(userId: String, workerId: String, onResult: (Boolean, Chat?) -> Unit) {
    repository.chatExists(
        userId = userId,
        workerId = workerId,
        onSuccess = { (exists, chat) ->
          if (exists) {
            Log.d("ChatCheck", "Chat between this user and worker exists")
            onResult(true, chat)
          } else {
            Log.d("ChatCheck", "Chat between this user and worker does not exist")
            onResult(false, null)
          }
        },
        onFailure = { e ->
          Log.e("ChatViewModel", "Failed to check if chat exists: ${e.message}")
          onResult(false, null)
        })
  }

  suspend fun sendMessage(chat: Chat, message: Message) {
    repository.sendMessage(
        chat = chat,
        message = message,
        onSuccess = { viewModelScope.launch { getChats() } },
        onFailure = { e -> Log.e("ChatViewModel", "Failed to send message: ${e.message}") })
  }

  suspend fun deleteMessage(chat: Chat, message: Message) {
    repository.deleteMessage(
        chat = chat,
        message = message,
        onSuccess = { viewModelScope.launch { getChats() } },
        onFailure = { e -> Log.e("ChatViewModel", "Failed to delete message: ${e.message}") })
  }

  fun getRandomUid(): String {
    return repository.getRandomUid()
  }

  suspend fun updateChat(chat: Chat, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
    repository.updateChat(
        chat = chat,
        onSuccess = {
          viewModelScope.launch { getChats() }
          onSuccess()
        },
        onFailure = { e ->
          Log.e("ChatViewModel", "Failed to update chat: ${e.message}")
          onFailure(e)
        })
  }

  fun selectChat(chat: Chat) {
    _selectedChat.value = chat
  }

  fun clearSelectedChat() {
    _selectedChat.value = null
  }
}
