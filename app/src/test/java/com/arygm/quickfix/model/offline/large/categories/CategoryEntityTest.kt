package com.arygm.quickfix.model.offline.large.categories

import com.arygm.quickfix.model.category.Subcategory
import com.arygm.quickfix.model.offline.large.Converters
import com.google.gson.Gson
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class CategoryEntityTest {

  private lateinit var gson: Gson
  private lateinit var converters: Converters

  @Before
  fun setUp() {
    gson = Gson()
    converters = Converters()
  }

  @Test
  fun `toCategory converts CategoryEntity to Category successfully`() {
    val subcategories =
        listOf(
            Subcategory(id = "sub1", name = "Subcategory 1", category = "cat1"),
            Subcategory(id = "sub2", name = "Subcategory 2", category = "cat1"))
    val serializedSubcategories = converters.fromSubcategoryList(subcategories)

    val entity =
        CategoryEntity(
            id = "cat1",
            name = "Category 1",
            description = "Test description",
            subcategories = serializedSubcategories)

    val category = entity.toCategory()

    assertEquals("cat1", category.id)
    assertEquals("Category 1", category.name)
    assertEquals("Test description", category.description)
    assertEquals(2, category.subcategories.size)
    assertEquals("sub1", category.subcategories[0].id)
    assertEquals("Subcategory 1", category.subcategories[0].name)
    assertEquals("sub2", category.subcategories[1].id)
    assertEquals("Subcategory 2", category.subcategories[1].name)
  }

  @Test
  fun `toCategory handles empty subcategories`() {
    val entity =
        CategoryEntity(
            id = "cat2",
            name = "Empty Category",
            description = "No subcategories",
            subcategories = "[]")

    val category = entity.toCategory()

    assertEquals("cat2", category.id)
    assertEquals("Empty Category", category.name)
    assertEquals("No subcategories", category.description)
    assertTrue(category.subcategories.isEmpty())
  }

  @Test
  fun `toCategory handles malformed subcategories`() {
    val malformedSubcategories = "[{\"id\":\"sub1\"}]" // Missing other fields

    val entity =
        CategoryEntity(
            id = "cat3",
            name = "Malformed Category",
            description = "Malformed subcategories",
            subcategories = malformedSubcategories)

    val category = entity.toCategory()

    assertEquals("cat3", category.id)
    assertEquals("Malformed Category", category.name)
    assertEquals("Malformed subcategories", category.description)
    assertEquals(1, category.subcategories.size)
    assertEquals("sub1", category.subcategories[0].id)
    assertEquals("", category.subcategories[0].name) // Default values for missing fields
  }

  @Test
  fun `CategoryEntity can serialize and deserialize correctly`() {
    val subcategories =
        listOf(
            Subcategory(id = "sub1", name = "Subcategory 1", category = "cat1"),
            Subcategory(id = "sub2", name = "Subcategory 2", category = "cat1"))

    val entity =
        CategoryEntity(
            id = "cat1",
            name = "Category 1",
            description = "Serialization test",
            subcategories = converters.fromSubcategoryList(subcategories))

    val serialized = gson.toJson(entity)
    val deserialized = gson.fromJson(serialized, CategoryEntity::class.java)

    assertEquals(entity.id, deserialized.id)
    assertEquals(entity.name, deserialized.name)
    assertEquals(entity.description, deserialized.description)
    assertEquals(entity.subcategories, deserialized.subcategories)

    val deserializedCategory = deserialized.toCategory()
    assertEquals(2, deserializedCategory.subcategories.size)
  }
}
