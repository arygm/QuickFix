package com.arygm.quickfix.model.category

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import com.arygm.quickfix.model.offline.large.Converters
import org.junit.Assert.*
import org.junit.Test

class CategoryTest {

  @Test
  fun toCategoryEntity_convertsCorrectly() {
    val subcategories =
        listOf(
            Subcategory(
                id = "sub1",
                name = "Sub 1",
                category = "Category 1",
                tags = listOf("tag1", "tag2")),
            Subcategory(
                id = "sub2", name = "Sub 2", category = "Category 1", tags = listOf("tag3")))
    val category =
        Category(
            id = "cat1",
            name = "Test Category",
            description = "This is a test category",
            subcategories = subcategories)

    val entity = category.toCategoryEntity()

    assertEquals("cat1", entity.id)
    assertEquals("Test Category", entity.name)
    assertEquals("This is a test category", entity.description)

    // Verify serialized subcategories
    val deserializedSubcategories = Converters().toSubcategoryList(entity.subcategories)
    assertEquals(subcategories.size, deserializedSubcategories.size)
    assertEquals(subcategories[0].id, deserializedSubcategories[0].id)
    assertEquals(subcategories[1].tags, deserializedSubcategories[1].tags)
  }

  @Test
  fun toCategoryEntity_handlesEmptySubcategories() {
    val category =
        Category(
            id = "cat2",
            name = "Empty Category",
            description = "No subcategories here",
            subcategories = emptyList())

    val entity = category.toCategoryEntity()

    assertEquals("cat2", entity.id)
    assertEquals("Empty Category", entity.name)
    assertEquals("No subcategories here", entity.description)
    assertEquals("[]", entity.subcategories) // Serialized empty list
  }

  @Test
  fun getCategoryIcon_returnsCorrectIcon() {
    val plumbingCategory = Category(name = "Plumbing")
    val electricalCategory = Category(name = "Electrical Work")
    val unknownCategory = Category(name = "Unknown Category")

    assertEquals(Icons.Outlined.Plumbing, getCategoryIcon(plumbingCategory))
    assertEquals(Icons.Outlined.ElectricalServices, getCategoryIcon(electricalCategory))
    assertEquals(Icons.Outlined.Refresh, getCategoryIcon(unknownCategory))
  }

  @Test
  fun getCategoryIcon_handlesEdgeCases() {
    val emptyCategory = Category(name = "")
    val nullNameCategory = Category(name = null ?: "")

    assertEquals(Icons.Outlined.Refresh, getCategoryIcon(emptyCategory))
    assertEquals(Icons.Outlined.Refresh, getCategoryIcon(nullNameCategory))
  }

  @Test
  fun subcategory_initialization_defaultValues() {
    val subcategory = Subcategory()

    assertEquals("", subcategory.id)
    assertEquals("", subcategory.name)
    assertEquals("", subcategory.category)
    assertTrue(subcategory.tags.isEmpty())
    assertNull(subcategory.scale)
    assertTrue(subcategory.setServices.isEmpty())
  }

  @Test
  fun scale_initialization() {
    val scale = Scale(longScale = "Long", shortScale = "Short")

    assertEquals("Long", scale.longScale)
    assertEquals("Short", scale.shortScale)
  }

  @Test
  fun category_initialization_defaultValues() {
    val category = Category()

    assertEquals("", category.id)
    assertEquals("", category.name)
    assertEquals("", category.description)
    assertTrue(category.subcategories.isEmpty())
  }

  @Test
  fun category_withSubcategories() {
    val subcategory1 = Subcategory(id = "sub1", name = "Sub 1", category = "Category 1")
    val subcategory2 = Subcategory(id = "sub2", name = "Sub 2", category = "Category 2")

    val category =
        Category(
            id = "cat1",
            name = "Test Category",
            description = "Description here",
            subcategories = listOf(subcategory1, subcategory2))

    assertEquals(2, category.subcategories.size)
    assertEquals("sub1", category.subcategories[0].id)
    assertEquals("Sub 2", category.subcategories[1].name)
  }
}
