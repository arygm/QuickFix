package com.arygm.quickfix.model.messaging

data class Chat(
    val chatId: String,
    val workeruid: String,
    val useruid: String,
    val messages: List<Message> = emptyList()
)
