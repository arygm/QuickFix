package com.arygm.quickfix.model.bill

data class BillField(
    val description: String,
    val unit: Unit,
    val amount: Int,
    val unitPrice: Double,
    val total: Double
)
