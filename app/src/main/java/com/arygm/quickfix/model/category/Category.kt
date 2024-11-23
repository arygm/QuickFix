package com.arygm.quickfix.model.category

data class Subcategory(
    val id: String = "",
    val name: String = "",
    val tags: List<String> = emptyList(),
    val scale: Scale? = null,
    val setServices: List<String> = emptyList()
)

data class Category(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val subcategories: List<Subcategory> = emptyList()
)

data class Scale(
    val longScale : String,
    val shortScale : String
)
