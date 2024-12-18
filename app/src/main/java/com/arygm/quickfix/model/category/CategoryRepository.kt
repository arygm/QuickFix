package com.arygm.quickfix.model.category

interface CategoryRepository {

  fun init(onSuccess: () -> Unit)

  suspend fun fetchCategories(onSuccess: (List<Category?>) -> Unit, onFailure: (Exception) -> Unit)

  suspend fun fetchSubcategories(
      categoryId: String,
      onSuccess: (List<Subcategory?>) -> Unit,
      onFailure: (Exception) -> Unit
  )

  suspend fun fetchCategoryBySubcategoryId(
      subcategoryId: String,
      onSuccess: (Category?) -> Unit,
      onFailure: (Exception) -> Unit
  )
}
