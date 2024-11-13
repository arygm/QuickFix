package com.arygm.quickfix.model.category

interface CategoryRepository {

    fun init(onSuccess: () -> Unit)

    fun fetchCategories(onSuccess: (List<Category?>) -> Unit, onFailure: (Exception) -> Unit)
}