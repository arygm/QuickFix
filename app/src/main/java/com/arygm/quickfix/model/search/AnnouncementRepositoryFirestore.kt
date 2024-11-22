package com.arygm.quickfix.model.search

import android.graphics.Bitmap
import com.arygm.quickfix.utils.performFirestoreOperation
import com.google.firebase.firestore.FirebaseFirestore

class AnnouncementRepositoryFirestore(private val db: FirebaseFirestore) : AnnouncementRepository {

  private val collectionPath = "announcements"

  override fun getNewUid(): String {
    return db.collection(collectionPath).document().id
  }

  override fun init(onSuccess: () -> Unit) {
    onSuccess()
  }

  override fun getAnnouncements(
      userId: String,
      onSuccess: (List<Announcement>) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    TODO("Not yet implemented")
  }

  override fun announce(
      announcement: Announcement,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    performFirestoreOperation(
        db.collection(collectionPath).document(announcement.announcementId).set(announcement),
        onSuccess,
        onFailure)
  }

  override fun uploadAnnouncementImages(
      announcementId: String,
      bitmaps: List<Bitmap>,
      onSuccess: (List<String>) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    TODO("Not yet implemented")
  }

  override fun updateAnnouncement(
      announcement: Announcement,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    performFirestoreOperation(
        db.collection(collectionPath).document(announcement.announcementId).set(announcement),
        onSuccess,
        onFailure)
  }

  override fun deleteAnnouncementById(
      id: String,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    performFirestoreOperation(
        db.collection(collectionPath).document(id).delete(), onSuccess, onFailure)
  }
}
