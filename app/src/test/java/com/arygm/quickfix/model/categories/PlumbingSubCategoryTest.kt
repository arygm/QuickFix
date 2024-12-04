package com.arygm.quickfix.model.categories

import com.arygm.quickfix.model.categories.plumbing.PlumbingCategory
import com.arygm.quickfix.model.categories.plumbing.PlumbingSubCategory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class PlumbingSubCategoryTest {

  @Test
  fun testAllEnumValues() {
    val values = PlumbingSubCategory.entries.toTypedArray()
    assertEquals(14, values.size)

    for (subCategory in values) {
      assertNotNull(subCategory)
      assertNotNull(subCategory.parentCategory)
      assertNotNull(subCategory.displayName)
    }
  }

  @Test
  fun testValueOf() {
    val rootRemoval = PlumbingSubCategory.valueOf("ROOT_REMOVAL")
    assertEquals(PlumbingSubCategory.ROOT_REMOVAL, rootRemoval)

    val installation = PlumbingSubCategory.valueOf("INSTALLATION")
    assertEquals(PlumbingSubCategory.INSTALLATION, installation)
  }

  @Test
  fun testEnumProperties() {
    val officeBuildingsPlumbing = PlumbingSubCategory.OFFICE_BUILDINGS_PLUMBING
    assertEquals(PlumbingCategory.COMMERCIAL_PLUMBING, officeBuildingsPlumbing.parentCategory)
    assertEquals("Office Buildings Plumbing", officeBuildingsPlumbing.displayName)
  }

  @Test
  fun testGroupingByParentCategory() {
    val pipeInspectionSubCategories =
        PlumbingSubCategory.values().filter {
          it.parentCategory == PlumbingCategory.PIPE_INSPECTION
        }
    val expectedSubCategories =
        listOf(PlumbingSubCategory.VIDEO_INSPECTION, PlumbingSubCategory.ROOT_REMOVAL)
    assertEquals(expectedSubCategories.size, pipeInspectionSubCategories.size)
    assertTrue(pipeInspectionSubCategories.containsAll(expectedSubCategories))
  }
}
