package com.arygm.quickfix.model.offline.large.categories

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.arygm.quickfix.model.offline.large.QuickFixRoomDatabase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class CategoryDaoTest {

  private lateinit var database: QuickFixRoomDatabase
  private lateinit var categoryDao: CategoryDao

  @Before
  fun setUp() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    database = Room.inMemoryDatabaseBuilder(context, QuickFixRoomDatabase::class.java).build()
    categoryDao = database.categoryDao()
  }

  @After
  fun tearDown() {
    database.close()
  }

  @Test
  fun insertAndRetrieveCategoryEntity() = runBlocking {
    val category =
        CategoryEntity(
            id = "category1",
            name = "Plumbing",
            description = "All plumbing services",
            subcategories = "[]")

    categoryDao.insertCategory(category)

    val retrievedCategory = categoryDao.getCategoryById("category1")
    assertNotNull(retrievedCategory)
    assertEquals("category1", retrievedCategory?.id)
    assertEquals("Plumbing", retrievedCategory?.name)
    assertEquals("All plumbing services", retrievedCategory?.description)
    assertEquals("[]", retrievedCategory?.subcategories)
  }

  @Test
  fun getAllCategories_emptyDatabase_returnsEmptyList() = runBlocking {
    val allCategories = categoryDao.getAllCategories().first()
    assertTrue(allCategories.isEmpty())
  }

  @Test
  fun insertMultipleCategories_andRetrieveAll() = runBlocking {
    val category1 =
        CategoryEntity(
            id = "category1",
            name = "Plumbing",
            description = "All plumbing services",
            subcategories = "[]")
    val category2 =
        CategoryEntity(
            id = "category2",
            name = "Electrical",
            description = "Electrical repairs and installations",
            subcategories = "[]")

    categoryDao.insertCategory(category1)
    categoryDao.insertCategory(category2)

    val allCategories = categoryDao.getAllCategories().first()
    assertEquals(2, allCategories.size)
    assertTrue(allCategories.any { it.id == "category1" })
    assertTrue(allCategories.any { it.id == "category2" })
  }

  @Test
  fun getCategoryById_existingCategory() = runBlocking {
    val category =
        CategoryEntity(
            id = "category3",
            name = "Carpentry",
            description = "Woodworking services",
            subcategories = "[]")
    categoryDao.insertCategory(category)

    val retrievedCategory = categoryDao.getCategoryById("category3")
    assertNotNull(retrievedCategory)
    assertEquals("category3", retrievedCategory?.id)
    assertEquals("Carpentry", retrievedCategory?.name)
  }

  @Test
  fun getCategoryById_nonExistingCategory_returnsNull() = runBlocking {
    val retrievedCategory = categoryDao.getCategoryById("nonExistentId")
    assertNull(retrievedCategory)
  }

  @Test
  fun insertCategory_replaceExistingCategory() = runBlocking {
    val originalCategory =
        CategoryEntity(
            id = "category4",
            name = "Painting",
            description = "Interior and exterior painting",
            subcategories = "[]")
    categoryDao.insertCategory(originalCategory)

    val updatedCategory =
        CategoryEntity(
            id = "category4",
            name = "Painting and Decorating",
            description = "Expanded painting services",
            subcategories = "[]")
    categoryDao.insertCategory(updatedCategory)

    val retrievedCategory = categoryDao.getCategoryById("category4")
    assertNotNull(retrievedCategory)
    assertEquals("Painting and Decorating", retrievedCategory?.name)
    assertEquals("Expanded painting services", retrievedCategory?.description)
  }

  @Test
  fun deleteCategory_existingCategory_removesIt() = runBlocking {
    val category =
        CategoryEntity(
            id = "category5",
            name = "Cleaning",
            description = "Cleaning services",
            subcategories = "[]")
    categoryDao.insertCategory(category)

    val beforeDelete = categoryDao.getCategoryById("category5")
    assertNotNull(beforeDelete)

    categoryDao.deleteCategory("category5")
    val afterDelete = categoryDao.getCategoryById("category5")
    assertNull(afterDelete)
  }

  @Test
  fun deleteCategory_doesNotAffectOthers() = runBlocking {
    val category1 =
        CategoryEntity(
            id = "catA",
            name = "Gardening",
            description = "Lawn and garden maintenance",
            subcategories = "[]")
    val category2 =
        CategoryEntity(
            id = "catB", name = "Moving", description = "Moving services", subcategories = "[]")

    categoryDao.insertCategory(category1)
    categoryDao.insertCategory(category2)

    categoryDao.deleteCategory("catA")

    val allCategories = categoryDao.getAllCategories().first()
    assertEquals(1, allCategories.size)
    assertNull(categoryDao.getCategoryById("catA"))
    assertNotNull(categoryDao.getCategoryById("catB"))
  }
}
