package com.arygm.quickfix.model.offline.large.categories

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.arygm.quickfix.model.category.Category
import com.arygm.quickfix.model.offline.large.Converters

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey val id: String,
    val name: String,
    val description: String,
    val subcategories: String, // Serialized List<Subcategory>
) {
  fun toCategory(): Category {
    return Category(
        id = id,
        name = name,
        description = description,
        subcategories = Converters().toSubcategoryList(subcategories))
  }
}
