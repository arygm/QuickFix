package com.arygm.quickfix.model.search

import android.graphics.Bitmap
import android.util.Log
import com.arygm.quickfix.model.locations.Location
import com.arygm.quickfix.utils.performFirestoreOperation
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import java.time.LocalDateTime

class AnnouncementRepositoryFirestore(private val db: FirebaseFirestore) : AnnouncementRepository {

  private val collectionPath = "announcements"

  override fun getNewUid(): String {
    return db.collection(collectionPath).document().id
  }

  override fun init(onSuccess: () -> Unit) {
    onSuccess()
  }

  override fun getAnnouncementsForUser(
      userId: String,
      onSuccess: (List<Announcement>) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    Log.d("TodosRepositoryFirestore", "getAnnouncements for userId: $userId")
    db.collection(collectionPath)
        .document(userId) // Access the document for the specified userId
        .collection(collectionPath) // Access the announcements subCollection for the userId
        .get()
        .addOnCompleteListener { task ->
          if (task.isSuccessful) {
            val announcements =
                task.result?.mapNotNull { document ->
                  documentToAnnouncement(document) // Convert document to Announcement object
                } ?: emptyList()
            onSuccess(announcements)
          } else {
            task.exception?.let { e ->
              Log.e("TodosRepositoryFirestore", "Error getting announcements", e)
              onFailure(e)
            }
          }
        }
  }

  override fun announce(
      announcement: Announcement,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    val userId = announcement.userId
    val announcementId = announcement.announcementId

    val userDocRef = db.collection(collectionPath).document(userId)
    val announcementSubCollectionRef =
        userDocRef.collection(collectionPath).document(announcementId)
    performFirestoreOperation(announcementSubCollectionRef.set(announcement), onSuccess, onFailure)
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
    val userId = announcement.userId
    val announcementId = announcement.announcementId

    val userDocRef = db.collection(collectionPath).document(userId)
    val announcementSubCollectionRef =
        userDocRef.collection(collectionPath).document(announcementId)
    performFirestoreOperation(announcementSubCollectionRef.set(announcement), onSuccess, onFailure)
  }

  override fun deleteAnnouncementById(
      userId: String,
      announcementId: String,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    val userDocRef = db.collection(collectionPath).document(userId)
    val announcementSubCollectionRef =
        userDocRef.collection(collectionPath).document(announcementId)
    performFirestoreOperation(announcementSubCollectionRef.delete(), onSuccess, onFailure)
  }

  /**
   * Converts a Firestore document to an Announcement object.
   *
   * @param document The Firestore document to convert.
   * @return The Announcement object.
   */
  private fun documentToAnnouncement(document: DocumentSnapshot): Announcement? {
    return try {
      val announcementId = document.id
      val userId = document.getString("userId") ?: return null
      val title = document.getString("title") ?: return null
      val category =
          document.getString("category") ?: return null // Replace with Category type if needed
      val description = document.getString("description") ?: return null

      val locationData = document.get("location") as? Map<*, *>
      val location =
          locationData?.let {
            Location(
                latitude = it["latitude"] as? Double ?: 0.0,
                longitude = it["longitude"] as? Double ?: 0.0,
                name = it["name"] as? String ?: "")
          }

      val availabilityData = document.get("availability") as? List<Map<*, *>>
      val availability =
          availabilityData?.mapNotNull { slot ->
            val start = slot["start"] as? String
            val end = slot["end"] as? String
            if (start != null && end != null) {
              AvailabilitySlot(start = LocalDateTime.parse(start), end = LocalDateTime.parse(end))
            } else null
          } ?: emptyList()

      val quickFixImages = document.get("quickFixImages") as? List<String> ?: emptyList()

      Announcement(
          announcementId = announcementId,
          userId = userId,
          title = title,
          category = category, // Adjust if mapping to Category type
          description = description,
          location = location,
          availability = availability,
          quickFixImages = quickFixImages)
    } catch (e: Exception) {
      Log.e("TodosRepositoryFirestore", "Error converting document to Announcement", e)
      null
    }
  }
}
