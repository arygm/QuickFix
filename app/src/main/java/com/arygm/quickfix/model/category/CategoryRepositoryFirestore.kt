package com.arygm.quickfix.model.category

import android.util.Log
import com.arygm.quickfix.model.offline.large.categories.CategoryDao
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

open class CategoryRepositoryFirestore(
    private val dao: CategoryDao,
    private val db: FirebaseFirestore
) : CategoryRepository {

  private val collectionPath = "categories"

  override fun init(onSuccess: () -> Unit) {
    Firebase.auth.addAuthStateListener {
      if (it.currentUser != null) {
        onSuccess()
      }
    }
  }

  override suspend fun fetchCategories(
      onSuccess: (List<Category?>) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    try {
      // Attempt to fetch from Room first
      val cachedCategories =
          dao.getAllCategories()
              .map { entities -> entities.map { it.toCategory() } }
              .first() // Take only the first emission from the flow

      if (cachedCategories.isNotEmpty()) {
        withContext(Dispatchers.Main) { onSuccess(cachedCategories) }
        return
      }
      // If no data in Room, fetch from Firestore
      val result = db.collection(collectionPath).get().await()
      val categories = result.documents.mapNotNull { documentToCategory(it) }

      // Cache fetched categories in Room
      categories.forEach { category -> dao.insertCategory(category.toCategoryEntity()) }

      withContext(Dispatchers.Main) { onSuccess(categories) }
    } catch (e: Exception) {
      Log.e("fetchCategories", "Error fetching categories", e)
      withContext(Dispatchers.Main) { onFailure(e) }
    }
  }

  override suspend fun fetchSubcategories(
      categoryId: String,
      onSuccess: (List<Subcategory?>) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    try {
      val subcategories = fetchSubcategoriesSuspend(categoryId)
      withContext(Dispatchers.Main) { onSuccess(subcategories) }
    } catch (e: Exception) {
      Log.e("fetchSubcategories", "Error fetching subcategories for $categoryId", e)
      withContext(Dispatchers.Main) { onFailure(e) }
    }
  }

  override suspend fun fetchCategoryBySubcategoryId(
      subcategoryId: String,
      onSuccess: (Category?) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    var isCached = false
    try {
      // Fetch from Room first
      val categories =
          dao.getAllCategories().map { entities -> entities.map { it.toCategory() } }.first()

      val cachedCategory =
          categories.find { category -> category.subcategories.any { it.name == subcategoryId } }

      if (cachedCategory != null) {
        withContext(Dispatchers.Main) { onSuccess(cachedCategory) }
        return
      }

      // Fallback to Firestore
      val categoriesResult = db.collection(collectionPath).get().await()
      val fetchedCategories = categoriesResult.documents.mapNotNull { documentToCategory(it) }
      val category =
          fetchedCategories.find { category ->
            category.subcategories.any { it.name == subcategoryId }
          }

      // Cache the fetched categories
      fetchedCategories.forEach { dao.insertCategory(it.toCategoryEntity()) }

      withContext(Dispatchers.Main) { onSuccess(category) }
    } catch (e: Exception) {
      Log.e("fetchCategoryBySubcategoryId", "Error fetching category by subcategory ID", e)
      withContext(Dispatchers.Main) { onFailure(e) }
    }
  }

  private suspend fun documentToCategory(document: DocumentSnapshot): Category? {
    val id = document.id
    val name = document.getString("name") ?: return null
    val description = document.getString("description") ?: return null

    // Fetch subcategories from the subcollection
    val subcategories = fetchSubcategoriesSuspend(id) ?: return null

    return Category(id = id, name = name, description = description, subcategories = subcategories)
  }

  private suspend fun fetchSubcategoriesSuspend(categoryId: String): List<Subcategory> {
    return try {
      // Attempt to fetch subcategories from Room
      val cachedCategory = dao.getCategoryById(categoryId)
      if (cachedCategory != null) {
        val subcategories = cachedCategory.toCategory().subcategories
        if (subcategories.isNotEmpty()) {
          Log.d("fetchSubcategoriesSuspend", "Returning cached subcategories for $categoryId")
          return subcategories
        }
      }

      // Fallback to Firestore if no cached data is available
      val subcategoryRef =
          db.collection(collectionPath).document(categoryId).collection("subcategories")
      val result = subcategoryRef.get().await()

      // Map Firestore documents to Subcategory objects
      val subcategories = result.documents.mapNotNull { documentToSubcategory(it) }

      // Cache fetched subcategories by updating the CategoryEntity in Room
      cachedCategory?.let {
        val updatedCategory = it.toCategory().copy(subcategories = subcategories)
        dao.insertCategory(updatedCategory.toCategoryEntity())
      }

      Log.d("fetchSubcategoriesSuspend", "Returning Firestore subcategories for $categoryId")
      subcategories
    } catch (e: Exception) {
      Log.e("fetchSubcategoriesSuspend", "Error fetching subcategories for $categoryId", e)
      emptyList() // Return an empty list in case of an error
    }
  }

  private fun documentToSubcategory(document: DocumentSnapshot): Subcategory? {
    val id = document.id
    val name = document.getString("name") ?: return null
    val category = document.getString("category") ?: return null
    val tags = document.get("tags") as? List<String> ?: emptyList()
    val setServices = document.get("setService") as? List<String> ?: emptyList()
    val scaleData = document.get("scale") as? Map<*, *>
    val scale =
        scaleData?.let {
          Scale(
              longScale = it["longScale"] as? String ?: "",
              shortScale = it["shortScale"] as? String ?: "")
        }

    return Subcategory(
        id = id,
        name = name,
        category = category,
        tags = tags,
        scale = scale,
        setServices = setServices,
    )
  }
}
