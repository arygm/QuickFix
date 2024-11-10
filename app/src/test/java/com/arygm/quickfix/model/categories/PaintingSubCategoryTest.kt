package com.arygm.quickfix.model.categories

import com.arygm.quickfix.model.categories.painting.PaintingCategory
import com.arygm.quickfix.model.categories.painting.PaintingSubCategory
import org.junit.Assert.*
import org.junit.Test

class PaintingSubCategoryTest {

  @Test
  fun testAllEnumValues() {
    val values = PaintingSubCategory.entries.toTypedArray()
    assertEquals(19, values.size)

    for (subCategory in values) {
      assertNotNull(subCategory)
      assertNotNull(subCategory.parentCategory)
      assertNotNull(subCategory.displayName)
    }
  }

  @Test
  fun testValueOf() {
    val interior = PaintingSubCategory.valueOf("INTERIOR")
    assertEquals(PaintingSubCategory.INTERIOR, interior)

    val murals = PaintingSubCategory.valueOf("MURALS")
    assertEquals(PaintingSubCategory.MURALS, murals)
  }

  @Test
  fun testEnumProperties() {
    val protectiveCoatings = PaintingSubCategory.PROTECTIVE_COATINGS
    assertEquals(PaintingCategory.INDUSTRIAL_PAINTING, protectiveCoatings.parentCategory)
    assertEquals("Protective Coatings", protectiveCoatings.displayName)
  }

  @Test
  fun testGroupingByParentCategory() {
    val commercialSubCategories =
        PaintingSubCategory.values().filter {
          it.parentCategory == PaintingCategory.COMMERCIAL_PAINTING
        }
    assertTrue(commercialSubCategories.contains(PaintingSubCategory.OFFICE_BUILDINGS))
    assertTrue(commercialSubCategories.contains(PaintingSubCategory.RETAIL_SPACES))
    assertEquals(2, commercialSubCategories.size)
  }

  @Test
  fun testCategoryInterface() {
    val subCategory: Category = PaintingSubCategory.FURNITURE_REFINISHING
    assertEquals("Furniture Refinishing", subCategory.displayName)
  }
}
