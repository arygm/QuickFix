package com.arygm.quickfix.model.chats

import com.arygm.quickfix.model.chats.messages.MessageRepository

class Chat (
    val chatId: String,
    val workeruid: String,
    val useruid: String,
    val messages: MessageRepository
)