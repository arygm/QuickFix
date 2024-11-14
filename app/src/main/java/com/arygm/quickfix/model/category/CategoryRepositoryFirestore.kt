package com.arygm.quickfix.model.category

import android.util.Log
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await

class CategoryRepositoryFirestore(private val db: FirebaseFirestore) : CategoryRepository {

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

  private suspend fun fetchSubcategories(categoryId: String): List<Subcategory>? {
    return try {
      val subcategoryRef =
          db.collection(collectionPath).document(categoryId).collection("subcategories")
      val result = subcategoryRef.get().await()
      result.documents.mapNotNull { documentToSubcategory(it) }
    } catch (e: Exception) {
      Log.e("fetchSubcategories", "Error fetching documents from Firestore", e)
      return null
    }
  }

  private suspend fun documentToCategory(document: DocumentSnapshot): Category? {
    val id = document.id
    val name = document.getString("name") ?: return null
    val description = document.getString("description") ?: return null

    // Fetch subcategories from the subcollection
    val subcategories = fetchSubcategories(id) ?: return null

    return Category(id = id, name = name, description = description, subcategories = subcategories)
  }

  private fun documentToSubcategory(document: DocumentSnapshot): Subcategory? {
    val id = document.id
    val name = document.getString("name") ?: return null
    val tags = document.get("tags") as? List<String> ?: emptyList()

    return Subcategory(id = id, name = name, tags = tags)
  }
}
