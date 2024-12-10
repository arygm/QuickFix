package com.arygm.quickfix.model.search

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.arygm.quickfix.model.offline.small.PreferencesRepository
import com.arygm.quickfix.model.profile.ProfileRepository
import com.arygm.quickfix.model.profile.UserProfile
import com.arygm.quickfix.utils.UID_KEY
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

open class AnnouncementViewModel(
    private val announcementRepository: AnnouncementRepository,
    private val preferencesRepository: PreferencesRepository,
    private val userProfileRepository: ProfileRepository
) : ViewModel() {

    private val announcementsForUser_ = MutableStateFlow<List<Announcement>>(emptyList())
    val announcementsForUser: StateFlow<List<Announcement>> = announcementsForUser_.asStateFlow()

    private val announcements_ = MutableStateFlow<List<Announcement>>(emptyList())
    val announcements: StateFlow<List<Announcement>> = announcements_.asStateFlow()

    private val uploadedImages_ = MutableStateFlow<List<Bitmap>>(emptyList())
    val uploadedImages: StateFlow<List<Bitmap>> = uploadedImages_.asStateFlow()

    private val announcementImagesMap_ = MutableStateFlow<Map<String, List<Bitmap>>>(emptyMap())
    val announcementImagesMap: StateFlow<Map<String, List<Bitmap>>> =
        announcementImagesMap_.asStateFlow()

    private val selectedAnnouncement_ = MutableStateFlow<Announcement?>(null)
    val selectedAnnouncement: StateFlow<Announcement?> = selectedAnnouncement_.asStateFlow()

    private val selectedAnnouncementImages_ = MutableStateFlow<List<Bitmap>>(emptyList())
    val selectedAnnouncementImages: StateFlow<List<Bitmap>> =
        selectedAnnouncementImages_.asStateFlow()

    init {
        announcementRepository.init { getAnnouncementsForCurrentUser() }
    }

    // create factory
    companion object {
        fun Factory(
            announcementRepository: AnnouncementRepository,
            preferencesRepository: PreferencesRepository,
            userProfileRepository: ProfileRepository
        ): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {

                    return AnnouncementViewModel(
                        announcementRepository, preferencesRepository, userProfileRepository
                    )
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
        return announcementRepository.getNewUid()
    }

    /** Gets all announcements documents. */
    fun getAnnouncements() {
        announcementRepository.getAnnouncements(
            onSuccess = { allAnnouncements ->
                announcements_.value = allAnnouncements // Update all announcements
            },
            onFailure = { e ->
                Log.d("AnnouncementsRepositoryFirestore", "init failed")
                Log.e("Failed to fetch all announcements", e.toString())
            })
    }

    /** Gets all announcements documents for a certain user. */
    fun getAnnouncementsForUser(announcementIds: List<String>) {
        announcementRepository.getAnnouncementsForUser(
            announcements = announcementIds,
            onSuccess = { announcements ->
                announcementsForUser_.value = announcements
                Log.d("AnnouncementViewModel", "Fetched announcements: ${announcements.size}")

                // Fetch images for each announcement
                announcements.forEach { announcement ->
                    fetchAnnouncementImagesAsBitmaps(announcement.announcementId)
                }
            },
            onFailure = { e ->
                Log.e(
                    "AnnouncementViewModel",
                    "Failed to fetch announcements for user: ${e.message}"
                )
            })
    }

    fun getAnnouncementsForCurrentUser() {
        viewModelScope.launch {
            try {
                // Step 1: Load the user ID from preferences
                preferencesRepository.getPreferenceByKey(UID_KEY).collect { userId ->
                    if (userId.isNullOrEmpty()) {
                        Log.e("AnnouncementViewModel", "Failed to load user ID")
                        return@collect
                    }

                    // Step 2: Fetch the user profile using the user ID
                    userProfileRepository.getProfileById(
                        uid = userId,
                        onSuccess = { profile ->
                            if (profile is UserProfile) {
                                // Step 3: Use the profile's announcements field to fetch announcements
                                val announcementIds = profile.announcements
                                if (announcementIds.isEmpty()) {
                                    Log.d(
                                        "AnnouncementViewModel",
                                        "No announcements found for user"
                                    )
                                } else {
                                    getAnnouncementsForUser(announcementIds)
                                }
                            } else {
                                Log.e(
                                    "AnnouncementViewModel",
                                    "No profile found for user ID: $userId"
                                )
                            }
                        },
                        onFailure = { e ->
                            Log.e(
                                "AnnouncementViewModel",
                                "Error fetching profile for user ID: $userId",
                                e
                            )
                        })
                }
            } catch (e: Exception) {
                Log.e("AnnouncementViewModel", "Error getting announcements for current user", e)
            }
        }
    }

    /**
     * Adds an announcement.
     *
     * @param announcement The announcement document to be added.
     */
    fun announce(announcement: Announcement) {
        announcementRepository.announce(
            announcement = announcement,
            onSuccess = {
                announcementsForUser_.value += announcement
                // Fetch and add images for the new announcement
                fetchAnnouncementImagesAsBitmaps(announcement.announcementId)
            },
            onFailure = { e ->
                Log.e(
                    "AnnouncementViewModel",
                    "User ${announcement.userId} failed to announce: ${e.message}"
                )
            })
    }

    fun uploadAnnouncementImages(
        announcementId: String,
        images: List<Bitmap>, // List of image file paths as strings
        onSuccess: (List<String>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        Log.d("UploadingImages", "$images.size")
        announcementRepository.uploadAnnouncementImages(
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
        announcementRepository.fetchAnnouncementsImagesAsBitmaps(
            announcementId = announcementId,
            onSuccess = { bitmaps ->
                Log.d("Announcement", "Fetched ${bitmaps.size} images for $announcementId")
                // Update the map with the new images
                announcementImagesMap_.value =
                    announcementImagesMap_.value.toMutableMap()
                        .apply { this[announcementId] = bitmaps }
            },
            onFailure = { e ->
                Log.e(
                    "AnnouncementViewModel",
                    "Failed to fetch images for announcement $announcementId: ${e.message}"
                )
            })
    }

    /**
     * Updates an announcement.
     *
     * @param announcement The announcement document to be updated.
     */
    fun updateAnnouncement(announcement: Announcement) {
        announcementRepository.updateAnnouncement(
            announcement = announcement,
            onSuccess = {
                announcementsForUser_.value =
                    announcementsForUser_.value
                        .filter { it.announcementId != announcement.announcementId }
                        .plus(announcement)
                // Fetch and update the images for the updated announcement
                fetchAnnouncementImagesAsBitmaps(announcement.announcementId)
            },
            onFailure = { e ->
                Log.e(
                    "AnnouncementViewModel",
                    "User ${announcement.userId} failed to update announcement: ${e.message}"
                )
            })
    }

    /**
     * Deletes an announcement by its id.
     *
     * @param announcementId The ID of the announcement document to be deleted.
     */
    fun deleteAnnouncementById(announcementId: String) {
        announcementRepository.deleteAnnouncementById(
            announcementId = announcementId,
            onSuccess = {
                // After deleting the announcement document itself, we must also remove it
                // from the user's profile announcements list.
                viewModelScope.launch {
                    try {
                        // Load the user ID from preferences
                        preferencesRepository.getPreferenceByKey(UID_KEY).collect { userId ->
                            if (userId.isNullOrEmpty()) {
                                Log.e("AnnouncementViewModel", "No user ID found in preferences.")
                                return@collect
                            }

                            // Fetch the user profile
                            userProfileRepository.getProfileById(
                                uid = userId,
                                onSuccess = { profile ->
                                    if (profile is UserProfile) {
                                        // Remove the announcementId from the user's announcements list
                                        val updatedAnnouncements = profile.announcements.filterNot {
                                            it == announcementId
                                        }

                                        if (updatedAnnouncements.size != profile.announcements.size) {
                                            val updatedProfile = UserProfile(
                                                profile.locations,
                                                updatedAnnouncements,
                                                profile.wallet,
                                                profile.uid,
                                                profile.quickFixes
                                            )

                                            // Update the profile
                                            userProfileRepository.updateProfile(
                                                profile = updatedProfile,
                                                onSuccess = {
                                                    // Remove the announcement from the local cached lists
                                                    announcementsForUser_.value =
                                                        announcementsForUser_.value.filter { it.announcementId != announcementId }
                                                    announcementImagesMap_.value =
                                                        announcementImagesMap_.value.toMutableMap()
                                                            .apply { remove(announcementId) }
                                                },
                                                onFailure = { e ->
                                                    Log.e(
                                                        "AnnouncementViewModel",
                                                        "Failed to update user profile after deleting announcement $announcementId: ${e.message}"
                                                    )
                                                }
                                            )
                                        }
                                    } else {
                                        Log.e(
                                            "AnnouncementViewModel",
                                            "No valid user profile found for userId: $userId"
                                        )
                                    }
                                },
                                onFailure = { e ->
                                    Log.e(
                                        "AnnouncementViewModel",
                                        "Failed to fetch user profile while deleting announcement $announcementId: ${e.message}"
                                    )
                                }
                            )
                        }
                    } catch (e: Exception) {
                        Log.e(
                            "AnnouncementViewModel",
                            "Exception while deleting announcement $announcementId: ${e.message}"
                        )
                    }
                }
            },
            onFailure = { e ->
                Log.e(
                    "AnnouncementViewModel",
                    "Failed to delete announcement $announcementId: ${e.message}"
                )
            }
        )
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

    /** Unselects the selected announcement. */
    fun unselectAnnouncement() {
        selectedAnnouncement_.value = null
    }
}
