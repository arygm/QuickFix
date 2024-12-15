package com.arygm.quickfix.model.messaging

import android.util.Log
import com.arygm.quickfix.model.offline.large.messaging.ChatDao
import com.arygm.quickfix.utils.performFirestoreOperation
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class ChatRepositoryFirestore(
    private val dao: ChatDao,
    private val db: FirebaseFirestore,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : ChatRepository {
  private val collectionPath = "chats"
  private val chats by lazy { db.collection(collectionPath) }

  override fun init(onSuccess: () -> Unit) {
    onSuccess()
  }

  override fun getRandomUid(): String {
    return db.collection(collectionPath).document().id
  }

  override suspend fun createChat(
      chat: Chat,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    // Add to Room first
    try {
      dao.insertChat(chat.toChatEntity())
      onSuccess()
    } catch (e: Exception) {
      onFailure(e)
      return
    }

    performFirestoreOperation(chats.document(chat.chatId).set(chat), onSuccess, onFailure)
  }

  override suspend fun deleteChat(
      chat: Chat,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    // Delete from Room first
    try {
      dao.deleteChat(chat.chatId)
      onSuccess()
    } catch (e: Exception) {
      onFailure(e)
      return
    }

    performFirestoreOperation(chats.document(chat.chatId).delete(), onSuccess, onFailure)
  }

  override suspend fun getChats(onSuccess: (List<Chat>) -> Unit, onFailure: (Exception) -> Unit) {
    var isCached = false
    try {
      // Fetch chats from Room (local database)
      val cachedChatsFlow =
          dao.getAllChats().map { entities ->
            entities.map { it.toChat() } // Convert each ChatEntity to Chat
          }

      // Collect the flow to get the data
      cachedChatsFlow.collect { cachedChats ->
        if (cachedChats.isNotEmpty()) {
          onSuccess(cachedChats)
          isCached = true
          return@collect
        }
      }
    } catch (localError: Exception) {
      Log.e("ChatRepositoryFirestore", "Error fetching local chats: ${localError.message}")
    }
    if (isCached) return
    chats
        .get()
        .addOnSuccessListener { result ->
          try {
            val chats = result.documents.mapNotNull { document -> documentToChat(document) }
            Log.d("FirebaseData", "Chats fetched: $chats")
            CoroutineScope(dispatcher).launch {
              chats.forEach { chat -> dao.insertChat(chat.toChatEntity()) }
            }
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

  override suspend fun chatExists(
      userId: String,
      workerId: String,
      onSuccess: (Pair<Boolean, Chat?>) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    try {
      // Check locally first
      val cachedChat = dao.getChatById(userId + workerId)?.toChat()
      if (cachedChat != null) {
        onSuccess(Pair(true, cachedChat))
        return
      }
    } catch (localError: Exception) {
      Log.e("ChatRepositoryFirestore", "Error checking chat locally: ${localError.message}")
    }

    // Fallback to Firestore if local check fails
    chats
        .whereEqualTo("chatId", userId + workerId)
        .get()
        .addOnSuccessListener { result ->
          val chat = result.toObjects(Chat::class.java).firstOrNull()
          onSuccess(Pair(chat != null, chat))
        }
        .addOnFailureListener { onFailure(it) }
  }

  override suspend fun sendMessage(
      chat: Chat,
      message: Message,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    Log.e("ChatRepositoryFirestore", "sendMessage: $message")
    Log.e("ChatRepositoryFirestore", "sendChat: $chat")
    try {
      val updatedMessages = chat.messages + message
      val updatedChat =
          Chat(
              chat.chatId,
              chat.workeruid,
              chat.useruid,
              chat.quickFixUid,
              updatedMessages,
              chat.chatStatus)
      dao.insertChat(updatedChat.toChatEntity())
    } catch (e: Exception) {
      onFailure(e)
      return
    }

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

  override suspend fun deleteMessage(
      chat: Chat,
      message: Message,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    // Delete from Room first
    try {
      val updatedMessages = chat.messages.filter { it.messageId != message.messageId }
      val updatedChat =
          Chat(
              chat.chatId,
              chat.workeruid,
              chat.useruid,
              chat.quickFixUid,
              updatedMessages,
              chat.chatStatus)
      dao.insertChat(updatedChat.toChatEntity())
      onSuccess()
    } catch (e: Exception) {
      onFailure(e)
      return
    }

    performFirestoreOperation(
        chats.document(chat.chatId).collection("messages").document(message.messageId).delete(),
        onSuccess,
        onFailure)
  }

  override suspend fun updateChat(
      chat: Chat,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    // Update Room first
    try {
      dao.insertChat(chat.toChatEntity())
      onSuccess()
    } catch (e: Exception) {
      onFailure(e)
      return
    }

    // Sync to Firestore
    performFirestoreOperation(chats.document(chat.chatId).set(chat), onSuccess, onFailure)
  }

  override suspend fun getChatByChatUid(
      chatUid: String,
      onSuccess: (Chat?) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    chats
        .document(chatUid)
        .get()
        .addOnSuccessListener { document ->
          val chat = documentToChat(document)
          onSuccess(chat)
        }
        .addOnFailureListener { exception -> onFailure(exception) }
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
