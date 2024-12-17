package com.arygm.quickfix.model.category

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Carpenter
import androidx.compose.material.icons.outlined.CleaningServices
import androidx.compose.material.icons.outlined.ElectricalServices
import androidx.compose.material.icons.outlined.Handyman
import androidx.compose.material.icons.outlined.ImagesearchRoller
import androidx.compose.material.icons.outlined.LocalShipping
import androidx.compose.material.icons.outlined.NaturePeople
import androidx.compose.material.icons.outlined.Plumbing
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.ui.graphics.vector.ImageVector
import com.arygm.quickfix.model.offline.large.Converters
import com.arygm.quickfix.model.offline.large.categories.CategoryEntity

data class Subcategory(
    val id: String = "",
    val name: String = "",
    val category: String = "",
    val tags: List<String> = emptyList(),
    val scale: Scale? = null,
    val setServices: List<String> = emptyList()
)

data class Category(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val subcategories: List<Subcategory> = emptyList()
) {
    fun toCategoryEntity(): CategoryEntity {
        return CategoryEntity(
            id = id,
            name = name,
            description = description,
            subcategories = Converters().fromSubcategoryList(subcategories)
        )
    }
}

data class Scale(val longScale: String, val shortScale: String)

fun getCategoryIcon(category: Category): ImageVector {
  return when (category.name) {
    "Plumbing" -> Icons.Outlined.Plumbing
    "Electrical Work" -> Icons.Outlined.ElectricalServices
    "Carpentry" -> Icons.Outlined.Carpenter
    "Painting" -> Icons.Outlined.ImagesearchRoller
    "Cleaning Services" -> Icons.Outlined.CleaningServices
    "Gardening" -> Icons.Outlined.NaturePeople
    "Handyman Services" -> Icons.Outlined.Handyman
    "Moving Services" -> Icons.Outlined.LocalShipping
    else -> Icons.Outlined.Refresh
  }
}
