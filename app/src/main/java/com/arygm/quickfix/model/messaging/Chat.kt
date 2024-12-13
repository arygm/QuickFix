package com.arygm.quickfix.model.messaging

import com.arygm.quickfix.model.offline.large.Converters
import com.arygm.quickfix.model.offline.large.messaging.ChatEntity

data class Chat(
    val chatId: String = "", // Default value
    val workeruid: String = "", // Default value
    val useruid: String = "", // Default value
    val quickFixUid: String = "", // New field for QuickFix association
    val messages: List<Message> = emptyList(), // Default empty list
    val chatStatus: ChatStatus = ChatStatus.WAITING_FOR_RESPONSE // Default to waiting for response
) {
  fun toChatEntity(): ChatEntity {
    return ChatEntity(
        chatId = chatId,
        workeruid = workeruid,
        useruid = useruid,
        messages = Converters().fromMessagesList(messages))
  }
}

enum class ChatStatus {
  WAITING_FOR_RESPONSE,
  ACCEPTED,
  WORKER_REFUSED,
  GETTING_SUGGESTIONS
}
