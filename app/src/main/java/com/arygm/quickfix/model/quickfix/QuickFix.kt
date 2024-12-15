package com.arygm.quickfix.model.quickfix

import com.arygm.quickfix.model.bill.BillField
import com.arygm.quickfix.model.locations.Location
import com.arygm.quickfix.model.profile.dataFields.Service
import com.google.firebase.Timestamp

data class QuickFix(
    val uid: String = "",
    val status: Status = Status.PENDING,
    val imageUrl: List<String> = emptyList(),
    val date: List<Timestamp> = emptyList(),
    val time: Timestamp = Timestamp.now(),
    val includedServices: List<Service> = emptyList(),
    val addOnServices: List<Service> = emptyList(),
    val workerId: String = "",
    val userId: String = "",
    val chatUid: String = "",
    val title: String = "",
    val description: String = "",
    val bill: List<BillField> = emptyList(),
    val location: Location = Location(),
)

enum class Status {
  PENDING,
  UNPAID,
  PAID,
  UPCOMING,
  COMPLETED,
  CANCELED,
  FINISHED
}
