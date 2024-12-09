package com.arygm.quickfix.model.search

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.storage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

open class AnnouncementViewModel(private val repository: AnnouncementRepository) : ViewModel() {

  private val announcementsForUser_ = MutableStateFlow<List<Announcement>>(emptyList())
  val announcementsForUser: StateFlow<List<Announcement>> = announcementsForUser_.asStateFlow()

  private val announcements_ = MutableStateFlow<List<Announcement>>(emptyList())
  val announcements: StateFlow<List<Announcement>> = announcements_.asStateFlow()

  private val uploadedImages_ = MutableStateFlow<List<Bitmap>>(emptyList())
  val uploadedImages: StateFlow<List<Bitmap>> = uploadedImages_.asStateFlow()

  private val selectedAnnouncement_ = MutableStateFlow<Announcement?>(null)
  val selectedAnnouncement: StateFlow<Announcement?> = selectedAnnouncement_.asStateFlow()

  private val selectedAnnouncementImages_ = MutableStateFlow<List<Bitmap>>(emptyList())
  val selectedAnnouncementImages: StateFlow<List<Bitmap>> =
      selectedAnnouncementImages_.asStateFlow()

  init {
    repository.init { getAnnouncements() }
  }
  // create factory
  companion object {
    val Factory: ViewModelProvider.Factory =
        object : ViewModelProvider.Factory {
          @Suppress("UNCHECKED_CAST")
          override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return AnnouncementViewModel(
                AnnouncementRepositoryFirestore(Firebase.firestore, Firebase.storage))
                as T
          }
        }
  }

  /**
   * Generates a new unique ID.
   *
   * @return A new unique ID.
   */
  fun getNewUid(): String {
    return repository.getNewUid()
  }

  /** Gets all announcements documents. */
  fun getAnnouncements() {
    repository.getAnnouncements(
        onSuccess = { allAnnouncements ->
          announcements_.value = allAnnouncements // Update all announcements
        },
        onFailure = { e -> Log.e("Failed to fetch all announcements", e.toString()) })
  }

  /** Gets all announcements documents for a certain user. */
  fun getAnnouncementsForUser(announcements: List<String>) {
    repository.getAnnouncementsForUser(
        announcements = announcements,
        onSuccess = { announcementsForUser_.value = it },
        onFailure = { e ->
          Log.e("AnnouncementViewModel", "Failed to fetch announcements for user: ${e.message}")
        })
  }

  /**
   * Adds an announcement.
   *
   * @param announcement The announcement document to be added.
   */
  fun announce(announcement: Announcement) {
    repository.announce(
        announcement = announcement,
        onSuccess = { announcementsForUser_.value += announcement },
        onFailure = { e ->
          Log.e(
              "AnnouncementViewModel",
              "User ${announcement.userId} failed to announce: ${e.message}")
        })
  }

  fun uploadAnnouncementImages(
      announcementId: String,
      images: List<Bitmap>, // List of image file paths as strings
      onSuccess: (List<String>) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    Log.d("UploadingImages", "$images.size")
    repository.uploadAnnouncementImages(
        announcementId = announcementId,
        images = images,
        onSuccess = { onSuccess(it) },
        onFailure = { e -> onFailure(e) })
  }
  /**
   * Fetches the images for an announcement as bitmaps and updates the state flow.
   *
   * @param announcementId The ID of the announcement whose images are to be fetched.
   */
  fun fetchAnnouncementImagesAsBitmaps(announcementId: String) {
    repository.fetchAnnouncementsImagesAsBitmaps(
        announcementId = announcementId,
        onSuccess = { bitmaps ->
          // Update the state flow with the fetched bitmaps
          selectedAnnouncementImages_.value = bitmaps
        },
        onFailure = { e ->
          Log.e(
              "AnnouncementViewModel",
              "Failed to fetch images for announcement $announcementId: ${e.message}")
        })
  }

  /**
   * Updates an announcement.
   *
   * @param announcement The announcement document to be updated.
   */
  fun updateAnnouncement(announcement: Announcement) {
    repository.updateAnnouncement(
        announcement = announcement,
        onSuccess = {
          announcementsForUser_.value =
              announcementsForUser_.value
                  .filter { it.announcementId != announcement.announcementId }
                  .plus(announcement)
        },
        onFailure = { e ->
          Log.e(
              "AnnouncementViewModel",
              "User ${announcement.userId} failed to update announcement: ${e.message}")
        })
  }

  /**
   * Deletes an announcement by its id.
   *
   * @param userId The ID of the user that deletes the announcement.
   * @param announcementId The ID of the announcement document to be deleted.
   */
  fun deleteAnnouncementById(userId: String, announcementId: String) {
    repository.deleteAnnouncementById(
        announcementId = announcementId,
        onSuccess = { // Remove the announcement with the matching ID from the list
          announcementsForUser_.value =
              announcementsForUser_.value.filter { it.announcementId != announcementId }
        },
        onFailure = { e ->
          Log.e("AnnouncementViewModel", "User $userId failed to delete announcement: ${e.message}")
        })
  }

  /**
   * Adds a new image to the list of uploaded images.
   *
   * @param image The `Bitmap` of the image to be added.
   */
  fun addUploadedImage(image: Bitmap) {
    uploadedImages_.value += image
  }

  /**
   * Deletes a list of images from the list of uploaded images.
   *
   * @param images The list of `Bitmap` images to be removed.
   */
  fun deleteUploadedImages(images: List<Bitmap>) {
    uploadedImages_.value = uploadedImages_.value.filterNot { it in images }
  }

  /** Clears the entire list of uploaded images. */
  fun clearUploadedImages() {
    uploadedImages_.value = emptyList()
  }

  /**
   * Selects an Announcement document.
   *
   * @param announcement The Announcement document to be selected.
   */
  fun selectAnnouncement(announcement: Announcement) {
    selectedAnnouncement_.value = announcement
  }
}
