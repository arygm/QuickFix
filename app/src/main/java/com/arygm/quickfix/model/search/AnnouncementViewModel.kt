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

  /** Gets all announcements documents for a certain user. */
  fun getAnnouncementsForUser(userId: String) {
    repository.getAnnouncementsForUser(
        userId = userId,
        onSuccess = { announcementsForUser_.value = it },
        onFailure = { e ->
          Log.e(
              "AnnouncementViewModel",
              "Failed to fetch announcements for user ${userId}: ${e.message}")
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
        onSuccess = { getAnnouncementsForUser(announcement.userId) },
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
        onSuccess = { getAnnouncementsForUser(announcement.userId) },
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
        userId = userId,
        announcementId = announcementId,
        onSuccess = { getAnnouncementsForUser(userId) },
        onFailure = { e ->
          Log.e("AnnouncementViewModel", "User $userId failed to delete announcement: ${e.message}")
        })
  }
}
