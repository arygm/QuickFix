package com.arygm.quickfix.model.search

import android.graphics.Bitmap

interface AnnouncementRepository {
  fun getNewUid(): String

  fun init(onSuccess: () -> Unit)

  fun getAnnouncementsForUser(
      userId: String,
      onSuccess: (List<Announcement>) -> Unit,
      onFailure: (Exception) -> Unit
  )

  fun announce(announcement: Announcement, onSuccess: () -> Unit, onFailure: (Exception) -> Unit)

  fun uploadAnnouncementImages(
      announcementId: String,
      bitmaps: List<Bitmap>,
      onSuccess: (List<String>) -> Unit,
      onFailure: (Exception) -> Unit
  )

  fun updateAnnouncement(
      announcement: Announcement,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  )

  fun deleteAnnouncementById(
      userId: String,
      announcementId: String,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  )
}
