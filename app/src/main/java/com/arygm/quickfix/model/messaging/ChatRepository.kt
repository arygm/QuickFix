package com.arygm.quickfix.model.messaging

interface ChatRepository {
  fun init(onSuccess: () -> Unit)

  fun getRandomUid(): String

  fun createChat(chat: Chat, onSuccess: () -> Unit, onFailure: (Exception) -> Unit)

  fun deleteChat(chat: Chat, onSuccess: () -> Unit, onFailure: (Exception) -> Unit)

  fun getChats(onSuccess: (List<Chat>) -> Unit, onFailure: (Exception) -> Unit)

  fun chatExists(
      userId: String,
      workerId: String,
      onSuccess: (Pair<Boolean, Chat?>) -> Unit,
      onFailure: (Exception) -> Unit
  )

  fun sendMessage(
      chat: Chat,
      message: Message,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  )

  fun deleteMessage(
      chat: Chat,
      message: Message,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  )

  fun updateChat(chat: Chat, onSuccess: () -> Unit, onFailure: (Exception) -> Unit)
}
