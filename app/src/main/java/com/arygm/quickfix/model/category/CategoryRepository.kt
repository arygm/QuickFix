package com.arygm.quickfix.model.category

interface CategoryRepository {

  fun init(onSuccess: () -> Unit)

  fun fetchCategories(onSuccess: (List<Category?>) -> Unit, onFailure: (Exception) -> Unit)

  fun fetchSubcategories(
      categoryId: String,
      onSuccess: (List<Subcategory?>) -> Unit,
      onFailure: (Exception) -> Unit
  )
}
