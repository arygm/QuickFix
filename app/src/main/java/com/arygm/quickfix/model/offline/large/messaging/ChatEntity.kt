package com.arygm.quickfix.model.offline.large.messaging

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.arygm.quickfix.model.messaging.Chat
import com.arygm.quickfix.model.offline.large.Converters

@Entity(tableName = "chats")
data class ChatEntity(
    @PrimaryKey val chatId: String,
    val workeruid: String,
    val useruid: String,
    val messages: String // Serialized List<Message>
) {
  fun toChat(): Chat {
    return Chat(
        chatId = chatId,
        workeruid = workeruid,
        useruid = useruid,
        messages = Converters().toMessagesList(messages))
  }
}
