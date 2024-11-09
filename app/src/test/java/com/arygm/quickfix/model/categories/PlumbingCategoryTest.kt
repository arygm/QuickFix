package com.arygm.quickfix.model.categories

import com.arygm.quickfix.model.categories.plumbing.PlumbingCategory
import com.arygm.quickfix.model.categories.plumbing.PlumbingSubCategory
import org.junit.Assert.*
import org.junit.Test

class PlumbingCategoryTest {

  @Test
  fun testAllEnumValues() {
    val values = PlumbingCategory.entries.toTypedArray()
    assertEquals(6, values.size)

    for (category in values) {
      assertNotNull(category)
      assertEquals(WorkerCategory.PLUMBING, category.category)
      assertNotNull(category.displayName)
    }
  }

  @Test
  fun testValueOf() {
    val residential = PlumbingCategory.valueOf("RESIDENTIAL_PLUMBING")
    assertEquals(PlumbingCategory.RESIDENTIAL_PLUMBING, residential)

    val pipeInspection = PlumbingCategory.valueOf("PIPE_INSPECTION")
    assertEquals(PlumbingCategory.PIPE_INSPECTION, pipeInspection)
  }

  @Test
  fun testEnumProperties() {
    val waterHeaterServices = PlumbingCategory.WATER_HEATER_SERVICES
    assertEquals("Water Heater Services", waterHeaterServices.displayName)
    assertEquals(WorkerCategory.PLUMBING, waterHeaterServices.category)
  }

  @Test
  fun testGetSubcategories() {
    val drainageSubCategories = PlumbingCategory.DRAINAGE_SYSTEMS.getSubcategories()
    val expectedSubCategories =
        listOf(PlumbingSubCategory.SEWER_CLEANING, PlumbingSubCategory.STORM_DRAIN_CLEANING)
    assertEquals(expectedSubCategories.size, drainageSubCategories.size)
    assertTrue(drainageSubCategories.containsAll(expectedSubCategories))
  }
}
