package com.arygm.quickfix.ui.profile.becomeWorker

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag // Import pour les testTag
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.unit.dp
import com.arygm.quickfix.model.account.AccountViewModel
import com.arygm.quickfix.model.account.LoggedInAccountViewModel
import com.arygm.quickfix.model.profile.ProfileViewModel
import com.arygm.quickfix.ressources.C
import com.arygm.quickfix.ui.navigation.NavigationActions
import com.arygm.quickfix.ui.profile.becomeWorker.views.personal.PersonalInfoScreen
import com.arygm.quickfix.ui.theme.poppinsTypography

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BusinessScreen(
    navigationActions: NavigationActions,
    accountViewModel: AccountViewModel,
    workerProfileViewModel: ProfileViewModel,
    loggedInAccountViewModel: LoggedInAccountViewModel
) {
  val pagerState = rememberPagerState(pageCount = { 3 })
  val focusManager = LocalFocusManager.current
  val displayName = remember { mutableStateOf("") }
  val description = remember { mutableStateOf("") }
  val imagePathPP = remember { mutableStateOf("") }
  val imagePathBP = remember { mutableStateOf("") }
  var displayNameError by remember { mutableStateOf(false) }
  var descriptionError by remember { mutableStateOf(false) }
  Scaffold(
      modifier =
          Modifier.pointerInput(Unit) { detectTapGestures(onTap = { focusManager.clearFocus() }) }
              .semantics { testTag = C.Tag.upgradeToWorkerScaffold },
      topBar = {
        TopAppBar(
            title = {
              Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    "Business Account",
                    modifier = Modifier.testTag("BusinessAccountTitle").padding(end = 29.dp),
                    style = poppinsTypography.headlineMedium,
                    color = colorScheme.primary)
              }
            },
            navigationIcon = {
              IconButton(
                  onClick = { navigationActions.goBack() },
                  modifier = Modifier.testTag("goBackButton")) {
                    Icon(
                        Icons.Outlined.ArrowBack,
                        contentDescription = "Back",
                        tint = colorScheme.primary)
                  }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = colorScheme.surface),
            modifier = Modifier.semantics { testTag = C.Tag.upgradeToWorkerTopBar })
      },
      containerColor = colorScheme.surface,
      content = { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
          HorizontalPager(
              state = pagerState,
              modifier =
                  Modifier.weight(0.9f).semantics { testTag = C.Tag.upgradeToWorkerPager }) { page
                ->
                when (page) {
                  0 -> {
                    PersonalInfoScreen(
                        pagerState,
                        displayName,
                        description,
                        imagePathPP,
                        imagePathBP,
                        displayNameError = displayNameError,
                        onDisplayNameErrorChange = { displayNameError = it },
                        descriptionError = descriptionError,
                        onDescriptionErrorChange = { descriptionError = it })
                  }
                  1 -> {

                  }
                  2 -> {

                  }
                }
              }
        }
      })
}
