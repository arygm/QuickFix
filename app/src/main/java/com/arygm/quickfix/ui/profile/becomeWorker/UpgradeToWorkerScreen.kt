package com.arygm.quickfix.ui.profile.becomeWorker

import android.graphics.Bitmap
import android.util.Log
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
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
import com.arygm.quickfix.model.account.Account
import com.arygm.quickfix.model.account.AccountViewModel
import com.arygm.quickfix.model.account.LoggedInAccountViewModel
import com.arygm.quickfix.model.category.CategoryViewModel
import com.arygm.quickfix.model.locations.Location
import com.arygm.quickfix.model.locations.LocationViewModel
import com.arygm.quickfix.model.offline.small.PreferencesViewModel
import com.arygm.quickfix.model.profile.ProfileViewModel
import com.arygm.quickfix.model.profile.WorkerProfile
import com.arygm.quickfix.model.profile.dataFields.AddOnService
import com.arygm.quickfix.model.profile.dataFields.IncludedService
import com.arygm.quickfix.ressources.C
import com.arygm.quickfix.ui.navigation.NavigationActions
import com.arygm.quickfix.ui.profile.becomeWorker.views.personal.PersonalInfoScreen
import com.arygm.quickfix.ui.profile.becomeWorker.views.professional.ProfessionalInfoScreen
import com.arygm.quickfix.ui.profile.becomeWorker.views.welcome.WelcomeOnBoardScreen
import com.arygm.quickfix.ui.theme.poppinsTypography
import com.arygm.quickfix.utils.loadBirthDate
import com.arygm.quickfix.utils.loadEmail
import com.arygm.quickfix.utils.loadFirstName
import com.arygm.quickfix.utils.loadIsWorker
import com.arygm.quickfix.utils.loadLastName
import com.arygm.quickfix.utils.loadUserId
import com.arygm.quickfix.utils.setAccountPreferences
import com.arygm.quickfix.utils.stringToTimestamp
import com.google.firebase.Timestamp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BusinessScreen(
    navigationActions: NavigationActions,
    accountViewModel: AccountViewModel,
    workerProfileViewModel: ProfileViewModel,
    loggedInAccountViewModel: LoggedInAccountViewModel,
    preferencesViewModel: PreferencesViewModel,
    categoryViewModel: CategoryViewModel,
    locationViewModel: LocationViewModel,
    testBitmapPP: Bitmap? = null,
    testLocation: Location? = Location()
) {
    val locationWorker = remember { mutableStateOf(Location()) }
    val categories = categoryViewModel.categories.collectAsState().value
  val pagerState = rememberPagerState(pageCount = { 3 })
  val focusManager = LocalFocusManager.current
  val displayName = remember { mutableStateOf("") }
  val description = remember { mutableStateOf("") }
  val imageBitmapPP = remember { mutableStateOf<Bitmap?>(testBitmapPP) }
  val imageBitmapBP = remember { mutableStateOf<Bitmap?>(null) }
  var displayNameError by remember { mutableStateOf(false) }
  var descriptionError by remember { mutableStateOf(false) }
  val price = remember { mutableDoubleStateOf(0.0) }
  val fieldOfWork = remember { mutableStateOf("") }
  val includedServices = remember { mutableStateOf(listOf<IncludedService>()) }
  val addOnServices = remember { mutableStateOf(listOf<AddOnService>()) }
  val tags = remember { mutableStateOf(listOf<String>()) }
    var workerId by remember { mutableStateOf("") }
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var birthDate by remember { mutableStateOf("") }
    var isWorker by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        workerId = loadUserId(preferencesViewModel)
        firstName = loadFirstName(preferencesViewModel)
        lastName = loadLastName(preferencesViewModel)
        email = loadEmail(preferencesViewModel)
        birthDate = loadBirthDate(preferencesViewModel)
        isWorker = loadIsWorker(preferencesViewModel)
    }


    val handleSuccessfulImageUpload: (String, List<String>) -> Unit =
        { accountId, uploadedImageUrls ->
            // Make the announcement
            val workerProfile =
                WorkerProfile(
                    location = locationWorker.value,
                    fieldOfWork = fieldOfWork.value,
                    description = description.value,
                    price = price.doubleValue,
                    displayName = displayName.value,
                    includedServices = includedServices.value,
                    addOnServices = addOnServices.value,
                    tags = tags.value,
                    profilePicture = uploadedImageUrls[0],
                    bannerPicture = if (uploadedImageUrls.size > 1) uploadedImageUrls[1] else "",
                    uid = accountId)
            Log.d("UpgradeToWorkerScreen", "workerProfile: ${locationWorker.value.name} ${fieldOfWork.value} ${description.value} ${price.doubleValue} ${displayName.value} ${includedServices.value} ${addOnServices.value} ${tags.value} ${uploadedImageUrls[0]} ${if (uploadedImageUrls.size > 1) uploadedImageUrls[1] else ""} $accountId")
            workerProfileViewModel.addProfile(workerProfile,
                onSuccess = {
                    val newAccount = Account(
                        uid = workerId,
                        firstName = firstName,
                        lastName = lastName,
                        email = email,
                        birthDate = stringToTimestamp(birthDate) ?: Timestamp.now(),
                        isWorker = true
                    )
                    accountViewModel.updateAccount(newAccount,
                    onSuccess = {
                        setAccountPreferences(preferencesViewModel, newAccount)
                    },
                    onFailure = { e ->
                        // Handle the failure case
                        Log.e("AnnouncementViewModel", "Failed to update account: ${e.message}")
                    })
                },
                onFailure = { e ->
                    // Handle the failure case
                    Log.e("AnnouncementViewModel", "Failed to add announcement: ${e.message}")
                })
        }

    LaunchedEffect(pagerState.currentPage) {
        if(pagerState.currentPage == 2){
            Log.d("UpgradeToWorker", "entered the last page")
            val images = listOfNotNull(imageBitmapPP.value, imageBitmapBP.value)
                // If there are no images to upload, proceed directly
            workerProfileViewModel.uploadProfileImages(
                    accountId = workerId,
                    images = images,
                    onSuccess = { uploadedImageUrls ->
                        handleSuccessfulImageUpload(workerId, uploadedImageUrls)
                    },
                    onFailure = { e ->
                        // Handle the failure case
                        Log.e("AnnouncementViewModel", "Failed to upload images: ${e.message}")
                    })
        }
    }
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
                }, userScrollEnabled = true) { page ->
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
                      onDescriptionErrorChange = { descriptionError = it },
                      locationViewModel =  locationViewModel,
                      locationWorker = locationWorker)
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
                2 -> {
                  WelcomeOnBoardScreen(navigationActions)
                }
              }
            }
      })
}
