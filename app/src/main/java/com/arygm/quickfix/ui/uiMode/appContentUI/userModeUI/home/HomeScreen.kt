package com.arygm.quickfix.ui.uiMode.appContentUI.userModeUI.home

import QuickFixToolboxFloatingButton
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Clear
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arygm.quickfix.R
import com.arygm.quickfix.model.category.Category
import com.arygm.quickfix.model.offline.small.PreferencesViewModel
import com.arygm.quickfix.model.profile.ProfileViewModel
import com.arygm.quickfix.model.quickfix.QuickFix
import com.arygm.quickfix.model.quickfix.QuickFixViewModel
import com.arygm.quickfix.model.quickfix.Status
import com.arygm.quickfix.model.search.SearchViewModel
import com.arygm.quickfix.model.tools.ai.GeminiViewModel
import com.arygm.quickfix.ressources.C
import com.arygm.quickfix.ui.elements.PopularServicesRow
import com.arygm.quickfix.ui.elements.QuickFixButton
import com.arygm.quickfix.ui.elements.QuickFixTextFieldCustom
import com.arygm.quickfix.ui.elements.QuickFixesWidget
import com.arygm.quickfix.ui.navigation.NavigationActions
import com.arygm.quickfix.ui.theme.BackgroundSecondary
import com.arygm.quickfix.ui.theme.poppinsTypography
import com.arygm.quickfix.ui.uiMode.appContentUI.userModeUI.navigation.USER_TOP_LEVEL_DESTINATIONS
import com.arygm.quickfix.ui.uiMode.appContentUI.userModeUI.navigation.UserScreen
import com.arygm.quickfix.ui.uiMode.appContentUI.userModeUI.navigation.UserTopLevelDestinations
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
    quickFixViewModel: QuickFixViewModel,
    navigationActionsRoot: NavigationActions,
    searchViewModel: SearchViewModel
) {
  val focusManager = LocalFocusManager.current
  val scrollState = rememberScrollState()

  val geminiViewModel = GeminiViewModel()
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
  // Sample data for services and quick fixes
  val services =
      listOf(
          Category(name = "Painting", description = "Paint", id = R.drawable.painter.toString()),
          Category(
              name = "Gardening", description = "Gardener", id = R.drawable.gardener.toString()),
          Category(
              name = "Electrical Work",
              description = "Electrician",
              id = R.drawable.electrician.toString()))

  var quickFixes by remember { mutableStateOf(emptyList<QuickFix>()) }
  var mode by remember { mutableStateOf("") }
  var uid by remember { mutableStateOf("") }

  var isChatVisible by remember { mutableStateOf(false) }
  LaunchedEffect(Unit) {
    mode = loadAppMode(preferencesViewModel)
    uid = loadUserId(preferencesViewModel)
    userViewModel.fetchUserProfile(uid) { profile ->
      profile?.quickFixes?.forEach { quickFix ->
        quickFixViewModel.fetchQuickFix(quickFix) { fetchedQuickFix ->
          if (fetchedQuickFix != null) {
            quickFixes = quickFixes + fetchedQuickFix
            Log.d("HomeScreen", "Added QuickFix: $fetchedQuickFix")
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
              iconList = listOf(Icons.Default.Map, Icons.Default.AutoAwesome),
              onIconClick = { index ->
                if (index == 0) {
                  navigationActions.navigateTo(UserScreen.MAP)
                } else if (index == 1) { // Assuming you want to handle the second icon
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
                    Box {
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
                          debug = "homescreen",
                          isTextField = true)

                      Box(
                          modifier =
                              Modifier.matchParentSize().clickable {
                                navigationActionsRoot.navigateTo(UserTopLevelDestinations.SEARCH)
                              })
                    }

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
                      color = colorScheme.onBackground,
                      style =
                          poppinsTypography.headlineMedium.copy(
                              fontSize = 20.sp, fontWeight = FontWeight.SemiBold),
                      modifier =
                          Modifier.fillMaxWidth()
                              .padding(
                                  horizontal = (screenWidth * 0.04).dp,
                                  vertical = (screenHeight * 0.02).dp))

                  PopularServicesRow(
                      services = services,
                      modifier = Modifier.testTag("PopularServicesRow"),
                      onServiceClick = {
                        searchViewModel.setPopularServiceCategory(it)
                        navigationActionsRoot.navigateTo(UserTopLevelDestinations.SEARCH)
                      })

                  Spacer(modifier = Modifier.weight(0.09f))

                  // Section QuickFixes
                  if (quickFixes.any { it.status == Status.UPCOMING }) {
                    Box(modifier = Modifier.fillMaxWidth().weight(1.5f)) {
                      QuickFixesWidget(
                          status = "Upcoming",
                          quickFixList = quickFixes.filter { it.status == Status.UPCOMING },
                          onShowAllClick = { /* Handle Show All Click */},
                          onItemClick = {
                            quickFixViewModel.setUpdateQuickFix(it)
                            navigationActions.navigateTo(UserScreen.QUICKFIX_ONBOARDING)
                          },
                          modifier =
                              Modifier.fillMaxWidth()
                                  .padding(horizontal = 4.dp, vertical = 8.dp)
                                  .testTag("UpcomingQuickFixes"),
                          workerViewModel = workerViewModel,
                      )
                    }
                  } else {
                    Box(
                        modifier =
                            Modifier.fillMaxWidth().weight(1.5f).testTag("NoQuickFixesBox")) {
                          Column(
                              modifier =
                                  Modifier.fillMaxWidth()
                                      .padding(horizontal = 16.dp)
                                      .wrapContentHeight()
                                      .clip(RoundedCornerShape(10.dp))
                                      .background(colorScheme.surface),
                              verticalArrangement = Arrangement.spacedBy((-64).dp),
                              horizontalAlignment = Alignment.CenterHorizontally) {
                                if (colorScheme.surface == BackgroundSecondary) {
                                  Image(
                                      painter = painterResource(id = R.drawable.noquickfix),
                                      contentDescription = "No QuickFixes Background",
                                      contentScale = ContentScale.Crop,
                                      modifier = Modifier.size(180.dp).alpha(1f))
                                } else {
                                  Image(
                                      painter = painterResource(id = R.drawable.noquickfixdark),
                                      contentDescription = "No QuickFixes Background",
                                      contentScale = ContentScale.Crop,
                                      modifier = Modifier.size(180.dp).alpha(1f))
                                }

                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Bottom,
                                    modifier = Modifier.fillMaxWidth()) {
                                      Text(
                                          text = "No QuickFixes found",
                                          style =
                                              poppinsTypography.bodyMedium.copy(
                                                  fontWeight = FontWeight.Bold,
                                                  color = colorScheme.onBackground,
                                                  fontSize = 14.sp),
                                          textAlign = TextAlign.Center,
                                          modifier = Modifier.padding(vertical = 8.dp))

                                      Text(
                                          text =
                                              "You have not yet created any QuickFixes. Create one now!",
                                          style =
                                              poppinsTypography.bodyMedium.copy(
                                                  fontWeight = FontWeight.Medium,
                                                  color = colorScheme.onSurface,
                                                  fontSize = 10.sp),
                                          textAlign = TextAlign.Center,
                                      )

                                      QuickFixButton(
                                          buttonText = "Book a QuickFix",
                                          buttonColor = colorScheme.primary,
                                          textColor = colorScheme.onPrimary,
                                          onClickAction = {
                                            navigationActionsRoot.navigateTo(
                                                USER_TOP_LEVEL_DESTINATIONS[1])
                                          },
                                          textStyle =
                                              MaterialTheme.typography.labelMedium.copy(
                                                  fontWeight = FontWeight.SemiBold,
                                                  fontSize = 14.sp),
                                          modifier = Modifier.padding(vertical = 8.dp),
                                          leadingIcon = Icons.Outlined.Search,
                                          leadingIconTint = colorScheme.onPrimary)
                                    }
                              }
                        }
                  }
                }
          }
        })
  }
}
