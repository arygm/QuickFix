package com.arygm.quickfix.model.bill

data class BillField(
    val description: String = "",
    val unit: Units = Units.U,
    val amount: Double = 0.0,
    val unitPrice: Double = 0.0,
    val total: Double = 0.0,
)
