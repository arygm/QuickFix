package com.arygm.quickfix.ui.dashboard

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import com.arygm.quickfix.model.account.AccountViewModel
import com.arygm.quickfix.model.category.CategoryViewModel
import com.arygm.quickfix.model.messaging.Chat
import com.arygm.quickfix.model.messaging.ChatViewModel
import com.arygm.quickfix.model.offline.small.PreferencesViewModel
import com.arygm.quickfix.model.profile.ProfileViewModel
import com.arygm.quickfix.model.profile.WorkerProfile
import com.arygm.quickfix.model.quickfix.QuickFix
import com.arygm.quickfix.model.quickfix.QuickFixViewModel
import com.arygm.quickfix.model.quickfix.Status
import com.arygm.quickfix.model.search.AnnouncementViewModel
import com.arygm.quickfix.ui.elements.QuickFixButton
import com.arygm.quickfix.ui.elements.QuickFixesWidget
import com.arygm.quickfix.ui.navigation.NavigationActions
import com.arygm.quickfix.ui.theme.poppinsTypography
import com.arygm.quickfix.ui.uiMode.appContentUI.userModeUI.navigation.UserScreen
import com.arygm.quickfix.ui.uiMode.workerMode.navigation.WorkerScreen
import com.arygm.quickfix.utils.loadAppMode
import com.arygm.quickfix.utils.loadUserId
import kotlinx.coroutines.launch

