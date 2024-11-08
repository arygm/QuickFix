package com.arygm.quickfix.model.categories

// Define sealed classes to represent subcategories of Painting
sealed class PaintingCategory(val category: WorkerCategory, val categoryName: String) {

    // Painting Categories
    data object ResidentialPainting : PaintingCategory(WorkerCategory.PAINTING, "Residential Painting")
    data object CommercialPainting : PaintingCategory(WorkerCategory.PAINTING, "Commercial Painting")
    data object IndustrialPainting : PaintingCategory(WorkerCategory.PAINTING, "Industrial Painting")
    data object DecorativePainting : PaintingCategory(WorkerCategory.PAINTING, "Decorative Painting")
    data object RestorationPainting : PaintingCategory(WorkerCategory.PAINTING, "Restoration Painting")
    data object NewConstructionPainting : PaintingCategory(WorkerCategory.PAINTING, "New Construction Painting")
    data object FurniturePainting : PaintingCategory(WorkerCategory.PAINTING, "Furniture Painting")
    data object OutdoorPainting : PaintingCategory(WorkerCategory.PAINTING, "Outdoor Painting")
    data object FloorPainting : PaintingCategory(WorkerCategory.PAINTING, "Floor Painting")
}

// Define sealed classes to represent subcategories within each PaintingCategory
sealed class PaintingSubCategory(val category: PaintingCategory, val subCategoryName: String) {

    // Subcategories for Residential Painting
    data object Interior : PaintingSubCategory(PaintingCategory.ResidentialPainting, "Interior")
    data object Exterior : PaintingSubCategory(PaintingCategory.ResidentialPainting, "Exterior")
    data object Cabinets : PaintingSubCategory(PaintingCategory.ResidentialPainting, "Cabinets")

    // Subcategories for Commercial Painting
    data object OfficeBuildings : PaintingSubCategory(PaintingCategory.CommercialPainting, "Office Buildings")
    data object RetailSpaces : PaintingSubCategory(PaintingCategory.CommercialPainting, "Retail Spaces")

    // Subcategories for Industrial Painting
    data object ProtectiveCoatings : PaintingSubCategory(PaintingCategory.IndustrialPainting, "Protective Coatings")
    data object SteelAndMachinery : PaintingSubCategory(PaintingCategory.IndustrialPainting, "Steel and Machinery")

    // Subcategories for Decorative Painting
    data object FauxFinishes : PaintingSubCategory(PaintingCategory.DecorativePainting, "Faux Finishes")
    data object Murals : PaintingSubCategory(PaintingCategory.DecorativePainting, "Murals")

    // Subcategories for Restoration Painting
    data object HistoricRestoration : PaintingSubCategory(PaintingCategory.RestorationPainting, "Historic Restoration")
    data object SurfaceRepair : PaintingSubCategory(PaintingCategory.RestorationPainting, "Surface Repair")

    // Subcategories for New Construction Painting
    data object ResidentialNewBuild : PaintingSubCategory(PaintingCategory.NewConstructionPainting, "Residential New Build")
    data object CommercialNewBuild : PaintingSubCategory(PaintingCategory.NewConstructionPainting, "Commercial New Build")

    // Subcategories for Furniture and Fixture Painting
    data object FurnitureRefinishing : PaintingSubCategory(PaintingCategory.FurniturePainting, "Furniture Refinishing")
    data object Fixtures : PaintingSubCategory(PaintingCategory.FurniturePainting, "Fixtures")

    // Subcategories for Outdoor Feature Painting
    data object Fences : PaintingSubCategory(PaintingCategory.OutdoorPainting, "Fences")
    data object DecksPatios : PaintingSubCategory(PaintingCategory.OutdoorPainting, "Decks and Patios")

    // Subcategories for Epoxy and Floor Painting
    data object GarageFloors : PaintingSubCategory(PaintingCategory.FloorPainting, "Garage Floors")
    data object IndustrialFloors : PaintingSubCategory(PaintingCategory.FloorPainting, "Industrial Floors")
}
