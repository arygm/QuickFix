package com.arygm.quickfix.model.categories.painting

import com.arygm.quickfix.model.categories.Category
import com.arygm.quickfix.model.categories.WorkerCategory

// Define sealed classes to represent subcategories of Painting
// Define an enum for Painting Categories
enum class PaintingCategory(val category: WorkerCategory, override val displayName: String) :
    Category {
    RESIDENTIAL_PAINTING(WorkerCategory.PAINTING, "Residential Painting"),
    COMMERCIAL_PAINTING(WorkerCategory.PAINTING, "Commercial Painting"),
    INDUSTRIAL_PAINTING(WorkerCategory.PAINTING, "Industrial Painting"),
    DECORATIVE_PAINTING(WorkerCategory.PAINTING, "Decorative Painting"),
    RESTORATION_PAINTING(WorkerCategory.PAINTING, "Restoration Painting"),
    NEW_CONSTRUCTION_PAINTING(WorkerCategory.PAINTING, "New Construction Painting"),
    FURNITURE_PAINTING(WorkerCategory.PAINTING, "Furniture Painting"),
    OUTDOOR_PAINTING(WorkerCategory.PAINTING, "Outdoor Painting"),
    FLOOR_PAINTING(WorkerCategory.PAINTING, "Floor Painting");

    fun getSubcategories(): List<Category> {
        return PaintingSubCategory.entries.filter { it.parentCategory == this }
    }
}

// Define an enum for Painting Subcategories
enum class PaintingSubCategory(val parentCategory: PaintingCategory, override val displayName: String) :
    Category {
    // Subcategories for Residential Painting
    INTERIOR(PaintingCategory.RESIDENTIAL_PAINTING, "Interior"),
    EXTERIOR(PaintingCategory.RESIDENTIAL_PAINTING, "Exterior"),
    CABINETS(PaintingCategory.RESIDENTIAL_PAINTING, "Cabinets"),

    // Subcategories for Commercial Painting
    OFFICE_BUILDINGS(PaintingCategory.COMMERCIAL_PAINTING, "Office Buildings"),
    RETAIL_SPACES(PaintingCategory.COMMERCIAL_PAINTING, "Retail Spaces"),

    // Subcategories for Industrial Painting
    PROTECTIVE_COATINGS(PaintingCategory.INDUSTRIAL_PAINTING, "Protective Coatings"),
    STEEL_AND_MACHINERY(PaintingCategory.INDUSTRIAL_PAINTING, "Steel and Machinery"),

    // Subcategories for Decorative Painting
    FAUX_FINISHES(PaintingCategory.DECORATIVE_PAINTING, "Faux Finishes"),
    MURALS(PaintingCategory.DECORATIVE_PAINTING, "Murals"),

    // Subcategories for Restoration Painting
    HISTORIC_RESTORATION(PaintingCategory.RESTORATION_PAINTING, "Historic Restoration"),
    SURFACE_REPAIR(PaintingCategory.RESTORATION_PAINTING, "Surface Repair"),

    // Subcategories for New Construction Painting
    RESIDENTIAL_NEW_BUILD(PaintingCategory.NEW_CONSTRUCTION_PAINTING, "Residential New Build"),
    COMMERCIAL_NEW_BUILD(PaintingCategory.NEW_CONSTRUCTION_PAINTING, "Commercial New Build"),

    // Subcategories for Furniture and Fixture Painting
    FURNITURE_REFINISHING(PaintingCategory.FURNITURE_PAINTING, "Furniture Refinishing"),
    FIXTURES(PaintingCategory.FURNITURE_PAINTING, "Fixtures"),

    // Subcategories for Outdoor Feature Painting
    FENCES(PaintingCategory.OUTDOOR_PAINTING, "Fences"),
    DECKS_PATIOS(PaintingCategory.OUTDOOR_PAINTING, "Decks and Patios"),

    // Subcategories for Epoxy and Floor Painting
    GARAGE_FLOORS(PaintingCategory.FLOOR_PAINTING, "Garage Floors"),
    INDUSTRIAL_FLOORS(PaintingCategory.FLOOR_PAINTING, "Industrial Floors");
}