@Composable
fun DashboardScreen(
    navigationActions: NavigationActions,
    userViewModel: ProfileViewModel,
    workerViewModel: ProfileViewModel,
    accountViewModel: AccountViewModel,
    quickFixViewModel: QuickFixViewModel,
    chatViewModel: ChatViewModel,
    preferencesViewModel: PreferencesViewModel,
    announcementViewModel: AnnouncementViewModel,
    categoryViewModel: CategoryViewModel
) {

  var mode by remember { mutableStateOf("") }
  var uid by remember { mutableStateOf("") }
  var quickFixes by remember { mutableStateOf(emptyList<QuickFix>()) }
  var chats by remember { mutableStateOf(emptyList<Chat>()) }
  LaunchedEffect(Unit) {
    mode = loadAppMode(preferencesViewModel)
    uid = loadUserId(preferencesViewModel)
    (if (mode == "USER") userViewModel else workerViewModel).fetchUserProfile(uid) { profile ->
      profile?.quickFixes?.forEach { quickFix ->
        quickFixViewModel.fetchQuickFix(quickFix) {
          if (it != null) {
            quickFixes = quickFixes + it
          }
        }
      }
    }
    accountViewModel.fetchUserAccount(
        uid,
        onResult = { account ->
          account?.activeChats?.forEach { chatUid ->
            Log.d("DashboardScreen", "chatUid: $chatUid")
            chatViewModel.viewModelScope.launch {
              chatViewModel.getChatByChatUid(
                  chatUid,
                  onSuccess = { chat ->
                    Log.d("DashboardScreen", "chat: $chat")
                    if (chat != null) {
                      chats = chats + chat
                    }
                  },
                  onFailure = { e ->
                    Log.e("DashboardScreen", "Failed to fetch chat: ${e.message}")
                  })
            }
          }
        })
    Log.d("DashboardScreen", "quickFixes: $quickFixes")
    Log.d("DashboardScreen", "chats: $chats")
  }

  data class QuickFixFilterButtons(
      val title: String,
      val isSelected: Boolean,
      val onClick: () -> Unit
  )

  var quickFixFilterButtons by remember {
    mutableStateOf(
        listOf(QuickFixFilterButtons("All", true, {})) +
            Status.entries
                .filter { it != Status.FINISHED }
                .map { status ->
                  QuickFixFilterButtons(
                      status.name.lowercase().replaceFirstChar { it.uppercase() }, false, {})
                })
  }

  Scaffold(
      containerColor = colorScheme.background,
      content = { padding ->
        LazyColumn(
            modifier =
                Modifier.fillMaxSize()
                    .testTag("DashboardContent")
                    .padding(padding)
                    .padding(6.dp)
                    .fillMaxWidth(),
            horizontalAlignment = Alignment.Start) {
              item {
                Card(
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier.padding(6.dp).fillMaxWidth(),
                    colors = CardDefaults.cardColors().copy(containerColor = colorScheme.surface),
                ) {
                  Text(
                      text = "QuickFixes",
                      style = poppinsTypography.headlineLarge,
                      textAlign = TextAlign.Start,
                      color = colorScheme.onBackground,
                      modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))

                  LazyRow(
                      horizontalArrangement = Arrangement.spacedBy(4.dp),
                      modifier = Modifier.testTag("QuickFixFilterButtons"),
                  ) {
                    items(quickFixFilterButtons.size) { index ->
                      QuickFixButton(
                          buttonText = quickFixFilterButtons[index].title,
                          onClickAction = {
                            quickFixFilterButtons =
                                quickFixFilterButtons.mapIndexed { i, button ->
                                  button.copy(isSelected = i == index)
                                }
                            quickFixFilterButtons[index].onClick()
                          },
                          textColor =
                              if (quickFixFilterButtons[index].isSelected) colorScheme.onPrimary
                              else colorScheme.primary,
                          textStyle = poppinsTypography.bodyMedium,
                          buttonColor =
                              if (quickFixFilterButtons[index].isSelected) colorScheme.primary
                              else colorScheme.onPrimary,
                          height = 40.dp,
                          modifier = Modifier.padding(7.dp),
                          contentPadding = PaddingValues(vertical = 0.dp, horizontal = 10.dp),
                          border =
                              if (quickFixFilterButtons[index].isSelected) null
                              else BorderStroke(1.dp, colorScheme.primary),
                      )
                    }
                  }
                  val buttonTitle = quickFixFilterButtons.firstOrNull { it.isSelected }?.title
                  var workerProfile by remember { mutableStateOf<WorkerProfile?>(null) }
                  when (buttonTitle) {
                    "Pending" ->
                        QuickFixesWidget(
                            status = "Pending",
                            quickFixList = quickFixes.filter { it.status == Status.PENDING },
                            onShowAllClick = { /* Handle Show All Click */},
                            onItemClick = {
                              quickFixViewModel.setUpdateQuickFix(it)
                              workerViewModel.fetchUserProfile(it.workerId) { profile ->
                                if (profile != null) {
                                  workerProfile = profile as WorkerProfile
                                  quickFixViewModel.setSelectedWorkerProfile(workerProfile!!)
                                }
                              }
                              if (mode == "USER") {
                                navigationActions.navigateTo(UserScreen.QUICKFIX_ONBOARDING)
                              } else {
                                navigationActions.navigateTo(WorkerScreen.QUIKFIX_ONBOARDING)
                              }
                            },
                            modifier = Modifier.testTag("${buttonTitle}QuickFixes"),
                            itemsToShowDefault = 3,
                            workerViewModel = workerViewModel,
                        )
                    "Unpaid" -> {
                      QuickFixesWidget(
                          status = "Unpaid",
                          quickFixList = quickFixes.filter { it.status == Status.UNPAID },
                          onShowAllClick = { /* Handle Show All Click */},
                          onItemClick = {
                            quickFixViewModel.setUpdateQuickFix(it)
                            workerViewModel.fetchUserProfile(it.workerId) { profile ->
                              if (profile != null) {
                                workerProfile = profile as WorkerProfile
                                quickFixViewModel.setSelectedWorkerProfile(workerProfile!!)
                              }
                            }
                            if (mode == "USER") {
                              navigationActions.navigateTo(UserScreen.QUICKFIX_ONBOARDING)
                            } else {
                              navigationActions.navigateTo(WorkerScreen.QUIKFIX_ONBOARDING)
                            }
                          },
                          modifier = Modifier.testTag("${buttonTitle}QuickFixes"),
                          itemsToShowDefault = 3,
                          workerViewModel = workerViewModel,
                      )
                    }
                    "Paid" -> {
                      QuickFixesWidget(
                          status = "Paid",
                          quickFixList = quickFixes.filter { it.status == Status.PAID },
                          onShowAllClick = { /* Handle Show All Click */},
                          onItemClick = {
                            quickFixViewModel.setUpdateQuickFix(it)
                            workerViewModel.fetchUserProfile(it.workerId) { profile ->
                              if (profile != null) {
                                workerProfile = profile as WorkerProfile
                                quickFixViewModel.setSelectedWorkerProfile(workerProfile!!)
                              }
                            }
                            if (mode == "USER") {
                              navigationActions.navigateTo(UserScreen.QUICKFIX_ONBOARDING)
                            } else {
                              navigationActions.navigateTo(WorkerScreen.QUIKFIX_ONBOARDING)
                            }
                          },
                          modifier = Modifier.testTag("${buttonTitle}QuickFixes"),
                          itemsToShowDefault = 3,
                          workerViewModel = workerViewModel,
                      )
                    }
                    "Upcoming" -> {
                      QuickFixesWidget(
                          status = "Upcoming",
                          quickFixList = quickFixes.filter { it.status == Status.UPCOMING },
                          onShowAllClick = { /* Handle Show All Click */},
                          onItemClick = {
                            quickFixViewModel.setUpdateQuickFix(it)
                            workerViewModel.fetchUserProfile(it.workerId) { profile ->
                              if (profile != null) {
                                workerProfile = profile as WorkerProfile
                                quickFixViewModel.setSelectedWorkerProfile(workerProfile!!)
                              }
                            }
                            if (mode == "USER") {
                              navigationActions.navigateTo(UserScreen.QUICKFIX_ONBOARDING)
                            } else {
                              navigationActions.navigateTo(WorkerScreen.QUIKFIX_ONBOARDING)
                            }
                          },
                          modifier = Modifier.testTag("${buttonTitle}QuickFixes"),
                          itemsToShowDefault = 3,
                          workerViewModel = workerViewModel,
                      )
                    }
                    "Completed" -> {
                      QuickFixesWidget(
                          status = "Completed",
                          quickFixList = quickFixes.filter { it.status == Status.COMPLETED },
                          onShowAllClick = { /* Handle Show All Click */},
                          onItemClick = {
                            quickFixViewModel.setUpdateQuickFix(it)
                            workerViewModel.fetchUserProfile(it.workerId) { profile ->
                              if (profile != null) {
                                workerProfile = profile as WorkerProfile
                                quickFixViewModel.setSelectedWorkerProfile(workerProfile!!)
                              }
                            }
                            if (mode == "USER") {
                              navigationActions.navigateTo(UserScreen.QUICKFIX_ONBOARDING)
                            } else {
                              navigationActions.navigateTo(WorkerScreen.QUIKFIX_ONBOARDING)
                            }
                          },
                          modifier = Modifier.testTag("${buttonTitle}QuickFixes"),
                          itemsToShowDefault = 3,
                          workerViewModel = workerViewModel,
                      )
                    }
                    "Canceled" -> {
                      QuickFixesWidget(
                          status = "Canceled",
                          quickFixList = quickFixes.filter { it.status == Status.CANCELED },
                          onShowAllClick = { /* Handle Show All Click */},
                          onItemClick = {
                            quickFixViewModel.setUpdateQuickFix(it)
                            workerViewModel.fetchUserProfile(it.workerId) { profile ->
                              if (profile != null) {
                                workerProfile = profile as WorkerProfile
                                quickFixViewModel.setSelectedWorkerProfile(workerProfile!!)
                              }
                            }
                            if (mode == "USER") {
                              navigationActions.navigateTo(UserScreen.QUICKFIX_ONBOARDING)
                            } else {
                              navigationActions.navigateTo(WorkerScreen.QUIKFIX_ONBOARDING)
                            }
                          },
                          modifier = Modifier.testTag("${buttonTitle}QuickFixes"),
                          itemsToShowDefault = 3,
                          workerViewModel = workerViewModel,
                      )
                    }
                    "All" -> {
                      QuickFixesWidget(
                          quickFixList = quickFixes,
                          onShowAllClick = { /* Handle Show All Click */},
                          onItemClick = {
                            quickFixViewModel.setUpdateQuickFix(it)
                            workerViewModel.fetchUserProfile(it.workerId) { profile ->
                              if (profile != null) {
                                workerProfile = profile as WorkerProfile
                                quickFixViewModel.setSelectedWorkerProfile(workerProfile!!)
                              }
                            }
                            if (mode == "USER") {
                              navigationActions.navigateTo(UserScreen.QUICKFIX_ONBOARDING)
                            } else {
                              navigationActions.navigateTo(WorkerScreen.QUIKFIX_ONBOARDING)
                            }
                          },
                          modifier = Modifier.testTag("${buttonTitle}QuickFixes"),
                          itemsToShowDefault = 3,
                          workerViewModel = workerViewModel,
                      )
                    }
                  }
                }
              }
              item {
                AnnouncementsWidget(
                    announcementViewModel = announcementViewModel,
                    navigationActions = navigationActions,
                    categoryViewModel = categoryViewModel,
                    itemsToShowDefault = 3,
                )
              }

              item {
                ChatWidget(
                    chatList = chats,
                    onItemClick = {
                      chatViewModel.selectChat(it)
                      if (mode == "USER") navigationActions.navigateTo(UserScreen.MESSAGES)
                      else navigationActions.navigateTo(WorkerScreen.MESSAGES)
                    },
                    onShowAllClick = { /*Handle Show All Click*/},
                    itemsToShowDefault = 3,
                    uid = uid,
                    accountViewModel = accountViewModel,
                    categoryViewModel = categoryViewModel,
                    workerViewModel = workerViewModel,
                    preferencesViewModel = preferencesViewModel,
                )
              }

              item {
                BillsWidget(
                    quickFixes = quickFixes,
                    onItemClick = { /*Handle Bill Item Click*/},
                    onShowAllClick = { /*Handle Show All Click*/},
                    itemsToShowDefault = 2,
                    workerViewModel = workerViewModel,
                )
              }
            }
      })
}
