package com.arygm.quickfix.model.quickfix

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.arygm.quickfix.model.profile.WorkerProfile
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.storage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

open class QuickFixViewModel(private val repository: QuickFixRepository) : ViewModel() {

  private val quickFixes_ = MutableStateFlow<List<QuickFix>>(emptyList())
  val quickFixes: StateFlow<List<QuickFix>> = quickFixes_.asStateFlow()

  private val currentQuickFix_ = MutableStateFlow(QuickFix())
  val currentQuickFix: StateFlow<QuickFix> = currentQuickFix_.asStateFlow()

  private val selectedWorkerProfile_ = MutableStateFlow(WorkerProfile())
  val selectedWorkerProfile: StateFlow<WorkerProfile> = selectedWorkerProfile_.asStateFlow()

  private val uploadedImages_ = MutableStateFlow<List<Bitmap>>(emptyList())
  val uploadedImages: StateFlow<List<Bitmap>> = uploadedImages_.asStateFlow()

  init {
    repository.init { getQuickFixes() }
  }

  companion object {
    val Factory: ViewModelProvider.Factory =
        object : ViewModelProvider.Factory {
          @Suppress("UNCHECKED_CAST")
          override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return QuickFixViewModel(
                QuickFixRepositoryFirestore(Firebase.firestore, Firebase.storage))
                as T
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

  fun uploadQuickFixImages(
      quickFixId: String,
      images: List<Bitmap>, // List of image file paths as strings
      onSuccess: (List<String>) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    repository.uploadQuickFixImages(
        quickFixId = quickFixId,
        images = images,
        onSuccess = { onSuccess(it) },
        onFailure = { e -> onFailure(e) })
  }

  fun getRandomUid(): String {
    return repository.getRandomUid()
  }

  fun setQuickFixes(quickFixes: List<QuickFix>) { // For testing purposes
    quickFixes_.value = quickFixes
  }

  fun setSelectedWorkerProfile(workerProfile: WorkerProfile) {
    selectedWorkerProfile_.value = workerProfile
  }

  fun setUpdateQuickFix(updatedQuickFix: QuickFix) {
    viewModelScope.launch { currentQuickFix_.value = updatedQuickFix }
  }

  fun resetCurrentQuickFix() {
    viewModelScope.launch { currentQuickFix_.value = QuickFix() }
  }

  fun addUploadedImage(image: Bitmap) {
    uploadedImages_.value += image
  }

  fun deleteUploadedImages(images: List<Bitmap>) {
    uploadedImages_.value = uploadedImages_.value.filterNot { it in images }
  }

  fun clearUploadedImages() {
    uploadedImages_.value = emptyList()
  }

  fun fetchQuickFixImageUrls(
      quickFixId: String,
      onSuccess: (List<String>) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    repository.fetchQuickFixImageUrls(
        quickFixId = quickFixId, onSuccess = { onSuccess(it) }, onFailure = { e -> onFailure(e) })
  }

  fun fetchQuickFixAsBitmaps(
      quickFixId: String,
      onSuccess: (List<Pair<String, Bitmap>>) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    repository.fetchQuickFixAsBitmaps(
        quickFixId = quickFixId, onSuccess = { onSuccess(it) }, onFailure = { e -> onFailure(e) })
  }
}
