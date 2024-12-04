package com.arygm.quickfix.ui.profile.becomeWorker

import android.graphics.Bitmap
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.unit.dp
import com.arygm.quickfix.model.account.AccountViewModel
import com.arygm.quickfix.model.account.LoggedInAccountViewModel
import com.arygm.quickfix.model.category.CategoryViewModel
import com.arygm.quickfix.model.profile.ProfileViewModel
import com.arygm.quickfix.model.profile.dataFields.AddOnService
import com.arygm.quickfix.model.profile.dataFields.IncludedService
import com.arygm.quickfix.ressources.C
import com.arygm.quickfix.ui.navigation.NavigationActions
import com.arygm.quickfix.ui.profile.becomeWorker.views.personal.PersonalInfoScreen
import com.arygm.quickfix.ui.profile.becomeWorker.views.professional.ProfessionalInfoScreen
import com.arygm.quickfix.ui.theme.poppinsTypography

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BusinessScreen(
    navigationActions: NavigationActions,
    accountViewModel: AccountViewModel,
    workerProfileViewModel: ProfileViewModel,
    loggedInAccountViewModel: LoggedInAccountViewModel,
    categoryViewModel: CategoryViewModel
) {
  val categories = categoryViewModel.categories.collectAsState().value
  val pagerState = rememberPagerState(pageCount = { 3 })
  val focusManager = LocalFocusManager.current
  val displayName = remember { mutableStateOf("") }
  val description = remember { mutableStateOf("") }
  val imageBitmapPP = remember { mutableStateOf<Bitmap?>(null) }
  val imageBitmapBP = remember { mutableStateOf<Bitmap?>(null) }
  var displayNameError by remember { mutableStateOf(false) }
  var descriptionError by remember { mutableStateOf(false) }
  val price = remember { mutableStateOf("") }
  val fieldOfWork = remember { mutableStateOf("") }
  val includedServices = remember { mutableStateOf(listOf<IncludedService>()) }
  val addOnServices = remember { mutableStateOf(listOf<AddOnService>()) }
  val tags = remember { mutableStateOf(listOf<String>()) }
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
        HorizontalPager(
            state = pagerState,
            modifier =
                Modifier.padding(innerPadding).fillMaxSize().semantics {
                  testTag = C.Tag.upgradeToWorkerPager
                }) { page ->
              when (page) {
                0 -> {
                  PersonalInfoScreen(
                      pagerState,
                      displayName,
                      description,
                      imageBitmapPP,
                      imageBitmapBP,
                      displayNameError = displayNameError,
                      onDisplayNameErrorChange = { displayNameError = it },
                      descriptionError = descriptionError,
                      onDescriptionErrorChange = { descriptionError = it })
                }
                1 -> {
                  ProfessionalInfoScreen(
                      pagerState,
                      price,
                      fieldOfWork,
                      includedServices,
                      addOnServices,
                      tags,
                      categories)
                }
                2 -> {}
              }
            }
      })
}
