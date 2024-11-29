package com.arygm.quickfix.model.profile

import com.arygm.quickfix.model.locations.Location
import com.arygm.quickfix.model.profile.dataFields.AddOnService
import com.arygm.quickfix.model.profile.dataFields.IncludedService
import com.arygm.quickfix.model.profile.dataFields.Review

open class Profile(
    val uid: String,
    val quickFixes: List<String> = emptyList(), // common field
) {
  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is Profile) return false

    return uid == other.uid
  }

  override fun hashCode(): Int {
    return uid.hashCode()
  }
}

class UserProfile(
    val locations: List<Location>,
    val announcements: List<String>, // Each string correspond to an announcement id.
    uid: String,
    quickFixes: List<String> =
        emptyList(), // String of uid that will represents the uid of the QuickFixes
) : Profile(uid, quickFixes) {
  // quickFixes: List<String>, // String of uid that will represents the uid of the QuickFixes
  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is UserProfile) return false
    if (!super.equals(other)) return false

    return locations == other.locations
  }

  override fun hashCode(): Int {
    return listOf(super.hashCode(), locations).hashCode()
  }
}

class WorkerProfile(
    val rating: Double = 0.0,
    val fieldOfWork: String = "",
    val description: String = "",
    val location: Location? = null,
    quickFixes: List<String> = emptyList(),
    val includedServices: List<IncludedService> = emptyList<IncludedService>(),
    val addOnServices: List<AddOnService> = emptyList<AddOnService>(),
    val reviews: ArrayDeque<Review> = ArrayDeque<Review>(),
    val profilePicture: String = "",
    val price: Double = 130.0,
    val displayName: String = "",
    uid: String = "",
    val tags : List<String> = emptyList(),
) : Profile(uid, quickFixes) {
  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is WorkerProfile) return false
    if (!super.equals(other)) return false

    return fieldOfWork == other.fieldOfWork &&
        description == other.description &&
        location == other.location &&
        rating == other.rating &&
        reviews == other.reviews
  }

  override fun hashCode(): Int {
    return listOf(super.hashCode(), fieldOfWork, description, location, rating, reviews).hashCode()
  }
}
