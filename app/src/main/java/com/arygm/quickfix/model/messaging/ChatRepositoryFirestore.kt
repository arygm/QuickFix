package com.arygm.quickfix.model.messaging

import com.arygm.quickfix.utils.performFirestoreOperation
import com.google.firebase.firestore.FirebaseFirestore

class ChatRepositoryFirestore(private val db: FirebaseFirestore) : ChatRepository {
  private val collectionPath = "chats"
  private val chats by lazy { db.collection(collectionPath) }

  override fun init(onSuccess: () -> Unit) {
    onSuccess()
  }

  override fun getRandomUid(): String {
    return db.collection(collectionPath).document().id
  }

  override fun createChat(chat: Chat, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
    performFirestoreOperation(chats.document(chat.chatId).set(chat), onSuccess, onFailure)
  }

  override fun deleteChat(chat: Chat, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
    performFirestoreOperation(chats.document(chat.chatId).delete(), onSuccess, onFailure)
  }

  override fun getChats(onSuccess: (List<Chat>) -> Unit, onFailure: (Exception) -> Unit) {
    chats
        .get()
        .addOnSuccessListener { result ->
          val chats = result.toObjects(Chat::class.java)
          onSuccess(chats)
        }
        .addOnFailureListener { onFailure(it) }
  }

  override fun chatExists(
      userId: String,
      workerId: String,
      onSuccess: (Pair<Boolean, Chat?>) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    chats
        .whereEqualTo("chatId", userId + workerId)
        .get()
        .addOnSuccessListener { result ->
          val chat = result.toObjects(Chat::class.java).firstOrNull()
          onSuccess(Pair(result.size() > 0, chat))
        }
        .addOnFailureListener { onFailure(it) }
  }

  override fun sendMessage(
      chat: Chat,
      message: Message,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    performFirestoreOperation(
        chats.document(chat.chatId).collection("messages").document(message.messageId).set(message),
        onSuccess,
        onFailure)
  }

  override fun deleteMessage(
      chat: Chat,
      message: Message,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    performFirestoreOperation(
        chats.document(chat.chatId).collection("messages").document(message.messageId).delete(),
        onSuccess,
        onFailure)
  }
}
