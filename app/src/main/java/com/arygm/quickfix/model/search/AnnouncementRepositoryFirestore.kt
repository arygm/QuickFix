package com.arygm.quickfix.model.search

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.arygm.quickfix.model.locations.Location
import com.arygm.quickfix.utils.performFirestoreOperation
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream

class AnnouncementRepositoryFirestore(
    private val db: FirebaseFirestore,
    private val storage: FirebaseStorage
) : AnnouncementRepository {

  private val collectionPath = "announcements"
  private val storageRef = storage.reference
  private val compressionQuality = 50

  override fun getNewUid(): String {
    return db.collection(collectionPath).document().id
  }

  override fun init(onSuccess: () -> Unit) {
    onSuccess()
  }

  override fun getAnnouncements(
      onSuccess: (List<Announcement>) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    db.collection(collectionPath).get().addOnCompleteListener { task ->
      if (task.isSuccessful) {
        val announcements =
            task.result?.mapNotNull { document -> documentToAnnouncement(document) } ?: emptyList()
        onSuccess(announcements)
      } else {
        task.exception?.let { e ->
          Log.e("TodosRepositoryFirestore", "Error getting documents", e)
          onFailure(e)
        }
      }
    }
  }

  override fun getAnnouncementsForUser(
      announcements: List<String>,
      onSuccess: (List<Announcement>) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    Log.d("TodosRepositoryFirestore", "getAnnouncements for IDs: $announcements")

    if (announcements.isEmpty()) {
      Log.d("TodosRepositoryFirestore", "No announcement IDs provided")
      onSuccess(emptyList())
      return
    }

    // Query the "announcements" collection with the provided IDs
    db.collection(collectionPath) // Access the main announcements collection
        .whereIn(FieldPath.documentId(), announcements) // Filter by IDs
        .get()
        .addOnCompleteListener { task ->
          if (task.isSuccessful) {
            val fetchedAnnouncements =
                task.result?.mapNotNull { document ->
                  Log.d("Mapping", "Trying to map announcement with ID: ${document.id}")
                  documentToAnnouncement(document) // Convert document to Announcement object
                } ?: emptyList()
            onSuccess(fetchedAnnouncements)
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
    val announcementId = announcement.announcementId
    announcement.quickFixImages.forEach { uri -> Log.d("UploadingImages", uri) }
    val announcementDocRef = db.collection(collectionPath).document(announcementId)
    performFirestoreOperation(announcementDocRef.set(announcement), onSuccess, onFailure)
  }

  override fun uploadAnnouncementImages(
      announcementId: String,
      images: List<Bitmap>,
      onSuccess: (List<String>) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    val announcementFolderRef = storageRef.child("announcements/$announcementId")
    val uploadedImageUrls = mutableListOf<String>()
    var uploadCount = 0

    images.forEach { bitmap ->
      val fileRef = announcementFolderRef.child("image_${System.currentTimeMillis()}.jpg")

      val baos = ByteArrayOutputStream()
      bitmap.compress(Bitmap.CompressFormat.JPEG, compressionQuality, baos)
      val byteArray = baos.toByteArray()

      fileRef
          .putBytes(byteArray)
          .addOnSuccessListener {
            fileRef.downloadUrl
                .addOnSuccessListener { uri ->
                  uploadedImageUrls.add(uri.toString())
                  uploadCount++
                  if (uploadCount == images.size) {
                    onSuccess(uploadedImageUrls)
                  }
                }
                .addOnFailureListener { exception -> onFailure(exception) }
          }
          .addOnFailureListener { exception -> onFailure(exception) }
    }
  }

  override fun fetchAnnouncementsImageUrls(
      announcementId: String,
      onSuccess: (List<String>) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    val firestore = db
    val collection = firestore.collection(collectionPath)

    collection
        .document(announcementId)
        .get()
        .addOnSuccessListener { document ->
          val imageUrls = document["quickFixImages"] as? List<String> ?: emptyList()
          onSuccess(imageUrls)
        }
        .addOnFailureListener { onFailure(it) }
  }

  override fun fetchAnnouncementsImagesAsBitmaps(
      announcementId: String,
      onSuccess: (List<Bitmap>) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    fetchAnnouncementsImageUrls(
        announcementId,
        { urls ->
          val bitmaps = mutableListOf<Bitmap>()
          var successCount = 0

          urls.forEach { url ->
            val imageRef = FirebaseStorage.getInstance().getReferenceFromUrl(url)
            imageRef
                .getBytes(Long.MAX_VALUE)
                .addOnSuccessListener { bytes ->
                  val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                  bitmaps.add(bitmap)
                  successCount++
                  if (successCount == urls.size) onSuccess(bitmaps)
                }
                .addOnFailureListener { onFailure(it) }
          }
        },
        onFailure)
  }

  override fun updateAnnouncement(
      announcement: Announcement,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    val announcementId = announcement.announcementId

    val announcementDocRef = db.collection(collectionPath).document(announcementId)
    performFirestoreOperation(announcementDocRef.set(announcement), onSuccess, onFailure)
  }

  override fun deleteAnnouncementById(
      announcementId: String,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    val announcementDocRef = db.collection(collectionPath).document(announcementId)
    performFirestoreOperation(announcementDocRef.delete(), onSuccess, onFailure)
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

      // Parse location
      val locationData = document.get("location") as? Map<*, *>
      val location =
          locationData?.let {
            Location(
                latitude = it["latitude"] as? Double ?: 0.0,
                longitude = it["longitude"] as? Double ?: 0.0,
                name = it["name"] as? String ?: "")
          }

      // Parse availability
      val availabilityData = document.get("availability") as? List<Map<*, *>>
      val availability =
          availabilityData?.mapNotNull { slot ->
            val start = slot["start"] as? Timestamp
            val end = slot["end"] as? Timestamp
            if (start != null && end != null) {
              AvailabilitySlot(start = start, end = end)
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
