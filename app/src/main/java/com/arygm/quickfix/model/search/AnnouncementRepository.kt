package com.arygm.quickfix.model.search

interface AnnouncementRepository {
  fun getNewUid(): String

  fun init(onSuccess: () -> Unit)

  fun getAnnouncements(onSuccess: (List<Announcement>) -> Unit, onFailure: (Exception) -> Unit)

  fun announce(announcement: Announcement, onSuccess: () -> Unit, onFailure: (Exception) -> Unit)

  fun updateAnnouncement(
      announcement: Announcement,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  )

  fun deleteAnnouncementById(id: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit)
}
