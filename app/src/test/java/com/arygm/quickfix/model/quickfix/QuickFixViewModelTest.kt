package com.arygm.quickfix.model.quickfix

import com.arygm.quickfix.model.bill.BillField
import com.arygm.quickfix.model.bill.Units
import com.arygm.quickfix.model.locations.Location
import com.arygm.quickfix.model.profile.dataFields.AddOnService
import com.arygm.quickfix.model.profile.dataFields.IncludedService
import com.google.firebase.Timestamp
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.clearInvocations
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.doNothing
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class QuickFixViewModelTest {

  private lateinit var quickFixRepository: QuickFixRepository
  private lateinit var quickFixViewModel: QuickFixViewModel

  private val testTimestamp = Timestamp.now()
  private val testLocation = Location(latitude = 1.0, longitude = 2.0, name = "Test Location")
  private val testBillField =
      BillField(
          description = "Test Service",
          unit = Units.H,
          amount = 2.0,
          unitPrice = 50.0,
          total = 100.0)
  private val testQuickFix =
      QuickFix(
          uid = "1",
          status = Status.PENDING,
          imageUrl = listOf("http://example.com/image1.jpg"),
          date = listOf(testTimestamp),
          time = testTimestamp,
          includedServices = listOf(IncludedService("Painting")),
          addOnServices = listOf(AddOnService("Wall Repair")),
          workerId = "Worker Id A",
          userId = "User Id B",
          chatUid = "chat123",
          title = "Fix My Wall",
          description = "I need my wall fixed",
          bill = listOf(testBillField),
          location = testLocation)

  @Before
  fun setUp() {
    quickFixRepository = mock()
    quickFixViewModel = QuickFixViewModel(quickFixRepository)

    val initCaptor = argumentCaptor<() -> Unit>()
    verify(quickFixRepository).init(initCaptor.capture())

    // Mock getQuickFixes to do nothing when called
    doNothing().whenever(quickFixRepository).getQuickFixes(any(), any())

    // Simulate repository calling the init callback
    initCaptor.firstValue.invoke()
  }

  @Test
  fun init_callsRepositoryInit() {
    verify(quickFixRepository).init(any())
  }

  @Test
  fun init_invokesGetQuickFixesWhenRepositoryInitCallsCallback() {
    verify(quickFixRepository).getQuickFixes(any(), any())
  }

  @Test
  fun getQuickFixes_whenSuccess_updatesQuickFixesStateFlow() = runTest {
    val quickFixesList = listOf(testQuickFix)
    doAnswer { invocation ->
          val onSuccess = invocation.getArgument<(List<QuickFix>) -> Unit>(0)
          onSuccess(quickFixesList)
          null
        }
        .whenever(quickFixRepository)
        .getQuickFixes(any(), any())

    quickFixViewModel.getQuickFixes()

    val result = quickFixViewModel.quickFixes.first()
    assertThat(result, `is`(quickFixesList))
  }

  @Test
  fun getQuickFixes_whenFailure_logsError() {
    val exception = Exception("Test exception")
    doAnswer { invocation ->
          val onFailure = invocation.getArgument<(Exception) -> Unit>(1)
          onFailure(exception)
          null
        }
        .whenever(quickFixRepository)
        .getQuickFixes(any(), any())

    quickFixViewModel.getQuickFixes()

    // We can check that quickFixes remains empty
    val result = quickFixViewModel.quickFixes.value
    assertThat(result, `is`(emptyList()))
  }

  @Test
  fun addQuickFix_whenSuccess_callsGetQuickFixesAndOnSuccess() {
    clearInvocations(quickFixRepository)
    val onSuccessMock = mock<() -> Unit>()
    val onFailureMock = mock<(Exception) -> Unit>()

    doAnswer { invocation ->
          val onSuccess = invocation.getArgument<() -> Unit>(1)
          onSuccess()
          null
        }
        .whenever(quickFixRepository)
        .addQuickFix(any(), any(), any())

    doNothing().whenever(quickFixRepository).getQuickFixes(any(), any())

    quickFixViewModel.addQuickFix(testQuickFix, onSuccessMock, onFailureMock)

    verify(quickFixRepository).getQuickFixes(any(), any())
    verify(onSuccessMock).invoke()
  }

  @Test
  fun addQuickFix_whenFailure_callsOnFailure() {
    val exception = Exception("Test exception")
    val onSuccessMock = mock<() -> Unit>()
    val onFailureMock = mock<(Exception) -> Unit>()

    doAnswer { invocation ->
          val onFailure = invocation.getArgument<(Exception) -> Unit>(2)
          onFailure(exception)
          null
        }
        .whenever(quickFixRepository)
        .addQuickFix(any(), any(), any())

    quickFixViewModel.addQuickFix(testQuickFix, onSuccessMock, onFailureMock)

    verify(onFailureMock).invoke(exception)
  }

  @Test
  fun updateQuickFix_whenSuccess_callsGetQuickFixesAndOnSuccess() {
    clearInvocations(quickFixRepository)
    val onSuccessMock = mock<() -> Unit>()
    val onFailureMock = mock<(Exception) -> Unit>()

    doAnswer { invocation ->
          val onSuccess = invocation.getArgument<() -> Unit>(1)
          onSuccess()
          null
        }
        .whenever(quickFixRepository)
        .updateQuickFix(any(), any(), any())

    doNothing().whenever(quickFixRepository).getQuickFixes(any(), any())

    quickFixViewModel.updateQuickFix(testQuickFix, onSuccessMock, onFailureMock)

    verify(quickFixRepository).getQuickFixes(any(), any())
    verify(onSuccessMock).invoke()
  }

  @Test
  fun updateQuickFix_whenFailure_callsOnFailure() {
    val exception = Exception("Test exception")
    val onSuccessMock = mock<() -> Unit>()
    val onFailureMock = mock<(Exception) -> Unit>()

    doAnswer { invocation ->
          val onFailure = invocation.getArgument<(Exception) -> Unit>(2)
          onFailure(exception)
          null
        }
        .whenever(quickFixRepository)
        .updateQuickFix(any(), any(), any())

    quickFixViewModel.updateQuickFix(testQuickFix, onSuccessMock, onFailureMock)

    verify(onFailureMock).invoke(exception)
  }

  @Test
  fun deleteQuickFixById_whenSuccess_callsGetQuickFixes() {
    clearInvocations(quickFixRepository)
    doAnswer { invocation ->
          val onSuccess = invocation.getArgument<() -> Unit>(1)
          onSuccess()
          null
        }
        .whenever(quickFixRepository)
        .deleteQuickFixById(any(), any(), any())

    doNothing().whenever(quickFixRepository).getQuickFixes(any(), any())

    quickFixViewModel.deleteQuickFixById(testQuickFix.uid)

    verify(quickFixRepository).getQuickFixes(any(), any())
  }

  @Test
  fun deleteQuickFixById_whenFailure_logsError() {
    val exception = Exception("Test exception")
    doAnswer { invocation ->
          val onFailure = invocation.getArgument<(Exception) -> Unit>(2)
          onFailure(exception)
          null
        }
        .whenever(quickFixRepository)
        .deleteQuickFixById(any(), any(), any())

    quickFixViewModel.deleteQuickFixById(testQuickFix.uid)

    // Can't easily verify logging, but we can check that quickFixes remains unchanged
    val result = quickFixViewModel.quickFixes.value
    assertThat(result, `is`(emptyList()))
  }

  @Test
  fun fetchQuickFix_whenQuickFixExists_callsOnResultWithQuickFix() {
    val onResultMock = mock<(QuickFix?) -> Unit>()

    doAnswer { invocation ->
          val onSuccess = invocation.getArgument<(QuickFix?) -> Unit>(1)
          onSuccess(testQuickFix)
          null
        }
        .whenever(quickFixRepository)
        .getQuickFixById(any(), any(), any())

    quickFixViewModel.fetchQuickFix(testQuickFix.uid, onResultMock)

    verify(onResultMock).invoke(testQuickFix)
  }

  @Test
  fun fetchQuickFix_whenQuickFixDoesNotExist_callsOnResultWithNull() {
    val onResultMock = mock<(QuickFix?) -> Unit>()

    doAnswer { invocation ->
          val onSuccess = invocation.getArgument<(QuickFix?) -> Unit>(1)
          onSuccess(null)
          null
        }
        .whenever(quickFixRepository)
        .getQuickFixById(any(), any(), any())

    quickFixViewModel.fetchQuickFix("nonexistent", onResultMock)

    verify(onResultMock).invoke(null)
  }

  @Test
  fun fetchQuickFix_whenFailure_logsError() {
    val exception = Exception("Test exception")
    val onResultMock = mock<(QuickFix?) -> Unit>()

    doAnswer { invocation ->
          val onFailure = invocation.getArgument<(Exception) -> Unit>(2)
          onFailure(exception)
          null
        }
        .whenever(quickFixRepository)
        .getQuickFixById(any(), any(), any())

    quickFixViewModel.fetchQuickFix(testQuickFix.uid, onResultMock)

    verify(onResultMock).invoke(null)
  }

  @Test
  fun getQuickFixes_callsRepositoryGetQuickFixes() {
    clearInvocations(quickFixRepository)
    quickFixViewModel.getQuickFixes()
    verify(quickFixRepository).getQuickFixes(any(), any())
  }

  @Test
  fun addQuickFix_callsRepositoryAddQuickFix() {
    val onSuccessMock = mock<() -> Unit>()
    val onFailureMock = mock<(Exception) -> Unit>()

    quickFixViewModel.addQuickFix(testQuickFix, onSuccessMock, onFailureMock)
    verify(quickFixRepository).addQuickFix(eq(testQuickFix), any(), any())
  }

  @Test
  fun updateQuickFix_callsRepositoryUpdateQuickFix() {
    val onSuccessMock = mock<() -> Unit>()
    val onFailureMock = mock<(Exception) -> Unit>()

    quickFixViewModel.updateQuickFix(testQuickFix, onSuccessMock, onFailureMock)
    verify(quickFixRepository).updateQuickFix(eq(testQuickFix), any(), any())
  }

  @Test
  fun deleteQuickFixById_callsRepositoryDeleteQuickFixById() {
    val id = "1"
    quickFixViewModel.deleteQuickFixById(id)
    verify(quickFixRepository).deleteQuickFixById(eq(id), any(), any())
  }

  @Test
  fun fetchQuickFix_callsRepositoryGetQuickFixById() {
    val uid = "1"
    val onResultMock = mock<(QuickFix?) -> Unit>()
    quickFixViewModel.fetchQuickFix(uid, onResultMock)
    verify(quickFixRepository).getQuickFixById(eq(uid), any(), any())
  }
}
