package com.arygm.quickfix.model.messaging

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ChatViewModel(private val repository: ChatRepository) : ViewModel() {

  private val chats_ = MutableStateFlow<List<Chat>>(emptyList())
  val chats: StateFlow<List<Chat>> = chats_.asStateFlow()

  fun getChats() {
    repository.getChats(
        onSuccess = { chats_.value = it },
        onFailure = { e -> Log.e("ChatViewModel", "Failed to fetch chats: ${e.message}") })
  }

  companion object {
    val Factory: ViewModelProvider.Factory =
        object : ViewModelProvider.Factory {
          @Suppress("UNCHECKED_CAST")
          override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ChatViewModel(ChatRepositoryFirestore(Firebase.firestore)) as T
          }
        }
  }

  fun addChat(chat: Chat, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
    repository.createChat(
        chat = chat,
        onSuccess = {
          getChats()
          onSuccess()
        },
        onFailure = { e ->
          Log.e("ChatViewModel", "Failed to add chat: ${e.message}")
          onFailure(e)
        })
  }

  fun deleteChat(chat: Chat) {
    repository.deleteChat(
        chat = chat,
        onSuccess = { getChats() },
        onFailure = { e -> Log.e("ChatViewModel", "Failed to delete chat: ${e.message}") })
  }

  fun chatExists(userId: String, workerId: String, onResult: (Boolean, Chat?) -> Unit) {
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

  fun sendMessage(chat: Chat, message: Message) {
    repository.sendMessage(
        chat = chat,
        message = message,
        onSuccess = { getChats() },
        onFailure = { e -> Log.e("ChatViewModel", "Failed to send message: ${e.message}") })
  }

  fun deleteMessage(chat: Chat, message: Message) {
    repository.deleteMessage(
        chat = chat,
        message = message,
        onSuccess = { getChats() },
        onFailure = { e -> Log.e("ChatViewModel", "Failed to delete message: ${e.message}") })
  }

  fun getRandomUid(): String {
    return repository.getRandomUid()
  }
}
