package com.arygm.quickfix.model.chats.messages

class Message (
    val messageId: String,
    val senderId: String,
    val message: String,
    val timestamp: Long
)