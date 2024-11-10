package com.arygm.quickfix.model.messaging


class Chat (
    val chatId: String,
    val workeruid: String,
    val useruid: String,
    val messages: List<Message>
)