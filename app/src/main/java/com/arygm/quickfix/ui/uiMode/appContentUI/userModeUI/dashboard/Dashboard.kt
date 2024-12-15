package com.arygm.quickfix.ui.uiMode.appContentUI.userModeUI.dashboard

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
import com.arygm.quickfix.model.messaging.Chat
import com.arygm.quickfix.model.messaging.ChatStatus
import com.arygm.quickfix.model.messaging.ChatViewModel
import com.arygm.quickfix.model.offline.small.PreferencesViewModel
import com.arygm.quickfix.model.profile.ProfileViewModel
import com.arygm.quickfix.model.quickfix.QuickFix
import com.arygm.quickfix.model.quickfix.QuickFixViewModel
import com.arygm.quickfix.model.quickfix.Status
import com.arygm.quickfix.ui.elements.QuickFixButton
import com.arygm.quickfix.ui.elements.QuickFixesWidget
import com.arygm.quickfix.ui.navigation.NavigationActions
import com.arygm.quickfix.ui.theme.poppinsTypography
import com.arygm.quickfix.utils.loadAppMode
import com.arygm.quickfix.utils.loadUserId
import kotlinx.coroutines.launch

@Composable
fun DashboardScreen(
    navigationActions: NavigationActions,
    profileViewModel: ProfileViewModel,
    accountViewModel: AccountViewModel,
    quickFixViewModel: QuickFixViewModel,
    chatViewModel: ChatViewModel,
    preferencesViewModel: PreferencesViewModel
) {

  var mode by remember { mutableStateOf("") }
  var uid by remember { mutableStateOf("") }
  var quickFixes by remember { mutableStateOf(emptyList<QuickFix>()) }
  var chats by remember { mutableStateOf(emptyList<Chat>()) }
  LaunchedEffect(Unit) {
    mode = loadAppMode(preferencesViewModel)
    uid = loadUserId(preferencesViewModel)
    profileViewModel.fetchUserProfile(uid) { profile ->
      if (profile != null) {
        Log.d("DashboardScreen", "profile quickfixes: ${profile.quickFixes}")
      }
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
        Status.entries.map { status ->
          QuickFixFilterButtons(status.name, status == Status.UPCOMING, {})
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
                  when (buttonTitle) {
                    "Upcoming" ->
                        QuickFixesWidget(
                            status = "Upcoming",
                            quickFixList = quickFixes,
                            onShowAllClick = { /* Handle Show All Click */},
                            onItemClick = { /* Handle QuickFix Item Click */},
                            modifier = Modifier.testTag("${buttonTitle}QuickFixes"),
                            itemsToShowDefault = 3,
                        )
                    "Canceled" -> {
                      QuickFixesWidget(
                          status = "Canceled",
                          quickFixList = quickFixes,
                          onShowAllClick = { /* Handle Show All Click */},
                          onItemClick = { /* Handle QuickFix Item Click */},
                          modifier = Modifier.testTag("${buttonTitle}QuickFixes"),
                          itemsToShowDefault = 3,
                      )
                    }
                    "Unpaid" -> {
                      QuickFixesWidget(
                          status = "Unpaid",
                          quickFixList = quickFixes,
                          onShowAllClick = { /* Handle Show All Click */},
                          onItemClick = { /* Handle QuickFix Item Click */},
                          modifier = Modifier.testTag("${buttonTitle}QuickFixes"),
                          itemsToShowDefault = 3,
                      )
                    }
                    "Finished" -> {
                      QuickFixesWidget(
                          status = "Finished",
                          quickFixList = quickFixes,
                          onShowAllClick = { /* Handle Show All Click */},
                          onItemClick = { /* Handle QuickFix Item Click */},
                          modifier = Modifier.testTag("${buttonTitle}QuickFixes"),
                          itemsToShowDefault = 3,
                      )
                    }
                    "All" -> {
                      QuickFixesWidget(
                          quickFixList = quickFixes,
                          onShowAllClick = { /* Handle Show All Click */},
                          onItemClick = { /* Handle QuickFix Item Click */},
                          modifier = Modifier.testTag("${buttonTitle}QuickFixes"),
                          itemsToShowDefault = 3,
                      )
                    }
                  }
                }
              }

              item {
                ChatWidget(
                    chatList =
                        chats.filter {
                          it.chatStatus == ChatStatus.ACCEPTED ||
                              it.chatStatus == ChatStatus.GETTING_SUGGESTIONS
                        },
                    onItemClick = { /*Handle Message Item Click*/},
                    onShowAllClick = { /*Handle Show All Click*/},
                    itemsToShowDefault = 3,
                    uid = uid,
                )
              }

              item {
                BillsWidget(
                    quickFixes = quickFixes,
                    onItemClick = { /*Handle Bill Item Click*/},
                    onShowAllClick = { /*Handle Show All Click*/},
                    itemsToShowDefault = 2,
                )
              }
            }
      })
}
