package com.arygm.quickfix.model.search

class AnnouncementRepositoryFirestore : AnnouncementRepository {

  override fun getNewUid(): String {
    TODO("Not yet implemented")
  }

  override fun init(onSuccess: () -> Unit) {
    TODO("Not yet implemented")
  }

  override fun getAnnouncements(
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
    TODO("Not yet implemented")
  }

  override fun updateAnnouncement(
      announcement: Announcement,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    TODO("Not yet implemented")
  }

  override fun deleteAnnouncementById(
      id: String,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    TODO("Not yet implemented")
  }
}
