package com.arygm.quickfix.model.messaging

import android.util.Log
import com.arygm.quickfix.utils.performFirestoreOperation
import com.google.firebase.firestore.DocumentSnapshot
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
          try {
            val chats = result.documents.mapNotNull { document -> documentToChat(document) }
            Log.d("FirebaseData", "Chats fetched: $chats")
            onSuccess(chats)
          } catch (e: Exception) {
            Log.e("FirebaseError", "Error deserializing chats: ${e.message}")
            onFailure(e)
          }
        }
        .addOnFailureListener { error ->
          Log.e("FirebaseError", "Error fetching chats: ${error.message}")
          onFailure(error)
        }
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
    Log.e("ChatRepositoryFirestore", "sendMessage: $message")
    Log.e("ChatRepositoryFirestore", "sendChat: $chat")

    val messageData =
        mapOf(
            "messageId" to message.messageId,
            "senderId" to message.senderId,
            "content" to message.content,
            "timestamp" to message.timestamp)

    chats
        .document(chat.chatId)
        .update("messages", com.google.firebase.firestore.FieldValue.arrayUnion(messageData))
        .addOnSuccessListener {
          Log.d("ChatRepositoryFirestore", "Message added successfully!")
          onSuccess()
        }
        .addOnFailureListener { exception ->
          Log.e("ChatRepositoryFirestore", "Failed to add message: ${exception.message}")
          onFailure(exception)
        }
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

  override fun updateChat(chat: Chat, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
    performFirestoreOperation(
        db.collection(collectionPath).document(chat.chatId).set(chat), onSuccess, onFailure)
  }

  private fun documentToChat(document: DocumentSnapshot): Chat? {
    return try {
      val chatId = document.getString("chatId") ?: return null
      val workerUid = document.getString("workeruid") ?: return null
      val userUid = document.getString("useruid") ?: return null
      val quickFixUid =
          document.getString("quickFixUid") ?: return null // Handle optional QuickFixUid
      val chatStatusString =
          document.getString("chatStatus") ?: ChatStatus.WAITING_FOR_RESPONSE.name
      val chatStatus = ChatStatus.valueOf(chatStatusString)
      val messagesList = document.get("messages") as? List<Map<String, Any>> ?: emptyList()

      val messages =
          messagesList.mapNotNull { messageData ->
            try {
              Message(
                  messageId = messageData["messageId"] as? String ?: "",
                  senderId = messageData["senderId"] as? String ?: "",
                  content = messageData["content"] as? String ?: "",
                  timestamp =
                      messageData["timestamp"] as? com.google.firebase.Timestamp
                          ?: com.google.firebase.Timestamp.now())
            } catch (e: Exception) {
              Log.e("FirebaseError", "Error deserializing message: ${e.message}")
              null
            }
          }

      Chat(
          chatId = chatId,
          workeruid = workerUid,
          useruid = userUid,
          quickFixUid = quickFixUid,
          messages = messages,
          chatStatus = chatStatus,
      )
    } catch (e: Exception) {
      Log.e("FirebaseError", "Error converting document to Chat: ${e.message}")
      null
    }
  }
}
