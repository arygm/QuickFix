package com.arygm.quickfix.model.categories.plumbing

import com.arygm.quickfix.model.categories.Category
import com.arygm.quickfix.model.categories.WorkerCategory

// Define an enum for Plumbing Categories
enum class PlumbingCategory(val category: WorkerCategory, override val displayName: String) :
    Category {
  RESIDENTIAL_PLUMBING(WorkerCategory.PLUMBING, "Residential Plumbing"),
  COMMERCIAL_PLUMBING(WorkerCategory.PLUMBING, "Commercial Plumbing"),
  EMERGENCY_PLUMBING(WorkerCategory.PLUMBING, "Emergency Plumbing"),
  PIPE_INSPECTION(WorkerCategory.PLUMBING, "Pipe Inspection"),
  DRAINAGE_SYSTEMS(WorkerCategory.PLUMBING, "Drainage Systems"),
  WATER_HEATER_SERVICES(WorkerCategory.PLUMBING, "Water Heater Services");

  // Function to get all subcategories related to this PlumbingCategory
  fun getSubcategories(): List<Category> {
    return PlumbingSubCategory.entries.filter { it.parentCategory == this }
  }
}

// Define an enum for Plumbing Subcategories
enum class PlumbingSubCategory(
    val parentCategory: PlumbingCategory,
    override val displayName: String
) : Category {
  // Subcategories for Residential Plumbing
  KITCHEN_PLUMBING(PlumbingCategory.RESIDENTIAL_PLUMBING, "Kitchen Plumbing"),
  BATHROOM_PLUMBING(PlumbingCategory.RESIDENTIAL_PLUMBING, "Bathroom Plumbing"),
  LEAK_REPAIR(PlumbingCategory.RESIDENTIAL_PLUMBING, "Leak Repair"),

  // Subcategories for Commercial Plumbing
  OFFICE_BUILDINGS_PLUMBING(PlumbingCategory.COMMERCIAL_PLUMBING, "Office Buildings Plumbing"),
  RESTAURANT_PLUMBING(PlumbingCategory.COMMERCIAL_PLUMBING, "Restaurant Plumbing"),

  // Subcategories for Emergency Plumbing
  BURST_PIPE_REPAIR(PlumbingCategory.EMERGENCY_PLUMBING, "Burst Pipe Repair"),
  FLOOD_ASSISTANCE(PlumbingCategory.EMERGENCY_PLUMBING, "Flood Assistance"),

  // Subcategories for Pipe Inspection
  VIDEO_INSPECTION(PlumbingCategory.PIPE_INSPECTION, "Video Inspection"),
  ROOT_REMOVAL(PlumbingCategory.PIPE_INSPECTION, "Root Removal"),

  // Subcategories for Drainage Systems
  SEWER_CLEANING(PlumbingCategory.DRAINAGE_SYSTEMS, "Sewer Cleaning"),
  STORM_DRAIN_CLEANING(PlumbingCategory.DRAINAGE_SYSTEMS, "Storm Drain Cleaning"),

  // Subcategories for Water Heater Services
  INSTALLATION(PlumbingCategory.WATER_HEATER_SERVICES, "Installation"),
  REPAIR(PlumbingCategory.WATER_HEATER_SERVICES, "Repair"),
  MAINTENANCE(PlumbingCategory.WATER_HEATER_SERVICES, "Maintenance")
}
