package com.arygm.quickfix.model.quickfix

import com.arygm.quickfix.model.bill.BillField
import com.arygm.quickfix.model.locations.Location
import com.arygm.quickfix.model.profile.dataFields.Service
import com.google.firebase.Timestamp

data class QuickFix(
    val uid: String,
    val status: Status,
    val imageUrl: List<String>,
    val date: List<Timestamp>,
    val time: Timestamp,
    val includedServices: List<Service>,
    val addOnServices: List<Service>,
    val workerId: String,
    val userId: String,
    val chatUid: String,
    val title: String,
    val description: String,
    val bill: List<BillField>,
    val location: Location
)

enum class Status {
  PENDING,
  UNPAID,
  PAID,
  UPCOMING,
  COMPLETED,
  CANCELED
}
