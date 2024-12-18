package com.arygm.quickfix.ui.uiMode.appContentUI.userModeUI.home

import QuickFixToolboxFloatingButton
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.arygm.quickfix.R
import com.arygm.quickfix.model.offline.small.PreferencesViewModel
import com.arygm.quickfix.model.profile.ProfileViewModel
import com.arygm.quickfix.model.quickfix.QuickFix
import com.arygm.quickfix.model.quickfix.QuickFixViewModel
import com.arygm.quickfix.model.tools.ai.GeminiViewModel
import com.arygm.quickfix.ressources.C
import com.arygm.quickfix.ui.elements.PopularServicesRow
import com.arygm.quickfix.ui.elements.QuickFixTextFieldCustom
import com.arygm.quickfix.ui.elements.QuickFixesWidget
import com.arygm.quickfix.ui.elements.Service
import com.arygm.quickfix.ui.navigation.NavigationActions
import com.arygm.quickfix.ui.theme.poppinsTypography
import com.arygm.quickfix.ui.uiMode.appContentUI.userModeUI.navigation.UserScreen
import com.arygm.quickfix.ui.uiMode.appContentUI.userModeUI.tools.ai.QuickFixAIChatScreen
import com.arygm.quickfix.utils.loadAppMode
import com.arygm.quickfix.utils.loadUserId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navigationActions: NavigationActions,
    preferencesViewModel: PreferencesViewModel,
    userViewModel: ProfileViewModel,
    workerViewModel: ProfileViewModel,
    quickFixViewModel: QuickFixViewModel
) {
  val focusManager = LocalFocusManager.current
  val geminiViewModel = GeminiViewModel()
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
  // Sample data for services and quick fixes
  val services =
      listOf(
          Service("Painter", R.drawable.painter),
          Service("Gardener", R.drawable.gardener),
          Service("Electrician", R.drawable.electrician))

  var quickFixes by remember { mutableStateOf(emptyList<QuickFix>()) }
  var mode by remember { mutableStateOf("") }
  var uid by remember { mutableStateOf("") }
  var isChatVisible by remember { mutableStateOf(false) }
  LaunchedEffect(Unit) {
    mode = loadAppMode(preferencesViewModel)
    uid = loadUserId(preferencesViewModel)
    userViewModel.fetchUserProfile(uid) { profile ->
      profile?.quickFixes?.forEach { quickFix ->
        quickFixViewModel.fetchQuickFix(quickFix) {
          if (it != null) {
            quickFixes = quickFixes + it
          }
        }
      }
    }
  }
  BoxWithConstraints {
    val screenHeight = maxHeight.value
    val screenWidth = maxWidth.value

    // Modal Bottom Sheet
    if (isChatVisible) {
      ModalBottomSheet(
          onDismissRequest = {
            isChatVisible = false
            geminiViewModel.clearMessages() // Clear messages when the chat is closed
          },
          sheetState = sheetState) {
            QuickFixAIChatScreen(viewModel = geminiViewModel)
          }
    }

    Scaffold(
        modifier =
            Modifier.pointerInput(Unit) { detectTapGestures(onTap = { focusManager.clearFocus() }) }
                .testTag("HomeScreen"),
        containerColor = colorScheme.background,
        floatingActionButton = {
          QuickFixToolboxFloatingButton(
              iconList = listOf(Icons.Default.Map, Icons.Default.AutoAwesome, Icons.Default.Create),
              onIconClick = { index ->
                if (index == 1) { // Assuming you want to handle the second icon
                  isChatVisible = true
                }
              },
              modifier =
                  Modifier.padding(bottom = (screenHeight * 0.07).dp)
                      .testTag("ToolboxFloatingButton"))
        },
        topBar = {
          TopAppBar(
              title = {
                Column {
                  Row(
                      modifier = Modifier.fillMaxSize().padding(0.dp),
                      horizontalArrangement = Arrangement.Start,
                      verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.home_screen_headline),
                            contentDescription = "home_title",
                            modifier = Modifier.size((screenHeight * 0.25).dp))
                      }
                }
              },
              colors = TopAppBarDefaults.topAppBarColors(containerColor = colorScheme.background),
              navigationIcon = {},
              actions = {
                IconButton(
                    onClick = { navigationActions.navigateTo(UserScreen.MESSAGES) },
                    Modifier.testTag("MessagesButton")) {
                      Icon(
                          imageVector = Icons.Outlined.Email,
                          contentDescription = "Messages",
                          tint = colorScheme.background)
                    }
              })
        },
        content = { padding ->
          Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            Column(
                modifier =
                    Modifier.fillMaxSize()
                        .padding(vertical = (screenHeight * 0.02).dp)
                        .testTag("homeContent"),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start) {
                  Row(
                      modifier = Modifier.fillMaxWidth(),
                  ) {
                    Spacer(modifier = Modifier.width((screenWidth * 0.03).dp))
                    Log.d("QuickFixTextFieldCustomHomeScreen", "DISPLAYED")
                    QuickFixTextFieldCustom(
                        modifier = Modifier.semantics { testTag = "searchBar" },
                        showLeadingIcon = { true },
                        showTrailingIcon = { true },
                        leadingIcon = Icons.Outlined.Search,
                        trailingIcon = { Icons.Default.Clear },
                        descriptionLeadIcon = "Search",
                        descriptionTrailIcon = "Clear",
                        placeHolderText = "Find your perfect fix with QuickFix",
                        shape = CircleShape,
                        textStyle = poppinsTypography.bodyMedium,
                        textColor = colorScheme.onBackground,
                        placeHolderColor = colorScheme.onBackground,
                        leadIconColor = colorScheme.onBackground,
                        trailIconColor = colorScheme.onBackground,
                        widthField = (screenWidth * 0.8).dp,
                        heightField = (screenHeight * 0.045).dp,
                        onValueChange = {},
                        value = "",
                        debug = "homescreen")

                    Spacer(modifier = Modifier.width((screenWidth * 0.04).dp))

                    IconButton(
                        onClick = { navigationActions.navigateTo(UserScreen.MESSAGES) },
                        modifier =
                            Modifier.size((screenHeight * 0.045).dp)
                                .clip(CircleShape)
                                .background(colorScheme.surface)
                                .padding(8.dp)
                                .testTag(C.Tag.notification)) {
                          Icon(
                              painter = painterResource(id = R.drawable.bell),
                              contentDescription = "notifications",
                              tint = colorScheme.primary)
                        }
                  }

                  Text(
                      text = "Popular services",
                      style = MaterialTheme.typography.titleMedium,
                      modifier =
                          Modifier.fillMaxWidth()
                              .padding(
                                  horizontal = (screenWidth * 0.04).dp,
                                  vertical = (screenHeight * 0.02).dp))

                  PopularServicesRow(
                      services = services,
                      modifier = Modifier.testTag("PopularServicesRow"),
                      onServiceClick = { /* Handle Service Click */})

                  Spacer(modifier = Modifier.weight(0.09f))

                  Box(modifier = Modifier.fillMaxWidth().zIndex(2f).weight(1.5f)) {
                    QuickFixesWidget(
                        status = "Upcoming",
                        quickFixList = quickFixes,
                        onShowAllClick = { /* Handle Show All Click */},
                        onItemClick = { /* Handle QuickFix Item Click */},
                        modifier = Modifier.testTag("UpcomingQuickFixes"),
                        workerViewModel = workerViewModel,
                    )
                  }
                }
          }
        })
  }
}
