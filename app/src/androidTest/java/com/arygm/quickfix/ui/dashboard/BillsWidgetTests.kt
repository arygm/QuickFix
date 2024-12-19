package com.arygm.quickfix.ui.dashboard

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import com.arygm.quickfix.model.bill.BillField
import com.arygm.quickfix.model.bill.Units
import com.arygm.quickfix.model.locations.Location
import com.arygm.quickfix.model.profile.Profile
import com.arygm.quickfix.model.profile.ProfileViewModel
import com.arygm.quickfix.model.profile.WorkerProfile
import com.arygm.quickfix.model.profile.WorkerProfileRepositoryFirestore
import com.arygm.quickfix.model.quickfix.QuickFix
import com.arygm.quickfix.model.quickfix.Status
import com.google.firebase.Timestamp
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.whenever

class BillsWidgetTests {

  @get:Rule val composeTestRule = createComposeRule()

  private lateinit var workerProfileRepositoryFirestore: WorkerProfileRepositoryFirestore
  private lateinit var workerViewModel: ProfileViewModel

  private val testBills =
      listOf(
          BillField("Bill 1", Units.M2, 100.0, 25.0),
          BillField("Bill 2", Units.H, 100.0, 25.0),
          BillField("Bill 3", Units.M, 100.0, 25.0),
          BillField("Bill 4", Units.U, 100.0, 25.0))

  private val fakeQuickFix =
      QuickFix(
          uid = "1",
          title = "Fake QuickFix",
          description = "This is a fake QuickFix for testing",
          imageUrl = listOf("https://example.com/image.jpg"),
          date = listOf(Timestamp.now()), // Example timestamp
          time = Timestamp.now(),
          includedServices = emptyList(),
          addOnServices = emptyList(),
          workerId = "1",
          userId = "1",
          chatUid = "1",
          status = Status.UPCOMING,
          bill = testBills,
          location = Location(0.0, 0.0, "Fake Location"))

  @Before
  fun setup() {
    workerProfileRepositoryFirestore = mock(WorkerProfileRepositoryFirestore::class.java)
    workerViewModel = ProfileViewModel(workerProfileRepositoryFirestore)

    doAnswer { invocation ->
          val onSuccess = invocation.arguments[1] as (Profile?) -> Unit
          onSuccess(WorkerProfile(displayName = "Anonymous Worker"))
        }
        .whenever(workerProfileRepositoryFirestore)
        .getProfileById(any(), any(), any())
  }

  @Test
  fun billSample_displaysDefaultNumberOfItems() {
    composeTestRule.setContent {
      BillsWidget(
          quickFixes = listOf(fakeQuickFix),
          onShowAllClick = {},
          onItemClick = {},
          itemsToShowDefault = 3,
          workerViewModel = workerViewModel)
    }

    composeTestRule.onNodeWithTag("BillItem_${fakeQuickFix.title}").assertIsDisplayed()

    // Verify that the fourth item is not displayed
    composeTestRule.onNodeWithTag("BillItem_Bill 4").assertDoesNotExist()
  }
}
