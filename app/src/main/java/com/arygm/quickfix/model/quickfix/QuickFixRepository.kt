package com.arygm.quickfix.model.quickfix

interface QuickFixRepository {
  fun init(onSuccess: () -> Unit)

  fun getRandomUid(): String

  fun getQuickFixes(onSuccess: (List<QuickFix>) -> Unit, onFailure: (Exception) -> Unit)

  fun addQuickFix(quickFix: QuickFix, onSuccess: () -> Unit, onFailure: (Exception) -> Unit)

  fun updateQuickFix(quickFix: QuickFix, onSuccess: () -> Unit, onFailure: (Exception) -> Unit)

  fun deleteQuickFixById(id: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit)

  fun getQuickFixById(uid: String, onSuccess: (QuickFix?) -> Unit, onFailure: (Exception) -> Unit)
}
