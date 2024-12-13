package com.arygm.quickfix.model.messaging

interface ChatRepository {
  fun init(onSuccess: () -> Unit)

  fun getRandomUid(): String

  suspend fun createChat(chat: Chat, onSuccess: () -> Unit, onFailure: (Exception) -> Unit)

  suspend fun deleteChat(chat: Chat, onSuccess: () -> Unit, onFailure: (Exception) -> Unit)

  suspend fun getChats(onSuccess: (List<Chat>) -> Unit, onFailure: (Exception) -> Unit)

  suspend fun chatExists(
      userId: String,
      workerId: String,
      onSuccess: (Pair<Boolean, Chat?>) -> Unit,
      onFailure: (Exception) -> Unit
  )

  suspend fun sendMessage(
      chat: Chat,
      message: Message,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  )

  suspend fun deleteMessage(
      chat: Chat,
      message: Message,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  )

  suspend fun updateChat(chat: Chat, onSuccess: () -> Unit, onFailure: (Exception) -> Unit)
}
