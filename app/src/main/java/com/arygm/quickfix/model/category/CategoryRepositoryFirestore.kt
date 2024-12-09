package com.arygm.quickfix.model.category

import android.util.Log
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

open class CategoryRepositoryFirestore(private val db: FirebaseFirestore) : CategoryRepository {

  private val collectionPath = "categories"

  override fun init(onSuccess: () -> Unit) {
    Firebase.auth.addAuthStateListener {
      if (it.currentUser != null) {
        onSuccess()
      }
    }
  }

  override fun fetchCategories(
      onSuccess: (List<Category?>) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    CoroutineScope(Dispatchers.IO).launch {
      try {
        val result = db.collection(collectionPath).get().await()
        Log.d("fetchCategories", "Fetched ${result.documents.size} documents")
        val categories = result.documents.mapNotNull { document -> documentToCategory(document) }
        withContext(Dispatchers.Main) { onSuccess(categories) }
      } catch (e: Exception) {
        Log.e("fetchCategories", "Error fetching categories", e)
        withContext(Dispatchers.Main) { onFailure(e) }
      }
    }
  }

  override fun fetchSubcategories(
      categoryId: String,
      onSuccess: (List<Subcategory?>) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    CoroutineScope(Dispatchers.IO).launch {
      try {
        val subcategoryRef =
            db.collection(collectionPath).document(categoryId).collection("subcategories")
        val result = subcategoryRef.get().await()
        Log.d(
            "fetchSubcategories", "Fetched ${result.documents.size} subcategories for $categoryId")
        val subcategories = result.documents.mapNotNull { documentToSubcategory(it) }
        withContext(Dispatchers.Main) { onSuccess(subcategories) }
      } catch (e: Exception) {
        Log.e("fetchSubcategories", "Error fetching subcategories for $categoryId", e)
        withContext(Dispatchers.Main) { onFailure(e) }
      }
    }
  }

  override fun fetchCategoryBySubcategoryId(
      subcategoryId: String,
      onSuccess: (Category?) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    CoroutineScope(Dispatchers.IO).launch {
      try {
        val categoriesResult = db.collection(collectionPath).get().await()
        val categories = categoriesResult.documents.mapNotNull { documentToCategory(it) }
        val category =
            categories.find { category -> category.subcategories.any { it.name == subcategoryId } }
        withContext(Dispatchers.Main) { onSuccess(category) }
      } catch (e: Exception) {
        Log.e("fetchCategoryBySubcategoryId", "Error fetching category by subcategory ID", e)
        withContext(Dispatchers.Main) { onFailure(e) }
      }
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

  private suspend fun fetchSubcategoriesSuspend(categoryId: String): List<Subcategory>? {
    return try {
      val subcategoryRef =
          db.collection(collectionPath).document(categoryId).collection("subcategories")
      val result = subcategoryRef.get().await()
      result.documents.mapNotNull { documentToSubcategory(it) }
    } catch (e: Exception) {
      Log.e("fetchSubcategoriesSuspend", "Error fetching documents from Firestore", e)
      null
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
