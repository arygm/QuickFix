package com.arygm.quickfix.model.chats.messages

interface MessageRepository {
    fun init(onSuccess: () -> Unit)
    fun sendMessage(message: Message, onSuccess: () -> Unit, onFailure: (Exception) -> Unit)
    fun deleteMessage(message: Message, onSuccess: () -> Unit, onFailure: (Exception) -> Unit)
    fun getMessages(onSuccess: (List<Message>) -> Unit, onFailure: (Exception) -> Unit)
}