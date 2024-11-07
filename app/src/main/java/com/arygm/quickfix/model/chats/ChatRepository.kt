package com.arygm.quickfix.model.chats

interface ChatRepository {
    fun init(onSuccess: () -> Unit)
    fun createChat(chat: Chat, onSuccess: () -> Unit, onFailure: (Exception) -> Unit)
    fun deleteChat(chat: Chat, onSuccess: () -> Unit, onFailure: (Exception) -> Unit)
    fun getChats(onSuccess: (List<Chat>) -> Unit, onFailure: (Exception) -> Unit)

}