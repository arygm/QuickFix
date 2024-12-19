package com.arygm.quickfix.model.quickfix

import android.graphics.Bitmap

interface QuickFixRepository {
  fun init(onSuccess: () -> Unit)

  fun getRandomUid(): String

  fun getQuickFixes(onSuccess: (List<QuickFix>) -> Unit, onFailure: (Exception) -> Unit)

  fun addQuickFix(quickFix: QuickFix, onSuccess: () -> Unit, onFailure: (Exception) -> Unit)

  fun updateQuickFix(quickFix: QuickFix, onSuccess: () -> Unit, onFailure: (Exception) -> Unit)

  fun deleteQuickFixById(id: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit)

  fun getQuickFixById(uid: String, onSuccess: (QuickFix?) -> Unit, onFailure: (Exception) -> Unit)

  fun uploadQuickFixImages(
      quickFixId: String,
      images: List<Bitmap>, // List of image file paths as strings
      onSuccess: (List<String>) -> Unit,
      onFailure: (Exception) -> Unit
  )

  fun fetchQuickFixImageUrls(
      quickFixId: String,
      onSuccess: (List<String>) -> Unit,
      onFailure: (Exception) -> Unit
  )

  fun fetchQuickFixAsBitmaps(
      quickFixId: String,
      onSuccess: (List<Pair<String, Bitmap>>) -> Unit,
      onFailure: (Exception) -> Unit
  )
}
