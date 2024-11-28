package com.arygm.quickfix.model.quickfix

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class QuickFixViewModel(private val repository: QuickFixRepository) : ViewModel() {

  private val quickFixes_ = MutableStateFlow<List<QuickFix>>(emptyList())
  val quickFixes: StateFlow<List<QuickFix>> = quickFixes_.asStateFlow()

  init {
    repository.init { getQuickFixes() }
  }

  companion object {
    val Factory: ViewModelProvider.Factory =
        object : ViewModelProvider.Factory {
          @Suppress("UNCHECKED_CAST")
          override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return QuickFixViewModel(QuickFixRepositoryFirestore(Firebase.firestore)) as T
          }
        }
  }

  fun getQuickFixes() {
    repository.getQuickFixes(
        onSuccess = { quickFixes_.value = it },
        onFailure = { e -> Log.e("QuickFixViewModel", "Failed to fetch QuickFixes: ${e.message}") })
  }

  fun addQuickFix(quickFix: QuickFix, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
    repository.addQuickFix(
        quickFix = quickFix,
        onSuccess = {
          getQuickFixes()
          onSuccess()
        },
        onFailure = { e ->
          Log.e("QuickFixViewModel", "Failed to add QuickFix: ${e.message}")
          onFailure(e)
        })
  }

  fun updateQuickFix(quickFix: QuickFix, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
    repository.updateQuickFix(
        quickFix = quickFix,
        onSuccess = {
          getQuickFixes()
          onSuccess()
        },
        onFailure = { e ->
          Log.e("QuickFixViewModel", "Failed to update QuickFix: ${e.message}")
          onFailure(e)
        })
  }

  fun deleteQuickFixById(id: String) {
    repository.deleteQuickFixById(
        id = id,
        onSuccess = { getQuickFixes() },
        onFailure = { e -> Log.e("QuickFixViewModel", "Failed to delete QuickFix: ${e.message}") })
  }

  fun fetchQuickFix(uid: String, onResult: (QuickFix?) -> Unit) {
    repository.getQuickFixById(
        uid,
        onSuccess = { quickFix ->
          if (quickFix != null) {
            onResult(quickFix)
          } else {
            Log.e("QuickFixViewModel", "No QuickFix found for UID: $uid")
            onResult(null)
          }
        },
        onFailure = { e ->
          Log.e("QuickFixViewModel", "Error fetching QuickFix: ${e.message}")
          onResult(null)
        })
  }
}
