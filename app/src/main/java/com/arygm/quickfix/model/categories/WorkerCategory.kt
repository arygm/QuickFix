package com.arygm.quickfix.model.categories

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.ui.graphics.vector.ImageVector
import com.arygm.quickfix.model.categories.painting.PaintingCategory
import com.arygm.quickfix.model.categories.plumbing.PlumbingCategory

// Define an enum for top-level worker categories
enum class WorkerCategory(
    override val displayName: String,
    val icon: ImageVector,
    val description: String
) : Category {
  PAINTING(
      "Painting",
      Icons.Outlined.ImagesearchRoller,
      "Find skilled painters for residential or commercial projects."),
  PLUMBING(
      "Plumbing",
      Icons.Outlined.Plumbing,
      "Connect with expert plumbers for repairs and installations."),
  GARDENING(
      "Gardening",
      Icons.Outlined.NaturePeople,
      "Hire professional gardeners for landscaping and maintenance."),
  ELECTRICAL_WORK(
      "Electrical Work",
      Icons.Outlined.ElectricalServices,
      "Locate certified electricians for safe and efficient service."),
  HANDYMAN(
      "Handyman Services",
      Icons.Outlined.Handyman,
      "Get help with various minor home repairs and tasks."),
  CLEANING(
      "Cleaning Services",
      Icons.Outlined.CleaningServices,
      "Book reliable cleaners for home or office maintenance."),
  CARPENTRY(
      "Carpentry",
      Icons.Outlined.Carpenter,
      "Hire experienced carpenters for woodwork and construction tasks."),
  MOVING(
      "Moving Services",
      Icons.Outlined.LocalShipping,
      "Find professional movers to help with local or long-distance relocation tasks.");

  fun getSubcategories(): List<Category> {
    return when (this) {
      PAINTING -> PaintingCategory.entries
      PLUMBING -> PlumbingCategory.entries
      GARDENING -> TODO()
      ELECTRICAL_WORK -> TODO()
      HANDYMAN -> TODO()
      CLEANING -> TODO()
      CARPENTRY -> TODO()
      MOVING -> TODO()
    }
  }
}
