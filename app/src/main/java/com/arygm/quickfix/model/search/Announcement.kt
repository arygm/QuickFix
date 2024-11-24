package com.arygm.quickfix.model.search

import com.arygm.quickfix.model.locations.Location
import java.time.LocalDateTime

data class AvailabilitySlot(val start: LocalDateTime, val end: LocalDateTime)

data class Announcement(
    val announcementId: String,
    val userId: String,
    val title: String,
    val category: String, // replace by the category type
    val description: String,
    val location: Location?,
    val availability: List<AvailabilitySlot>,
    val quickFixImages: List<String>
)
