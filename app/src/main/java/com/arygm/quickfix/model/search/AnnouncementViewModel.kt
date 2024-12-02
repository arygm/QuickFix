package com.arygm.quickfix.model.search

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

open class AnnouncementViewModel(private val repository: AnnouncementRepository) : ViewModel() {

  private val announcementsForUser_ = MutableStateFlow<List<Announcement>>(emptyList())
  val announcementsForUser: StateFlow<List<Announcement>> = announcementsForUser_.asStateFlow()

  private val announcements_ = MutableStateFlow<List<Announcement>>(emptyList())
  val announcements: StateFlow<List<Announcement>> = announcements_.asStateFlow()

  private val uploadedImages_ = MutableStateFlow<List<String>>(emptyList())
  val uploadedImages: StateFlow<List<String>> = uploadedImages_.asStateFlow()

  // create factory
  companion object {
    val Factory: ViewModelProvider.Factory =
        object : ViewModelProvider.Factory {
          @Suppress("UNCHECKED_CAST")
          override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return AnnouncementViewModel(AnnouncementRepositoryFirestore(Firebase.firestore)) as T
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
   * @param imageUrl The URL of the image to be added.
   */
  fun addUploadedImage(imageUrl: String) {
    uploadedImages_.value += imageUrl
  }

  /**
   * Deletes a list of images from the list of uploaded images.
   *
   * @param imageUrls The list of image URLs to be removed.
   */
  fun deleteUploadedImages(imageUrls: List<String>) {
    uploadedImages_.value = uploadedImages_.value.filterNot { it in imageUrls }
  }

  /** Clears the entire list of uploaded images. */
  fun clearUploadedImages() {
    uploadedImages_.value = emptyList()
  }
}